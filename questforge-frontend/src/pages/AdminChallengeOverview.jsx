import React, { useEffect, useMemo, useState } from 'react';
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

  // ==========================================
  // LOAD CHALLENGE OVERVIEW
  // ==========================================
  const loadChallenges = async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest('/admin/challenges', {
        method: 'GET',
      });

      if (!response.ok) {
        setError(data?.message || 'Failed to load challenge overview.');
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
    } catch (err) {
      console.error('Admin challenge overview error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadChallenges();
  }, []);

  // ==========================================
  // UNIQUE DIFFICULTIES & CATEGORIES
  // ==========================================
  const difficulties = useMemo(() => {
    return [...new Set(challenges.map((c) => c.difficulty).filter(Boolean))];
  }, [challenges]);

  const categories = useMemo(() => {
    return [...new Set(challenges.map((c) => c.category).filter(Boolean))];
  }, [challenges]);

  // ==========================================
  // FILTER
  // ==========================================
  const filteredChallenges = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return challenges.filter((challenge) => {
      const difficulty = String(challenge.difficulty || '');
      const category = String(challenge.category || '');

      if (difficultyFilter !== 'ALL' && difficulty !== difficultyFilter) {
        return false;
      }

      if (categoryFilter !== 'ALL' && category !== categoryFilter) {
        return false;
      }

      if (!normalizedSearch) return true;

      const searchableText = [
        challenge.challengeId,
        challenge.id,
        challenge.title,
        challenge.difficulty,
        challenge.category,
      ]
        .filter((val) => val !== null && val !== undefined)
        .join(' ')
        .toLowerCase();

      return searchableText.includes(normalizedSearch);
    });
  }, [challenges, search, difficultyFilter, categoryFilter]);

  // ==========================================
  // FORMATTERS
  // ==========================================
  const formatNumber = (value, digits = 0) => {
    const number = Number(value);
    if (!Number.isFinite(number)) return '0';
    return number.toFixed(digits);
  };

  const formatPercentage = (value) => {
    const number = Number(value);
    if (!Number.isFinite(number)) return '0%';
    return `${number.toFixed(1)}%`;
  };

  const clearFilters = () => {
    setSearch('');
    setDifficultyFilter('ALL');
    setCategoryFilter('ALL');
  };

  if (error) {
    return (
      <div className="page admin-page">
        <div className="empty-card">
          <p className="error-text">{error}</p>
          <button className="primary-button" onClick={loadChallenges}>
            Retry Loading
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="page admin-page admin-challenge-overview-page">
      {/* HEADER */}
      <div className="page-header">
        <div>
          <span className="eyebrow">ADMIN</span>
          <h1>Challenge Overview</h1>
          <p>Monitor challenge participation, scores and completion rates.</p>
        </div>
        <div className="header-actions">
          <Link to="/admin" className="secondary-button">
            ← Admin Dashboard
          </Link>
          <button
            className="secondary-button refresh-btn"
            onClick={loadChallenges}
            disabled={loading}
          >
            {loading ? 'Loading...' : '↻ Refresh'}
          </button>
        </div>
      </div>

      {/* SUMMARY STATS */}
      <div className="result-stats">
        <div className="result-stat-card">
          <span className="result-stat-label">Challenges</span>
          <strong>{challenges.length}</strong>
        </div>
        <div className="result-stat-card">
          <span className="result-stat-label">Showing</span>
          <strong>{filteredChallenges.length}</strong>
        </div>
        <div className="result-stat-card">
          <span className="result-stat-label">Submissions</span>
          <strong>
            {challenges.reduce(
              (total, c) => total + Number(c.totalSubmissions || 0),
              0
            )}
          </strong>
        </div>
        <div className="result-stat-card">
          <span className="result-stat-label">Participants</span>
          <strong>
            {challenges.reduce(
              (total, c) => total + Number(c.uniqueParticipants || 0),
              0
            )}
          </strong>
        </div>
      </div>

      {/* FILTERS CARD */}
      <div className="admin-form-card filter-card">
        <div className="filter-controls-wrapper">
          <input
            type="text"
            placeholder="Search challenge..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="admin-search-input"
          />

          <select
            value={difficultyFilter}
            onChange={(e) => setDifficultyFilter(e.target.value)}
            className="admin-select"
          >
            <option value="ALL">All Difficulties</option>
            {difficulties.map((diff) => (
              <option key={diff} value={diff}>
                {diff}
              </option>
            ))}
          </select>

          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="admin-select"
          >
            <option value="ALL">All Categories</option>
            {categories.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>

          {(search || difficultyFilter !== 'ALL' || categoryFilter !== 'ALL') && (
            <button className="secondary-button" onClick={clearFilters}>
              Clear
            </button>
          )}
        </div>
      </div>

      {/* TABLE SECTION */}
      <div className="admin-table-card">
        <div className="section-heading">
          <div>
            <span className="eyebrow">CHALLENGES</span>
            <h2>Challenge Performance</h2>
          </div>
        </div>

        {loading ? (
          <div className="empty-card">
            <div className="loading-spinner"></div>
            <h2>Loading challenge data...</h2>
          </div>
        ) : filteredChallenges.length === 0 ? (
          <div className="empty-card">
            <h2>No challenges found</h2>
            <p>Try changing the filters or search criteria.</p>
            <button className="secondary-button" onClick={clearFilters}>
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="table-responsive-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Challenge</th>
                  <th>Difficulty</th>
                  <th>Category</th>
                  <th>Submissions</th>
                  <th>Participants</th>
                  <th>Avg Score</th>
                  <th>Completion</th>
                  <th>Analytics</th>
                </tr>
              </thead>
              <tbody>
                {filteredChallenges.map((challenge) => {
                  const challengeId = challenge.challengeId ?? challenge.id;

                  return (
                    <tr key={challengeId}>
                      <td>
                        <strong>
                          {challenge.title || `Challenge #${challengeId ?? '—'}`}
                        </strong>
                        <br />
                        <small className="table-subtext">
                          #{challengeId ?? '—'}
                        </small>
                      </td>
                      <td>
                        <span className={`table-difficulty ${String(challenge.difficulty || '').toLowerCase()}`}>
                          {challenge.difficulty || '—'}
                        </span>
                      </td>
                      <td>{challenge.category || 'General'}</td>
                      <td>
                        <strong>{challenge.totalSubmissions ?? 0}</strong>
                        <br />
                        <small className="table-subtext">
                          First: {challenge.firstSubmissions ?? 0}
                        </small>
                      </td>
                      <td>{challenge.uniqueParticipants ?? 0}</td>
                      <td>{formatNumber(challenge.averageScore, 2)}</td>
                      <td>{formatPercentage(challenge.completionRate)}</td>
                      <td>
                        <Link
                          to={`/admin/challenge-analytics/${challengeId}`}
                          className="secondary-button analytics-btn"
                        >
                          View Analytics
                        </Link>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminChallengeOverview;