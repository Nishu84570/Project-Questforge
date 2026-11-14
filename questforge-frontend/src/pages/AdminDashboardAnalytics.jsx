
import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminDashboardAnalytics.css';

const METRICS = [
  {
    key: 'users',
    label: 'Total Users',
    fields: ['totalUsers', 'userCount'],
    icon: '👥',
    description: 'Registered platform users',
  },
  {
    key: 'challenges',
    label: 'Total Challenges',
    fields: ['totalChallenges', 'challengeCount'],
    icon: '⚔️',
    description: 'Available coding challenges',
  },
  {
    key: 'questions',
    label: 'Total Questions',
    fields: ['totalQuestions', 'questionCount'],
    icon: '❓',
    description: 'Questions in the platform',
  },
  {
    key: 'submissions',
    label: 'Total Submissions',
    fields: ['totalSubmissions', 'submissionCount'],
    icon: '📝',
    description: 'Submissions received',
  },
];

const MODULES = [
  {
    title: 'Manage Users',
    description: 'Review users and manage platform accounts.',
    icon: '👥',
    path: '/admin/users',
  },
  {
    title: 'Review Submissions',
    description: 'Inspect coding submissions and their status.',
    icon: '📝',
    path: '/admin/submissions',
  },
  {
    title: 'Challenge Overview',
    description: 'Manage and review coding challenges.',
    icon: '⚔️',
    path: '/admin/challenge-overview',
  },
  {
    title: 'Challenge Analytics',
    description: 'Explore challenge-related analytics.',
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

function formatValue(value) {
  if (value === null || value === undefined) {
    return 'Not available';
  }

  if (typeof value === 'boolean') {
    return value ? 'Yes' : 'No';
  }

  if (Array.isArray(value)) {
    if (value.length === 0) return 'No items';

    try {
      return JSON.stringify(value, null, 2);
    } catch {
      return 'Unable to display this value';
    }
  }

  if (typeof value === 'object') {
    try {
      return JSON.stringify(value, null, 2);
    } catch {
      return 'Unable to display this value';
    }
  }

  if (typeof value === 'number' && Number.isFinite(value)) {
    return value.toLocaleString();
  }

  return String(value);
}

function formatLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/[_-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
    .replace(/\b\w/g, (character) => character.toUpperCase());
}

function getMetricValue(data, fields) {
  for (const field of fields) {
    const value = data?.[field];

    if (value !== null && value !== undefined) {
      return value;
    }
  }

  return 0;
}

function AdminDashboardAnalytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [lastUpdated, setLastUpdated] = useState(null);

  const loadAnalytics = useCallback(async ({ silent = false } = {}) => {
    if (silent) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError('');

    try {
      const { response, data: result } = await apiRequest(
        '/admin/dashboard',
        { method: 'GET' }
      );

      if (!response.ok) {
        setError(
          result?.message || 'Failed to load administrator analytics.'
        );
        return;
      }

      if (!result || typeof result !== 'object' || Array.isArray(result)) {
        setError('The backend returned an invalid analytics response.');
        return;
      }

      setData(result);
      setLastUpdated(new Date());
    } catch (err) {
      console.error('Admin analytics error:', err);
      setError('Unable to connect to the QuestForge backend.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadAnalytics();

    // The request is handled by the component's loading state.
    // No API cancellation is assumed because apiRequest's implementation
    // has not been provided here.
  }, [loadAnalytics]);

  if (loading) {
    return (
      <main className="page qfa-page">
        <section className="qfa-loading-card">
          <div className="qfa-loader" aria-hidden="true" />
          <h2>Preparing your analytics</h2>
          <p>Fetching the latest QuestForge dashboard data...</p>
          <div className="qfa-loading-track">
            <span />
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="page qfa-page">
      <div className="qfa-shell">
        {/* HEADER */}
        <header className="qfa-header">
          <div className="qfa-header-content">
            <div className="qfa-breadcrumb">
              <span className="qfa-breadcrumb-dot" />
              QUESTFORGE / ADMINISTRATION
            </div>

            <div className="qfa-heading-row">
              <div>
                <h1>Dashboard Analytics</h1>
                <p>
                  Your platform overview, performance indicators and
                  administration insights.
                </p>
              </div>

              <div className="qfa-header-icon" aria-hidden="true">
                📊
              </div>
            </div>

            <div className="qfa-header-meta">
              <span className="qfa-live-indicator">
                <span />
                Backend connected
              </span>

              {lastUpdated && (
                <span className="qfa-updated">
                  Last updated: {lastUpdated.toLocaleTimeString()}
                </span>
              )}
            </div>
          </div>

          <div className="qfa-header-actions">
            <button
              type="button"
              className="qfa-refresh-button"
              onClick={() => loadAnalytics({ silent: true })}
              disabled={refreshing}
            >
              <span className={refreshing ? 'qfa-refresh-icon spinning' : 'qfa-refresh-icon'}>
                ↻
              </span>
              {refreshing ? 'Refreshing...' : 'Refresh data'}
            </button>

            <Link className="qfa-back-button" to="/admin">
              <span>←</span>
              Admin Dashboard
            </Link>
          </div>
        </header>

        {/* ERROR STATE */}
        {error && (
          <section className="qfa-error-panel" role="alert">
            <div className="qfa-error-icon">!</div>

            <div className="qfa-error-content">
              <h2>Analytics unavailable</h2>
              <p>{error}</p>
            </div>

            <button
              type="button"
              className="qfa-retry-button"
              onClick={() => loadAnalytics({ silent: Boolean(data) })}
              disabled={refreshing}
            >
              Try again
            </button>
          </section>
        )}

        {data && (
          <>
            {/* METRICS */}
            <section className="qfa-section">
              <div className="qfa-section-heading">
                <div>
                  <span className="qfa-section-kicker">AT A GLANCE</span>
                  <h2>Platform overview</h2>
                  <p>Key figures returned by your admin dashboard API.</p>
                </div>

                <span className="qfa-section-tag">LIVE DATA</span>
              </div>

              <div className="qfa-metrics-grid">
                {METRICS.map((metric, index) => (
                  <article
                    className="qfa-metric-card"
                    key={metric.key}
                    style={{ '--qfa-card-index': index }}
                  >
                    <div className="qfa-metric-top">
                      <span className="qfa-metric-label">
                        {metric.label}
                      </span>
                      <span className="qfa-metric-icon" aria-hidden="true">
                        {metric.icon}
                      </span>
                    </div>

                    <strong className="qfa-metric-value">
                      {formatNumber(getMetricValue(data, metric.fields))}
                    </strong>

                    <span className="qfa-metric-description">
                      {metric.description}
                    </span>

                    <div className="qfa-metric-bottom-line" />
                  </article>
                ))}
              </div>
            </section>

            {/* BACKEND RESPONSE */}
            <section className="qfa-section">
              <div className="qfa-section-heading">
                <div>
                  <span className="qfa-section-kicker">API INSIGHTS</span>
                  <h2>Dashboard data</h2>
                  <p>
                    Additional fields received directly from{' '}
                    <code>/admin/dashboard</code>.
                  </p>
                </div>

                <span className="qfa-field-count">
                  {Object.keys(data).length} fields
                </span>
              </div>

              {Object.keys(data).length > 0 ? (
                <div className="qfa-fields-grid">
                  {Object.entries(data).map(([key, value]) => (
                    <article className="qfa-field-card" key={key}>
                      <div className="qfa-field-heading">
                        <span className="qfa-field-indicator" />
                        <h3>{formatLabel(key)}</h3>
                      </div>

                      <pre className="qfa-field-value">
                        {formatValue(value)}
                      </pre>
                    </article>
                  ))}
                </div>
              ) : (
                <div className="qfa-empty-fields">
                  <span>📭</span>
                  <h3>No additional fields</h3>
                  <p>The API returned an empty dashboard object.</p>
                </div>
              )}
            </section>

            {/* ADMIN MODULES */}
            <section className="qfa-section qfa-modules-section">
              <div className="qfa-section-heading">
                <div>
                  <span className="qfa-section-kicker">QUICK ACCESS</span>
                  <h2>Administration modules</h2>
                  <p>Jump directly into the tools you use to manage QuestForge.</p>
                </div>
              </div>

              <div className="qfa-modules-grid">
                {MODULES.map((module, index) => (
                  <Link
                    to={module.path}
                    className="qfa-module-card"
                    key={module.path}
                    style={{ '--qfa-card-index': index }}
                  >
                    <div className="qfa-module-top">
                      <span className="qfa-module-icon" aria-hidden="true">
                        {module.icon}
                      </span>
                      <span className="qfa-module-arrow">↗</span>
                    </div>

                    <h3>{module.title}</h3>
                    <p>{module.description}</p>

                    <span className="qfa-module-link">
                      Open module <span>→</span>
                    </span>
                  </Link>
                ))}
              </div>
            </section>

            {/* FOOTER */}
            <footer className="qfa-footer">
              <span>
                <span className="qfa-footer-dot" />
                QuestForge Administration
              </span>
              <span>Analytics powered by your backend API</span>
            </footer>
          </>
        )}
      </div>
    </main>
  );
}

export default AdminDashboardAnalytics;
