
import React, {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { Link, useNavigate } from 'react-router-dom';

import { apiRequest } from '../services/api';
import { isAdmin } from '../utils/auth';

import SubmissionCard from '../components/SubmissionCard';
import '../styles/AdminDashboard.css';

const INITIAL_CHALLENGE = {
  title: '',
  description: '',
  difficulty: 'EASY',
  category: '',
};

const INITIAL_QUESTION = {
  challengeId: '',
  questionText: '',
};

const INITIAL_OPTION = {
  questionId: '',
  optionText: '',
  isCorrect: false,
};

const STATUS_FILTERS = ['ALL', 'PENDING', 'APPROVED', 'REJECTED'];

function AdminDashboard() {
  const navigate = useNavigate();

  // =====================================================
  // MAIN DATA
  // =====================================================

  const [submissions, setSubmissions] = useState([]);
  const [challenges, setChallenges] = useState([]);
  const [questions, setQuestions] = useState([]);
  const [options, setOptions] = useState([]);

  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const [updatingId, setUpdatingId] = useState(null);

  // =====================================================
  // SUBMISSION FILTERS
  // =====================================================

  const [submissionFilter, setSubmissionFilter] = useState('ALL');
  const [submissionSearch, setSubmissionSearch] = useState('');

  // =====================================================
  // CHALLENGE FORM
  // =====================================================

  const [showChallengeForm, setShowChallengeForm] = useState(false);
  const [editingChallengeId, setEditingChallengeId] = useState(null);
  const [challengeForm, setChallengeForm] = useState(INITIAL_CHALLENGE);
  const [challengeLoading, setChallengeLoading] = useState(false);

  // =====================================================
  // QUESTION FORM
  // =====================================================

  const [showQuestionForm, setShowQuestionForm] = useState(false);
  const [editingQuestionId, setEditingQuestionId] = useState(null);
  const [questionForm, setQuestionForm] = useState(INITIAL_QUESTION);
  const [questionLoading, setQuestionLoading] = useState(false);

  // =====================================================
  // OPTION FORM
  // =====================================================

  const [showOptionForm, setShowOptionForm] = useState(false);
  const [editingOptionId, setEditingOptionId] = useState(null);
  const [optionForm, setOptionForm] = useState(INITIAL_OPTION);
  const [optionLoading, setOptionLoading] = useState(false);

  // =====================================================
  // EXPANSION STATES
  // =====================================================

  const [expandedChallengeId, setExpandedChallengeId] = useState(null);
  const [expandedQuestionId, setExpandedQuestionId] = useState(null);

  // =====================================================
  // HELPERS
  // =====================================================

  const formatNumber = (value) => {
    const number = Number(value);
    return new Intl.NumberFormat('en-US').format(
      Number.isFinite(number) ? number : 0
    );
  };

  const getStatus = (status) => {
    const normalized = String(status || 'PENDING').toUpperCase();

    return STATUS_FILTERS.includes(normalized) && normalized !== 'ALL'
      ? normalized
      : normalized;
  };

  const getChallengeTitle = (challenge) => {
    return challenge.title || `Challenge #${challenge.id}`;
  };

  const getQuestionTitle = (question) => {
    return question.questionText || `Question #${question.id}`;
  };

  // =====================================================
  // FETCH ALL ADMIN DATA
  // =====================================================

  const fetchData = useCallback(async (showLoader = false) => {
    if (showLoader) {
      setLoading(true);
    }

    setError('');

    try {
      const [
        submissionResult,
        challengeResult,
        questionResult,
        optionResult,
      ] = await Promise.all([
        apiRequest('/submissions', { method: 'GET' }),
        apiRequest('/challenges', { method: 'GET' }),
        apiRequest('/questions', { method: 'GET' }),
        apiRequest('/options', { method: 'GET' }),
      ]);

      const results = [
        { result: submissionResult, label: 'submissions' },
        { result: challengeResult, label: 'challenges' },
        { result: questionResult, label: 'questions' },
        { result: optionResult, label: 'options' },
      ];

      for (const { result, label } of results) {
        if (!result.response.ok) {
          throw new Error(
            result.data?.message || `Failed to load ${label}.`
          );
        }
      }

      const normalizeArray = (data) => {
        if (Array.isArray(data)) return data;
        if (Array.isArray(data?.content)) return data.content;
        return [];
      };

      setSubmissions(normalizeArray(submissionResult.data));
      setChallenges(normalizeArray(challengeResult.data));
      setQuestions(normalizeArray(questionResult.data));
      setOptions(normalizeArray(optionResult.data));

    } catch (err) {
      console.error('Admin dashboard error:', err);

      setError(
        err?.message || 'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // AUTHENTICATION & INITIAL LOAD
  // =====================================================

  useEffect(() => {
    if (!isAdmin()) {
      navigate('/challenges', { replace: true });
      return;
    }

    fetchData(true);
  }, [navigate, fetchData]);

  // =====================================================
  // REFRESH SUBMISSIONS
  // =====================================================

  const refreshSubmissions = async () => {
    if (refreshing) return;

    setRefreshing(true);
    setError('');

    try {
      const { response, data } = await apiRequest('/submissions', {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error(
          data?.message || 'Failed to refresh submissions.'
        );
      }

      setSubmissions(
        Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : []
      );
    } catch (err) {
      console.error('Refresh submissions error:', err);
      setError(err.message || 'Unable to refresh submissions.');
    } finally {
      setRefreshing(false);
    }
  };

  // =====================================================
  // UPDATE SUBMISSION STATUS
  // =====================================================

  const updateSubmissionStatus = async (submissionId, status) => {
    if (updatingId !== null) return;

    setUpdatingId(submissionId);
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/submissions/${submissionId}/status`,
        {
          method: 'PUT',
          body: JSON.stringify({ status }),
        }
      );

      if (!response.ok) {
        throw new Error(
          data?.message || 'Failed to update submission.'
        );
      }

      setSubmissions((current) =>
        current.map((submission) =>
          submission.id === submissionId
            ? {
                ...submission,
                status: data?.status || status,
              }
            : submission
        )
      );
    } catch (err) {
      console.error('Update submission status error:', err);
      setError(err.message || 'Unable to update submission.');
    } finally {
      setUpdatingId(null);
    }
  };

  // =====================================================
  // SUBMISSION STATISTICS
  // =====================================================

  const submissionStats = useMemo(() => {
    return submissions.reduce(
      (stats, submission) => {
        const status = getStatus(submission.status);

        if (status === 'PENDING') stats.pending++;
        if (status === 'APPROVED') stats.approved++;
        if (status === 'REJECTED') stats.rejected++;

        return stats;
      },
      {
        pending: 0,
        approved: 0,
        rejected: 0,
      }
    );
  }, [submissions]);

  const filteredSubmissions = useMemo(() => {
    const search = submissionSearch.trim().toLowerCase();

    return submissions
      .filter((submission) => {
        const status = getStatus(submission.status);

        if (
          submissionFilter !== 'ALL' &&
          status !== submissionFilter
        ) {
          return false;
        }

        if (!search) return true;

        const searchableValues = [
          submission.id,
          submission.userId,
          submission.challengeId,
          submission.status,
        ];

        return searchableValues.some((value) =>
          String(value ?? '').toLowerCase().includes(search)
        );
      })
      .sort((a, b) => {
        const dateA = new Date(a.submittedAt || 0).getTime();
        const dateB = new Date(b.submittedAt || 0).getTime();

        return (
          (Number.isFinite(dateB) ? dateB : 0) -
          (Number.isFinite(dateA) ? dateA : 0)
        );
      });
  }, [submissions, submissionFilter, submissionSearch]);

  // =====================================================
  // DASHBOARD STATISTICS
  // =====================================================

  const dashboardStats = useMemo(
    () => [
      {
        label: 'Total Challenges',
        value: challenges.length,
        icon: '▦',
        type: 'purple',
        description: 'Available coding challenges',
      },
      {
        label: 'Total Questions',
        value: questions.length,
        icon: '☷',
        type: 'blue',
        description: 'Questions across challenges',
      },
      {
        label: 'Total Submissions',
        value: submissions.length,
        icon: '↗',
        type: 'green',
        description: 'User coding attempts',
      },
      {
        label: 'Pending Reviews',
        value: submissionStats.pending,
        icon: '◷',
        type: 'orange',
        description: 'Awaiting administrator review',
      },
    ],
    [challenges.length, questions.length, submissions.length, submissionStats.pending]
  );

  // =====================================================
  // CHALLENGE FORM HANDLERS
  // =====================================================

  const handleChallengeChange = (event) => {
    const { name, value } = event.target;

    setChallengeForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const resetChallengeForm = () => {
    setChallengeForm({ ...INITIAL_CHALLENGE });
    setEditingChallengeId(null);
    setShowChallengeForm(false);
  };

  const openChallengeForm = () => {
    if (showChallengeForm) {
      resetChallengeForm();
      return;
    }

    setChallengeForm({ ...INITIAL_CHALLENGE });
    setEditingChallengeId(null);
    setShowChallengeForm(true);
  };

  const submitChallenge = async (event) => {
    event.preventDefault();

    if (challengeLoading) return;

    const payload = {
      title: challengeForm.title.trim(),
      description: challengeForm.description.trim(),
      difficulty: challengeForm.difficulty,
      category: challengeForm.category.trim(),
    };

    if (!payload.title || !payload.description) {
      setError('Challenge title and description are required.');
      return;
    }

    setChallengeLoading(true);
    setError('');

    try {
      const endpoint = editingChallengeId
        ? `/challenges/${editingChallengeId}`
        : '/challenges';

      const method = editingChallengeId ? 'PUT' : 'POST';

      const { response, data } = await apiRequest(endpoint, {
        method,
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to save challenge.');
      }

      await fetchData();
      resetChallengeForm();

    } catch (err) {
      console.error('Challenge save error:', err);
      setError(err.message || 'Unable to save challenge.');
    } finally {
      setChallengeLoading(false);
    }
  };

  const editChallenge = (challenge) => {
    setChallengeForm({
      title: challenge.title || '',
      description: challenge.description || '',
      difficulty: challenge.difficulty || 'EASY',
      category: challenge.category || '',
    });

    setEditingChallengeId(challenge.id);
    setShowChallengeForm(true);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const deleteChallenge = async (challengeId) => {
    const confirmed = window.confirm(
      'Are you sure you want to delete this challenge? Related data may also be affected.'
    );

    if (!confirmed) return;

    setError('');

    try {
      const { response, data } = await apiRequest(
        `/challenges/${challengeId}`,
        { method: 'DELETE' }
      );

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to delete challenge.');
      }

      if (expandedChallengeId === challengeId) {
        setExpandedChallengeId(null);
      }

      await fetchData();

    } catch (err) {
      console.error('Delete challenge error:', err);
      setError(err.message || 'Unable to delete challenge.');
    }
  };

  // =====================================================
  // QUESTION FORM HANDLERS
  // =====================================================

  const handleQuestionChange = (event) => {
    const { name, value } = event.target;

    setQuestionForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const resetQuestionForm = () => {
    setQuestionForm({ ...INITIAL_QUESTION });
    setEditingQuestionId(null);
    setShowQuestionForm(false);
  };

  const openQuestionForm = (challengeId = '') => {
    setQuestionForm({
      challengeId: challengeId !== '' ? String(challengeId) : '',
      questionText: '',
    });

    setEditingQuestionId(null);
    setShowQuestionForm(true);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const submitQuestion = async (event) => {
    event.preventDefault();

    if (questionLoading) return;

    const payload = {
      challengeId: Number(questionForm.challengeId),
      questionText: questionForm.questionText.trim(),
    };

    if (!payload.challengeId || !payload.questionText) {
      setError('Please select a challenge and enter question text.');
      return;
    }

    setQuestionLoading(true);
    setError('');

    try {
      const endpoint = editingQuestionId
        ? `/questions/${editingQuestionId}`
        : '/questions';

      const method = editingQuestionId ? 'PUT' : 'POST';

      const { response, data } = await apiRequest(endpoint, {
        method,
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to save question.');
      }

      await fetchData();
      resetQuestionForm();

    } catch (err) {
      console.error('Question save error:', err);
      setError(err.message || 'Unable to save question.');
    } finally {
      setQuestionLoading(false);
    }
  };

  const editQuestion = (question) => {
    setQuestionForm({
      challengeId: String(question.challengeId ?? ''),
      questionText: question.questionText || '',
    });

    setEditingQuestionId(question.id);
    setShowQuestionForm(true);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const deleteQuestion = async (questionId) => {
    const confirmed = window.confirm(
      'Are you sure you want to delete this question?'
    );

    if (!confirmed) return;

    setError('');

    try {
      const { response, data } = await apiRequest(
        `/questions/${questionId}`,
        { method: 'DELETE' }
      );

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to delete question.');
      }

      if (expandedQuestionId === questionId) {
        setExpandedQuestionId(null);
      }

      await fetchData();

    } catch (err) {
      console.error('Delete question error:', err);
      setError(err.message || 'Unable to delete question.');
    }
  };

  // =====================================================
  // OPTION FORM HANDLERS
  // =====================================================

  const handleOptionChange = (event) => {
    const { name, value, type, checked } = event.target;

    setOptionForm((current) => ({
      ...current,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const resetOptionForm = () => {
    setOptionForm({ ...INITIAL_OPTION });
    setEditingOptionId(null);
    setShowOptionForm(false);
  };

  const openOptionForm = (questionId = '') => {
    setOptionForm({
      questionId: questionId !== '' ? String(questionId) : '',
      optionText: '',
      isCorrect: false,
    });

    setEditingOptionId(null);
    setShowOptionForm(true);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const submitOption = async (event) => {
    event.preventDefault();

    if (optionLoading) return;

    const payload = {
      questionId: Number(optionForm.questionId),
      optionText: optionForm.optionText.trim(),
      isCorrect: optionForm.isCorrect,
    };

    if (!payload.questionId || !payload.optionText) {
      setError('Please select a question and enter option text.');
      return;
    }

    setOptionLoading(true);
    setError('');

    try {
      const endpoint = editingOptionId
        ? `/options/${editingOptionId}`
        : '/options';

      const method = editingOptionId ? 'PUT' : 'POST';

      const { response, data } = await apiRequest(endpoint, {
        method,
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to save option.');
      }

      await fetchData();
      resetOptionForm();

    } catch (err) {
      console.error('Option save error:', err);
      setError(err.message || 'Unable to save option.');
    } finally {
      setOptionLoading(false);
    }
  };

  const editOption = (option) => {
    setOptionForm({
      questionId: String(option.questionId ?? ''),
      optionText: option.optionText || '',
      isCorrect: option.isCorrect === true,
    });

    setEditingOptionId(option.id);
    setShowOptionForm(true);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const deleteOption = async (optionId) => {
    const confirmed = window.confirm(
      'Are you sure you want to delete this option?'
    );

    if (!confirmed) return;

    setError('');

    try {
      const { response, data } = await apiRequest(
        `/options/${optionId}`,
        { method: 'DELETE' }
      );

      if (!response.ok) {
        throw new Error(data?.message || 'Failed to delete option.');
      }

      await fetchData();

    } catch (err) {
      console.error('Delete option error:', err);
      setError(err.message || 'Unable to delete option.');
    }
  };

  // =====================================================
  // TOGGLE EXPANSION
  // =====================================================

  const toggleChallenge = (challengeId) => {
    setExpandedChallengeId((current) =>
      current === challengeId ? null : challengeId
    );
  };

  const toggleQuestion = (questionId) => {
    setExpandedQuestionId((current) =>
      current === questionId ? null : questionId
    );
  };

  // =====================================================
  // LOADING SCREEN
  // =====================================================

  if (loading) {
    return (
      <div className="page admin-dashboard-page">
        <div className="qad-loading">
          <div className="qad-spinner" />
          <h2>Loading Admin Dashboard</h2>
          <p>Preparing your QuestForge administration workspace...</p>
        </div>
      </div>
    );
  }

  // =====================================================
  // MAIN DASHBOARD
  // =====================================================

  return (
    <div className="page admin-dashboard-page qad-page">

      {/* HEADER */}

      <header className="qad-header">
        <div className="qad-header-content">
          <span className="qad-eyebrow">
            <span className="qad-eyebrow-dot" />
            QUESTFORGE ADMINISTRATION
          </span>

          <h1>Admin Dashboard</h1>

          <p>
            Your central workspace to manage challenges,
            questions, answer options and user submissions.
          </p>
        </div>

        <div className="qad-header-actions">
          <button
            type="button"
            className="qad-button qad-button-secondary"
            onClick={() => fetchData(true)}
          >
            ↻ Refresh Dashboard
          </button>

          <Link
            to="/challenges"
            className="qad-button qad-button-primary"
          >
            🚀 User View
          </Link>
        </div>
      </header>

      {/* ERROR */}

      {error && (
        <div className="qad-error-banner" role="alert">
          <div>
            <strong>Something needs your attention</strong>
            <p>{error}</p>
          </div>

          <button
            type="button"
            onClick={() => setError('')}
            aria-label="Dismiss error"
          >
            ×
          </button>
        </div>
      )}

      {/* MAIN STATISTICS */}

      <section className="qad-stats-grid">
        {dashboardStats.map((stat) => (
          <article className="qad-stat-card" key={stat.label}>
            <div className="qad-stat-top">
              <span>{stat.label}</span>

              <div className={`qad-stat-icon ${stat.type}`}>
                {stat.icon}
              </div>
            </div>

            <strong>{formatNumber(stat.value)}</strong>
            <p>{stat.description}</p>
          </article>
        ))}
      </section>

      {/* CHALLENGE MANAGEMENT */}

      <section className="qad-section">
        <div className="qad-section-header">
          <div>
            <span className="qad-section-eyebrow">01 / CONTENT MANAGEMENT</span>
            <h2>Challenge Management</h2>
            <p>Create, organize, edit and manage coding challenges.</p>
          </div>

          <button
            type="button"
            className="qad-button qad-button-primary"
            onClick={openChallengeForm}
          >
            {showChallengeForm ? '× Close Form' : '+ New Challenge'}
          </button>
        </div>

        {showChallengeForm && (
          <form
            className="qad-form-card"
            onSubmit={submitChallenge}
          >
            <div className="qad-form-heading">
              <div>
                <span className="qad-section-eyebrow">
                  CHALLENGE BUILDER
                </span>

                <h3>
                  {editingChallengeId
                    ? 'Edit Challenge'
                    : 'Create New Challenge'}
                </h3>
              </div>

              <span className="qad-form-step">01</span>
            </div>

            <div className="qad-form-grid">
              <label className="qad-field qad-field-full">
                <span>Challenge Title *</span>
                <input
                  name="title"
                  type="text"
                  placeholder="e.g. Two Sum Problem"
                  value={challengeForm.title}
                  onChange={handleChallengeChange}
                  maxLength={150}
                  required
                />
              </label>

              <label className="qad-field qad-field-full">
                <span>Description *</span>
                <textarea
                  name="description"
                  placeholder="Describe the coding challenge..."
                  value={challengeForm.description}
                  onChange={handleChallengeChange}
                  rows={5}
                  maxLength={5000}
                  required
                />
              </label>

              <label className="qad-field">
                <span>Difficulty *</span>
                <select
                  name="difficulty"
                  value={challengeForm.difficulty}
                  onChange={handleChallengeChange}
                  required
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </label>

              <label className="qad-field">
                <span>Category</span>
                <input
                  name="category"
                  type="text"
                  placeholder="e.g. Arrays, Strings, Java"
                  value={challengeForm.category}
                  onChange={handleChallengeChange}
                  maxLength={100}
                />
              </label>
            </div>

            <div className="qad-form-actions">
              <button
                type="submit"
                className="qad-button qad-button-primary"
                disabled={challengeLoading}
              >
                {challengeLoading
                  ? 'Saving...'
                  : editingChallengeId
                  ? 'Update Challenge'
                  : 'Create Challenge'}
              </button>

              <button
                type="button"
                className="qad-button qad-button-secondary"
                onClick={resetChallengeForm}
                disabled={challengeLoading}
              >
                Cancel
              </button>
            </div>
          </form>
        )}

        {/* CHALLENGE GRID */}

        {challenges.length === 0 ? (
          <div className="qad-empty-state">
            <span>▦</span>
            <h3>No Challenges Yet</h3>
            <p>Create your first coding challenge to get started.</p>
          </div>
        ) : (
          <div className="qad-challenge-grid">
            {challenges.map((challenge) => {
              const challengeQuestions = questions.filter(
                (question) =>
                  Number(question.challengeId) === Number(challenge.id)
              );

              const expanded = expandedChallengeId === challenge.id;

              return (
                <article className="qad-challenge-card" key={challenge.id}>
                  <div className="qad-challenge-card-top">
                    <span
                      className={`qad-difficulty ${String(
                        challenge.difficulty || 'unknown'
                      ).toLowerCase()}`}
                    >
                      {challenge.difficulty || 'UNKNOWN'}
                    </span>

                    <span className="qad-challenge-id">
                      #{challenge.id}
                    </span>
                  </div>

                  <h3>{getChallengeTitle(challenge)}</h3>

                  <p className="qad-challenge-description">
                    {challenge.description || 'No description available.'}
                  </p>

                  <div className="qad-challenge-meta">
                    <span>
                      <strong>Category:</strong>{' '}
                      {challenge.category || 'General'}
                    </span>

                    <span>
                      <strong>Questions:</strong>{' '}
                      {challengeQuestions.length}
                    </span>
                  </div>

                  <div className="qad-card-actions">
                    <button
                      type="button"
                      className="qad-button qad-button-secondary"
                      onClick={() => editChallenge(challenge)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      className="qad-button qad-button-danger"
                      onClick={() => deleteChallenge(challenge.id)}
                    >
                      Delete
                    </button>

                    <button
                      type="button"
                      className="qad-button qad-button-primary"
                      onClick={() => toggleChallenge(challenge.id)}
                    >
                      {expanded ? 'Hide' : 'Questions'}
                    </button>
                  </div>

                  {expanded && (
                    <div className="qad-expanded-panel">
                      <div className="qad-expanded-header">
                        <div>
                          <h4>Challenge Questions</h4>
                          <span>
                            {challengeQuestions.length} questions
                          </span>
                        </div>

                        <button
                          type="button"
                          className="qad-mini-button"
                          onClick={() => openQuestionForm(challenge.id)}
                        >
                          + Add Question
                        </button>
                      </div>

                      {challengeQuestions.length === 0 ? (
                        <div className="qad-nested-empty">
                          No questions added to this challenge.
                        </div>
                      ) : (
                        challengeQuestions.map((question) => {
                          const questionOptions = options.filter(
                            (option) =>
                              Number(option.questionId) === Number(question.id)
                          );

                          const questionExpanded =
                            expandedQuestionId === question.id;

                          return (
                            <div
                              className="qad-question-card"
                              key={question.id}
                            >
                              <div className="qad-question-content">
                                <span className="qad-question-number">
                                  Q
                                </span>

                                <strong>{getQuestionTitle(question)}</strong>
                              </div>

                              <div className="qad-question-actions">
                                <button
                                  type="button"
                                  onClick={() => editQuestion(question)}
                                >
                                  Edit
                                </button>

                                <button
                                  type="button"
                                  className="qad-danger-text"
                                  onClick={() => deleteQuestion(question.id)}
                                >
                                  Delete
                                </button>

                                <button
                                  type="button"
                                  onClick={() => toggleQuestion(question.id)}
                                >
                                  {questionExpanded ? 'Hide Options' : 'Options'}
                                </button>
                              </div>

                              {questionExpanded && (
                                <div className="qad-options-panel">
                                  <div className="qad-options-heading">
                                    <strong>Answer Options</strong>

                                    <span>
                                      {questionOptions.length} options
                                    </span>
                                  </div>

                                  {questionOptions.length === 0 ? (
                                    <p className="qad-nested-empty">
                                      No answer options available.
                                    </p>
                                  ) : (
                                    questionOptions.map((option) => (
                                      <div
                                        className="qad-option-row"
                                        key={option.id}
                                      >
                                        <div className="qad-option-text">
                                          <span
                                            className={
                                              option.isCorrect
                                                ? 'qad-correct-mark'
                                                : 'qad-incorrect-mark'
                                            }
                                          >
                                            {option.isCorrect ? '✓' : '○'}
                                          </span>

                                          <span>{option.optionText}</span>

                                          {option.isCorrect && (
                                            <span className="qad-correct-label">
                                              CORRECT
                                            </span>
                                          )}
                                        </div>

                                        <div className="qad-option-actions">
                                          <button
                                            type="button"
                                            onClick={() => editOption(option)}
                                          >
                                            Edit
                                          </button>

                                          <button
                                            type="button"
                                            className="qad-danger-text"
                                            onClick={() => deleteOption(option.id)}
                                          >
                                            Delete
                                          </button>
                                        </div>
                                      </div>
                                    ))
                                  )}

                                  <button
                                    type="button"
                                    className="qad-add-option"
                                    onClick={() => openOptionForm(question.id)}
                                  >
                                    + Add Answer Option
                                  </button>
                                </div>
                              )}
                            </div>
                          );
                        })
                      )}
                    </div>
                  )}
                </article>
              );
            })}
          </div>
        )}
      </section>

      {/* QUESTION FORM */}

      {showQuestionForm && (
        <section className="qad-section">
          <form
            className="qad-form-card"
            onSubmit={submitQuestion}
          >
            <div className="qad-form-heading">
              <div>
                <span className="qad-section-eyebrow">02 / QUESTION BUILDER</span>
                <h3>
                  {editingQuestionId ? 'Edit Question' : 'Create Question'}
                </h3>
              </div>

              <span className="qad-form-step">02</span>
            </div>

            <div className="qad-form-grid">
              <label className="qad-field qad-field-full">
                <span>Select Challenge *</span>

                <select
                  name="challengeId"
                  value={questionForm.challengeId}
                  onChange={handleQuestionChange}
                  required
                >
                  <option value="">Select a challenge</option>

                  {challenges.map((challenge) => (
                    <option key={challenge.id} value={challenge.id}>
                      {getChallengeTitle(challenge)}
                    </option>
                  ))}
                </select>
              </label>

              <label className="qad-field qad-field-full">
                <span>Question Text *</span>

                <textarea
                  name="questionText"
                  placeholder="Write your question..."
                  value={questionForm.questionText}
                  onChange={handleQuestionChange}
                  rows={5}
                  maxLength={5000}
                  required
                />
              </label>
            </div>

            <div className="qad-form-actions">
              <button
                type="submit"
                className="qad-button qad-button-primary"
                disabled={questionLoading}
              >
                {questionLoading
                  ? 'Saving...'
                  : editingQuestionId
                  ? 'Update Question'
                  : 'Create Question'}
              </button>

              <button
                type="button"
                className="qad-button qad-button-secondary"
                onClick={resetQuestionForm}
                disabled={questionLoading}
              >
                Cancel
              </button>
            </div>
          </form>
        </section>
      )}

      {/* OPTION FORM */}

      {showOptionForm && (
        <section className="qad-section">
          <form
            className="qad-form-card"
            onSubmit={submitOption}
          >
            <div className="qad-form-heading">
              <div>
                <span className="qad-section-eyebrow">03 / ANSWER BUILDER</span>
                <h3>
                  {editingOptionId ? 'Edit Answer Option' : 'Create Answer Option'}
                </h3>
              </div>

              <span className="qad-form-step">03</span>
            </div>

            <div className="qad-form-grid">
              <label className="qad-field qad-field-full">
                <span>Select Question *</span>

                <select
                  name="questionId"
                  value={optionForm.questionId}
                  onChange={handleOptionChange}
                  required
                >
                  <option value="">Select a question</option>

                  {questions.map((question) => (
                    <option key={question.id} value={question.id}>
                      {getQuestionTitle(question)}
                    </option>
                  ))}
                </select>
              </label>

              <label className="qad-field qad-field-full">
                <span>Answer Option *</span>

                <input
                  name="optionText"
                  type="text"
                  placeholder="Enter answer option..."
                  value={optionForm.optionText}
                  onChange={handleOptionChange}
                  maxLength={1000}
                  required
                />
              </label>

              <label className="qad-correct-checkbox">
                <input
                  name="isCorrect"
                  type="checkbox"
                  checked={optionForm.isCorrect}
                  onChange={handleOptionChange}
                />

                <span>
                  <strong>Mark as Correct Answer</strong>
                  <small>Identify this option as the correct response.</small>
                </span>
              </label>
            </div>

            <div className="qad-form-actions">
              <button
                type="submit"
                className="qad-button qad-button-primary"
                disabled={optionLoading}
              >
                {optionLoading
                  ? 'Saving...'
                  : editingOptionId
                  ? 'Update Option'
                  : 'Create Option'}
              </button>

              <button
                type="button"
                className="qad-button qad-button-secondary"
                onClick={resetOptionForm}
                disabled={optionLoading}
              >
                Cancel
              </button>
            </div>
          </form>
        </section>
      )}

      {/* QUICK ACTIONS */}

      <section className="qad-quick-actions">
        <div>
          <span className="qad-section-eyebrow">QUICK ACTIONS</span>
          <h3>Content Management</h3>
          <p>Quickly add new learning material to QuestForge.</p>
        </div>

        <div className="qad-quick-buttons">
          {!showQuestionForm && (
            <button
              type="button"
              className="qad-button qad-button-secondary"
              onClick={() => openQuestionForm()}
            >
              + Add Question
            </button>
          )}

          {!showOptionForm && (
            <button
              type="button"
              className="qad-button qad-button-secondary"
              onClick={() => openOptionForm()}
            >
              + Add Option
            </button>
          )}
        </div>
      </section>

      {/* SUBMISSION MANAGEMENT */}

      <section className="qad-section">
        <div className="qad-section-header">
          <div>
            <span className="qad-section-eyebrow">04 / REVIEW CENTER</span>
            <h2>Submission Management</h2>
            <p>Review, approve and manage user submissions.</p>
          </div>

          <button
            type="button"
            className="qad-button qad-button-secondary"
            onClick={refreshSubmissions}
            disabled={refreshing}
          >
            {refreshing ? 'Refreshing...' : '↻ Refresh'}
          </button>
        </div>

        {/* SUBMISSION STATUS STATS */}

        <div className="qad-submission-stats">
          {[
            {
              label: 'All Submissions',
              value: submissions.length,
              filter: 'ALL',
              type: 'purple',
            },
            {
              label: 'Pending',
              value: submissionStats.pending,
              filter: 'PENDING',
              type: 'orange',
            },
            {
              label: 'Approved',
              value: submissionStats.approved,
              filter: 'APPROVED',
              type: 'green',
            },
            {
              label: 'Rejected',
              value: submissionStats.rejected,
              filter: 'REJECTED',
              type: 'red',
            },
          ].map((stat) => (
            <button
              type="button"
              key={stat.filter}
              className={`qad-submission-stat ${stat.type} ${
                submissionFilter === stat.filter ? 'active' : ''
              }`}
              onClick={() => setSubmissionFilter(stat.filter)}
              aria-pressed={submissionFilter === stat.filter}
            >
              <span>{stat.label}</span>
              <strong>{formatNumber(stat.value)}</strong>
            </button>
          ))}
        </div>

        {/* SUBMISSION SEARCH */}

        <div className="qad-submission-toolbar">
          <div className="qad-submission-search">
            <span>⌕</span>

            <input
              type="search"
              placeholder="Search by submission ID, user ID or challenge ID..."
              value={submissionSearch}
              onChange={(event) => setSubmissionSearch(event.target.value)}
              aria-label="Search submissions"
            />

            {submissionSearch && (
              <button
                type="button"
                onClick={() => setSubmissionSearch('')}
                aria-label="Clear search"
              >
                ×
              </button>
            )}
          </div>

          <select
            value={submissionFilter}
            onChange={(event) => setSubmissionFilter(event.target.value)}
            aria-label="Filter submissions by status"
          >
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>

          <button
            type="button"
            className="qad-button qad-button-secondary"
            onClick={() => {
              setSubmissionSearch('');
              setSubmissionFilter('ALL');
            }}
          >
            Clear
          </button>
        </div>

        {/* SUBMISSION RESULTS */}

        <div className="qad-submission-results">
          <div className="qad-results-heading">
            <strong>Submission Records</strong>

            <span>
              {formatNumber(filteredSubmissions.length)} results
            </span>
          </div>

          {submissions.length === 0 ? (
            <div className="qad-empty-state">
              <span>◷</span>
              <h3>No Submissions Yet</h3>
              <p>User submissions will appear here when available.</p>
            </div>
          ) : filteredSubmissions.length === 0 ? (
            <div className="qad-empty-state">
              <span>⌕</span>
              <h3>No Matching Submissions</h3>
              <p>Try changing the search or status filter.</p>

              <button
                type="button"
                className="qad-button qad-button-secondary"
                onClick={() => {
                  setSubmissionSearch('');
                  setSubmissionFilter('ALL');
                }}
              >
                Reset Filters
              </button>
            </div>
          ) : (
            <div className="qad-submission-grid">
              {filteredSubmissions.map((submission) => (
                <SubmissionCard
                  key={submission.id}
                  submission={submission}
                  admin={true}
                  onStatusUpdate={updateSubmissionStatus}
                  updating={updatingId === submission.id}
                />
              ))}
            </div>
          )}
        </div>
      </section>

      {/* FOOTER */}

      <footer className="qad-footer">
        <span>QUESTFORGE ADMINISTRATION</span>
        <span>Manage. Monitor. Build. Improve.</span>
      </footer>

    </div>
  );
}

export default AdminDashboard;
