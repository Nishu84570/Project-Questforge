
import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminDashboardOverview.css';

const STATISTICS = [
  {
    label: 'Total Users',
    fields: ['totalUsers', 'userCount'],
    icon: '👥',
    accent: 'violet',
    description: 'Registered platform users',
  },
  {
    label: 'Total Challenges',
    fields: ['totalChallenges', 'challengeCount'],
    icon: '⚔️',
    accent: 'cyan',
    description: 'Available coding challenges',
  },
  {
    label: 'Total Submissions',
    fields: ['totalSubmissions', 'submissionCount'],
    icon: '📝',
    accent: 'blue',
    description: 'Submissions received',
  },
  {
    label: 'Pending Submissions',
    fields: ['pendingSubmissions', 'pendingCount'],
    icon: '⏳',
    accent: 'orange',
    description: 'Awaiting review',
  },
  {
    label: 'Total Questions',
    fields: ['totalQuestions', 'questionCount'],
    icon: '❓',
    accent: 'purple',
    description: 'Questions in the platform',
  },
  {
    label: 'Total Attempts',
    fields: ['totalAttempts', 'attemptCount'],
    icon: '🎯',
    accent: 'pink',
    description: 'Challenge attempts recorded',
  },
  {
    label: 'Approved',
    fields: ['approvedSubmissions', 'approvedCount'],
    icon: '✅',
    accent: 'green',
    description: 'Approved submissions',
  },
  {
    label: 'Rejected',
    fields: ['rejectedSubmissions', 'rejectedCount'],
    icon: '❌',
    accent: 'red',
    description: 'Rejected submissions',
  },
];

const QUICK_ACTIONS = [
  {
    title: 'Manage Users',
    description: 'View and manage platform users.',
    icon: '👥',
    path: '/admin/users',
  },
  {
    title: 'Review Submissions',
    description: 'Inspect and review coding submissions.',
    icon: '📝',
    path: '/admin/submissions',
  },
  {
    title: 'Challenge Overview',
    description: 'Explore and manage coding challenges.',
    icon: '⚔️',
    path: '/admin/challenge-overview',
  },
  {
    title: 'Challenge Analytics',
    description: 'Analyze challenge performance.',
    icon: '📈',
    path: '/admin/challenge-analytics',
  },
];

function formatNumber(value) {
  if (value === null || value === undefined) {
    return '—';
  }

  if (typeof value === 'number' && Number.isFinite(value)) {
    return value.toLocaleString();
  }

  return String(value);
}

function getStatisticValue(data, fields) {
  for (const field of fields) {
    if (data?.[field] !== null && data?.[field] !== undefined) {
      return data[field];
    }
  }

  return 0;
}

function getUserName(user) {
  return (
    user?.name ||
    user?.userName ||
    `User #${user?.userId ?? user?.id ?? 'N/A'}`
  );
}

function getUserId(user, index) {
  return user?.userId ?? user?.id ?? `user-${index}`;
}

function getChallengeId(challenge, index) {
  return (
    challenge?.challengeId ??
    challenge?.id ??
    `challenge-${index}`
  );
}

function getChallengeTitle(challenge) {
  return (
    challenge?.title ||
    challenge?.challengeTitle ||
    'Untitled Challenge'
  );
}

function getDifficultyClass(difficulty) {
  const normalized = String(difficulty || 'UNKNOWN').toLowerCase();

  if (normalized === 'easy') return 'easy';
  if (normalized === 'medium') return 'medium';
  if (normalized === 'hard') return 'hard';

  return 'unknown';
}

