import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminDashboardOverview() {
  const [dashboard, setDashboard] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const loadDashboard =
    async () => {
      setLoading(true);
      setError('');

      try {
        const {
          response,
          data,
        } = await apiRequest(
          '/admin/dashboard',
          {
            method: 'GET',
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to load admin dashboard.'
          );

          return;
        }

        setDashboard(data);
      } catch (err) {
        console.error(
          'Admin dashboard overview error:',
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
    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div className="page">

        <div className="empty-card">

          <h2>
            Loading Admin Overview...
          </h2>

          <p>
            Fetching live dashboard
            statistics from QuestForge.
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
            Dashboard Overview 📊
          </h1>

          <p>
            Live platform statistics
            powered by the admin dashboard
            API.
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
            onClick={loadDashboard}
            disabled={loading}
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
        <div
          className="empty-card"
          style={{
            marginBottom: '20px',
          }}
        >

          <h2>
            Unable to load dashboard
          </h2>

          <p className="error-text">
            {error}
          </p>

          <button
            className="primary-button"
            onClick={loadDashboard}
          >
            Try Again
          </button>

        </div>
      )}


      {dashboard && (
        <>

          {/* =========================
              MAIN STATISTICS
          ========================= */}

          <div className="result-stats">

            <div className="result-stat-card">

              <span>
                Total Users
              </span>

              <strong>
                {
                  dashboard.totalUsers ??
                  dashboard.userCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Total Challenges
              </span>

              <strong>
                {
                  dashboard.totalChallenges ??
                  dashboard.challengeCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Total Submissions
              </span>

              <strong>
                {
                  dashboard.totalSubmissions ??
                  dashboard.submissionCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Pending Submissions
              </span>

              <strong>
                {
                  dashboard.pendingSubmissions ??
                  dashboard.pendingCount ??
                  0
                }
              </strong>

            </div>

          </div>


          {/* =========================
              ADDITIONAL STATISTICS
          ========================= */}

          <div className="result-stats">

            <div className="result-stat-card">

              <span>
                Total Questions
              </span>

              <strong>
                {
                  dashboard.totalQuestions ??
                  dashboard.questionCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Total Attempts
              </span>

              <strong>
                {
                  dashboard.totalAttempts ??
                  dashboard.attemptCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Approved
              </span>

              <strong>
                {
                  dashboard.approvedSubmissions ??
                  dashboard.approvedCount ??
                  0
                }
              </strong>

            </div>

            <div className="result-stat-card">

              <span>
                Rejected
              </span>

              <strong>
                {
                  dashboard.rejectedSubmissions ??
                  dashboard.rejectedCount ??
                  0
                }
              </strong>

            </div>

          </div>


          {/* =========================
              TOP USERS
          ========================= */}

          <div className="admin-section">

            <div className="admin-section-header">

              <div>

                <p className="eyebrow">
                  TOP USERS
                </p>

                <h2>
                  Leading Players 🏆
                </h2>

                <p>
                  Top users returned by the
                  admin dashboard endpoint.
                </p>

              </div>

              <Link
                className="secondary-button"
                to="/admin/users"
              >
                Manage Users
              </Link>

            </div>

            {Array.isArray(
              dashboard.topUsers
            ) &&
            dashboard.topUsers.length > 0 ? (

              <div className="challenge-grid">

                {dashboard.topUsers.map(
                  (user, index) => (

                    <div
                      className="challenge-card"
                      key={
                        user.userId ??
                        user.id ??
                        index
                      }
                    >

                      <div className="challenge-top">

                        <span className="challenge-id">
                          #{index + 1}
                        </span>

                        <span className="difficulty">
                          LEVEL{' '}
                          {
                            user.level ??
                            1
                          }
                        </span>

                      </div>

                      <h2>
                        {
                          user.name ||
                          user.userName ||
                          `User #${
                            user.userId ??
                            user.id ??
                            'N/A'
                          }`
                        }
                      </h2>

                      <p className="description">

                        <strong>
                          XP:
                        </strong>{' '}

                        {
                          user.xp ??
                          0
                        }

                      </p>

                      <p>

                        <strong>
                          Completed:
                        </strong>{' '}

                        {
                          user.challengesCompleted ??
                          user.completedChallenges ??
                          0
                        }

                      </p>

                      <p>

                        <strong>
                          Streak:
                        </strong>{' '}

                        {
                          user.currentStreak ??
                          0
                        }

                        {' days'}

                      </p>

                    </div>

                  )
                )}

              </div>

            ) : (

              <div className="empty-card">

                <h3>
                  No top users available
                </h3>

                <p>
                  The backend did not return
                  top-user data.
                </p>

              </div>

            )}

          </div>


          {/* =========================
              MOST ACTIVE CHALLENGES
          ========================= */}

          <div className="admin-section">

            <div className="admin-section-header">

              <div>

                <p className="eyebrow">
                  CHALLENGE ACTIVITY
                </p>

                <h2>
                  Most Active Challenges ⚔️
                </h2>

                <p>
                  Challenges with the highest
                  activity according to the
                  admin dashboard API.
                </p>

              </div>

              <Link
                className="secondary-button"
                to="/admin/challenge-overview"
              >
                Challenge Overview
              </Link>

            </div>

            {Array.isArray(
              dashboard.mostActiveChallenges
            ) &&
            dashboard
              .mostActiveChallenges
              .length > 0 ? (

              <div className="challenge-grid">

                {dashboard
                  .mostActiveChallenges
                  .map(
                    (
                      challenge,
                      index
                    ) => (

                      <div
                        className="challenge-card"
                        key={
                          challenge.challengeId ??
                          challenge.id ??
                          index
                        }
                      >

                        <div className="challenge-top">

                          <span className="challenge-id">
                            #
                            {
                              challenge.challengeId ??
                              challenge.id ??
                              'N/A'
                            }
                          </span>

                          <span className="difficulty">
                            {
                              challenge.difficulty ||
                              'UNKNOWN'
                            }
                          </span>

                        </div>

                        <h2>
                          {
                            challenge.title ||
                            challenge.challengeTitle ||
                            'Untitled Challenge'
                          }
                        </h2>

                        <p className="description">

                          <strong>
                            Submissions:
                          </strong>{' '}

                          {
                            challenge.totalSubmissions ??
                            challenge.submissions ??
                            0
                          }

                        </p>

                        <p>

                          <strong>
                            Participants:
                          </strong>{' '}

                          {
                            challenge.uniqueParticipants ??
                            challenge.participants ??
                            0
                          }

                        </p>

                        <p>

                          <strong>
                            Average Score:
                          </strong>{' '}

                          {
                            challenge.averageScore ??
                            0
                          }%

                        </p>

                      </div>

                    )
                  )}

              </div>

            ) : (

              <div className="empty-card">

                <h3>
                  No challenge activity
                </h3>

                <p>
                  The backend did not return
                  active challenge data.
                </p>

              </div>

            )}

          </div>


          {/* =========================
              QUICK ACTIONS
          ========================= */}

          <div className="admin-section">

            <div className="admin-section-header">

              <div>

                <p className="eyebrow">
                  ADMIN TOOLS
                </p>

                <h2>
                  Quick Actions
                </h2>

                <p>
                  Jump directly to the
                  different administration
                  modules.
                </p>

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
                📊 Challenges
              </Link>

              <Link
                className="primary-button"
                to="/admin/challenge-analytics"
              >
                📈 Analytics
              </Link>

              <Link
                className="secondary-button"
                to="/admin"
              >
                🛠️ Full Admin Panel
              </Link>

            </div>

          </div>

        </>
      )}

    </div>
  );
}

export default AdminDashboardOverview;