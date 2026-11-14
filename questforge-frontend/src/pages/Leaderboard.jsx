
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/Leaderboard.css';

function Leaderboard() {
  const [searchParams] = useSearchParams();
  const challengeId = searchParams.get('challengeId');

  const isGlobal = !challengeId;

  const [leaderboard, setLeaderboard] = useState([]);
  const [challengeTitle, setChallengeTitle] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('rank');
  const [retryCount, setRetryCount] = useState(0);

  // Fetch leaderboard from the actual backend endpoints.
  const fetchLeaderboard = useCallback(async (isActive = () => true) => {
    setLoading(true);
    setError('');

    try {
      const endpoint = challengeId
        ? `/leaderboard/challenge/${challengeId}`
        : '/leaderboard/global';

      const { response, data } = await apiRequest(endpoint, {
        method: 'GET',
      });

      if (!isActive()) return;

      if (!response.ok) {
        setLeaderboard([]);
        setError(data?.message || 'Failed to load leaderboard.');
        return;
      }

      if (!Array.isArray(data)) {
        setLeaderboard([]);
        setError('Unexpected leaderboard response from the server.');
        return;
      }

      setLeaderboard(data);

      if (challengeId) {
        setChallengeTitle(
          data[0]?.challengeTitle || `Challenge #${challengeId}`
        );
      } else {
        setChallengeTitle('');
      }
    } catch (err) {
      if (!isActive()) return;

      setLeaderboard([]);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      if (isActive()) {
        setLoading(false);
      }
    }
  }, [challengeId]);

  useEffect(() => {
    let active = true;

    setSearchTerm('');
    setSortBy('rank');

    fetchLeaderboard(() => active);

    return () => {
      active = false;
    };
  }, [fetchLeaderboard, retryCount]);

  // Backend already calculates rank. Keep that rank unchanged.
  const rankedLeaderboard = useMemo(() => {
    return [...leaderboard].sort(
      (a, b) => (a.rank ?? Number.MAX_SAFE_INTEGER) -
                (b.rank ?? Number.MAX_SAFE_INTEGER)
    );
  }, [leaderboard]);

  // Search and sorting are frontend-only operations.
  const filteredLeaderboard = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    let result = rankedLeaderboard.filter((entry) => {
      const userName = String(entry.userName || '').toLowerCase();
      const title = String(entry.challengeTitle || '').toLowerCase();

      return (
        !search ||
        userName.includes(search) ||
        title.includes(search)
      );
    });

    result = [...result];

    switch (sortBy) {
      case 'xp':
        result.sort((a, b) => (b.xp ?? 0) - (a.xp ?? 0));
        break;

      case 'level':
        result.sort((a, b) => (b.level ?? 0) - (a.level ?? 0));
        break;

      case 'score':
        result.sort((a, b) => (b.score ?? 0) - (a.score ?? 0));
        break;

      case 'correctAnswers':
        result.sort(
          (a, b) => (b.correctAnswers ?? 0) - (a.correctAnswers ?? 0)
        );
        break;

      case 'submittedAt':
        result.sort((a, b) => {
          const dateA = a.submittedAt
            ? new Date(a.submittedAt).getTime()
            : Number.MAX_SAFE_INTEGER;

          const dateB = b.submittedAt
            ? new Date(b.submittedAt).getTime()
            : Number.MAX_SAFE_INTEGER;

          return dateA - dateB;
        });
        break;

      case 'rank':
      default:
        result.sort(
          (a, b) => (a.rank ?? Number.MAX_SAFE_INTEGER) -
                    (b.rank ?? Number.MAX_SAFE_INTEGER)
        );
    }

    return result;
  }, [rankedLeaderboard, searchTerm, sortBy]);

  // Podium always follows the original backend ranking.
  const podiumPlayers = rankedLeaderboard.slice(0, 3);

  const getPlayerName = (entry) => {
    return entry.userName || 'Unknown User';
  };

  const getPlayerInitial = (entry) => {
    return getPlayerName(entry).charAt(0).toUpperCase();
  };

  const getRankIcon = (rank) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return `#${rank}`;
  };

  const formatDate = (dateValue) => {
    if (!dateValue) return 'Not available';

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
      return 'Not available';
    }

    return date.toLocaleString(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  };

  const clearFilters = () => {
    setSearchTerm('');
    setSortBy('rank');
  };

  const handleRetry = () => {
    setRetryCount((current) => current + 1);
  };

  if (loading) {
    return (
      <div className="page leaderboard-page">
        <div className="leaderboard-state-card">
          <div className="leaderboard-state-icon">🏆</div>
          <div className="leaderboard-spinner" />
          <h2>Loading Leaderboard</h2>
          <p>Fetching the latest QuestForge rankings...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page leaderboard-page">
        <div className="leaderboard-state-card">
          <div className="leaderboard-state-icon error-icon">⚠️</div>
          <h2>Leaderboard unavailable</h2>
          <p className="leaderboard-error-text">{error}</p>

          <button
            type="button"
            className="leaderboard-primary-button"
            onClick={handleRetry}
          >
            ↻ Try Again
          </button>

          <Link className="leaderboard-text-link" to="/challenges">
            Back to Challenges
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page leaderboard-page">
      {/* HERO */}
      <section className="leaderboard-hero">
        <div className="leaderboard-hero-glow" />

        <div className="leaderboard-hero-content">
          <div className="leaderboard-hero-icon">🏆</div>

          <div className="leaderboard-hero-copy">
            <span className="leaderboard-eyebrow">QUESTFORGE / RANKINGS</span>
            <h1>Leaderboard</h1>

            <p>
              {isGlobal
                ? 'Every challenge counts. Every XP brings you closer to the top.'
                : 'See how your first submissions compare with other challengers.'}
            </p>
          </div>
        </div>

        <div className="leaderboard-hero-actions">
          {!isGlobal && (
            <Link className="leaderboard-secondary-button" to="/leaderboard">
              🌎 Global Rankings
            </Link>
          )}

          <Link
            className="leaderboard-secondary-button"
            to="/challenges"
          >
            ⚔ Explore Challenges
          </Link>
        </div>
      </section>

      {/* QUICK STATS */}
      <section className="leaderboard-overview">
        <article className="leaderboard-overview-card">
          <div className="overview-icon">👥</div>
          <div>
            <span>Total Players</span>
            <strong>{leaderboard.length}</strong>
          </div>
        </article>

        <article className="leaderboard-overview-card">
          <div className="overview-icon">🥇</div>
          <div>
            <span>Top Rank</span>
            <strong>{leaderboard.length > 0 ? '#1' : '—'}</strong>
          </div>
        </article>

        <article className="leaderboard-overview-card">
          <div className="overview-icon">
            {isGlobal ? '⚡' : '🎯'}
          </div>
          <div>
            <span>{isGlobal ? 'Ranking Mode' : 'Competition'}</span>
            <strong className="overview-mode">
              {isGlobal ? 'XP Based' : 'Challenge'}
            </strong>
          </div>
        </article>
      </section>

      {/* PODIUM */}
      {podiumPlayers.length > 0 && (
        <section className="leaderboard-podium-section">
          <div className="leaderboard-section-heading">
            <div>
              <span className="leaderboard-eyebrow">THE BEST OF THE BEST</span>
              <h2>Top Performers</h2>
              <p>Meet the players currently leading the rankings.</p>
            </div>

            <span className="podium-heading-badge">🏆 Hall of Fame</span>
          </div>

          <div className="leaderboard-podium">
            {podiumPlayers.map((entry) => {
              const rank = entry.rank;

              return (
                <article
                  key={`${entry.userId}-${entry.challengeId ?? 'global'}`}
                  className={`podium-card podium-rank-${rank}`}
                >
                  <div className="podium-medal">
                    {getRankIcon(rank)}
                  </div>

                  <div className="podium-avatar">
                    {getPlayerInitial(entry)}
                  </div>

                  <h3>{getPlayerName(entry)}</h3>

                  <span className="podium-rank-label">
                    {rank === 1
                      ? 'CHAMPION'
                      : rank === 2
                        ? 'RUNNER UP'
                        : 'THIRD PLACE'}
                  </span>

                  {isGlobal ? (
                    <>
                      <strong className="podium-main-value">
                        {(entry.xp ?? 0).toLocaleString()} XP
                      </strong>
                      <span className="podium-sub-value">
                        ⭐ Level {entry.level ?? 1}
                      </span>
                    </>
                  ) : (
                    <>
                      <strong className="podium-main-value">
                        {(entry.score ?? 0).toLocaleString()}
                      </strong>
                      <span className="podium-sub-value">
                        {entry.correctAnswers ?? 0} correct out of{' '}
                        {entry.totalQuestions ?? 0}
                      </span>
                    </>
                  )}
                </article>
              );
            })}
          </div>
        </section>
      )}

      {/* MAIN LEADERBOARD */}
      <section className="leaderboard-container">
        <div className="leaderboard-main-header">
          <div className="leaderboard-main-title">
            <div className="leaderboard-main-icon">
              {isGlobal ? '🌎' : '⚔️'}
            </div>

            <div>
              <span className="leaderboard-eyebrow">
                {isGlobal ? 'GLOBAL RANKINGS' : 'CHALLENGE RANKINGS'}
              </span>

              <h2>
                {isGlobal ? 'Top Players' : challengeTitle}
              </h2>

              <p>
                {isGlobal
                  ? 'Players ranked by their total experience points.'
                  : 'Players ranked by their first submission performance.'}
              </p>
            </div>
          </div>

          <div className="leaderboard-entry-count">
            <strong>{filteredLeaderboard.length}</strong>
            <span>
              {filteredLeaderboard.length === 1 ? 'Player' : 'Players'}
            </span>
          </div>
        </div>

        {/* SEARCH AND SORT */}
        {leaderboard.length > 0 && (
          <div className="leaderboard-toolbar">
            <div className="leaderboard-search">
              <span>⌕</span>
              <input
                type="search"
                placeholder="Search players..."
                value={searchTerm}
                onChange={(event) => setSearchTerm(event.target.value)}
                aria-label="Search players"
              />

              {searchTerm && (
                <button
                  type="button"
                  className="search-clear-button"
                  onClick={() => setSearchTerm('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}
            </div>

            <div className="leaderboard-sort">
              <label htmlFor="leaderboard-sort-select">Sort by</label>

              <select
                id="leaderboard-sort-select"
                value={sortBy}
                onChange={(event) => setSortBy(event.target.value)}
              >
                <option value="rank">Rank</option>

                {isGlobal ? (
                  <>
                    <option value="xp">XP</option>
                    <option value="level">Level</option>
                  </>
                ) : (
                  <>
                    <option value="score">Score</option>
                    <option value="correctAnswers">Correct Answers</option>
                    <option value="submittedAt">Submission Time</option>
                  </>
                )}
              </select>
            </div>

            {(searchTerm || sortBy !== 'rank') && (
              <button
                type="button"
                className="leaderboard-clear-button"
                onClick={clearFilters}
              >
                ✕ Reset
              </button>
            )}
          </div>
        )}

        {/* EMPTY STATES */}
        {leaderboard.length === 0 ? (
          <div className="leaderboard-empty-state">
            <div className="empty-state-icon">🏆</div>
            <h3>No rankings yet</h3>
            <p>
              {isGlobal
                ? 'Complete challenges to earn XP and appear on the global leaderboard.'
                : 'Be the first player to submit this challenge.'}
            </p>

            <Link
              className="leaderboard-primary-button"
              to="/challenges"
            >
              Explore Challenges
            </Link>
          </div>
        ) : filteredLeaderboard.length === 0 ? (
          <div className="leaderboard-empty-state">
            <div className="empty-state-icon">🔎</div>
            <h3>No players found</h3>
            <p>Try another player name or reset your search.</p>

            <button
              type="button"
              className="leaderboard-primary-button"
              onClick={clearFilters}
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="leaderboard-table-wrapper">
            <div className="leaderboard-table-heading">
              <span>
                <span className="leaderboard-live-dot" />
                Rankings
              </span>

              <span>
                Showing {filteredLeaderboard.length} of {leaderboard.length}
              </span>
            </div>

            <div className="leaderboard-table-scroll">
              <table className="leaderboard-table">
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Player</th>

                    {isGlobal ? (
                      <>
                        <th>Experience</th>
                        <th>Level</th>
                      </>
                    ) : (
                      <>
                        <th>Score</th>
                        <th>Correct Answers</th>
                        <th>Submitted At</th>
                      </>
                    )}
                  </tr>
                </thead>

                <tbody>
                  {filteredLeaderboard.map((entry) => (
                    <tr
                      key={`${entry.userId}-${entry.challengeId ?? 'global'}`}
                      className={
                        entry.rank <= 3 ? `top-player-row rank-row-${entry.rank}` : ''
                      }
                    >
                      <td>
                        <div className={`rank-cell rank-${entry.rank}`}>
                          {getRankIcon(entry.rank)}
                        </div>
                      </td>

                      <td>
                        <div className="leaderboard-player-cell">
                          <div className="player-avatar">
                            {getPlayerInitial(entry)}
                          </div>

                          <div className="player-details">
                            <strong>{getPlayerName(entry)}</strong>
                            <span>
                              Player #{entry.userId ?? '—'}
                            </span>
                          </div>
                        </div>
                      </td>

                      {isGlobal ? (
                        <>
                          <td>
                            <span className="xp-value">
                              ⚡ {(entry.xp ?? 0).toLocaleString()} XP
                            </span>
                          </td>

                          <td>
                            <span className="level-badge">
                              Level {entry.level ?? 1}
                            </span>
                          </td>
                        </>
                      ) : (
                        <>
                          <td>
                            <strong className="score-value">
                              {(entry.score ?? 0).toLocaleString()}
                            </strong>
                          </td>

                          <td>
                            <div className="answer-progress">
                              <strong>
                                {entry.correctAnswers ?? 0}
                                <span> / {entry.totalQuestions ?? 0}</span>
                              </strong>
                            </div>
                          </td>

                          <td>
                            <span className="submission-date">
                              {formatDate(entry.submittedAt)}
                            </span>
                          </td>
                        </>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>

      {/* FOOTER NOTE */}
      <div className="leaderboard-footer-note">
        <span>💡</span>
        <p>
          {isGlobal
            ? 'Keep completing challenges and earning XP to improve your global position.'
            : 'Challenge rankings are based on first submissions. Your rank is provided by the server.'}
        </p>
      </div>
    </div>
  );
}

export default Leaderboard;
