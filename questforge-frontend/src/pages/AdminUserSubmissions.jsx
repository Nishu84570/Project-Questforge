import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminUserSubmissions.css';

function AdminUserSubmissions() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const requestVersion = useRef(0);

  const [submissions, setSubmissions] = useState([]);
  const [user, setUser] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('LATEST');

  const loadUserSubmissions = async (isRefresh = false) => {
    const currentRequest = ++requestVersion.current;

    if (!userId) {
      setError('User ID is missing.');
      setLoading(false);
      return;
    }

    if (isRefresh) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError('');

    try {
      const [submissionsResult, userResult] = await Promise.all([
        apiRequest(`/admin/users/${userId}/submissions`, {
          method: 'GET',
        }),
        apiRequest(`/admin/users/${userId}`, {
          method: 'GET',
        }),
      ]);

      // Ignore stale responses when the user changes or another refresh starts.
      if (currentRequest !== requestVersion.current) return;

      if (!submissionsResult.response.ok) {
        throw new Error(
          submissionsResult.data?.message ||
            'Failed to load user submissions.'
        );
      }

      const responseData = submissionsResult.data;

      const submissionData = Array.isArray(responseData)
        ? responseData
        : Array.isArray(responseData?.content)
        ? responseData.content
        : Array.isArray(responseData?.submissions)
        ? responseData.submissions
        : [];

      setSubmissions(submissionData);

      if (userResult.response.ok && userResult.data) {
        setUser(userResult.data);
      } else {
        setUser(null);
      }
    } catch (err) {
      if (currentRequest !== requestVersion.current) return;

      console.error('Admin user submissions error:', err);
      setError(err.message || 'Unable to load user submissions.');
    } finally {
      if (currentRequest === requestVersion.current) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  };

  useEffect(() => {
    setSubmissions([]);
    setUser(null);
    setSearch('');
    setStatusFilter('ALL');
    setSortOrder('LATEST');

    loadUserSubmissions();

    return () => {
      requestVersion.current += 1;
    };
  }, [userId]);

  const getSubmissionId = (submission) =>
    submission.submissionId ?? submission.id ?? submission._id;

  const getChallengeTitle = (submission) =>
    submission.challengeTitle ||
    submission.challenge?.title ||
    `Challenge #${
      submission.challengeId ?? submission.challenge?.id ?? 'Unknown'
    }`;

  const getChallengeId = (submission) =>
    submission.challengeId ?? submission.challenge?.id;

  const getStatus = (submission) =>
    String(submission.status || 'UNKNOWN').trim().toUpperCase();

  const getScore = (submission) => {
    const score = Number(submission.score ?? 0);
    return Number.isFinite(score) ? score : 0;
  };

  const getCorrectAnswers = (submission) =>
    submission.correctAnswers ?? submission.correct ?? 0;

  const getTotalQuestions = (submission) =>
    submission.totalQuestions ?? submission.total ?? 0;

  const getTimestamp = (submission) => {
    const value = submission.submittedAt || submission.createdAt;
    if (!value) return 0;

    const timestamp = new Date(value).getTime();
    return Number.isNaN(timestamp) ? 0 : timestamp;
  };

  const filteredSubmissions = useMemo(() => {
    let result = [...submissions];
    const query = search.trim().toLowerCase();

    if (query) {
      result = result.filter((submission) => {
        const searchableValues = [
          getSubmissionId(submission),
          getChallengeTitle(submission),
          submission.answer,
          submission.status,
          getChallengeId(submission),
          submission.score,
        ];

        return searchableValues
          .filter((value) => value !== null && value !== undefined)
          .some((value) =>
            String(value).toLowerCase().includes(query)
          );
      });
    }

    if (statusFilter !== 'ALL') {
      result = result.filter(
        (submission) => getStatus(submission) === statusFilter
      );
    }

    result.sort((a, b) => {
      if (sortOrder === 'OLDEST') {
        return getTimestamp(a) - getTimestamp(b);
      }

      if (sortOrder === 'SCORE_HIGH') {
        return getScore(b) - getScore(a);
      }

      if (sortOrder === 'SCORE_LOW') {
        return getScore(a) - getScore(b);
      }

      return getTimestamp(b) - getTimestamp(a);
    });

    return result;
  }, [submissions, search, statusFilter, sortOrder]);

  const stats = useMemo(() => {
    const pending = submissions.filter(
      (submission) => getStatus(submission) === 'PENDING'
    ).length;

    const approved = submissions.filter(
      (submission) => getStatus(submission) === 'APPROVED'
    ).length;

    const rejected = submissions.filter(
      (submission) => getStatus(submission) === 'REJECTED'
    ).length;

    const totalScore = submissions.reduce(
      (total, submission) => total + getScore(submission),
      0
    );

    return {
      total: submissions.length,
      pending,
      approved,
      rejected,
      averageScore: submissions.length
        ? Math.round(totalScore / submissions.length)
        : 0,
    };
  }, [submissions]);

  const approvalRate = stats.total
    ? Math.round((stats.approved / stats.total) * 100)
    : 0;

  const formatDate = (value) => {
    if (!value) return 'N/A';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return String(value);
    }

    return date.toLocaleString();
  };

  const getStatusClass = (status) => {
    const normalized = String(status || 'UNKNOWN')
      .trim()
      .toLowerCase()
      .replace(/\s+/g, '-');

    return `aus-status aus-status-${normalized}`;
  };

  const clearFilters = () => {
    setSearch('');
    setStatusFilter('ALL');
    setSortOrder('LATEST');
  };

  const hasActiveFilters =
    search.trim() !== '' ||
    statusFilter !== 'ALL' ||
    sortOrder !== 'LATEST';

  const handleViewSubmission = (submission) => {
    const submissionId = getSubmissionId(submission);

    if (!submissionId) return;

    navigate(`/submission-result/${submissionId}`);
  };

  return (
    <div className="page admin-user-submissions-page aus-page">
      {/* HEADER */}
      <header className="aus-header">
        <div className="aus-header-content">
          <div className="aus-heading-copy">
            <span className="aus-eyebrow">
              <span className="aus-eyebrow-dot" />
              ADMIN WORKSPACE / USER ACTIVITY
            </span>

            <h1>User Submissions</h1>

            <p>
              Explore submission history, review performance, and track
              challenge activity.
            </p>
          </div>

          <div className="aus-header-actions">
            <Link to="/admin/users" className="aus-button aus-button-secondary">
              <span aria-hidden="true">←</span>
              Back to Users
            </Link>

            <button
              type="button"
              className="aus-button aus-button-primary"
              onClick={() => loadUserSubmissions(true)}
              disabled={loading || refreshing}
            >
              <span aria-hidden="true" className={refreshing ? 'aus-spin' : ''}>
                ↻
              </span>
              {refreshing ? 'Refreshing...' : 'Refresh Data'}
            </button>
          </div>
        </div>
      </header>

      {/* ERROR */}
      {error && (
        <div className="aus-alert aus-alert-error" role="alert">
          <span className="aus-alert-icon" aria-hidden="true">!</span>
          <div>
            <strong>Unable to load activity</strong>
            <p>{error}</p>
          </div>
          <button
            type="button"
            className="aus-alert-retry"
            onClick={() => loadUserSubmissions(true)}
            disabled={refreshing}
          >
            Retry
          </button>
        </div>
      )}

      {/* USER PROFILE */}
      <section className="aus-user-card">
        <div className="aus-user-identity">
          <div className="aus-avatar" aria-hidden="true">
            {String(user?.name || user?.username || 'U')
              .trim()
              .charAt(0)
              .toUpperCase()}
          </div>

          <div className="aus-user-copy">
            <span className="aus-section-label">SELECTED USER</span>
            <h2>{user?.name || user?.username || 'Unknown User'}</h2>
            <p>{user?.email || 'Email unavailable'}</p>
          </div>
        </div>

        <div className="aus-user-meta">
          <div>
            <span>User ID</span>
            <strong>#{user?.userId ?? user?.id ?? userId}</strong>
          </div>

          <div>
            <span>Account Role</span>
            <strong>{user?.role || '—'}</strong>
          </div>

          <div>
            <span>Member Since</span>
            <strong>{user?.createdAt ? formatDate(user.createdAt) : '—'}</strong>
          </div>
        </div>
      </section>

      {/* STATISTICS */}
      <section className="aus-stats-grid" aria-label="Submission statistics">
        <article className="aus-stat-card aus-stat-total">
          <div className="aus-stat-top">
            <span className="aus-stat-label">Total Submissions</span>
            <span className="aus-stat-icon" aria-hidden="true">▤</span>
          </div>
          <strong className="aus-stat-value">{stats.total}</strong>
          <span className="aus-stat-caption">All recorded attempts</span>
        </article>

        <article className="aus-stat-card aus-stat-pending">
          <div className="aus-stat-top">
            <span className="aus-stat-label">Pending Review</span>
            <span className="aus-stat-icon" aria-hidden="true">◷</span>
          </div>
          <strong className="aus-stat-value">{stats.pending}</strong>
          <span className="aus-stat-caption">Awaiting a decision</span>
        </article>

        <article className="aus-stat-card aus-stat-approved">
          <div className="aus-stat-top">
            <span className="aus-stat-label">Approved</span>
            <span className="aus-stat-icon" aria-hidden="true">✓</span>
          </div>
          <strong className="aus-stat-value">{stats.approved}</strong>
          <span className="aus-stat-caption">Accepted submissions</span>
        </article>

        <article className="aus-stat-card aus-stat-rejected">
          <div className="aus-stat-top">
            <span className="aus-stat-label">Rejected</span>
            <span className="aus-stat-icon" aria-hidden="true">×</span>
          </div>
          <strong className="aus-stat-value">{stats.rejected}</strong>
          <span className="aus-stat-caption">Not accepted</span>
        </article>
      </section>

      {/* PERFORMANCE OVERVIEW */}
      <section className="aus-performance-card">
        <div className="aus-performance-heading">
          <div>
            <span className="aus-section-label">PERFORMANCE OVERVIEW</span>
            <h2>Submission Insights</h2>
          </div>
          <span className="aus-performance-period">ALL TIME</span>
        </div>

        <div className="aus-performance-content">
          <div className="aus-performance-metric">
            <span>Average Score</span>
            <strong>{stats.averageScore}</strong>
            <small>Across all submissions</small>
          </div>

          <div className="aus-performance-divider" />

          <div className="aus-approval-metric">
            <div className="aus-approval-copy">
              <span>Approval Rate</span>
              <strong>{approvalRate}%</strong>
            </div>

            <div
              className="aus-progress-track"
              role="progressbar"
              aria-label="Approval rate"
              aria-valuemin={0}
              aria-valuemax={100}
              aria-valuenow={approvalRate}
            >
              <div
                className="aus-progress-fill"
                style={{ width: `${approvalRate}%` }}
              />
            </div>

            <small>
              {stats.approved} approved out of {stats.total} submissions
            </small>
          </div>
        </div>
      </section>

      {/* FILTERS */}
      <section className="aus-filter-card">
        <div className="aus-filter-heading">
          <div>
            <span className="aus-section-label">ACTIVITY EXPLORER</span>
            <h2>Find Submissions</h2>
          </div>

          {hasActiveFilters && (
            <button
              type="button"
              className="aus-clear-button"
              onClick={clearFilters}
            >
              Reset filters
            </button>
          )}
        </div>

        <div className="aus-filter-controls">
          <div className="aus-search-field">
            <label htmlFor="aus-search">Search submissions</label>
            <div className="aus-input-wrap">
              <span className="aus-input-icon" aria-hidden="true">⌕</span>
              <input
                id="aus-search"
                type="search"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Challenge, answer, status, ID..."
              />
              {search && (
                <button
                  type="button"
                  className="aus-input-clear"
                  onClick={() => setSearch('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}
            </div>
          </div>

          <div className="aus-select-field">
            <label htmlFor="aus-status">Status</label>
            <select
              id="aus-status"
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
            >
              <option value="ALL">All statuses</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div className="aus-select-field">
            <label htmlFor="aus-sort">Sort by</label>
            <select
              id="aus-sort"
              value={sortOrder}
              onChange={(event) => setSortOrder(event.target.value)}
            >
              <option value="LATEST">Latest first</option>
              <option value="OLDEST">Oldest first</option>
              <option value="SCORE_HIGH">Highest score</option>
              <option value="SCORE_LOW">Lowest score</option>
            </select>
          </div>
        </div>
      </section>

      {/* SUBMISSION TABLE */}
      <section className="aus-table-section">
        <div className="aus-table-heading">
          <div>
            <span className="aus-section-label">USER ACTIVITY</span>
            <h2>Submission History</h2>
            <p>
              Showing <strong>{filteredSubmissions.length}</strong> of{' '}
              <strong>{submissions.length}</strong> submissions
            </p>
          </div>

          <span className="aus-result-count">
            {filteredSubmissions.length} RESULTS
          </span>
        </div>

        {loading ? (
          <div className="aus-state-card">
            <div className="aus-spinner" />
            <h3>Loading submissions</h3>
            <p>Retrieving this user's activity from QuestForge.</p>
          </div>
        ) : filteredSubmissions.length === 0 ? (
          <div className="aus-state-card">
            <div className="aus-empty-icon" aria-hidden="true">⌕</div>
            <h3>
              {submissions.length === 0
                ? 'No submissions yet'
                : 'No matching submissions'}
            </h3>
            <p>
              {submissions.length === 0
                ? 'This user has not submitted any challenges yet.'
                : 'Try adjusting your search or changing the selected filters.'}
            </p>

            {hasActiveFilters && (
              <button
                type="button"
                className="aus-button aus-button-secondary"
                onClick={clearFilters}
              >
                Clear Filters
              </button>
            )}
          </div>
        ) : (
          <div className="aus-table-wrapper">
            <table className="aus-table">
              <thead>
                <tr>
                  <th>Submission</th>
                  <th>Challenge</th>
                  <th>Score</th>
                  <th>Correct Answers</th>
                  <th>Status</th>
                  <th>Submitted At</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {filteredSubmissions.map((submission, index) => {
                  const submissionId = getSubmissionId(submission);
                  const challengeId = getChallengeId(submission);
                  const correct = getCorrectAnswers(submission);
                  const total = getTotalQuestions(submission);
                  const status = getStatus(submission);

                  return (
                    <tr
                      key={submissionId ?? `${userId}-${index}`}
                    >
                      <td>
                        <div className="aus-submission-id">
                          <strong>#{submissionId ?? index + 1}</strong>
                          {submission.firstSubmission && (
                            <span className="aus-first-badge">
                              ★ First
                            </span>
                          )}
                        </div>
                      </td>

                      <td>
                        <div className="aus-challenge-cell">
                          <strong>{getChallengeTitle(submission)}</strong>
                          {challengeId !== undefined &&
                            challengeId !== null && (
                              <small>Challenge #{challengeId}</small>
                            )}
                        </div>
                      </td>

                      <td>
                        <span className="aus-score-value">
                          {getScore(submission)}
                        </span>
                      </td>

                      <td>
                        <span className="aus-answer-count">
                          {correct}
                          {Number(total) > 0 ? ` / ${total}` : ''}
                        </span>
                      </td>

                      <td>
                        <span className={getStatusClass(status)}>
                          <span className="aus-status-dot" />
                          {status}
                        </span>
                      </td>

                      <td>
                        <span className="aus-date-value">
                          {formatDate(
                            submission.submittedAt || submission.createdAt
                          )}
                        </span>
                      </td>

                      <td>
                        {submissionId ? (
                          <button
                            type="button"
                            className="aus-view-button"
                            onClick={() => handleViewSubmission(submission)}
                          >
                            View Details
                            <span aria-hidden="true">↗</span>
                          </button>
                        ) : (
                          <span className="aus-unavailable">N/A</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <footer className="aus-footer">
        <span>QUESTFORGE ADMIN</span>
        <span>User activity management</span>
      </footer>
    </div>
  );
}

export default AdminUserSubmissions;