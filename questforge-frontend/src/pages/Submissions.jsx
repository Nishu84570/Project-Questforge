import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import SubmissionCard from '../components/SubmissionCard';
import '../styles/Submissions.css';

function Submissions() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('LATEST');

  useEffect(() => {
    let active = true;

    const fetchSubmissions = async () => {
      try {
        const { response, data } = await apiRequest('/submissions/my', {
          method: 'GET',
        });

        if (!active) return;

        if (!response.ok) {
          setError(data?.message || 'Failed to load submissions');
          return;
        }

        setSubmissions(Array.isArray(data) ? data : []);
      } catch (err) {
        if (active) {
          setError('Unable to connect to QuestForge backend.');
        }
      } finally {
        if (active) setLoading(false);
      }
    };

    fetchSubmissions();

    return () => {
      active = false;
    };
  }, []);

  const filteredSubmissions = useMemo(() => {
    let result = [...submissions];

    const search = searchTerm.trim().toLowerCase();

    if (search) {
      result = result.filter((submission) => {
        const submissionId = String(submission.id ?? '');
        const challengeId = String(submission.challengeId ?? '');
        const status = submission.status?.toLowerCase() || '';

        return (
          submissionId.includes(search) ||
          challengeId.includes(search) ||
          status.includes(search)
        );
      });
    }

    if (statusFilter !== 'ALL') {
      result = result.filter(
        (submission) => submission.status?.toUpperCase() === statusFilter
      );
    }

    if (sortBy === 'LATEST') {
      result.sort(
        (a, b) =>
          new Date(b.submittedAt || 0) - new Date(a.submittedAt || 0)
      );
    } else if (sortBy === 'OLDEST') {
      result.sort(
        (a, b) =>
          new Date(a.submittedAt || 0) - new Date(b.submittedAt || 0)
      );
    } else if (sortBy === 'HIGHEST_SCORE') {
      result.sort((a, b) => (b.score ?? 0) - (a.score ?? 0));
    } else if (sortBy === 'LOWEST_SCORE') {
      result.sort((a, b) => (a.score ?? 0) - (b.score ?? 0));
    }

    return result;
  }, [submissions, searchTerm, statusFilter, sortBy]);

  const clearFilters = () => {
    setSearchTerm('');
    setStatusFilter('ALL');
    setSortBy('LATEST');
  };

  const stats = useMemo(() => {
    return {
      total: submissions.length,
      approved: submissions.filter(
        (s) => s.status?.toUpperCase() === 'APPROVED'
      ).length,
      pending: submissions.filter(
        (s) => s.status?.toUpperCase() === 'PENDING'
      ).length,
      average: submissions.length
        ? Math.round(
            submissions.reduce((sum, s) => sum + (Number(s.score) || 0), 0) /
            submissions.length
          )
        : 0,
    };
  }, [submissions]);

  if (loading) {
    return (
      <div className="page submissions-page">
        <div className="empty-card submissions-state-card">
          <div className="loading-spinner" />
          <h2>Loading Your Submissions</h2>
          <p>Preparing your coding journey...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page submissions-page">
        <div className="empty-card error-card-container submissions-state-card">
          <div className="submission-state-icon">!</div>
          <h2>Unable to Load Submissions</h2>
          <p className="error-text">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page submissions-page">

      {/* HERO HEADER */}
      <header className="submissions-hero">

        <div className="submissions-hero-content">
          <div className="submissions-hero-label">
            <span className="hero-label-dot" />
            QUESTFORGE / YOUR ACTIVITY
          </div>

          <h1>
            My <span>Submissions.</span>
          </h1>

          <p>
            Every challenge is a step forward.
            Track your progress and see how far you've come.
          </p>

          <div className="submissions-hero-bottom">
            <span className="hero-decoration-line" />
            <span>YOUR CODING JOURNEY</span>
          </div>
        </div>

        <div className="submissions-hero-art">
          <div className="hero-orbit orbit-one" />
          <div className="hero-orbit orbit-two" />
          <div className="hero-center-icon">⌘</div>
          <span className="hero-floating-icon hero-icon-one">{"{ }"}</span>
          <span className="hero-floating-icon hero-icon-two">01</span>
          <span className="hero-floating-icon hero-icon-three">✦</span>
        </div>

        <Link className="secondary-button submissions-challenge-link" to="/challenges">
          Explore Challenges <span>→</span>
        </Link>

      </header>

      {submissions.length === 0 ? (
        <div className="empty-card submissions-empty-card">
          <div className="submission-empty-icon">⌘</div>
          <h2>Your Journey Starts Here</h2>
          <p>
            You haven't submitted any coding challenges yet.
            Complete your first challenge and start building your record.
          </p>

          <Link className="primary-button" to="/challenges">
            Explore Challenges →
          </Link>
        </div>
      ) : (
        <>
          {/* OVERVIEW */}
          <section className="submission-overview">
            <div className="submission-overview-heading">
              <div>
                <span className="submission-section-label">YOUR PERFORMANCE</span>
                <h2>Activity Overview</h2>
              </div>

              <span className="submission-live-label">
                <span /> LIVE STATS
              </span>
            </div>

            <div className="submission-overview-grid">

              <div className="submission-overview-card">
                <div className="overview-card-top">
                  <span>Total Submissions</span>
                  <span className="overview-icon purple">▤</span>
                </div>
                <strong>{stats.total}</strong>
                <span className="overview-card-caption">All attempts</span>
              </div>

              <div className="submission-overview-card">
                <div className="overview-card-top">
                  <span>Approved</span>
                  <span className="overview-icon green">✓</span>
                </div>
                <strong>{stats.approved}</strong>
                <span className="overview-card-caption">Successful reviews</span>
              </div>

              <div className="submission-overview-card">
                <div className="overview-card-top">
                  <span>Pending</span>
                  <span className="overview-icon orange">◷</span>
                </div>
                <strong>{stats.pending}</strong>
                <span className="overview-card-caption">Awaiting review</span>
              </div>

              <div className="submission-overview-card">
                <div className="overview-card-top">
                  <span>Average Score</span>
                  <span className="overview-icon blue">↗</span>
                </div>
                <strong>{stats.average}%</strong>
                <span className="overview-card-caption">Across all attempts</span>
              </div>

            </div>
          </section>

          {/* FILTERS */}
          <section className="submission-filter-section">

            <div className="submission-filter-heading">
              <div>
                <span className="submission-section-label">YOUR HISTORY</span>
                <h2>All Submissions</h2>
              </div>

              <span className="submission-total-badge">
                {filteredSubmissions.length} RESULTS
              </span>
            </div>

            <div className="submission-filters-card">
              <div className="submission-filters">

                <div className="submission-search-wrapper">
                  <span className="submission-search-icon">⌕</span>

                  <input
                    type="text"
                    placeholder="Search submission, challenge or status..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    className="submission-search-input"
                  />
                </div>

                <select
                  value={statusFilter}
                  onChange={(event) => setStatusFilter(event.target.value)}
                  className="submission-select"
                >
                  <option value="ALL">All Statuses</option>
                  <option value="PENDING">Pending</option>
                  <option value="APPROVED">Approved</option>
                  <option value="REJECTED">Rejected</option>
                </select>

                <select
                  value={sortBy}
                  onChange={(event) => setSortBy(event.target.value)}
                  className="submission-select"
                >
                  <option value="LATEST">Latest First</option>
                  <option value="OLDEST">Oldest First</option>
                  <option value="HIGHEST_SCORE">Highest Score</option>
                  <option value="LOWEST_SCORE">Lowest Score</option>
                </select>

                <button
                  type="button"
                  className="secondary-button submission-clear-btn"
                  onClick={clearFilters}
                >
                  ↻ Clear
                </button>

              </div>
            </div>

            <div className="submission-results-info">
              Showing <strong>{filteredSubmissions.length}</strong> of{' '}
              <strong>{submissions.length}</strong> submissions
            </div>

          </section>

          {/* RESULTS */}
          {filteredSubmissions.length === 0 ? (
            <div className="empty-card submissions-empty-card">
              <div className="submission-empty-icon">⌕</div>
              <h2>No Matching Submissions</h2>
              <p>Try adjusting your search or filters.</p>

              <button
                type="button"
                className="secondary-button"
                onClick={clearFilters}
              >
                Clear Filters
              </button>
            </div>
          ) : (
            <div className="challenge-grid submission-grid">
              {filteredSubmissions.map((submission, index) => (
                <div
                  className="submission-grid-item"
                  key={submission.id}
                  style={{ '--card-index': index }}
                >
                  <SubmissionCard submission={submission} />
                </div>
              ))}
            </div>
          )}
        </>
      )}

      <footer className="submissions-page-footer">
        <span>QUESTFORGE</span>
        <span>LEVEL UP EVERY DAY. ✦</span>
      </footer>

    </div>
  );
}

export default Submissions;