function AdminDashboardOverview() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [lastUpdated, setLastUpdated] = useState(null);

  const loadDashboard = useCallback(async ({ silent = false } = {}) => {
    if (silent) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError('');

    try {
      const { response, data } = await apiRequest('/admin/dashboard', {
        method: 'GET',
      });

      if (!response.ok) {
        setError(data?.message || 'Failed to load admin dashboard.');
        return;
      }

      if (!data || typeof data !== 'object' || Array.isArray(data)) {
        setError('The backend returned an invalid dashboard response.');
        return;
      }

      setDashboard(data);
      setLastUpdated(new Date());
    } catch (err) {
      console.error('Admin dashboard overview error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  if (loading) {
    return (
      <main className="page qfo-page">
        <section className="qfo-loading-card">
          <div className="qfo-loader" aria-hidden="true" />
          <h2>Preparing Admin Dashboard</h2>
          <p>Fetching the latest QuestForge platform statistics...</p>
          <div className="qfo-loading-track">
            <span />
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="page qfo-page">
      <div className="qfo-shell">
        {/* HEADER */}
        <header className="qfo-header">
          <div className="qfo-header-content">
            <div className="qfo-breadcrumb">
              <span className="qfo-breadcrumb-dot" />
              QUESTFORGE / ADMINISTRATION
            </div>

            <div className="qfo-title-row">
              <div>
                <h1>Dashboard Overview</h1>
                <p>
                  Monitor your platform, track activity and manage
                  the QuestForge ecosystem.
                </p>
              </div>

              <div className="qfo-header-icon" aria-hidden="true">
                📊
              </div>
            </div>

            <div className="qfo-header-meta">
              <span className="qfo-status">
                <span />
                Dashboard data loaded
              </span>

              {lastUpdated && (
                <span className="qfo-last-updated">
                  Updated at {lastUpdated.toLocaleTimeString()}
                </span>
              )}
            </div>
          </div>

          <div className="qfo-header-actions">
            <button
              type="button"
              className="qfo-refresh-button"
              onClick={() => loadDashboard({ silent: true })}
              disabled={refreshing}
            >
              <span
                className={
                  refreshing
                    ? 'qfo-refresh-icon qfo-spinning'
                    : 'qfo-refresh-icon'
                }
              >
                ↻
              </span>
              {refreshing ? 'Refreshing...' : 'Refresh'}
            </button>

            <Link className="qfo-back-button" to="/admin">
              <span>←</span>
              Admin Panel
            </Link>
          </div>
        </header>

        {/* ERROR STATE */}
        {error && (
          <section className="qfo-error-panel" role="alert">
            <div className="qfo-error-icon">!</div>

            <div className="qfo-error-content">
              <h2>Dashboard update failed</h2>
              <p>{error}</p>
              {dashboard && (
                <span>
                  Previously loaded dashboard data is still displayed.
                </span>
              )}
            </div>

            <button
              type="button"
              className="qfo-retry-button"
              onClick={() =>
                loadDashboard({ silent: Boolean(dashboard) })
              }
              disabled={refreshing}
            >
              Try Again
            </button>
          </section>
        )}

        {dashboard && (
          <>
            {/* STATISTICS */}
            <section className="qfo-section">
              <div className="qfo-section-heading">
                <div>
                  <span className="qfo-section-kicker">
                    PLATFORM PERFORMANCE
                  </span>
                  <h2>Platform Statistics</h2>
                  <p>
                    A complete snapshot of your platform's activity.
                  </p>
                </div>

                <span className="qfo-section-tag">OVERVIEW</span>
              </div>

              <div className="qfo-stats-grid">
                {STATISTICS.map((stat, index) => (
                  <article
                    className={`qfo-stat-card qfo-${stat.accent}`}
                    key={stat.label}
                    style={{ '--qfo-index': index }}
                  >
                    <div className="qfo-stat-top">
                      <span className="qfo-stat-label">
                        {stat.label}
                      </span>

                      <span className="qfo-stat-icon" aria-hidden="true">
                        {stat.icon}
                      </span>
                    </div>

                    <strong className="qfo-stat-value">
                      {formatNumber(
                        getStatisticValue(dashboard, stat.fields)
                      )}
                    </strong>

                    <span className="qfo-stat-description">
                      {stat.description}
                    </span>

                    <div className="qfo-stat-line" />
                  </article>
                ))}
              </div>
            </section>

            {/* TOP USERS */}
            <section className="qfo-section">
              <div className="qfo-section-heading">
                <div>
                  <span className="qfo-section-kicker">
                    COMMUNITY LEADERS
                  </span>
                  <h2>Leading Players 🏆</h2>
                  <p>
                    Top users returned by your admin dashboard endpoint.
                  </p>
                </div>

                <Link className="qfo-section-action" to="/admin/users">
                  Manage Users <span>→</span>
                </Link>
              </div>

              {Array.isArray(dashboard.topUsers) &&
              dashboard.topUsers.length > 0 ? (
                <div className="qfo-users-grid">
                  {dashboard.topUsers.map((user, index) => (
                    <article
                      className="qfo-user-card"
                      key={getUserId(user, index)}
                      style={{ '--qfo-index': index }}
                    >
                      <div className="qfo-user-top">
                        <span
                          className={`qfo-rank-badge qfo-rank-${index + 1}`}
                        >
                          #{index + 1}
                        </span>

                        <span className="qfo-level-badge">
                          LEVEL {formatNumber(user?.level ?? 1)}
                        </span>
                      </div>

                      <div className="qfo-user-identity">
                        <div className="qfo-user-avatar">
                          {String(getUserName(user)).charAt(0).toUpperCase()}
                        </div>

                        <div className="qfo-user-name-wrap">
                          <h3>{getUserName(user)}</h3>
                          <span>QuestForge Player</span>
                        </div>
                      </div>

                      <div className="qfo-user-divider" />

                      <div className="qfo-user-stats">
                        <div>
                          <span>Experience</span>
                          <strong>
                            {formatNumber(user?.xp ?? 0)} XP
                          </strong>
                        </div>

                        <div>
                          <span>Completed</span>
                          <strong>
                            {formatNumber(
                              user?.challengesCompleted ??
                              user?.completedChallenges ??
                              0
                            )}
                          </strong>
                        </div>

                        <div>
                          <span>Current Streak</span>
                          <strong>
                            {formatNumber(user?.currentStreak ?? 0)} days
                          </strong>
                        </div>
                      </div>
                    </article>
                  ))}
                </div>
              ) : (
                <div className="qfo-empty-state">
                  <span>🏆</span>
                  <h3>No top users available</h3>
                  <p>
                    The backend did not return any top-user data.
                  </p>
                </div>
              )}
            </section>

            {/* MOST ACTIVE CHALLENGES */}
            <section className="qfo-section">
              <div className="qfo-section-heading">
                <div>
                  <span className="qfo-section-kicker">
                    CHALLENGE PERFORMANCE
                  </span>
                  <h2>Most Active Challenges ⚔️</h2>
                  <p>
                    Challenges with the highest activity according to
                    the admin dashboard API.
                  </p>
                </div>

                <Link
                  className="qfo-section-action"
                  to="/admin/challenge-overview"
                >
                  View Challenges <span>→</span>
                </Link>
              </div>

              {Array.isArray(dashboard.mostActiveChallenges) &&
              dashboard.mostActiveChallenges.length > 0 ? (
                <div className="qfo-challenges-grid">
                  {dashboard.mostActiveChallenges.map((challenge, index) => {
                    const difficulty =
                      challenge?.difficulty || 'UNKNOWN';

                    return (
                      <article
                        className="qfo-challenge-card"
                        key={getChallengeId(challenge, index)}
                        style={{ '--qfo-index': index }}
                      >
                        <div className="qfo-challenge-top">
                          <span className="qfo-challenge-id">
                            ID #{challenge?.challengeId ?? challenge?.id ?? 'N/A'}
                          </span>

                          <span
                            className={`qfo-difficulty qfo-difficulty-${getDifficultyClass(
                              difficulty
                            )}`}
                          >
                            {difficulty}
                          </span>
                        </div>

                        <div className="qfo-challenge-icon">
                          ⚔️
                        </div>

                        <h3>{getChallengeTitle(challenge)}</h3>

                        <div className="qfo-challenge-divider" />

                        <div className="qfo-challenge-metrics">
                          <div>
                            <span>Total Submissions</span>
                            <strong>
                              {formatNumber(
                                challenge?.totalSubmissions ??
                                challenge?.submissions ??
                                0
                              )}
                            </strong>
                          </div>

                          <div>
                            <span>Participants</span>
                            <strong>
                              {formatNumber(
                                challenge?.uniqueParticipants ??
                                challenge?.participants ??
                                0
                              )}
                            </strong>
                          </div>

                          <div>
                            <span>Average Score</span>
                            <strong>
                              {formatNumber(challenge?.averageScore ?? 0)}%
                            </strong>
                          </div>
                        </div>
                      </article>
                    );
                  })}
                </div>
              ) : (
                <div className="qfo-empty-state">
                  <span>⚔️</span>
                  <h3>No challenge activity</h3>
                  <p>
                    The backend did not return active challenge data.
                  </p>
                </div>
              )}
            </section>

            {/* QUICK ACTIONS */}
            <section className="qfo-section qfo-actions-section">
              <div className="qfo-section-heading">
                <div>
                  <span className="qfo-section-kicker">
                    ADMINISTRATION
                  </span>
                  <h2>Quick Actions</h2>
                  <p>
                    Access your essential platform management tools.
                  </p>
                </div>
              </div>

              <div className="qfo-actions-grid">
                {QUICK_ACTIONS.map((action, index) => (
                  <Link
                    to={action.path}
                    className="qfo-action-card"
                    key={action.path}
                    style={{ '--qfo-index': index }}
                  >
                    <div className="qfo-action-top">
                      <span className="qfo-action-icon">
                        {action.icon}
                      </span>
                      <span className="qfo-action-arrow">↗</span>
                    </div>

                    <h3>{action.title}</h3>
                    <p>{action.description}</p>

                    <span className="qfo-action-link">
                      Open module <span>→</span>
                    </span>
                  </Link>
                ))}

                <Link
                  to="/admin"
                  className="qfo-action-card qfo-main-action"
                >
                  <div className="qfo-action-top">
                    <span className="qfo-action-icon">🛠️</span>
                    <span className="qfo-action-arrow">↗</span>
                  </div>

                  <h3>Full Admin Panel</h3>
                  <p>
                    Return to the main QuestForge administration panel.
                  </p>

                  <span className="qfo-action-link">
                    Open admin panel <span>→</span>
                  </span>
                </Link>
              </div>
            </section>

            {/* FOOTER */}
            <footer className="qfo-footer">
              <span className="qfo-footer-brand">
                <span />
                QuestForge Administration
              </span>

              <span>Platform overview powered by the backend API</span>
            </footer>
          </>
        )}
      </div>
    </main>
  );
}

export default AdminDashboardOverview;
