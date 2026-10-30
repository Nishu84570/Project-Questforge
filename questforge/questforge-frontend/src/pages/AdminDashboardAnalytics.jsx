import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminDashboardAnalytics() {
  const [data, setData] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const loadAnalytics =
    async () => {
      setLoading(true);
      setError('');

      try {
        const {
          response,
          data: result,
        } = await apiRequest(
          '/admin/dashboard',
          {
            method: 'GET',
          }
        );

        if (!response.ok) {
          setError(
            result?.message ||
              'Failed to load admin analytics.'
          );

          return;
        }

        setData(result);
      } catch (err) {
        console.error(
          'Admin analytics error:',
          err
        );

        setError(
          'Unable to connect to QuestForge backend.'
        );
      } finally {
        setLoading(false);
      }
    };

  useEffect(() => {
    loadAnalytics();
  }, []);

  const formatValue =
    (value) => {
      if (
        value === null ||
        value === undefined
      ) {
        return '—';
      }

      if (
        typeof value === 'object'
      ) {
        return JSON.stringify(
          value
        );
      }

      return String(value);
    };

  if (loading) {
    return (
      <div className="page">

        <div className="empty-card">

          <h2>
            Loading Analytics...
          </h2>

          <p>
            Fetching administrator
            analytics from the backend.
          </p>

        </div>

      </div>
    );
  }

  return (
    <div className="page">

      {/* =========================
          HEADER
      ========================= */}

      <div className="challenges-header">

        <div>

          <p className="eyebrow">
            QUESTFORGE ADMIN
          </p>

          <h1>
            Dashboard Analytics 📈
          </h1>

          <p>
            Backend-powered administration
            analytics.
          </p>

        </div>

        <div
          style={{
            display: 'flex',
            gap: '10px',
            flexWrap: 'wrap',
          }}
        >

          <button
            className="secondary-button"
            onClick={loadAnalytics}
          >
            ↻ Refresh
          </button>

          <Link
            className="secondary-button"
            to="/admin"
          >
            ← Admin Dashboard
          </Link>

        </div>

      </div>


      {/* =========================
          ERROR
      ========================= */}

      {error && (
        <div className="empty-card">

          <h2>
            Analytics unavailable
          </h2>

          <p className="error-text">
            {error}
          </p>

          <button
            className="primary-button"
            onClick={loadAnalytics}
          >
            Try Again
          </button>

        </div>
      )}


      {data && (
        <>

          {/* =========================
              KNOWN AGGREGATES
          ========================= */}

          <div className="result-stats">

            <div className="result-stat-card">

              <span>
                Users
              </span>

              <strong>
                {
                  data.totalUsers ??
                  data.userCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Challenges
              </span>

              <strong>
                {
                  data.totalChallenges ??
                  data.challengeCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Questions
              </span>

              <strong>
                {
                  data.totalQuestions ??
                  data.questionCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Submissions
              </span>

              <strong>
                {
                  data.totalSubmissions ??
                  data.submissionCount ??
                  0
                }
              </strong>

            </div>

          </div>


          {/* =========================
              RAW DASHBOARD FIELDS
          ========================= */}

          <div className="admin-section">

            <div className="admin-section-header">

              <div>

                <p className="eyebrow">
                  BACKEND RESPONSE
                </p>

                <h2>
                  Dashboard Data
                </h2>

                <p>
                  Values below are read
                  directly from the
                  `/admin/dashboard`
                  response.
                </p>

              </div>

            </div>

            <div className="challenge-grid">

              {Object.entries(
                data
              ).map(
                ([key, value]) => (

                  <div
                    className="challenge-card"
                    key={key}
                  >

                    <p className="eyebrow">
                      {key}
                    </p>

                    <h3>
                      {formatValue(
                        value
                      )}
                    </h3>

                  </div>

                )
              )}

            </div>

          </div>


          {/* =========================
              NAVIGATION
          ========================= */}

          <div className="admin-section">

            <div className="admin-section-header">

              <div>

                <p className="eyebrow">
                  ADMIN MODULES
                </p>

                <h2>
                  Continue Managing
                </h2>

              </div>

            </div>

            <div
              style={{
                display: 'flex',
                gap: '12px',
                flexWrap: 'wrap',
              }}
            >

              <Link
                className="primary-button"
                to="/admin/users"
              >
                👥 Users
              </Link>

              <Link
                className="primary-button"
                to="/admin/submissions"
              >
                📝 Submissions
              </Link>

              <Link
                className="primary-button"
                to="/admin/challenge-overview"
              >
                ⚔️ Challenges
              </Link>

              <Link
                className="primary-button"
                to="/admin/challenge-analytics"
              >
                📈 Challenge Analytics
              </Link>

            </div>

          </div>

        </>
      )}

    </div>
  );
}

export default AdminDashboardAnalytics;