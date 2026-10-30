import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminUserSubmissions() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [submissions, setSubmissions] = useState([]);
  const [user, setUser] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('LATEST');

  useEffect(() => {
    loadUserSubmissions();
  }, [userId]);

  async function loadUserSubmissions() {
    if (!userId) {
      setError('User ID is missing.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError('');

    try {
      const [
        submissionsResult,
        userResult,
      ] = await Promise.all([
        apiRequest(
          `/admin/users/${userId}/submissions`,
          {
            method: 'GET',
          }
        ),
        apiRequest(
          `/admin/users/${userId}`,
          {
            method: 'GET',
          }
        ),
      ]);

      if (!submissionsResult.response.ok) {
        throw new Error(
          submissionsResult.data?.message ||
          'Failed to load user submissions.'
        );
      }

      const submissionData =
        Array.isArray(submissionsResult.data)
          ? submissionsResult.data
          : submissionsResult.data?.content ||
            submissionsResult.data?.submissions ||
            [];

      setSubmissions(submissionData);

      if (userResult.response.ok) {
        setUser(userResult.data);
      }
    } catch (err) {
      console.error(
        'Admin user submissions error:',
        err
      );

      setError(
        err.message ||
        'Unable to load user submissions.'
      );
    } finally {
      setLoading(false);
    }
  }

  const filteredSubmissions = useMemo(() => {
    let result = [...submissions];

    const query = search
      .trim()
      .toLowerCase();

    if (query) {
      result = result.filter((submission) => {
        const challengeTitle =
          submission.challengeTitle ||
          submission.challenge?.title ||
          '';

        const answer =
          submission.answer || '';

        const status =
          submission.status || '';

        const challengeId =
          submission.challengeId ??
          submission.challenge?.id ??
          '';

        return (
          String(challengeTitle)
            .toLowerCase()
            .includes(query) ||
          String(answer)
            .toLowerCase()
            .includes(query) ||
          String(status)
            .toLowerCase()
            .includes(query) ||
          String(challengeId)
            .toLowerCase()
            .includes(query)
        );
      });
    }

    if (statusFilter !== 'ALL') {
      result = result.filter(
        (submission) =>
          String(
            submission.status || ''
          ).toUpperCase() === statusFilter
      );
    }

    result.sort((a, b) => {
      const dateA = new Date(
        a.submittedAt ||
        a.createdAt ||
        0
      ).getTime();

      const dateB = new Date(
        b.submittedAt ||
        b.createdAt ||
        0
      ).getTime();

      if (sortOrder === 'OLDEST') {
        return dateA - dateB;
      }

      if (sortOrder === 'SCORE_HIGH') {
        return (
          Number(b.score || 0) -
          Number(a.score || 0)
        );
      }

      if (sortOrder === 'SCORE_LOW') {
        return (
          Number(a.score || 0) -
          Number(b.score || 0)
        );
      }

      return dateB - dateA;
    });

    return result;
  }, [
    submissions,
    search,
    statusFilter,
    sortOrder,
  ]);

  function getSubmissionId(submission) {
    return (
      submission.submissionId ??
      submission.id ??
      submission._id
    );
  }

  function getChallengeTitle(submission) {
    return (
      submission.challengeTitle ||
      submission.challenge?.title ||
      `Challenge #${
        submission.challengeId ??
        submission.challenge?.id ??
        'Unknown'
      }`
    );
  }

  function getChallengeId(submission) {
    return (
      submission.challengeId ??
      submission.challenge?.id
    );
  }

  function getStatusClass(status) {
    const normalized =
      String(status || 'UNKNOWN')
        .toLowerCase()
        .replace(/\s+/g, '-');

    return `submission-status submission-status-${normalized}`;
  }

  function formatDate(value) {
    if (!value) {
      return 'N/A';
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return String(value);
    }

    return date.toLocaleString();
  }

  function getScore(submission) {
    if (
      submission.score !== undefined &&
      submission.score !== null
    ) {
      return submission.score;
    }

    return 0;
  }

  function getCorrectAnswers(submission) {
    return (
      submission.correctAnswers ??
      submission.correct ??
      0
    );
  }

  function getTotalQuestions(submission) {
    return (
      submission.totalQuestions ??
      submission.total ??
      0
    );
  }

  function clearFilters() {
    setSearch('');
    setStatusFilter('ALL');
    setSortOrder('LATEST');
  }

  return (
    <div className="page admin-user-submissions-page">

      <div className="page-header">

        <div>
          <span className="page-kicker">
            ADMIN • USER ACTIVITY
          </span>

          <h1>
            User Submissions
          </h1>

          <p>
            Review all submissions made by this user.
          </p>
        </div>

        <div className="page-header-actions">

          <Link
            to="/admin/users"
            className="secondary-button"
          >
            ← Back to Users
          </Link>

          <button
            type="button"
            className="secondary-button"
            onClick={loadUserSubmissions}
          >
            ↻ Refresh
          </button>

        </div>

      </div>


      {/* =========================
          USER INFORMATION
      ========================= */}

      <section className="admin-user-summary">

        <div className="admin-summary-card">

          <span className="admin-summary-label">
            User ID
          </span>

          <strong>
            {user?.userId ??
              user?.id ??
              userId}
          </strong>

        </div>

        <div className="admin-summary-card">

          <span className="admin-summary-label">
            Name
          </span>

          <strong>
            {user?.name ||
              user?.username ||
              'Unknown User'}
          </strong>

        </div>

        <div className="admin-summary-card">

          <span className="admin-summary-label">
            Email
          </span>

          <strong>
            {user?.email || 'N/A'}
          </strong>

        </div>

        <div className="admin-summary-card">

          <span className="admin-summary-label">
            Total Submissions
          </span>

          <strong>
            {submissions.length}
          </strong>

        </div>

      </section>


      {/* =========================
          FILTERS
      ========================= */}

      <section className="admin-filters">

        <div className="filter-group">

          <label htmlFor="submission-search">
            Search
          </label>

          <input
            id="submission-search"
            type="text"
            value={search}
            onChange={(event) =>
              setSearch(event.target.value)
            }
            placeholder="Search challenge, answer, status..."
          />

        </div>


        <div className="filter-group">

          <label htmlFor="submission-status">
            Status
          </label>

          <select
            id="submission-status"
            value={statusFilter}
            onChange={(event) =>
              setStatusFilter(event.target.value)
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

        </div>


        <div className="filter-group">

          <label htmlFor="submission-sort">
            Sort
          </label>

          <select
            id="submission-sort"
            value={sortOrder}
            onChange={(event) =>
              setSortOrder(event.target.value)
            }
          >

            <option value="LATEST">
              Latest First
            </option>

            <option value="OLDEST">
              Oldest First
            </option>

            <option value="SCORE_HIGH">
              Highest Score
            </option>

            <option value="SCORE_LOW">
              Lowest Score
            </option>

          </select>

        </div>


        <button
          type="button"
          className="secondary-button"
          onClick={clearFilters}
        >
          Clear Filters
        </button>

      </section>


      {/* =========================
          ERROR
      ========================= */}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}


      {/* =========================
          LOADING
      ========================= */}

      {loading ? (
        <section className="loading-state">

          <div className="loading-spinner">
            ⏳
          </div>

          <h2>
            Loading submissions...
          </h2>

          <p>
            Fetching user submission history.
          </p>

        </section>
      ) : (
        <>

          {/* =========================
              EMPTY STATE
          ========================= */}

          {filteredSubmissions.length === 0 ? (

            <section className="empty-state">

              <div className="empty-state-icon">
                📭
              </div>

              <h2>
                No submissions found
              </h2>

              <p>
                This user has no submissions matching
                the current filters.
              </p>

              {(search ||
                statusFilter !== 'ALL') && (
                <button
                  type="button"
                  className="secondary-button"
                  onClick={clearFilters}
                >
                  Clear Filters
                </button>
              )}

            </section>

          ) : (

            /* =========================
                SUBMISSIONS TABLE
            ========================= */

            <section className="admin-table-section">

              <div className="section-heading">

                <div>
                  <h2>
                    Submission History
                  </h2>

                  <p>
                    Showing{' '}
                    {filteredSubmissions.length}{' '}
                    of{' '}
                    {submissions.length}{' '}
                    submissions
                  </p>
                </div>

              </div>


              <div className="admin-table-wrapper">

                <table className="admin-table">

                  <thead>
                    <tr>

                      <th>
                        Submission
                      </th>

                      <th>
                        Challenge
                      </th>

                      <th>
                        Score
                      </th>

                      <th>
                        Result
                      </th>

                      <th>
                        Status
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
                      (submission, index) => {

                        const submissionId =
                          getSubmissionId(
                            submission
                          );

                        const challengeId =
                          getChallengeId(
                            submission
                          );

                        const correct =
                          getCorrectAnswers(
                            submission
                          );

                        const total =
                          getTotalQuestions(
                            submission
                          );

                        return (
                          <tr
                            key={
                              submissionId ??
                              `${userId}-${index}`
                            }
                          >

                            <td>

                              <strong>
                                #
                                {submissionId ??
                                  index + 1}
                              </strong>

                              {submission.firstSubmission && (
                                <span className="admin-table-badge">
                                  First
                                </span>
                              )}

                            </td>


                            <td>

                              <div className="table-primary-text">
                                {getChallengeTitle(
                                  submission
                                )}
                              </div>

                              {challengeId !==
                                undefined &&
                                challengeId !==
                                  null && (
                                  <div className="table-secondary-text">
                                    Challenge #
                                    {challengeId}
                                  </div>
                                )}

                            </td>


                            <td>

                              <strong>
                                {getScore(
                                  submission
                                )}
                              </strong>

                            </td>


                            <td>

                              <span>
                                {correct}
                                {total > 0
                                  ? ` / ${total}`
                                  : ''}
                              </span>

                            </td>


                            <td>

                              <span
                                className={getStatusClass(
                                  submission.status
                                )}
                              >
                                {String(
                                  submission.status ||
                                    'UNKNOWN'
                                ).toUpperCase()}
                              </span>

                            </td>


                            <td>
                              {formatDate(
                                submission.submittedAt ||
                                  submission.createdAt
                              )}
                            </td>


                            <td>

                              {submissionId ? (
                                <button
                                  type="button"
                                  className="table-action-button"
                                  onClick={() =>
                                    navigate(
                                      `/submission-result/${submissionId}`
                                    )
                                  }
                                >
                                  View
                                </button>
                              ) : (
                                <span>
                                  N/A
                                </span>
                              )}

                            </td>

                          </tr>
                        );
                      }
                    )}

                  </tbody>

                </table>

              </div>

            </section>

          )}

        </>
      )}

    </div>
  );
}

export default AdminUserSubmissions;