import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import { apiRequest } from '../services/api';

function ChallengeAnalytics() {
  const { challengeId } = useParams();

  const [analytics, setAnalytics] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  // =========================
  // LOAD ANALYTICS
  // =========================

  const loadAnalytics = async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/admin/challenges/${challengeId}/analytics`,
          {
            method: 'GET',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load challenge analytics.'
        );
        return;
      }

      setAnalytics(data);
    } catch (error) {
      console.error(
        'Challenge analytics error:',
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
    if (challengeId) {
      loadAnalytics();
    }
  }, [challengeId]);

  // =========================
  // FORMAT NUMBER
  // =========================

  const formatNumber = (
    value,
    digits = 0
  ) => {
    const number = Number(value);

    if (!Number.isFinite(number)) {
      return '0';
    }

    return number.toFixed(digits);
  };

  // =========================
  // PERCENTAGE
  // =========================

  const formatPercentage = (value) => {
    const number = Number(value);

    if (!Number.isFinite(number)) {
      return '0%';
    }

    return `${number.toFixed(1)}%`;
  };

  // =========================
  // LOADING
  // =========================

  if (loading) {
    return (
      <div className="page admin-page">

        <div className="empty-card">

          <h2>
            Loading challenge analytics...
          </h2>

          <p>
            QuestForge is preparing the
            analytics for this challenge.
          </p>

        </div>

      </div>
    );
  }

  // =========================
  // ERROR
  // =========================

  if (error) {
    return (
      <div className="page admin-page">

        <div className="page-header">

          <div>
            <span className="eyebrow">
              ADMIN
            </span>

            <h1>
              Challenge Analytics
            </h1>
          </div>

          <Link
            to="/admin"
            className="secondary-button"
          >
            ← Admin Dashboard
          </Link>

        </div>

        <div className="error-message">
          {error}
        </div>

        <div className="empty-card">

          <button
            className="primary-button"
            onClick={loadAnalytics}
          >
            Try Again
          </button>

        </div>

      </div>
    );
  }

  if (!analytics) {
    return (
      <div className="page admin-page">

        <div className="empty-card">

          <h2>
            No analytics found
          </h2>

          <Link
            to="/admin"
            className="secondary-button"
          >
            ← Admin Dashboard
          </Link>

        </div>

      </div>
    );
  }

  // =========================
  // DATA
  // =========================

  const totalSubmissions =
    Number(
      analytics.totalSubmissions || 0
    );

  const firstSubmissions =
    Number(
      analytics.firstSubmissions || 0
    );

  const averageScore =
    Number(
      analytics.averageScore || 0
    );

  const completionRate =
    Number(
      analytics.completionRate || 0
    );

  // =========================
  // RENDER
  // =========================

  return (
    <div className="page admin-page">

      {/* =========================
          HEADER
      ========================= */}

      <div className="page-header">

        <div>

          <span className="eyebrow">
            ADMIN ANALYTICS
          </span>

          <h1>
            {analytics.title ||
              `Challenge #${challengeId}`}
          </h1>

          <p>
            Detailed performance analytics
            for this QuestForge challenge.
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
            onClick={loadAnalytics}
          >
            ↻ Refresh
          </button>

        </div>

      </div>

      {/* =========================
          OVERVIEW STATS
      ========================= */}

      <div className="result-stats">

        <div className="result-stat-card">

          <span className="result-stat-label">
            Total Submissions
          </span>

          <strong>
            {totalSubmissions}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            First Submissions
          </span>

          <strong>
            {firstSubmissions}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Average Score
          </span>

          <strong>
            {formatNumber(
              averageScore,
              2
            )}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Completion Rate
          </span>

          <strong>
            {formatPercentage(
              completionRate
            )}
          </strong>

        </div>

      </div>

      {/* =========================
          ANALYTICS DETAILS
      ========================= */}

      <div className="admin-form-card">

        <div className="section-heading">

          <div>

            <span className="eyebrow">
              PERFORMANCE
            </span>

            <h2>
              Challenge Performance
            </h2>

          </div>

        </div>

        <div
          className="result-stats"
          style={{
            marginTop: '20px',
          }}
        >

          <div className="result-stat-card">

            <span className="result-stat-label">
              Challenge ID
            </span>

            <strong>
              {analytics.challengeId ??
                challengeId}
            </strong>

          </div>

          <div className="result-stat-card">

            <span className="result-stat-label">
              Total Attempts
            </span>

            <strong>
              {totalSubmissions}
            </strong>

          </div>

          <div className="result-stat-card">

            <span className="result-stat-label">
              First Attempts
            </span>

            <strong>
              {firstSubmissions}
            </strong>

          </div>

          <div className="result-stat-card">

            <span className="result-stat-label">
              Completion
            </span>

            <strong>
              {formatPercentage(
                completionRate
              )}
            </strong>

          </div>

        </div>

      </div>

      {/* =========================
          SCORE INSIGHT
      ========================= */}

      <div className="admin-form-card">

        <span className="eyebrow">
          SCORE INSIGHT
        </span>

        <h2>
          Average Score
        </h2>

        <p>
          The current average score for
          this challenge is{' '}
          <strong>
            {formatNumber(
              averageScore,
              2
            )}
          </strong>
          .
        </p>

        <div
          style={{
            marginTop: '20px',
            padding: '18px',
            borderRadius: '14px',
            background:
              'rgba(255,255,255,0.04)',
          }}
        >

          <strong>
            {formatPercentage(
              completionRate
            )}
          </strong>

          <span
            style={{
              marginLeft: '10px',
            }}
          >
            completion rate
          </span>

        </div>

      </div>

      {/* =========================
          NAVIGATION
      ========================= */}

      <div
        style={{
          display: 'flex',
          gap: '12px',
          flexWrap: 'wrap',
        }}
      >

        <Link
          to="/admin"
          className="primary-button"
        >
          Admin Dashboard
        </Link>

        <Link
          to="/admin/challenge-overview"
          className="secondary-button"
        >
          Challenge Overview
        </Link>

      </div>

    </div>
  );
}

export default ChallengeAnalytics;