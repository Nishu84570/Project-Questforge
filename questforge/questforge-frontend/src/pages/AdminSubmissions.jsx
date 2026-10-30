import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminSubmissions() {
  const [submissions, setSubmissions] = useState([]);

  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] =
    useState(null);

  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] =
    useState('ALL');

  const [selectedSubmission, setSelectedSubmission] =
    useState(null);

  // ==========================================
  // LOAD ADMIN SUBMISSIONS
  // ==========================================

  const loadSubmissions = async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest('/admin/submissions', {
          method: 'GET',
        });

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load admin submissions.'
        );

        return;
      }

      setSubmissions(
        Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data?.submissions)
          ? data.submissions
          : []
      );
    } catch (error) {
      console.error(
        'Admin submissions error:',
        error
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSubmissions();
  }, []);

  // ==========================================
  // FILTER SUBMISSIONS
  // ==========================================

  const filteredSubmissions = useMemo(() => {
    const normalizedSearch =
      search.trim().toLowerCase();

    return submissions.filter(
      (submission) => {
        const status =
          String(
            submission.status || ''
          ).toUpperCase();

        if (
          statusFilter !== 'ALL' &&
          status !== statusFilter
        ) {
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
          .filter(
            (value) =>
              value !== null &&
              value !== undefined
          )
          .join(' ')
          .toLowerCase();

        return searchableText.includes(
          normalizedSearch
        );
      }
    );
  }, [
    submissions,
    search,
    statusFilter,
  ]);

  // ==========================================
  // STATUS COUNTS
  // ==========================================

  const pendingCount = submissions.filter(
    (submission) =>
      String(
        submission.status || ''
      ).toUpperCase() === 'PENDING'
  ).length;

  const approvedCount = submissions.filter(
    (submission) =>
      String(
        submission.status || ''
      ).toUpperCase() === 'APPROVED'
  ).length;

  const rejectedCount = submissions.filter(
    (submission) =>
      String(
        submission.status || ''
      ).toUpperCase() === 'REJECTED'
  ).length;

  // ==========================================
  // UPDATE STATUS
  // ==========================================

  const updateStatus = async (
    submission,
    newStatus
  ) => {
    const submissionId =
      submission.submissionId ??
      submission.id;

    if (!submissionId) {
      setError(
        'Submission ID is missing.'
      );

      return;
    }

    setActionLoading(
      `${submissionId}-${newStatus}`
    );

    setError('');
    setMessage('');

    try {
      const { response, data } =
        await apiRequest(
          `/admin/submissions/${submissionId}/status`,
          {
            method: 'PUT',
            body: JSON.stringify({
              status: newStatus,
            }),
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            `Failed to ${newStatus.toLowerCase()} submission.`
        );

        return;
      }

      const updatedStatus =
        data?.status ||
        newStatus;

      setSubmissions(
        (currentSubmissions) =>
          currentSubmissions.map(
            (item) => {
              const itemId =
                item.submissionId ??
                item.id;

              if (
                String(itemId) !==
                String(submissionId)
              ) {
                return item;
              }

              return {
                ...item,
                ...(
                  data &&
                  typeof data === 'object'
                    ? data
                    : {}
                ),
                status:
                  updatedStatus,
              };
            }
          )
      );

      setSelectedSubmission(
        (current) => {
          if (!current) {
            return current;
          }

          const currentId =
            current.submissionId ??
            current.id;

          if (
            String(currentId) !==
            String(submissionId)
          ) {
            return current;
          }

          return {
            ...current,
            ...(
              data &&
              typeof data === 'object'
                ? data
                : {}
            ),
            status:
              updatedStatus,
          };
        }
      );

      setMessage(
        `Submission #${submissionId} ${updatedStatus.toLowerCase()} successfully.`
      );
    } catch (error) {
      console.error(
        'Update submission status error:',
        error
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(null);
    }
  };

  // ==========================================
  // FORMAT DATE
  // ==========================================

  const formatDate = (value) => {
    if (!value) {
      return '—';
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString();
  };

  // ==========================================
  // STATUS CLASS
  // ==========================================

  const getStatusClass = (status) => {
    const normalized =
      String(
        status || 'PENDING'
      )
        .toLowerCase()
        .replace(/\s+/g, '-');

    return `status-badge status-${normalized}`;
  };

  // ==========================================
  // CLEAR FILTERS
  // ==========================================

  const clearFilters = () => {
    setSearch('');
    setStatusFilter('ALL');
  };

  // ==========================================
  // SELECT SUBMISSION
  // ==========================================

  const selectSubmission = async (
    submission
  ) => {
    const submissionId =
      submission.submissionId ??
      submission.id;

    setSelectedSubmission(
      submission
    );

    if (!submissionId) {
      return;
    }

    try {
      const { response, data } =
        await apiRequest(
          `/admin/submissions/${submissionId}`,
          {
            method: 'GET',
          }
        );

      if (
        response.ok &&
        data
      ) {
        setSelectedSubmission(
          data
        );
      }
    } catch (error) {
      console.error(
        'Submission detail error:',
        error
      );
    }
  };

  // ==========================================
  // CLOSE DETAILS
  // ==========================================

  const closeDetails = () => {
    setSelectedSubmission(null);
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="page admin-page">

      {/* ======================================
          HEADER
      ====================================== */}

      <div className="page-header">

        <div>

          <span className="eyebrow">
            ADMIN
          </span>

          <h1>
            Submission Management
          </h1>

          <p>
            Review, approve and reject
            QuestForge submissions.
          </p>

        </div>

        <div
          style={{
            display: 'flex',
            gap: '10px',
            flexWrap: 'wrap',
          }}
        >

          <Link
            to="/admin"
            className="secondary-button"
          >
            ← Admin Dashboard
          </Link>

          <button
            className="secondary-button"
            onClick={
              loadSubmissions
            }
            disabled={loading}
          >
            {loading
              ? 'Loading...'
              : '↻ Refresh'}
          </button>

        </div>

      </div>

      {/* ======================================
          MESSAGES
      ====================================== */}

      {message && (
        <div className="success-message">
          {message}
        </div>
      )}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* ======================================
          SUMMARY
      ====================================== */}

      <div className="result-stats">

        <div className="result-stat-card">

          <span className="result-stat-label">
            Total
          </span>

          <strong>
            {submissions.length}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Pending
          </span>

          <strong>
            {pendingCount}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Approved
          </span>

          <strong>
            {approvedCount}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Rejected
          </span>

          <strong>
            {rejectedCount}
          </strong>

        </div>

      </div>

      {/* ======================================
          FILTERS
      ====================================== */}

      <div className="admin-form-card">

        <div
          style={{
            display: 'flex',
            gap: '12px',
            flexWrap: 'wrap',
            alignItems: 'center',
          }}
        >

          <input
            type="text"
            placeholder="Search user, challenge, ID..."
            value={search}
            onChange={(event) =>
              setSearch(
                event.target.value
              )
            }
            style={{
              flex: 1,
              minWidth: '240px',
            }}
          />

          <select
            value={statusFilter}
            onChange={(event) =>
              setStatusFilter(
                event.target.value
              )
            }
          >

            <option value="ALL">
              All Statuses
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
            onClick={
              clearFilters
            }
          >
            Clear
          </button>

        </div>

      </div>

      {/* ======================================
          TABLE
      ====================================== */}

      <div className="admin-table-card">

        <div className="section-heading">

          <div>

            <span className="eyebrow">
              SUBMISSIONS
            </span>

            <h2>
              {filteredSubmissions.length}{' '}
              Submission
              {filteredSubmissions.length !==
              1
                ? 's'
                : ''}
            </h2>

          </div>

        </div>

        {loading ? (

          <div className="empty-card">

            <h2>
              Loading submissions...
            </h2>

          </div>

        ) : filteredSubmissions.length ===
          0 ? (

          <div className="empty-card">

            <h2>
              No submissions found
            </h2>

            <p>
              Try changing your search
              or status filter.
            </p>

          </div>

        ) : (

          <div
            style={{
              overflowX: 'auto',
            }}
          >

            <table className="admin-table">

              <thead>

                <tr>

                  <th>
                    Submission
                  </th>

                  <th>
                    User
                  </th>

                  <th>
                    Challenge
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Score
                  </th>

                  <th>
                    Submitted
                  </th>

                  <th>
                    Action
                  </th>

                </tr>

              </thead>

              <tbody>

                {filteredSubmissions.map(
                  (submission) => {

                    const submissionId =
                      submission.submissionId ??
                      submission.id;

                    const status =
                      String(
                        submission.status ||
                          'PENDING'
                      ).toUpperCase();

                    return (
                      <tr
                        key={
                          submissionId
                        }
                      >

                        <td>

                          <strong>
                            #
                            {
                              submissionId ??
                              '—'
                            }
                          </strong>

                          {submission.firstSubmission && (
                            <div>
                              <small>
                                ⭐ First
                                Submission
                              </small>
                            </div>
                          )}

                        </td>

                        <td>

                          <strong>
                            {submission.userName ||
                              'Unknown User'}
                          </strong>

                          <br />

                          <small>
                            User #
                            {
                              submission.userId ??
                              '—'
                            }
                          </small>

                        </td>

                        <td>

                          <strong>
                            {submission.challengeTitle ||
                              `Challenge #${
                                submission.challengeId ??
                                '—'
                              }`}
                          </strong>

                          <br />

                          <small>
                            Challenge #
                            {
                              submission.challengeId ??
                              '—'
                            }
                          </small>

                        </td>

                        <td>

                          <span
                            className={
                              getStatusClass(
                                status
                              )
                            }
                          >
                            {status}
                          </span>

                        </td>

                        <td>

                          {submission.score ??
                            0}

                          {submission.totalQuestions !==
                            undefined &&
                            submission.totalQuestions !==
                              null && (
                              <small>
                                {' '}
                                /{' '}
                                {
                                  submission.totalQuestions
                                }
                              </small>
                            )}

                        </td>

                        <td>

                          {formatDate(
                            submission.submittedAt
                          )}

                        </td>

                        <td>

                          <button
                            className="secondary-button"
                            onClick={() =>
                              selectSubmission(
                                submission
                              )
                            }
                          >
                            Details
                          </button>

                        </td>

                      </tr>
                    );
                  }
                )}

              </tbody>

            </table>

          </div>

        )}

      </div>

      {/* ======================================
          DETAILS PANEL
      ====================================== */}

      {selectedSubmission && (

        <div className="admin-form-card">

          <div
            style={{
              display: 'flex',
              justifyContent:
                'space-between',
              gap: '15px',
              flexWrap: 'wrap',
              alignItems: 'flex-start',
            }}
          >

            <div>

              <span className="eyebrow">
                SUBMISSION DETAILS
              </span>

              <h2>
                Submission #
                {
                  selectedSubmission.submissionId ??
                  selectedSubmission.id ??
                  '—'
                }
              </h2>

              <p>
                Review the complete
                submission information.
              </p>

            </div>

            <button
              className="secondary-button"
              onClick={
                closeDetails
              }
            >
              ✕ Close
            </button>

          </div>

          <div
            className="result-stats"
            style={{
              marginTop: '20px',
            }}
          >

            <div className="result-stat-card">

              <span className="result-stat-label">
                User
              </span>

              <strong>
                {selectedSubmission.userName ||
                  'Unknown'}
              </strong>

            </div>

            <div className="result-stat-card">

              <span className="result-stat-label">
                Challenge
              </span>

              <strong>
                {selectedSubmission.challengeTitle ||
                  `#${selectedSubmission.challengeId ??
                    '—'}`}
              </strong>

            </div>

            <div className="result-stat-card">

              <span className="result-stat-label">
                Score
              </span>

              <strong>
                {selectedSubmission.score ??
                  0}
              </strong>

            </div>

            <div className="result-stat-card">

              <span className="result-stat-label">
                Status
              </span>

              <strong>
                {String(
                  selectedSubmission.status ||
                    'PENDING'
                ).toUpperCase()}
              </strong>

            </div>

          </div>

          {/* ANSWER */}

          <div
            style={{
              marginTop: '25px',
            }}
          >

            <h3>
              Submitted Answer
            </h3>

            <div
              style={{
                padding: '18px',
                borderRadius: '14px',
                background:
                  'rgba(255,255,255,0.04)',
                whiteSpace:
                  'pre-wrap',
                wordBreak:
                  'break-word',
              }}
            >
              {selectedSubmission.answer ||
                'No answer available.'}
            </div>

          </div>

          {/* SUBMISSION INFO */}

          <div
            style={{
              marginTop: '25px',
            }}
          >

            <h3>
              Submission Information
            </h3>

            <p>
              <strong>
                Submission ID:
              </strong>{' '}
              {selectedSubmission.submissionId ??
                selectedSubmission.id ??
                '—'}
            </p>

            <p>
              <strong>
                User ID:
              </strong>{' '}
              {selectedSubmission.userId ??
                '—'}
            </p>

            <p>
              <strong>
                Challenge ID:
              </strong>{' '}
              {selectedSubmission.challengeId ??
                '—'}
            </p>

            <p>
              <strong>
                Submitted At:
              </strong>{' '}
              {formatDate(
                selectedSubmission.submittedAt
              )}
            </p>

            <p>
              <strong>
                Correct Answers:
              </strong>{' '}
              {selectedSubmission.correctAnswers ??
                0}
              {' / '}
              {selectedSubmission.totalQuestions ??
                0}
            </p>

            <p>
              <strong>
                First Submission:
              </strong>{' '}
              {selectedSubmission.firstSubmission
                ? 'Yes'
                : 'No'}
            </p>

          </div>

          {/* ACTIONS */}

          <div
            style={{
              display: 'flex',
              gap: '12px',
              flexWrap: 'wrap',
              marginTop: '25px',
            }}
          >

            <button
              className="primary-button"
              disabled={
                actionLoading ===
                  `${
                    selectedSubmission.submissionId ??
                    selectedSubmission.id
                  }-APPROVED` ||
                String(
                  selectedSubmission.status ||
                    ''
                ).toUpperCase() ===
                  'APPROVED'
              }
              onClick={() =>
                updateStatus(
                  selectedSubmission,
                  'APPROVED'
                )
              }
            >
              {actionLoading ===
              `${
                selectedSubmission.submissionId ??
                selectedSubmission.id
              }-APPROVED`
                ? 'Approving...'
                : '✓ Approve'}
            </button>

            <button
              className="secondary-button"
              disabled={
                actionLoading ===
                  `${
                    selectedSubmission.submissionId ??
                    selectedSubmission.id
                  }-REJECTED` ||
                String(
                  selectedSubmission.status ||
                    ''
                ).toUpperCase() ===
                  'REJECTED'
              }
              onClick={() =>
                updateStatus(
                  selectedSubmission,
                  'REJECTED'
                )
              }
            >
              {actionLoading ===
              `${
                selectedSubmission.submissionId ??
                selectedSubmission.id
              }-REJECTED`
                ? 'Rejecting...'
                : '✕ Reject'}
            </button>

          </div>

        </div>

      )}

    </div>
  );
}

export default AdminSubmissions;