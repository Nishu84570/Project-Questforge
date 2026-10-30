import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminChallengeOverview() {
  const [challenges, setChallenges] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const [search, setSearch] =
    useState('');

  const [difficultyFilter, setDifficultyFilter] =
    useState('ALL');

  const [categoryFilter, setCategoryFilter] =
    useState('ALL');

  // ==========================================
  // LOAD CHALLENGE OVERVIEW
  // ==========================================

  const loadChallenges = async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          '/admin/challenges',
          {
            method: 'GET',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load challenge overview.'
        );

        return;
      }

      setChallenges(
        Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data?.challenges)
          ? data.challenges
          : []
      );
    } catch (error) {
      console.error(
        'Admin challenge overview error:',
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
    loadChallenges();
  }, []);

  // ==========================================
  // UNIQUE DIFFICULTIES
  // ==========================================

  const difficulties = useMemo(() => {
    return [
      ...new Set(
        challenges
          .map(
            (challenge) =>
              challenge.difficulty
          )
          .filter(Boolean)
      ),
    ];
  }, [challenges]);

  // ==========================================
  // UNIQUE CATEGORIES
  // ==========================================

  const categories = useMemo(() => {
    return [
      ...new Set(
        challenges
          .map(
            (challenge) =>
              challenge.category
          )
          .filter(Boolean)
      ),
    ];
  }, [challenges]);

  // ==========================================
  // FILTER
  // ==========================================

  const filteredChallenges =
    useMemo(() => {
      const normalizedSearch =
        search.trim().toLowerCase();

      return challenges.filter(
        (challenge) => {
          const difficulty =
            String(
              challenge.difficulty || ''
            );

          const category =
            String(
              challenge.category || ''
            );

          if (
            difficultyFilter !==
              'ALL' &&
            difficulty !==
              difficultyFilter
          ) {
            return false;
          }

          if (
            categoryFilter !==
              'ALL' &&
            category !==
              categoryFilter
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
            .filter(
              (value) =>
                value !== null &&
                value !== undefined
            )
            .join(' ')
            .toLowerCase();

          return searchableText.includes(
            normalizedSearch
          );
        }
      );
    }, [
      challenges,
      search,
      difficultyFilter,
      categoryFilter,
    ]);

  // ==========================================
  // FORMAT NUMBER
  // ==========================================

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

  // ==========================================
  // FORMAT PERCENTAGE
  // ==========================================

  const formatPercentage = (value) => {
    const number = Number(value);

    if (!Number.isFinite(number)) {
      return '0%';
    }

    return `${number.toFixed(1)}%`;
  };

  // ==========================================
  // CLEAR FILTERS
  // ==========================================

  const clearFilters = () => {
    setSearch('');
    setDifficultyFilter('ALL');
    setCategoryFilter('ALL');
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="page admin-page">

      {/* ======================================
          HEADER
      ====================================== */}

      <div className="page-header">

        <div>

          <span className="eyebrow">
            ADMIN
          </span>

          <h1>
            Challenge Overview
          </h1>

          <p>
            Monitor challenge participation,
            scores and completion rates.
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
            onClick={
              loadChallenges
            }
            disabled={loading}
          >
            {loading
              ? 'Loading...'
              : '↻ Refresh'}
          </button>

        </div>

      </div>

      {/* ======================================
          SUMMARY
      ====================================== */}

      <div className="result-stats">

        <div className="result-stat-card">

          <span className="result-stat-label">
            Challenges
          </span>

          <strong>
            {challenges.length}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Showing
          </span>

          <strong>
            {filteredChallenges.length}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Submissions
          </span>

          <strong>
            {challenges.reduce(
              (total, challenge) =>
                total +
                Number(
                  challenge.totalSubmissions ||
                    0
                ),
              0
            )}
          </strong>

        </div>

        <div className="result-stat-card">

          <span className="result-stat-label">
            Participants
          </span>

          <strong>
            {challenges.reduce(
              (total, challenge) =>
                total +
                Number(
                  challenge.uniqueParticipants ||
                    0
                ),
              0
            )}
          </strong>

        </div>

      </div>

      {/* ======================================
          FILTERS
      ====================================== */}

      <div className="admin-form-card">

        <div
          style={{
            display: 'flex',
            gap: '12px',
            flexWrap: 'wrap',
            alignItems: 'center',
          }}
        >

          <input
            type="text"
            placeholder="Search challenge..."
            value={search}
            onChange={(event) =>
              setSearch(
                event.target.value
              )
            }
            style={{
              flex: 1,
              minWidth: '220px',
            }}
          />

          <select
            value={difficultyFilter}
            onChange={(event) =>
              setDifficultyFilter(
                event.target.value
              )
            }
          >

            <option value="ALL">
              All Difficulties
            </option>

            {difficulties.map(
              (difficulty) => (
                <option
                  key={difficulty}
                  value={difficulty}
                >
                  {difficulty}
                </option>
              )
            )}

          </select>

          <select
            value={categoryFilter}
            onChange={(event) =>
              setCategoryFilter(
                event.target.value
              )
            }
          >

            <option value="ALL">
              All Categories
            </option>

            {categories.map(
              (category) => (
                <option
                  key={category}
                  value={category}
                >
                  {category}
                </option>
              )
            )}

          </select>

          <button
            className="secondary-button"
            onClick={
              clearFilters
            }
          >
            Clear
          </button>

        </div>

      </div>

      {/* ======================================
          TABLE
      ====================================== */}

      <div className="admin-table-card">

        <div className="section-heading">

          <div>

            <span className="eyebrow">
              CHALLENGES
            </span>

            <h2>
              Challenge Performance
            </h2>

          </div>

        </div>

        {loading ? (

          <div className="empty-card">

            <h2>
              Loading challenge data...
            </h2>

          </div>

        ) : filteredChallenges.length ===
          0 ? (

          <div className="empty-card">

            <h2>
              No challenges found
            </h2>

            <p>
              Try changing the filters.
            </p>

          </div>

        ) : (

          <div
            style={{
              overflowX: 'auto',
            }}
          >

            <table className="admin-table">

              <thead>

                <tr>

                  <th>
                    Challenge
                  </th>

                  <th>
                    Difficulty
                  </th>

                  <th>
                    Category
                  </th>

                  <th>
                    Submissions
                  </th>

                  <th>
                    Participants
                  </th>

                  <th>
                    Avg Score
                  </th>

                  <th>
                    Completion
                  </th>

                  <th>
                    Analytics
                  </th>

                </tr>

              </thead>

              <tbody>

                {filteredChallenges.map(
                  (challenge) => {

                    const challengeId =
                      challenge.challengeId ??
                      challenge.id;

                    return (
                      <tr
                        key={
                          challengeId
                        }
                      >

                        <td>

                          <strong>
                            {challenge.title ||
                              `Challenge #${challengeId ??
                                '—'}`}
                          </strong>

                          <br />

                          <small>
                            #
                            {
                              challengeId ??
                              '—'
                            }
                          </small>

                        </td>

                        <td>
                          {challenge.difficulty ||
                            '—'}
                        </td>

                        <td>
                          {challenge.category ||
                            'General'}
                        </td>

                        <td>

                          <strong>
                            {
                              challenge.totalSubmissions ??
                              0
                            }
                          </strong>

                          <br />

                          <small>
                            First:{' '}
                            {
                              challenge.firstSubmissions ??
                              0
                            }
                          </small>

                        </td>

                        <td>
                          {
                            challenge.uniqueParticipants ??
                            0
                          }
                        </td>

                        <td>
                          {formatNumber(
                            challenge.averageScore,
                            2
                          )}
                        </td>

                        <td>
                          {formatPercentage(
                            challenge.completionRate
                          )}
                        </td>

                        <td>

                          <Link
                            to={`/admin/challenge-analytics/${challengeId}`}
                            className="secondary-button"
                          >
                            View Analytics
                          </Link>

                        </td>

                      </tr>
                    );
                  }
                )}

              </tbody>

            </table>

          </div>

        )}

      </div>

    </div>
  );
}

export default AdminChallengeOverview;