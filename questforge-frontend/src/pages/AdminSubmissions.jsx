
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminSubmissions.css';

const STATUS_OPTIONS = [
  { value: 'ALL', label: 'All Submissions' },
  { value: 'PENDING', label: 'Pending' },
  { value: 'APPROVED', label: 'Approved' },
  { value: 'REJECTED', label: 'Rejected' },
];

const getSubmissionId = (submission) =>
  submission?.submissionId ?? submission?.id;

const normalizeStatus = (status) =>
  String(status || 'PENDING').trim().toUpperCase();

function AdminSubmissions() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [detailLoadingId, setDetailLoadingId] = useState(null);

  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedSubmission, setSelectedSubmission] = useState(null);

  const detailRequestRef = useRef(0);

  // =========================================
  // LOAD SUBMISSIONS
  // =========================================

  const loadSubmissions = async () => {
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const { response, data } = await apiRequest('/admin/submissions', {
        method: 'GET',
      });

      if (!response.ok) {
        setError(data?.message || 'Failed to load admin submissions.');
        return;
      }

      const result = Array.isArray(data)
        ? data
        : Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data?.submissions)
            ? data.submissions
            : [];

      setSubmissions(result);
    } catch (err) {
      console.error('Admin submissions error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSubmissions();

    return () => {
      detailRequestRef.current += 1;
    };
  }, []);

  // =========================================
  // FILTERED SUBMISSIONS
  // =========================================

  const filteredSubmissions = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return submissions.filter((submission) => {
      const status = normalizeStatus(submission.status);

      if (statusFilter !== 'ALL' && status !== statusFilter) {
        return false;
      }

      if (!normalizedSearch) {
        return true;
      }

      const searchableText = [
        submission.submissionId,
        submission.id,
        submission.userId,
        submission.userName,
        submission.challengeId,
        submission.challengeTitle,
        submission.answer,
        submission.status,
      ]
        .filter((value) => value !== null && value !== undefined)
        .join(' ')
        .toLowerCase();

      return searchableText.includes(normalizedSearch);
    });
  }, [submissions, search, statusFilter]);

  // =========================================
  // SUMMARY STATISTICS
  // =========================================

  const stats = useMemo(() => {
    return submissions.reduce(
      (result, submission) => {
        const status = normalizeStatus(submission.status);

        result.total += 1;

        if (status === 'PENDING') result.pending += 1;
        if (status === 'APPROVED') result.approved += 1;
        if (status === 'REJECTED') result.rejected += 1;

        return result;
      },
      {
        total: 0,
        pending: 0,
        approved: 0,
        rejected: 0,
      }
    );
  }, [submissions]);

  const approvalRate = stats.total
    ? Math.round((stats.approved / stats.total) * 100)
    : 0;

  // =========================================
  // UPDATE SUBMISSION STATUS
  // =========================================

  const updateStatus = async (submission, newStatus) => {
    const submissionId = getSubmissionId(submission);

    if (submissionId === null || submissionId === undefined || submissionId === '') {
      setError('Submission ID is missing.');
      return;
    }

    if (actionLoading !== null) {
      return;
    }

    const actionKey = `${submissionId}-${newStatus}`;

    setActionLoading(actionKey);
    setError('');
    setMessage('');

    try {
      const { response, data } = await apiRequest(
        `/admin/submissions/${encodeURIComponent(submissionId)}/status`,
        {
          method: 'PUT',
          body: JSON.stringify({ status: newStatus }),
        }
      );

      if (!response.ok) {
        setError(
          data?.message ||
            `Failed to ${newStatus.toLowerCase()} submission.`
        );
        return;
      }

      const responseData =
        data && typeof data === 'object' && !Array.isArray(data)
          ? data
          : {};

      const updatedStatus = responseData.status || newStatus;

      const updatedSubmission = {
        ...submission,
        ...responseData,
        status: updatedStatus,
      };

      setSubmissions((currentSubmissions) =>
        currentSubmissions.map((item) => {
          const itemId = getSubmissionId(item);

          if (String(itemId) !== String(submissionId)) {
            return item;
          }

          return {
            ...item,
            ...responseData,
            status: updatedStatus,
          };
        })
      );

      setSelectedSubmission((current) => {
        if (!current) return current;

        const currentId = getSubmissionId(current);

        if (String(currentId) !== String(submissionId)) {
          return current;
        }

        return {
          ...current,
          ...responseData,
          status: updatedStatus,
        };
      });

      setMessage(
        `Submission #${submissionId} ${String(updatedStatus).toLowerCase()} successfully.`
      );
    } catch (err) {
      console.error('Update submission status error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================
  // SUBMISSION DETAILS
  // =========================================

  const selectSubmission = async (submission) => {
    const submissionId = getSubmissionId(submission);
    const requestId = ++detailRequestRef.current;

    setSelectedSubmission(submission);
    setError('');
    setMessage('');

    if (
      submissionId === null ||
      submissionId === undefined ||
      submissionId === ''
    ) {
      return;
    }

    setDetailLoadingId(String(submissionId));

    try {
      const { response, data } = await apiRequest(
        `/admin/submissions/${encodeURIComponent(submissionId)}`,
        { method: 'GET' }
      );

      // Ignore an older response if another submission was selected.
      if (requestId !== detailRequestRef.current) {
        return;
      }

      if (!response.ok) {
        setError(data?.message || 'Unable to load submission details.');
        return;
      }

      if (data && typeof data === 'object' && !Array.isArray(data)) {
        setSelectedSubmission(data);
      }
    } catch (err) {
      if (requestId === detailRequestRef.current) {
        console.error('Submission detail error:', err);
        setError('Unable to load submission details.');
      }
    } finally {
      if (requestId === detailRequestRef.current) {
        setDetailLoadingId(null);
      }
    }
  };

  const closeDetails = () => {
    detailRequestRef.current += 1;
    setSelectedSubmission(null);
    setDetailLoadingId(null);
  };

  // =========================================
  // HELPERS
  // =========================================

  const formatDate = (value) => {
    if (!value) return '—';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return String(value);
    }

    return date.toLocaleString();
  };

  const getStatusClass = (status) => {
    const normalized = normalizeStatus(status).toLowerCase();

    return `asm-status-badge asm-status-${normalized}`;
  };

  const clearFilters = () => {
    setSearch('');
    setStatusFilter('ALL');
  };

  const selectedId = selectedSubmission
    ? getSubmissionId(selectedSubmission)
    : null;

  const selectedStatus = selectedSubmission
    ? normalizeStatus(selectedSubmission.status)
    : '';

  const selectedActionLoading = actionLoading !== null;

  // =========================================
  // RENDER
  // =========================================

  return (
    <main className="page asm-page">
      <div className="asm-shell">
        {/* HEADER */}
        <header className="asm-header">
          <div className="asm-header-content">
            <div className="asm-breadcrumb">
              <span />
              QUESTFORGE / ADMIN / SUBMISSION MANAGEMENT
            </div>

            <div className="asm-title-row">
              <div>
                <h1>Submission Management</h1>
                <p>
                  Review coding submissions, evaluate results and manage
                  approval decisions.
                </p>
              </div>

              <div className="asm-header-icon" aria-hidden="true">
                ⚔️
              </div>
            </div>

            <div className="asm-header-status">
              <span />
              Moderation workspace
            </div>
          </div>

          <div className="asm-header-actions">
            <Link to="/admin" className="asm-secondary-button">
              <span>←</span>
              Admin Dashboard
            </Link>

            <button
              type="button"
              className="asm-primary-button asm-refresh-button"
              onClick={loadSubmissions}
              disabled={loading}
            >
              <span className={loading ? 'asm-refresh-spinning' : ''}>
                ↻
              </span>
              {loading ? 'Refreshing...' : 'Refresh Data'}
            </button>
          </div>
        </header>

        {/* FEEDBACK */}
        {message && (
          <div className="asm-alert asm-alert-success" role="status">
            <span>✓</span>
            <p>{message}</p>
            <button
              type="button"
              onClick={() => setMessage('')}
              aria-label="Dismiss success message"
            >
              ×
            </button>
          </div>
        )}

        {error && (
          <div className="asm-alert asm-alert-error" role="alert">
            <span>!</span>
            <p>{error}</p>
            <button
              type="button"
              onClick={() => setError('')}
              aria-label="Dismiss error message"
            >
              ×
            </button>
          </div>
        )}

        {/* SUMMARY */}
        <section className="asm-stats-grid" aria-label="Submission statistics">
          <article className="asm-stat-card asm-stat-total">
            <div className="asm-stat-top">
              <span className="asm-stat-label">Total Submissions</span>
              <span className="asm-stat-icon">📋</span>
            </div>
            <strong>{stats.total}</strong>
            <span className="asm-stat-description">
              All recorded submissions
            </span>
          </article>

          <article className="asm-stat-card asm-stat-pending">
            <div className="asm-stat-top">
              <span className="asm-stat-label">Pending Review</span>
              <span className="asm-stat-icon">⏳</span>
            </div>
            <strong>{stats.pending}</strong>
            <span className="asm-stat-description">
              Awaiting admin decision
            </span>
          </article>

          <article className="asm-stat-card asm-stat-approved">
            <div className="asm-stat-top">
              <span className="asm-stat-label">Approved</span>
              <span className="asm-stat-icon">✓</span>
            </div>
            <strong>{stats.approved}</strong>
            <span className="asm-stat-description">
              Successfully approved
            </span>
          </article>

          <article className="asm-stat-card asm-stat-rejected">
            <div className="asm-stat-top">
              <span className="asm-stat-label">Rejected</span>
              <span className="asm-stat-icon">✕</span>
            </div>
            <strong>{stats.rejected}</strong>
            <span className="asm-stat-description">
              Rejected submissions
            </span>
          </article>
        </section>

        {/* OVERVIEW */}
        <section className="asm-overview-card">
          <div className="asm-overview-heading">
            <div>
              <span className="asm-section-kicker">REVIEW OVERVIEW</span>
              <h2>Approval Progress</h2>
            </div>

            <strong>{approvalRate}%</strong>
          </div>

          <div
            className="asm-progress-track"
            role="progressbar"
            aria-label="Submission approval rate"
            aria-valuemin={0}
            aria-valuemax={100}
            aria-valuenow={approvalRate}
          >
            <div
              className="asm-progress-fill"
              style={{ width: `${approvalRate}%` }}
            />
          </div>

          <div className="asm-progress-footer">
            <span>
              {stats.approved} approved out of {stats.total} submissions
            </span>
            <span>{stats.pending} awaiting review</span>
          </div>
        </section>

        {/* FILTERS */}
        <section className="asm-filter-card">
          <div className="asm-filter-heading">
            <div>
              <span className="asm-section-kicker">FIND SUBMISSIONS</span>
              <h2>Search & Filter</h2>
            </div>

            {(search || statusFilter !== 'ALL') && (
              <button
                type="button"
                className="asm-text-button"
                onClick={clearFilters}
              >
                Reset filters ↺
              </button>
            )}
          </div>

          <div className="asm-filter-controls">
            <div className="asm-search-wrapper">
              <span aria-hidden="true">⌕</span>
              <input
                type="search"
                placeholder="Search user, challenge, answer or ID..."
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                aria-label="Search submissions"
              />

              {search && (
                <button
                  type="button"
                  onClick={() => setSearch('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}
            </div>

            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
              className="asm-status-select"
              aria-label="Filter by submission status"
            >
              {STATUS_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          <div className="asm-filter-result">
            Showing <strong>{filteredSubmissions.length}</strong> of{' '}
            <strong>{submissions.length}</strong> submissions
          </div>
        </section>

        {/* SUBMISSIONS TABLE */}
        <section className="asm-table-card">
          <div className="asm-table-heading">
            <div>
              <span className="asm-section-kicker">MODERATION QUEUE</span>
              <h2>All Submissions</h2>
              <p>Open a submission to inspect its complete details.</p>
            </div>

            <span className="asm-table-count">
              {filteredSubmissions.length} Results
            </span>
          </div>

          {loading ? (
            <div className="asm-empty-state">
              <span className="asm-loader" />
              <h3>Loading submissions</h3>
              <p>Fetching the latest submission records...</p>
            </div>
          ) : filteredSubmissions.length === 0 ? (
            <div className="asm-empty-state">
              <span className="asm-empty-icon">📭</span>
              <h3>No submissions found</h3>
              <p>
                No records match your current search or status filter.
              </p>
              <button
                type="button"
                className="asm-secondary-button"
                onClick={clearFilters}
              >
                Reset Filters
              </button>
            </div>
          ) : (
            <div className="asm-table-wrapper">
              <table className="asm-table">
                <thead>
                  <tr>
                    <th>Submission</th>
                    <th>User</th>
                    <th>Challenge</th>
                    <th>Status</th>
                    <th>Score</th>
                    <th>Submitted</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredSubmissions.map((submission, index) => {
                    const submissionId = getSubmissionId(submission);
                    const status = normalizeStatus(submission.status);
                    const isSelected =
                      String(selectedId) === String(submissionId);

                    return (
                      <tr
                        key={submissionId ?? `submission-${index}`}
                        className={isSelected ? 'asm-row-selected' : ''}
                      >
                        <td>
                          <div className="asm-id-cell">
                            <strong>#{submissionId ?? '—'}</strong>

                            {submission.firstSubmission && (
                              <span className="asm-first-badge">
                                ⭐ First
                              </span>
                            )}
                          </div>
                        </td>

                        <td>
                          <div className="asm-user-cell">
                            <span className="asm-user-avatar">
                              {(submission.userName || 'U')
                                .trim()
                                .charAt(0)
                                .toUpperCase()}
                            </span>

                            <div>
                              <strong>
                                {submission.userName || 'Unknown User'}
                              </strong>
                              <small>
                                User #{submission.userId ?? '—'}
                              </small>
                            </div>
                          </div>
                        </td>

                        <td>
                          <div className="asm-challenge-cell">
                            <strong>
                              {submission.challengeTitle ||
                                `Challenge #${submission.challengeId ?? '—'}`}
                            </strong>
                            <small>
                              ID: {submission.challengeId ?? '—'}
                            </small>
                          </div>
                        </td>

                        <td>
                          <span className={getStatusClass(status)}>
                            <span />
                            {status}
                          </span>
                        </td>

                        <td>
                          <div className="asm-score-cell">
                            <strong>{submission.score ?? 0}</strong>

                            {submission.totalQuestions !== null &&
                              submission.totalQuestions !== undefined && (
                                <small>
                                  / {submission.totalQuestions}
                                </small>
                              )}
                          </div>
                        </td>

                        <td>
                          <span className="asm-date-cell">
                            {formatDate(submission.submittedAt)}
                          </span>
                        </td>

                        <td>
                          <button
                            type="button"
                            className={`asm-details-button ${
                              isSelected ? 'asm-details-active' : ''
                            }`}
                            onClick={() => selectSubmission(submission)}
                            disabled={
                              detailLoadingId === String(submissionId)
                            }
                          >
                            {detailLoadingId === String(submissionId) ? (
                              <>
                                <span className="asm-mini-spinner" />
                                Loading
                              </>
                            ) : (
                              <>
                                View Details
                                <span>→</span>
                              </>
                            )}
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* DETAILS PANEL */}
        {selectedSubmission && (
          <section
            className="asm-details-panel"
            aria-label="Selected submission details"
          >
            <div className="asm-details-header">
              <div>
                <span className="asm-section-kicker">
                  SUBMISSION INSPECTOR
                </span>

                <h2>
                  Submission #{getSubmissionId(selectedSubmission) ?? '—'}
                </h2>

                <p>
                  Review the submitted answer and moderation information.
                </p>
              </div>

              <button
                type="button"
                className="asm-close-button"
                onClick={closeDetails}
                aria-label="Close submission details"
              >
                ×
              </button>
            </div>

            {detailLoadingId === String(selectedId) && (
              <div className="asm-detail-loading">
                <span className="asm-mini-spinner" />
                Loading complete submission details...
              </div>
            )}

            <div className="asm-detail-summary">
              <div>
                <span>User</span>
                <strong>
                  {selectedSubmission.userName || 'Unknown User'}
                </strong>
              </div>

              <div>
                <span>Challenge</span>
                <strong>
                  {selectedSubmission.challengeTitle ||
                    `#${selectedSubmission.challengeId ?? '—'}`}
                </strong>
              </div>

              <div>
                <span>Score</span>
                <strong>{selectedSubmission.score ?? 0}</strong>
              </div>

              <div>
                <span>Status</span>
                <strong>
                  <span className={getStatusClass(selectedStatus)}>
                    <span />
                    {selectedStatus}
                  </span>
                </strong>
              </div>
            </div>

            {/* ANSWER */}
            <div className="asm-detail-section">
              <div className="asm-detail-section-heading">
                <div>
                  <span className="asm-section-kicker">
                    SUBMITTED CONTENT
                  </span>
                  <h3>Submitted Answer</h3>
                </div>

                <span className="asm-answer-language">
                  TEXT RESPONSE
                </span>
              </div>

              <pre className="asm-answer-block">
                {selectedSubmission.answer || 'No answer available.'}
              </pre>
            </div>

            {/* INFORMATION */}
            <div className="asm-detail-section">
              <div className="asm-detail-section-heading">
                <div>
                  <span className="asm-section-kicker">
                    RECORD INFORMATION
                  </span>
                  <h3>Submission Information</h3>
                </div>
              </div>

              <div className="asm-information-grid">
                <div>
                  <span>Submission ID</span>
                  <strong>
                    {getSubmissionId(selectedSubmission) ?? '—'}
                  </strong>
                </div>

                <div>
                  <span>User ID</span>
                  <strong>{selectedSubmission.userId ?? '—'}</strong>
                </div>

                <div>
                  <span>Challenge ID</span>
                  <strong>{selectedSubmission.challengeId ?? '—'}</strong>
                </div>

                <div>
                  <span>Submitted At</span>
                  <strong>{formatDate(selectedSubmission.submittedAt)}</strong>
                </div>

                <div>
                  <span>Correct Answers</span>
                  <strong>
                    {selectedSubmission.correctAnswers ?? 0} /{' '}
                    {selectedSubmission.totalQuestions ?? 0}
                  </strong>
                </div>

                <div>
                  <span>First Submission</span>
                  <strong>
                    {selectedSubmission.firstSubmission ? 'Yes' : 'No'}
                  </strong>
                </div>
              </div>
            </div>

            {/* MODERATION ACTIONS */}
            <div className="asm-moderation-footer">
              <div>
                <span className="asm-section-kicker">
                  MODERATION DECISION
                </span>
                <p>
                  Choose the appropriate status for this submission.
                </p>
              </div>

              <div className="asm-action-buttons">
                <button
                  type="button"
                  className="asm-approve-button"
                  disabled={
                    selectedActionLoading || selectedStatus === 'APPROVED'
                  }
                  onClick={() =>
                    updateStatus(selectedSubmission, 'APPROVED')
                  }
                >
                  {actionLoading === `${selectedId}-APPROVED` ? (
                    <>
                      <span className="asm-mini-spinner" />
                      Approving...
                    </>
                  ) : (
                    <>✓ Approve</>
                  )}
                </button>

                <button
                  type="button"
                  className="asm-reject-button"
                  disabled={
                    selectedActionLoading || selectedStatus === 'REJECTED'
                  }
                  onClick={() =>
                    updateStatus(selectedSubmission, 'REJECTED')
                  }
                >
                  {actionLoading === `${selectedId}-REJECTED` ? (
                    <>
                      <span className="asm-mini-spinner" />
                      Rejecting...
                    </>
                  ) : (
                    <>✕ Reject</>
                  )}
                </button>
              </div>
            </div>
          </section>
        )}

        {/* FOOTER */}
        <footer className="asm-footer">
          <span>
            <span />
            QuestForge Administration
          </span>
          <span>Submission Moderation Workspace</span>
        </footer>
      </div>
    </main>
  );
}

export default AdminSubmissions;
