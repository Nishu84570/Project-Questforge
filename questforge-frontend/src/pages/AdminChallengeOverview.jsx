
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminChallengeOverview.css';

function AdminChallengeOverview() {
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [search, setSearch] = useState('');
  const [difficultyFilter, setDifficultyFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');

  // ------------------------------------------
  // HELPERS
  // ------------------------------------------

  const toNumber = (value, fallback = 0) => {
    const number = Number(value);
    return Number.isFinite(number) ? number : fallback;
  };

  const formatNumber = (value, digits = 0) => {
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: digits,
      maximumFractionDigits: digits,
    }).format(toNumber(value));
  };

  const formatPercentage = (value) => {
    return `${formatNumber(value, 1)}%`;
  };

  const getCompletionValue = (value) => {
    return Math.min(100, Math.max(0, toNumber(value)));
  };

  const getChallengeId = (challenge) => {
    return challenge?.challengeId ?? challenge?.id;
  };

  const getDifficultyClass = (difficulty) => {
    const normalized = String(difficulty || 'unknown')
      .trim()
      .toLowerCase()
      .replace(/[^a-z0-9-]/g, '');

    return normalized || 'unknown';
  };

  // ------------------------------------------
  // LOAD CHALLENGES
  // ------------------------------------------

  const loadChallenges = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest('/admin/challenges', {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error(
          data?.message || 'Failed to load challenge overview.'
        );
      }

      const challengeData = Array.isArray(data)
        ? data
        : Array.isArray(data?.content)
        ? data.content
        : Array.isArray(data?.challenges)
        ? data.challenges
        : [];

      setChallenges(challengeData.filter(
        (challenge) =>
          challenge !== null &&
          typeof challenge === 'object' &&
          !Array.isArray(challenge)
      ));
    } catch (err) {
      console.error('Admin challenge overview error:', err);

      setError(
        err?.message || 'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadChallenges();
  }, [loadChallenges]);

  // ------------------------------------------
  // FILTER OPTIONS
  // ------------------------------------------

  const difficulties = useMemo(() => {
    return [
      ...new Set(
        challenges
          .map((challenge) => challenge.difficulty)
          .filter((value) => value != null && String(value).trim())
          .map(String)
      ),
    ].sort((a, b) => a.localeCompare(b));
  }, [challenges]);

  const categories = useMemo(() => {
    return [
      ...new Set(
        challenges
          .map((challenge) => challenge.category)
          .filter((value) => value != null && String(value).trim())
          .map(String)
      ),
    ].sort((a, b) => a.localeCompare(b));
  }, [challenges]);

  // ------------------------------------------
  // SEARCH & FILTER
  // ------------------------------------------

  const filteredChallenges = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return challenges.filter((challenge) => {
      const difficulty = String(challenge.difficulty || '');
      const category = String(challenge.category || '');

      if (
        difficultyFilter !== 'ALL' &&
        difficulty !== difficultyFilter
      ) {
        return false;
      }

      if (
        categoryFilter !== 'ALL' &&
        category !== categoryFilter
      ) {
        return false;
      }

      if (!normalizedSearch) {
        return true;
      }

      const searchableText = [
        challenge.challengeId,
        challenge.id,
        challenge.title,
        challenge.difficulty,
        challenge.category,
      ]
        .filter((value) => value !== null && value !== undefined)
        .join(' ')
        .toLowerCase();

      return searchableText.includes(normalizedSearch);
    });
  }, [
    challenges,
    search,
    difficultyFilter,
    categoryFilter,
  ]);

  // ------------------------------------------
  // SUMMARY STATISTICS
  // ------------------------------------------

  const statistics = useMemo(() => {
    return challenges.reduce(
      (stats, challenge) => {
        stats.submissions += Math.max(
          0,
          toNumber(challenge.totalSubmissions)
        );

        stats.participants += Math.max(
          0,
          toNumber(challenge.uniqueParticipants)
        );

        return stats;
      },
      {
        submissions: 0,
        participants: 0,
      }
    );
  }, [challenges]);

  // ------------------------------------------
  // FILTER ACTIONS
  // ------------------------------------------

  const clearFilters = () => {
    setSearch('');
    setDifficultyFilter('ALL');
    setCategoryFilter('ALL');
  };

  const hasActiveFilters =
    search.trim() !== '' ||
    difficultyFilter !== 'ALL' ||
    categoryFilter !== 'ALL';

  // ------------------------------------------
  // RENDER
  // ------------------------------------------

  return (
    <div className="page admin-page admin-challenge-overview-page">

      {/* HEADER */}

      <header className="aco-header">
        <div className="aco-header-content">
          <div className="aco-breadcrumb">
            <span className="aco-breadcrumb-dot" />
            QUESTFORGE ADMIN
          </div>

          <h1>Challenge Overview</h1>

          <p>
            Monitor challenge performance, participation,
            submissions and completion analytics.
          </p>
        </div>

        <div className="aco-header-actions">
          <Link to="/admin" className="aco-button aco-button-secondary">
            <span>←</span>
            Admin Dashboard
          </Link>

          <button
            type="button"
            className="aco-button aco-button-primary"
            onClick={loadChallenges}
            disabled={loading}
          >
            <span className={loading ? 'aco-refresh-spinning' : ''}>
              ↻
            </span>
            {loading ? 'Refreshing...' : 'Refresh Data'}
          </button>
        </div>
      </header>

      {/* ERROR NOTICE */}

      {error && (
        <div className="aco-error-banner" role="alert">
          <div className="aco-error-content">
            <span className="aco-error-icon">!</span>

            <div>
              <strong>Unable to load challenge data</strong>
              <p>{error}</p>
            </div>
          </div>

          <button
            type="button"
            className="aco-error-retry"
            onClick={loadChallenges}
            disabled={loading}
          >
            Retry
          </button>
        </div>
      )}

      {/* STATISTICS */}

      <section className="aco-stats-grid" aria-label="Challenge statistics">

        <div className="aco-stat-card">
          <div className="aco-stat-top">
            <span className="aco-stat-label">Total Challenges</span>
            <span className="aco-stat-icon aco-icon-purple">▦</span>
          </div>

          <strong className="aco-stat-value">
            {formatNumber(challenges.length)}
          </strong>

          <span className="aco-stat-description">
            Challenges available in the overview
          </span>
        </div>

        <div className="aco-stat-card">
          <div className="aco-stat-top">
            <span className="aco-stat-label">Filtered Results</span>
            <span className="aco-stat-icon aco-icon-blue">⌕</span>
          </div>

          <strong className="aco-stat-value">
            {formatNumber(filteredChallenges.length)}
          </strong>

          <span className="aco-stat-description">
            Matching current search and filters
          </span>
        </div>

        <div className="aco-stat-card">
          <div className="aco-stat-top">
            <span className="aco-stat-label">Total Submissions</span>
            <span className="aco-stat-icon aco-icon-green">↗</span>
          </div>

          <strong className="aco-stat-value">
            {formatNumber(statistics.submissions)}
          </strong>

          <span className="aco-stat-description">
            Combined challenge submissions
          </span>
        </div>

        <div className="aco-stat-card">
          <div className="aco-stat-top">
            <span className="aco-stat-label">Total Participants</span>
            <span className="aco-stat-icon aco-icon-orange">♙</span>
          </div>

          <strong className="aco-stat-value">
            {formatNumber(statistics.participants)}
          </strong>

          <span className="aco-stat-description">
            Combined unique participants
          </span>
        </div>

      </section>

      {/* FILTER PANEL */}

      <section className="aco-filter-panel">

        <div className="aco-filter-heading">
          <div>
            <span className="aco-section-eyebrow">DISCOVER</span>
            <h2>Find Challenges</h2>
            <p>Search and narrow down your challenge records.</p>
          </div>

          {hasActiveFilters && (
            <button
              type="button"
              className="aco-clear-button"
              onClick={clearFilters}
            >
              Reset all filters
              <span>×</span>
            </button>
          )}
        </div>

        <div className="aco-filter-controls">

          <div className="aco-search-wrapper">
            <span className="aco-search-icon">⌕</span>

            <input
              type="search"
              placeholder="Search by title, ID, difficulty or category..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              className="aco-search-input"
              aria-label="Search challenges"
            />

            {search && (
              <button
                type="button"
                className="aco-search-clear"
                onClick={() => setSearch('')}
                aria-label="Clear search"
              >
                ×
              </button>
            )}
          </div>

          <select
            value={difficultyFilter}
            onChange={(event) => setDifficultyFilter(event.target.value)}
            className="aco-select"
            aria-label="Filter by difficulty"
          >
            <option value="ALL">All Difficulties</option>

            {difficulties.map((difficulty) => (
              <option key={difficulty} value={difficulty}>
                {difficulty}
              </option>
            ))}
          </select>

          <select
            value={categoryFilter}
            onChange={(event) => setCategoryFilter(event.target.value)}
            className="aco-select"
            aria-label="Filter by category"
          >
            <option value="ALL">All Categories</option>

            {categories.map((category) => (
              <option key={category} value={category}>
                {category}
              </option>
            ))}
          </select>

        </div>

        <div className="aco-filter-footer">
          <span>
            Showing <strong>{formatNumber(filteredChallenges.length)}</strong>
            {' '}of <strong>{formatNumber(challenges.length)}</strong> challenges
          </span>

          {hasActiveFilters && (
            <span className="aco-filter-active">
              Filters active
            </span>
          )}
        </div>

      </section>

      {/* CHALLENGE PERFORMANCE */}

      <section className="aco-table-card">

        <div className="aco-table-header">
          <div>
            <span className="aco-section-eyebrow">PERFORMANCE CENTER</span>
            <h2>Challenge Performance</h2>
            <p>
              Detailed statistics for every coding challenge.
            </p>
          </div>

          <div className="aco-live-indicator">
            <span />
            {loading ? 'Updating data' : 'Overview ready'}
          </div>
        </div>

        {/* LOADING STATE */}

        {loading && challenges.length === 0 ? (
          <div className="aco-state-container">
            <div className="aco-spinner" />
            <h3>Loading challenge data</h3>
            <p>Fetching the latest challenge statistics...</p>
          </div>
        ) : filteredChallenges.length === 0 ? (

          /* EMPTY STATE */

          <div className="aco-state-container">
            <div className="aco-empty-icon">⌕</div>

            <h3>
              {challenges.length === 0
                ? 'No challenges available'
                : 'No matching challenges'}
            </h3>

            <p>
              {challenges.length === 0
                ? 'There are currently no challenge records to display.'
                : 'Try changing your search or adjusting the filters.'}
            </p>

            {hasActiveFilters && (
              <button
                type="button"
                className="aco-button aco-button-primary"
                onClick={clearFilters}
              >
                Clear Filters
              </button>
            )}
          </div>

        ) : (

          /* DATA TABLE */

          <div className="aco-table-responsive">
            <table className="aco-table">

              <thead>
                <tr>
                  <th>CHALLENGE</th>
                  <th>DIFFICULTY</th>
                  <th>CATEGORY</th>
                  <th>SUBMISSIONS</th>
                  <th>PARTICIPANTS</th>
                  <th>AVERAGE SCORE</th>
                  <th>COMPLETION</th>
                  <th>ACTION</th>
                </tr>
              </thead>

              <tbody>
                {filteredChallenges.map((challenge, index) => {
                  const challengeId = getChallengeId(challenge);
                  const completion = getCompletionValue(
                    challenge.completionRate
                  );

                  return (
                    <tr key={challengeId ?? `challenge-${index}`}>

                      {/* CHALLENGE */}

                      <td>
                        <div className="aco-challenge-cell">
                          <div className="aco-challenge-avatar">
                            {String(challenge.title || 'C')
                              .trim()
                              .charAt(0)
                              .toUpperCase()}
                          </div>

                          <div className="aco-challenge-info">
                            <strong>
                              {challenge.title ||
                                `Challenge #${challengeId ?? '—'}`}
                            </strong>

                            <span>
                              ID: #{challengeId ?? '—'}
                            </span>
                          </div>
                        </div>
                      </td>

                      {/* DIFFICULTY */}

                      <td>
                        <span
                          className={`aco-difficulty-badge ${getDifficultyClass(
                            challenge.difficulty
                          )}`}
                        >
                          {challenge.difficulty || 'Unknown'}
                        </span>
                      </td>

                      {/* CATEGORY */}

                      <td>
                        <span className="aco-category-text">
                          {challenge.category || 'General'}
                        </span>
                      </td>

                      {/* SUBMISSIONS */}

                      <td>
                        <div className="aco-metric-cell">
                          <strong>
                            {formatNumber(challenge.totalSubmissions)}
                          </strong>

                          <span>
                            First: {formatNumber(challenge.firstSubmissions)}
                          </span>
                        </div>
                      </td>

                      {/* PARTICIPANTS */}

                      <td>
                        <strong className="aco-table-number">
                          {formatNumber(challenge.uniqueParticipants)}
                        </strong>
                      </td>

                      {/* AVERAGE SCORE */}

                      <td>
                        <strong className="aco-table-number">
                          {formatNumber(challenge.averageScore, 2)}
                        </strong>
                      </td>

                      {/* COMPLETION */}

                      <td>
                        <div className="aco-completion-cell">
                          <div className="aco-completion-info">
                            <strong>
                              {formatPercentage(challenge.completionRate)}
                            </strong>
                          </div>

                          <div
                            className="aco-progress-track"
                            role="progressbar"
                            aria-label={`Completion rate for ${
                              challenge.title || 'challenge'
                            }`}
                            aria-valuenow={completion}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          >
                            <div
                              className="aco-progress-fill"
                              style={{ width: `${completion}%` }}
                            />
                          </div>
                        </div>
                      </td>

                      {/* ANALYTICS */}

                      <td>
                        {challengeId != null && challengeId !== '' ? (
                          <Link
                            to={`/admin/challenge-analytics/${encodeURIComponent(
                              String(challengeId)
                            )}`}
                            className="aco-analytics-button"
                          >
                            Analytics
                            <span>→</span>
                          </Link>
                        ) : (
                          <span className="aco-unavailable">
                            Unavailable
                          </span>
                        )}
                      </td>

                    </tr>
                  );
                })}
              </tbody>

            </table>
          </div>
        )}

        {/* TABLE FOOTER */}

        {filteredChallenges.length > 0 && (
          <div className="aco-table-footer">
            <span>
              Displaying <strong>{formatNumber(filteredChallenges.length)}</strong>
              {' '}challenge records
            </span>

            <span className="aco-footer-status">
              QuestForge Analytics
            </span>
          </div>
        )}

      </section>

      {/* PAGE FOOTER */}

      <footer className="aco-page-footer">
        <span>QUESTFORGE ADMINISTRATION</span>
        <span>Challenge Intelligence & Performance</span>
      </footer>

    </div>
  );
}

export default AdminChallengeOverview;
