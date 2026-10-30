import { useEffect, useMemo, useState } from 'react';
import {
  Link,
  useNavigate,
} from 'react-router-dom';

import { apiRequest } from '../services/api';
import { isAdmin } from '../utils/auth';

import SubmissionCard from '../components/SubmissionCard';

function AdminDashboard() {
  const navigate = useNavigate();

  const [submissions, setSubmissions] =
    useState([]);

  const [challenges, setChallenges] =
    useState([]);

  const [questions, setQuestions] =
    useState([]);

  const [options, setOptions] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const [updatingId, setUpdatingId] =
    useState(null);

  const [refreshing, setRefreshing] =
    useState(false);

  const [submissionFilter, setSubmissionFilter] =
    useState('ALL');

  const [submissionSearch, setSubmissionSearch] =
    useState('');

  const [showChallengeForm, setShowChallengeForm] =
    useState(false);

  const [editingChallengeId, setEditingChallengeId] =
    useState(null);

  const [challengeForm, setChallengeForm] =
    useState({
      title: '',
      description: '',
      difficulty: 'EASY',
      category: '',
    });

  const [challengeLoading, setChallengeLoading] =
    useState(false);

  const [showQuestionForm, setShowQuestionForm] =
    useState(false);

  const [editingQuestionId, setEditingQuestionId] =
    useState(null);

  const [questionForm, setQuestionForm] =
    useState({
      challengeId: '',
      questionText: '',
    });

  const [questionLoading, setQuestionLoading] =
    useState(false);

  const [expandedChallengeId, setExpandedChallengeId] =
    useState(null);

  const [showOptionForm, setShowOptionForm] =
    useState(false);

  const [editingOptionId, setEditingOptionId] =
    useState(null);

  const [optionForm, setOptionForm] =
    useState({
      questionId: '',
      optionText: '',
      isCorrect: false,
    });

  const [optionLoading, setOptionLoading] =
    useState(false);

  const [expandedQuestionId, setExpandedQuestionId] =
    useState(null);

  const fetchData = async () => {
    setError('');

    try {
      const [
        submissionResult,
        challengeResult,
        questionResult,
        optionResult,
      ] = await Promise.all([
        apiRequest('/submissions', {
          method: 'GET',
        }),

        apiRequest('/challenges', {
          method: 'GET',
        }),

        apiRequest('/questions', {
          method: 'GET',
        }),

        apiRequest('/options', {
          method: 'GET',
        }),
      ]);

      if (!submissionResult.response.ok) {
        throw new Error(
          submissionResult.data?.message ||
            'Failed to load submissions.'
        );
      }

      if (!challengeResult.response.ok) {
        throw new Error(
          challengeResult.data?.message ||
            'Failed to load challenges.'
        );
      }

      if (!questionResult.response.ok) {
        throw new Error(
          questionResult.data?.message ||
            'Failed to load questions.'
        );
      }

      if (!optionResult.response.ok) {
        throw new Error(
          optionResult.data?.message ||
            'Failed to load options.'
        );
      }

      setSubmissions(
        Array.isArray(
          submissionResult.data
        )
          ? submissionResult.data
          : []
      );

      setChallenges(
        Array.isArray(
          challengeResult.data
        )
          ? challengeResult.data
          : []
      );

      setQuestions(
        Array.isArray(
          questionResult.data
        )
          ? questionResult.data
          : []
      );

      setOptions(
        Array.isArray(
          optionResult.data
        )
          ? optionResult.data
          : []
      );
    } catch (err) {
      console.error(
        'Admin dashboard error:',
        err
      );

      setError(
        err.message ||
          'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isAdmin()) {
      navigate('/challenges', {
        replace: true,
      });

      return;
    }

    fetchData();
  }, [navigate]);

  const refreshSubmissions = async () => {
    setRefreshing(true);
    setError('');

    try {
      const {
        response,
        data,
      } = await apiRequest(
        '/submissions',
        {
          method: 'GET',
        }
      );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to refresh submissions.'
        );

        return;
      }

      setSubmissions(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      console.error(
        'Refresh submissions error:',
        err
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setRefreshing(false);
    }
  };

  const updateSubmissionStatus =
    async (
      submissionId,
      status
    ) => {
      setUpdatingId(submissionId);
      setError('');

      try {
        const {
          response,
          data,
        } = await apiRequest(
          `/submissions/${submissionId}/status`,
          {
            method: 'PUT',
            body: JSON.stringify({
              status,
            }),
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              `Failed to update submission.`
          );

          return;
        }

        setSubmissions(
          (current) =>
            current.map(
              (submission) =>
                submission.id ===
                submissionId
                  ? {
                      ...submission,
                      status:
                        data?.status ||
                        status,
                    }
                  : submission
            )
        );
      } catch (err) {
        console.error(
          'Update submission status error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      } finally {
        setUpdatingId(null);
      }
    };

  const pendingCount =
    useMemo(
      () =>
        submissions.filter(
          (submission) =>
            submission.status?.toUpperCase() ===
            'PENDING'
        ).length,
      [submissions]
    );

  const approvedCount =
    useMemo(
      () =>
        submissions.filter(
          (submission) =>
            submission.status?.toUpperCase() ===
            'APPROVED'
        ).length,
      [submissions]
    );

  const rejectedCount =
    useMemo(
      () =>
        submissions.filter(
          (submission) =>
            submission.status?.toUpperCase() ===
            'REJECTED'
        ).length,
      [submissions]
    );

  const filteredSubmissions =
    useMemo(() => {
      const search =
        submissionSearch
          .trim()
          .toLowerCase();

      return submissions
        .filter((submission) => {
          const status =
            submission.status?.toUpperCase() ||
            'PENDING';

          if (
            submissionFilter !== 'ALL' &&
            status !== submissionFilter
          ) {
            return false;
          }

          if (!search) {
            return true;
          }

          return [
            submission.id,
            submission.userId,
            submission.challengeId,
            submission.status,
          ]
            .map((value) =>
              String(value ?? '')
                .toLowerCase()
            )
            .some((value) =>
              value.includes(search)
            );
        })
        .sort((a, b) => {
          return (
            new Date(
              b.submittedAt || 0
            ).getTime() -
            new Date(
              a.submittedAt || 0
            ).getTime()
          );
        });
    }, [
      submissions,
      submissionFilter,
      submissionSearch,
    ]);

  const handleChallengeChange =
    (event) => {
      const {
        name,
        value,
      } = event.target;

      setChallengeForm(
        (current) => ({
          ...current,
          [name]: value,
        })
      );
    };

  const resetChallengeForm = () => {
    setChallengeForm({
      title: '',
      description: '',
      difficulty: 'EASY',
      category: '',
    });

    setEditingChallengeId(null);
    setShowChallengeForm(false);
  };

  const submitChallenge =
    async (event) => {
      event.preventDefault();

      setChallengeLoading(true);
      setError('');

      try {
        const endpoint =
          editingChallengeId
            ? `/challenges/${editingChallengeId}`
            : '/challenges';

        const method =
          editingChallengeId
            ? 'PUT'
            : 'POST';

        const {
          response,
          data,
        } = await apiRequest(
          endpoint,
          {
            method,
            body: JSON.stringify({
              title:
                challengeForm.title.trim(),

              description:
                challengeForm.description.trim(),

              difficulty:
                challengeForm.difficulty,

              category:
                challengeForm.category.trim(),
            }),
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to save challenge.'
          );

          return;
        }

        await fetchData();
        resetChallengeForm();
      } catch (err) {
        console.error(
          'Challenge save error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      } finally {
        setChallengeLoading(false);
      }
    };

  const editChallenge = (
    challenge
  ) => {
    setChallengeForm({
      title:
        challenge.title || '',
      description:
        challenge.description || '',
      difficulty:
        challenge.difficulty || 'EASY',
      category:
        challenge.category || '',
    });

    setEditingChallengeId(
      challenge.id
    );

    setShowChallengeForm(true);
  };

  const deleteChallenge =
    async (challengeId) => {
      const confirmed =
        window.confirm(
          'Are you sure you want to delete this challenge?'
        );

      if (!confirmed) {
        return;
      }

      setError('');

      try {
        const {
          response,
          data,
        } = await apiRequest(
          `/challenges/${challengeId}`,
          {
            method: 'DELETE',
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to delete challenge.'
          );

          return;
        }

        await fetchData();
      } catch (err) {
        console.error(
          'Delete challenge error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      }
    };

  const handleQuestionChange =
    (event) => {
      const {
        name,
        value,
      } = event.target;

      setQuestionForm(
        (current) => ({
          ...current,
          [name]: value,
        })
      );
    };

  const resetQuestionForm = () => {
    setQuestionForm({
      challengeId: '',
      questionText: '',
    });

    setEditingQuestionId(null);
    setShowQuestionForm(false);
  };

  const submitQuestion =
    async (event) => {
      event.preventDefault();

      setQuestionLoading(true);
      setError('');

      try {
        const endpoint =
          editingQuestionId
            ? `/questions/${editingQuestionId}`
            : '/questions';

        const method =
          editingQuestionId
            ? 'PUT'
            : 'POST';

        const {
          response,
          data,
        } = await apiRequest(
          endpoint,
          {
            method,
            body: JSON.stringify({
              challengeId:
                Number(
                  questionForm.challengeId
                ),

              questionText:
                questionForm.questionText.trim(),
            }),
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to save question.'
          );

          return;
        }

        await fetchData();
        resetQuestionForm();
      } catch (err) {
        console.error(
          'Question save error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      } finally {
        setQuestionLoading(false);
      }
    };

  const editQuestion = (
    question
  ) => {
    setQuestionForm({
      challengeId:
        question.challengeId ??
        '',
      questionText:
        question.questionText ||
        '',
    });

    setEditingQuestionId(
      question.id
    );

    setShowQuestionForm(true);
  };

  const deleteQuestion =
    async (questionId) => {
      const confirmed =
        window.confirm(
          'Are you sure you want to delete this question?'
        );

      if (!confirmed) {
        return;
      }

      setError('');

      try {
        const {
          response,
          data,
        } = await apiRequest(
          `/questions/${questionId}`,
          {
            method: 'DELETE',
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to delete question.'
          );

          return;
        }

        await fetchData();
      } catch (err) {
        console.error(
          'Delete question error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      }
    };

  const handleOptionChange =
    (event) => {
      const {
        name,
        value,
        type,
        checked,
      } = event.target;

      setOptionForm(
        (current) => ({
          ...current,
          [name]:
            type === 'checkbox'
              ? checked
              : value,
        })
      );
    };

  const resetOptionForm = () => {
    setOptionForm({
      questionId: '',
      optionText: '',
      isCorrect: false,
    });

    setEditingOptionId(null);
    setShowOptionForm(false);
  };

  const submitOption =
    async (event) => {
      event.preventDefault();

      setOptionLoading(true);
      setError('');

      try {
        const endpoint =
          editingOptionId
            ? `/options/${editingOptionId}`
            : '/options';

        const method =
          editingOptionId
            ? 'PUT'
            : 'POST';

        const {
          response,
          data,
        } = await apiRequest(
          endpoint,
          {
            method,
            body: JSON.stringify({
              questionId:
                Number(
                  optionForm.questionId
                ),

              optionText:
                optionForm.optionText.trim(),

              isCorrect:
                optionForm.isCorrect,
            }),
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to save option.'
          );

          return;
        }

        await fetchData();
        resetOptionForm();
      } catch (err) {
        console.error(
          'Option save error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      } finally {
        setOptionLoading(false);
      }
    };

  const editOption = (
    option
  ) => {
    setOptionForm({
      questionId:
        option.questionId ?? '',
      optionText:
        option.optionText || '',
      isCorrect:
        option.isCorrect === true,
    });

    setEditingOptionId(
      option.id
    );

    setShowOptionForm(true);
  };

  const deleteOption =
    async (optionId) => {
      const confirmed =
        window.confirm(
          'Are you sure you want to delete this option?'
        );

      if (!confirmed) {
        return;
      }

      setError('');

      try {
        const {
          response,
          data,
        } = await apiRequest(
          `/options/${optionId}`,
          {
            method: 'DELETE',
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to delete option.'
          );

          return;
        }

        await fetchData();
      } catch (err) {
        console.error(
          'Delete option error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      }
    };

  if (loading) {
    return (
      <div className="page">
        <div className="empty-card">
          <h2>
            Loading Admin Dashboard...
          </h2>

          <p>
            Please wait while we load
            QuestForge administration data.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="page">

      <div className="challenges-header">
        <div>
          <p className="eyebrow">
            QUESTFORGE ADMIN
          </p>

          <h1>
            Admin Dashboard 🛠️
          </h1>

          <p>
            Manage challenges, questions,
            options, and user submissions.
          </p>
        </div>

        <Link
          className="secondary-button"
          to="/challenges"
        >
          🚀 User View
        </Link>
      </div>

      {error && (
        <div
          className="empty-card"
          style={{
            marginBottom: '20px',
          }}
        >
          <p className="error-text">
            {error}
          </p>
        </div>
      )}

      {/* =========================
          DASHBOARD STATS
      ========================= */}

      <div className="result-stats">

        <div className="result-stat-card">
          <span>
            Challenges
          </span>

          <strong>
            {challenges.length}
          </strong>
        </div>

        <div className="result-stat-card">
          <span>
            Questions
          </span>

          <strong>
            {questions.length}
          </strong>
        </div>

        <div className="result-stat-card">
          <span>
            Submissions
          </span>

          <strong>
            {submissions.length}
          </strong>
        </div>

        <div className="result-stat-card">
          <span>
            Pending
          </span>

          <strong>
            {pendingCount}
          </strong>
        </div>

      </div>

      {/* =========================
          CHALLENGE MANAGEMENT
      ========================= */}

      <div className="admin-section">

        <div className="admin-section-header">

          <div>
            <p className="eyebrow">
              CHALLENGE MANAGEMENT
            </p>

            <h2>
              Challenges
            </h2>

            <p>
              Create, edit, and delete
              challenges.
            </p>
          </div>

          <button
            className="primary-button"
            onClick={() => {
              if (
                showChallengeForm
              ) {
                resetChallengeForm();
              } else {
                setShowChallengeForm(true);
              }
            }}
          >
            {showChallengeForm
              ? 'Cancel'
              : '+ New Challenge'}
          </button>

        </div>

        {showChallengeForm && (
          <form
            className="admin-form-card"
            onSubmit={submitChallenge}
          >

            <h3>
              {editingChallengeId
                ? 'Edit Challenge'
                : 'Create Challenge'}
            </h3>

            <input
              name="title"
              type="text"
              placeholder="Challenge title"
              value={
                challengeForm.title
              }
              onChange={
                handleChallengeChange
              }
              required
            />

            <textarea
              name="description"
              placeholder="Challenge description"
              value={
                challengeForm.description
              }
              onChange={
                handleChallengeChange
              }
              rows="4"
              required
            />

            <select
              name="difficulty"
              value={
                challengeForm.difficulty
              }
              onChange={
                handleChallengeChange
              }
            >
              <option value="EASY">
                Easy
              </option>

              <option value="MEDIUM">
                Medium
              </option>

              <option value="HARD">
                Hard
              </option>
            </select>

            <input
              name="category"
              type="text"
              placeholder="Category"
              value={
                challengeForm.category
              }
              onChange={
                handleChallengeChange
              }
            />

            <div
              style={{
                display: 'flex',
                gap: '10px',
                flexWrap: 'wrap',
              }}
            >
              <button
                type="submit"
                className="primary-button"
                disabled={
                  challengeLoading
                }
              >
                {challengeLoading
                  ? 'Saving...'
                  : editingChallengeId
                  ? 'Update Challenge'
                  : 'Create Challenge'}
              </button>

              <button
                type="button"
                className="secondary-button"
                onClick={
                  resetChallengeForm
                }
              >
                Cancel
              </button>
            </div>

          </form>
        )}

        {challenges.length === 0 ? (
          <div className="empty-card">
            <h3>
              No Challenges
            </h3>

            <p>
              Create your first challenge
              above.
            </p>
          </div>
        ) : (
          <div className="challenge-grid">

            {challenges.map(
              (challenge) => (
                <div
                  className="challenge-card"
                  key={challenge.id}
                >

                  <p className="eyebrow">
                    {challenge.difficulty ||
                      'CHALLENGE'}
                  </p>

                  <h3>
                    {challenge.title ||
                      `Challenge #${challenge.id}`}
                  </h3>

                  <p>
                    {challenge.description ||
                      'No description available.'}
                  </p>

                  <p>
                    <strong>
                      Category:
                    </strong>{' '}
                    {challenge.category ||
                      'General'}
                  </p>

                  <div
                    style={{
                      display: 'flex',
                      gap: '8px',
                      flexWrap: 'wrap',
                    }}
                  >
                    <button
                      className="secondary-button"
                      onClick={() =>
                        editChallenge(
                          challenge
                        )
                      }
                    >
                      Edit
                    </button>

                    <button
                      className="secondary-button delete-button"
                      onClick={() =>
                        deleteChallenge(
                          challenge.id
                        )
                      }
                    >
                      Delete
                    </button>

                    <button
                      className="primary-button"
                      onClick={() =>
                        setExpandedChallengeId(
                          expandedChallengeId ===
                            challenge.id
                            ? null
                            : challenge.id
                        )
                      }
                    >
                      {expandedChallengeId ===
                      challenge.id
                        ? 'Hide Questions'
                        : 'Questions'}
                    </button>
                  </div>

                  {expandedChallengeId ===
                    challenge.id && (
                    <div
                      style={{
                        marginTop: '20px',
                      }}
                    >

                      {questions
                        .filter(
                          (question) =>
                            Number(
                              question.challengeId
                            ) ===
                            Number(
                              challenge.id
                            )
                        )
                        .map(
                          (question) => (
                            <div
                              key={
                                question.id
                              }
                              className="admin-form-card"
                              style={{
                                marginBottom:
                                  '12px',
                              }}
                            >

                              <strong>
                                {question.questionText ||
                                  `Question #${question.id}`}
                              </strong>

                              <div
                                style={{
                                  display:
                                    'flex',
                                  gap: '8px',
                                  flexWrap:
                                    'wrap',
                                  marginTop:
                                    '10px',
                                }}
                              >
                                <button
                                  className="secondary-button"
                                  onClick={() =>
                                    editQuestion(
                                      question
                                    )
                                  }
                                >
                                  Edit
                                </button>

                                <button
                                  className="secondary-button delete-button"
                                  onClick={() =>
                                    deleteQuestion(
                                      question.id
                                    )
                                  }
                                >
                                  Delete
                                </button>

                                <button
                                  className="secondary-button"
                                  onClick={() =>
                                    setExpandedQuestionId(
                                      expandedQuestionId ===
                                        question.id
                                        ? null
                                        : question.id
                                    )
                                  }
                                >
                                  Options
                                </button>
                              </div>

                              {expandedQuestionId ===
                                question.id && (
                                <div
                                  style={{
                                    marginTop:
                                      '12px',
                                  }}
                                >
                                  {options
                                    .filter(
                                      (
                                        option
                                      ) =>
                                        Number(
                                          option.questionId
                                        ) ===
                                        Number(
                                          question.id
                                        )
                                    )
                                    .map(
                                      (
                                        option
                                      ) => (
                                        <div
                                          key={
                                            option.id
                                          }
                                          style={{
                                            padding:
                                              '8px 0',
                                          }}
                                        >
                                          {option.isCorrect
                                            ? '✅'
                                            : '⭕'}{' '}
                                          {option.optionText}

                                          <button
                                            className="secondary-button"
                                            style={{
                                              marginLeft:
                                                '10px',
                                            }}
                                            onClick={() =>
                                              editOption(
                                                option
                                              )
                                            }
                                          >
                                            Edit
                                          </button>

                                          <button
                                            className="secondary-button delete-button"
                                            style={{
                                              marginLeft:
                                                '8px',
                                            }}
                                            onClick={() =>
                                              deleteOption(
                                                option.id
                                              )
                                            }
                                          >
                                            Delete
                                          </button>
                                        </div>
                                      )
                                    )}

                                  <button
                                    className="primary-button"
                                    style={{
                                      marginTop:
                                        '8px',
                                    }}
                                    onClick={() => {
                                      setOptionForm(
                                        {
                                          questionId:
                                            question.id,
                                          optionText:
                                            '',
                                          isCorrect:
                                            false,
                                        }
                                      );

                                      setEditingOptionId(
                                        null
                                      );

                                      setShowOptionForm(
                                        true
                                      );
                                    }}
                                  >
                                    + Add Option
                                  </button>
                                </div>
                              )}

                            </div>
                          )
                        )}

                    </div>
                  )}

                </div>
              )
            )}

          </div>
        )}

      </div>

      {/* =========================
          QUESTION FORM
      ========================= */}

      {showQuestionForm && (
        <div className="admin-section">

          <div className="admin-section-header">

            <div>
              <p className="eyebrow">
                QUESTION MANAGEMENT
              </p>

              <h2>
                {editingQuestionId
                  ? 'Edit Question'
                  : 'Create Question'}
              </h2>
            </div>

          </div>

          <form
            className="admin-form-card"
            onSubmit={submitQuestion}
          >

            <select
              name="challengeId"
              value={
                questionForm.challengeId
              }
              onChange={
                handleQuestionChange
              }
              required
            >
              <option value="">
                Select Challenge
              </option>

              {challenges.map(
                (challenge) => (
                  <option
                    key={challenge.id}
                    value={challenge.id}
                  >
                    {challenge.title ||
                      `Challenge #${challenge.id}`}
                  </option>
                )
              )}
            </select>

            <textarea
              name="questionText"
              placeholder="Enter question"
              value={
                questionForm.questionText
              }
              onChange={
                handleQuestionChange
              }
              rows="4"
              required
            />

            <button
              type="submit"
              className="primary-button"
              disabled={
                questionLoading
              }
            >
              {questionLoading
                ? 'Saving...'
                : editingQuestionId
                ? 'Update Question'
                : 'Create Question'}
            </button>

            <button
              type="button"
              className="secondary-button"
              onClick={
                resetQuestionForm
              }
              style={{
                marginLeft: '8px',
              }}
            >
              Cancel
            </button>

          </form>

        </div>
      )}

      {/* =========================
          OPTION FORM
      ========================= */}

      {showOptionForm && (
        <div className="admin-section">

          <div className="admin-section-header">

            <div>
              <p className="eyebrow">
                OPTION MANAGEMENT
              </p>

              <h2>
                {editingOptionId
                  ? 'Edit Option'
                  : 'Create Option'}
              </h2>
            </div>

          </div>

          <form
            className="admin-form-card"
            onSubmit={submitOption}
          >

            <select
              name="questionId"
              value={
                optionForm.questionId
              }
              onChange={
                handleOptionChange
              }
              required
            >
              <option value="">
                Select Question
              </option>

              {questions.map(
                (question) => (
                  <option
                    key={question.id}
                    value={question.id}
                  >
                    {question.questionText ||
                      `Question #${question.id}`}
                  </option>
                )
              )}
            </select>

            <input
              name="optionText"
              type="text"
              placeholder="Enter option"
              value={
                optionForm.optionText
              }
              onChange={
                handleOptionChange
              }
              required
            />

            <label
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                marginBottom: '15px',
              }}
            >
              <input
                name="isCorrect"
                type="checkbox"
                checked={
                  optionForm.isCorrect
                }
                onChange={
                  handleOptionChange
                }
              />

              Correct Answer
            </label>

            <button
              type="submit"
              className="primary-button"
              disabled={
                optionLoading
              }
            >
              {optionLoading
                ? 'Saving...'
                : editingOptionId
                ? 'Update Option'
                : 'Create Option'}
            </button>

            <button
              type="button"
              className="secondary-button"
              onClick={
                resetOptionForm
              }
              style={{
                marginLeft: '8px',
              }}
            >
              Cancel
            </button>

          </form>

        </div>
      )}

      {/* =========================
          QUICK QUESTION BUTTON
      ========================= */}

      {!showQuestionForm && (
        <div
          className="admin-section"
        >
          <button
            className="primary-button"
            onClick={() => {
              resetQuestionForm();
              setShowQuestionForm(true);
            }}
          >
            + Add Question
          </button>
        </div>
      )}

      {/* =========================
          SUBMISSION MANAGEMENT
      ========================= */}

      <div className="admin-section">

        <div className="admin-section-header">

          <div>
            <p className="eyebrow">
              SUBMISSION MANAGEMENT
            </p>

            <h2>
              User Submissions
            </h2>

            <p>
              Review and manage user attempts.
            </p>
          </div>

          <button
            className="secondary-button"
            onClick={
              refreshSubmissions
            }
            disabled={refreshing}
          >
            {refreshing
              ? 'Refreshing...'
              : '↻ Refresh'}
          </button>

        </div>

        <div className="result-stats">

          <div
            className="result-stat-card"
            onClick={() =>
              setSubmissionFilter(
                'ALL'
              )
            }
            style={{
              cursor: 'pointer',
            }}
          >
            <span>
              Total
            </span>

            <strong>
              {submissions.length}
            </strong>
          </div>

          <div
            className="result-stat-card"
            onClick={() =>
              setSubmissionFilter(
                'PENDING'
              )
            }
            style={{
              cursor: 'pointer',
            }}
          >
            <span>
              🟡 Pending
            </span>

            <strong>
              {pendingCount}
            </strong>
          </div>

          <div
            className="result-stat-card"
            onClick={() =>
              setSubmissionFilter(
                'APPROVED'
              )
            }
            style={{
              cursor: 'pointer',
            }}
          >
            <span>
              🟢 Approved
            </span>

            <strong>
              {approvedCount}
            </strong>
          </div>

          <div
            className="result-stat-card"
            onClick={() =>
              setSubmissionFilter(
                'REJECTED'
              )
            }
            style={{
              cursor: 'pointer',
            }}
          >
            <span>
              🔴 Rejected
            </span>

            <strong>
              {rejectedCount}
            </strong>
          </div>

        </div>

        <div className="admin-form-card">

          <div
            style={{
              display: 'flex',
              gap: '12px',
              flexWrap: 'wrap',
            }}
          >

            <input
              type="text"
              placeholder="Search submission ID, user ID, challenge ID..."
              value={
                submissionSearch
              }
              onChange={(event) =>
                setSubmissionSearch(
                  event.target.value
                )
              }
              style={{
                flex: 1,
                minWidth: '240px',
              }}
            />

            <select
              value={
                submissionFilter
              }
              onChange={(event) =>
                setSubmissionFilter(
                  event.target.value
                )
              }
            >
              <option value="ALL">
                All
              </option>

              <option value="PENDING">
                Pending
              </option>

              <option value="APPROVED">
                Approved
              </option>

              <option value="REJECTED">
                Rejected
              </option>
            </select>

            <button
              className="secondary-button"
              onClick={() => {
                setSubmissionSearch(
                  ''
                );

                setSubmissionFilter(
                  'ALL'
                );
              }}
            >
              Clear
            </button>

          </div>

        </div>

        {submissions.length ===
        0 ? (
          <div className="empty-card">
            <h2>
              No submissions
            </h2>

            <p>
              There are no submissions
              to review.
            </p>
          </div>
        ) : filteredSubmissions.length ===
          0 ? (
          <div className="empty-card">
            <h2>
              No matching submissions
            </h2>

            <p>
              Try changing your filters.
            </p>
          </div>
        ) : (
          <div className="challenge-grid">

            {filteredSubmissions.map(
              (submission) => (
                <SubmissionCard
                  key={
                    submission.id
                  }
                  submission={
                    submission
                  }
                  admin={true}
                  onStatusUpdate={
                    updateSubmissionStatus
                  }
                  updating={
                    updatingId ===
                    submission.id
                  }
                />
              )
            )}

          </div>
        )}

      </div>

    </div>
  );
}

export default AdminDashboard;