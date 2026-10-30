import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

import SubmissionCard from '../components/SubmissionCard';

function Submissions() {
  const [submissions, setSubmissions] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const [searchTerm, setSearchTerm] =
    useState('');

  const [statusFilter, setStatusFilter] =
    useState('ALL');

  const [sortBy, setSortBy] =
    useState('LATEST');

  useEffect(() => {

    const fetchSubmissions = async () => {

      try {

        const {
          response,
          data,
        } = await apiRequest(
          '/submissions/my',
          {
            method: 'GET',
          }
        );

        if (!response.ok) {

          setError(
            data?.message ||
              'Failed to load submissions'
          );

          return;
        }

        setSubmissions(data || []);

      } catch (error) {

        setError(
          'Unable to connect to QuestForge backend.'
        );

      } finally {

        setLoading(false);
      }
    };

    fetchSubmissions();

  }, []);

  // =========================================================
  // FILTER + SORT
  // =========================================================

  const filteredSubmissions = useMemo(() => {

    let result = [...submissions];

    const search =
      searchTerm
        .trim()
        .toLowerCase();

    if (search) {

      result = result.filter(
        (submission) => {

          const submissionId =
            String(
              submission.id ?? ''
            );

          const challengeId =
            String(
              submission.challengeId ?? ''
            );

          const status =
            submission.status
              ?.toLowerCase() || '';

          return (
            submissionId.includes(search) ||
            challengeId.includes(search) ||
            status.includes(search)
          );
        }
      );
    }

    if (statusFilter !== 'ALL') {

      result = result.filter(
        (submission) =>
          submission.status
            ?.toUpperCase() ===
          statusFilter
      );
    }

    if (sortBy === 'LATEST') {

      result.sort(
        (a, b) =>
          new Date(
            b.submittedAt || 0
          ) -
          new Date(
            a.submittedAt || 0
          )
      );
    }

    if (sortBy === 'OLDEST') {

      result.sort(
        (a, b) =>
          new Date(
            a.submittedAt || 0
          ) -
          new Date(
            b.submittedAt || 0
          )
      );
    }

    if (sortBy === 'HIGHEST_SCORE') {

      result.sort(
        (a, b) =>
          (b.score ?? 0) -
          (a.score ?? 0)
      );
    }

    if (sortBy === 'LOWEST_SCORE') {

      result.sort(
        (a, b) =>
          (a.score ?? 0) -
          (b.score ?? 0)
      );
    }

    return result;

  }, [
    submissions,
    searchTerm,
    statusFilter,
    sortBy,
  ]);

  // =========================================================
  // CLEAR FILTERS
  // =========================================================

  const clearFilters = () => {

    setSearchTerm('');
    setStatusFilter('ALL');
    setSortBy('LATEST');

  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {

    return (
      <div className="page submissions-page">

        <p className="status-text">
          Loading submissions...
        </p>

      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error) {

    return (
      <div className="page submissions-page">

        <p className="error-text">
          {error}
        </p>

      </div>
    );
  }

  // =========================================================
  // MAIN UI
  // =========================================================

  return (
    <div className="page submissions-page">

      <div className="challenges-header">

        <div>

          <p className="eyebrow">
            QUESTFORGE
          </p>

          <h1>
            My Submissions
          </h1>

          <p>
            Track the challenges you have submitted.
          </p>

        </div>

        <Link
          className="secondary-button"
          to="/challenges"
        >
          Challenges
        </Link>

      </div>

      {submissions.length === 0 ? (

        <div className="empty-card">

          <h2>
            No submissions yet
          </h2>

          <p>
            You haven't submitted any challenges yet.
          </p>

          <Link
            className="primary-button"
            to="/challenges"
          >
            Browse Challenges
          </Link>

        </div>

      ) : (

        <>

          {/* =================================================
              FILTERS
          ================================================= */}

          <div className="submission-filters">

            <input
              type="text"
              placeholder="Search by submission, challenge or status..."
              value={searchTerm}
              onChange={(event) =>
                setSearchTerm(
                  event.target.value
                )
              }
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

            <select
              value={sortBy}
              onChange={(event) =>
                setSortBy(
                  event.target.value
                )
              }
            >

              <option value="LATEST">
                Latest First
              </option>

              <option value="OLDEST">
                Oldest First
              </option>

              <option value="HIGHEST_SCORE">
                Highest Score
              </option>

              <option value="LOWEST_SCORE">
                Lowest Score
              </option>

            </select>

            <button
              type="button"
              className="secondary-button"
              onClick={clearFilters}
            >
              Clear
            </button>

          </div>

          {/* =================================================
              RESULT COUNT
          ================================================= */}

          <div className="submission-results-info">

            Showing{' '}
            <strong>
              {filteredSubmissions.length}
            </strong>{' '}
            of{' '}
            <strong>
              {submissions.length}
            </strong>{' '}
            submissions

          </div>

          {/* =================================================
              RESULTS
          ================================================= */}

          {filteredSubmissions.length === 0 ? (

            <div className="empty-card">

              <h2>
                No matching submissions
              </h2>

              <p>
                Try changing your search or filters.
              </p>

              <button
                type="button"
                className="secondary-button"
                onClick={clearFilters}
              >
                Clear Filters
              </button>

            </div>

          ) : (

            <div className="challenge-grid">

              {filteredSubmissions.map(
                (submission) => (

                  <SubmissionCard
                    key={submission.id}
                    submission={submission}
                  />

                )
              )}

            </div>

          )}

        </>

      )}

    </div>
  );
}

export default Submissions;
