
import React, { useEffect, useState, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/ChallengeAnalytics.css';

function ChallengeAnalytics() {
  const { challengeId } = useParams();

  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadAnalytics = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/admin/challenges/${challengeId}/analytics`,
        { method: 'GET' }
      );

      if (!response.ok) {
        setError(data?.message || 'Failed to load challenge analytics.');
        return;
      }

      setAnalytics(data);
    } catch (err) {
      console.error('Challenge analytics error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setLoading(false);
    }
  }, [challengeId]);

  useEffect(() => {
    if (challengeId) {
      loadAnalytics();
    } else {
      setError('Challenge analytics could not be identified.');
      setLoading(false);
    }
  }, [challengeId, loadAnalytics]);

  const formatNumber = (value, digits = 0) => {
    const number = Number(value);
    return Number.isFinite(number) ? number.toFixed(digits) : '0';
  };

  const formatPercentage = (value) => {
    const number = Number(value);
    return Number.isFinite(number) ? `${number.toFixed(1)}%` : '0%';
  };

  if (loading) {
    return (
      <div className="page admin-page challenge-analytics-page">
        <div className="ca-loading-card">
          <div className="ca-loader">
            <span />
            <span />
            <span />
          </div>

          <h2>Preparing Analytics</h2>
          <p>Gathering challenge performance insights...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page admin-page challenge-analytics-page">
        <div className="ca-page-header">
          <div>
            <span className="ca-eyebrow">QUESTFORGE / ADMIN</span>
            <h1>Challenge Analytics</h1>
            <p>Monitor challenge performance and engagement.</p>
          </div>

          <Link to="/admin" className="ca-button ca-button-secondary">
            <span>←</span> Dashboard
          </Link>
        </div>

        <div className="ca-error-card">
          <div className="ca-error-icon">!</div>
          <h2>Analytics unavailable</h2>
          <p>{error}</p>

          <button
            type="button"
            className="ca-button ca-button-primary"
            onClick={loadAnalytics}
          >
            <span>↻</span> Try Again
          </button>
        </div>
      </div>
    );
  }

  if (!analytics) {
    return (
      <div className="page admin-page challenge-analytics-page">
        <div className="ca-empty-card">
          <div className="ca-empty-icon">⌁</div>
          <h2>No Analytics Found</h2>
          <p>There is no performance data available for this challenge yet.</p>

          <Link to="/admin" className="ca-button ca-button-primary">
            Back to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  const totalSubmissions = Number(analytics.totalSubmissions) || 0;
  const firstSubmissions = Number(analytics.firstSubmissions) || 0;
  const averageScore = Number(analytics.averageScore) || 0;
  const completionRate = Number(analytics.completionRate) || 0;

  const safeCompletionRate = Math.max(
    0,
    Math.min(100, completionRate)
  );

  return (
    <div className="page admin-page challenge-analytics-page">

      {/* PAGE HEADER */}
      <header className="ca-page-header">
        <div className="ca-header-content">
          <div className="ca-breadcrumb">
            <Link to="/admin">Admin</Link>
            <span>/</span>
            <span>Analytics</span>
          </div>

          <span className="ca-eyebrow">
            <span className="ca-eyebrow-dot" />
            PERFORMANCE CENTER
          </span>

          <h1>
            Challenge <span>Analytics</span>
          </h1>

          <p>
            {analytics.title
              ? `Performance overview for ${analytics.title}.`
              : 'Detailed performance insights for your coding challenge.'}
          </p>
        </div>

        <div className="ca-header-actions">
          <Link to="/admin" className="ca-button ca-button-secondary">
            <span>←</span> Dashboard
          </Link>

          <button
            type="button"
            className="ca-button ca-button-refresh"
            onClick={loadAnalytics}
          >
            <span>↻</span> Refresh
          </button>
        </div>
      </header>

      {/* CHALLENGE OVERVIEW */}
      <section className="ca-overview-section">
        <div className="ca-section-heading">
          <div>
            <span className="ca-eyebrow">AT A GLANCE</span>
            <h2>Performance Overview</h2>
            <p>Key metrics from challenge submissions.</p>
          </div>

          <div className="ca-live-indicator">
            <span />
            Analytics Loaded
          </div>
        </div>

        <div className="ca-stats-grid">

          <article className="ca-stat-card ca-stat-violet">
            <div className="ca-stat-top">
              <div className="ca-stat-icon">▤</div>
              <span className="ca-stat-caption">ACTIVITY</span>
            </div>

            <div className="ca-stat-content">
              <span className="ca-stat-label">Total Submissions</span>
              <strong>{formatNumber(totalSubmissions)}</strong>
              <span className="ca-stat-description">
                All recorded attempts
              </span>
            </div>

            <div className="ca-stat-decoration" />
          </article>

          <article className="ca-stat-card ca-stat-blue">
            <div className="ca-stat-top">
              <div className="ca-stat-icon">✦</div>
              <span className="ca-stat-caption">FIRST TRY</span>
            </div>

            <div className="ca-stat-content">
              <span className="ca-stat-label">First Submissions</span>
              <strong>{formatNumber(firstSubmissions)}</strong>
              <span className="ca-stat-description">
                Initial attempts recorded
              </span>
            </div>

            <div className="ca-stat-decoration" />
          </article>

          <article className="ca-stat-card ca-stat-cyan">
            <div className="ca-stat-top">
              <div className="ca-stat-icon">◎</div>
              <span className="ca-stat-caption">QUALITY</span>
            </div>

            <div className="ca-stat-content">
              <span className="ca-stat-label">Average Score</span>
              <strong>{formatNumber(averageScore, 2)}</strong>
              <span className="ca-stat-description">
                Average performance score
              </span>
            </div>

            <div className="ca-stat-decoration" />
          </article>

          <article className="ca-stat-card ca-stat-green">
            <div className="ca-stat-top">
              <div className="ca-stat-icon">✓</div>
              <span className="ca-stat-caption">COMPLETION</span>
            </div>

            <div className="ca-stat-content">
              <span className="ca-stat-label">Completion Rate</span>
              <strong>{formatPercentage(completionRate)}</strong>
              <span className="ca-stat-description">
                Reported completion rate
              </span>
            </div>

            <div className="ca-stat-decoration" />
          </article>

        </div>
      </section>

      {/* PERFORMANCE DETAILS */}
      <section className="ca-performance-card">
        <div className="ca-section-heading">
          <div>
            <span className="ca-eyebrow">DETAILED INSIGHTS</span>
            <h2>Challenge Performance</h2>
            <p>A closer look at challenge participation.</p>
          </div>

          <div className="ca-performance-icon">◈</div>
        </div>

        <div className="ca-performance-grid">
          <div className="ca-performance-item">
            <span className="ca-performance-label">Total Attempts</span>
            <strong>{formatNumber(totalSubmissions)}</strong>
            <span className="ca-performance-hint">Submission records</span>
          </div>

          <div className="ca-performance-item">
            <span className="ca-performance-label">First Attempts</span>
            <strong>{formatNumber(firstSubmissions)}</strong>
            <span className="ca-performance-hint">Initial submissions</span>
          </div>

          <div className="ca-performance-item">
            <span className="ca-performance-label">Average Score</span>
            <strong>{formatNumber(averageScore, 2)}</strong>
            <span className="ca-performance-hint">Average score value</span>
          </div>
        </div>
      </section>

      {/* COMPLETION INSIGHT */}
      <section className="ca-insight-card">
        <div className="ca-insight-main">
          <div className="ca-insight-icon">◉</div>

          <div>
            <span className="ca-eyebrow">COMPLETION INSIGHT</span>
            <h2>Challenge Completion</h2>
            <p>
              Current reported completion rate for this challenge.
            </p>
          </div>
        </div>

        <div className="ca-completion-value">
          <strong>{formatPercentage(completionRate)}</strong>
          <span>Completion Rate</span>
        </div>

        <div className="ca-progress-track">
          <div
            className="ca-progress-fill"
            style={{ width: `${safeCompletionRate}%` }}
          />
        </div>

        <div className="ca-progress-footer">
          <span>Completion progress</span>
          <strong>{formatPercentage(completionRate)}</strong>
        </div>
      </section>

      {/* NAVIGATION */}
      <footer className="ca-navigation">
        <div>
          <span className="ca-eyebrow">KEEP EXPLORING</span>
          <h2>Manage your challenges</h2>
          <p>Continue managing your QuestForge platform.</p>
        </div>

        <div className="ca-navigation-actions">
          <Link to="/admin/challenge-overview" className="ca-button ca-button-secondary">
            Challenge Overview <span>→</span>
          </Link>

          <Link to="/admin" className="ca-button ca-button-primary">
            Admin Dashboard <span>↗</span>
          </Link>
        </div>
      </footer>

    </div>
  );
}

export default ChallengeAnalytics;
