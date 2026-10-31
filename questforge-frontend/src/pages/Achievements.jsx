import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/Achievements.css';

function Achievements() {
  const [achievements, setAchievements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');

  useEffect(() => {
    const fetchAchievements = async () => {
      try {
        const { response, data } = await apiRequest('/achievements/me', {
          method: 'GET',
        });

        if (!response.ok) {
          setError(data?.message || 'Failed to load achievements.');
          return;
        }

        setAchievements(Array.isArray(data) ? data : []);
      } catch (err) {
        setError('Unable to connect to QuestForge backend.');
      } finally {
        setLoading(false);
      }
    };

    fetchAchievements();
  }, []);

  const filteredAchievements = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    let result = achievements.filter((achievement) => {
      if (!search) return true;

      return (
        achievement.name?.toLowerCase().includes(search) ||
        achievement.description?.toLowerCase().includes(search) ||
        achievement.code?.toLowerCase().includes(search)
      );
    });

    result = [...result];

    if (sortBy === 'name') {
      result.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    } else if (sortBy === 'name-desc') {
      result.sort((a, b) => (b.name || '').localeCompare(a.name || ''));
    } else if (sortBy === 'latest') {
      result.sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
    } else if (sortBy === 'oldest') {
      result.sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
    }

    return result;
  }, [achievements, searchTerm, sortBy]);

  const clearFilters = () => {
    setSearchTerm('');
    setSortBy('name');
  };

  if (loading) {
    return (
      <div className="page">
        <div className="empty-card">
          <div className="loading-spinner"></div>
          <h2>Loading Achievements</h2>
          <p>Checking your unlocked badges...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page">
        <div className="empty-card">
          <p className="error-text">{error}</p>
          <Link className="secondary-button" to="/profile">
            Back to Profile
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page achievements-page">
      {/* HEADER SECTION */}
      <div className="challenges-header">
        <div>
          <p className="eyebrow">QUESTFORGE</p>
          <h1>🏆 My Achievements</h1>
          <p>Every badge you have unlocked on your QuestForge journey.</p>
        </div>
        <Link className="secondary-button" to="/profile">
          Profile
        </Link>
      </div>

      {/* STATS OVERVIEW */}
      <div className="result-stats">
        <div className="result-stat-card">
          <span className="result-stat-label">🏆 Unlocked</span>
          <strong>{achievements.length}</strong>
        </div>
        <div className="result-stat-card">
          <span className="result-stat-label">⭐ Status</span>
          <strong>{achievements.length > 0 ? 'Growing' : 'Start'}</strong>
        </div>
      </div>

      {/* FILTER & SEARCH BAR */}
      {achievements.length > 0 && (
        <div className="admin-form-card filter-card">
          <div className="filter-controls-wrapper">
            <input
              type="text"
              placeholder="Search achievements..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="achievement-search-input"
            />

            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="achievement-sort-select"
            >
              <option value="name">Name A-Z</option>
              <option value="name-desc">Name Z-A</option>
              <option value="latest">Latest Unlocked</option>
              <option value="oldest">Oldest Unlocked</option>
            </select>

            {(searchTerm || sortBy !== 'name') && (
              <button
                type="button"
                className="secondary-button"
                onClick={clearFilters}
              >
                Clear Filters
              </button>
            )}
          </div>
        </div>
      )}

      {/* CONTENT GRID / STATES */}
      {achievements.length === 0 ? (
        <div className="empty-card">
          <h2>No achievements yet</h2>
          <p>Complete challenges and keep progressing to unlock your first badge!</p>
          <Link className="primary-button" to="/challenges">
            Explore Challenges
          </Link>
        </div>
      ) : filteredAchievements.length === 0 ? (
        <div className="empty-card">
          <h2>No matching achievements</h2>
          <p>Try a different search term.</p>
          <button
            type="button"
            className="secondary-button"
            onClick={clearFilters}
          >
            Reset Filters
          </button>
        </div>
      ) : (
        <div className="challenge-grid">
          {filteredAchievements.map((achievement) => (
            <div
              className="challenge-card achievement-card"
              key={achievement.id}
            >
              <div className="challenge-top">
                <span className="challenge-id">Badge #{achievement.id}</span>
                <span className="submission-status status-approved">
                  ✓ Unlocked
                </span>
              </div>

              <div className="achievement-icon-wrapper">
                <div className="icon-glow"></div>
                <span className="achievement-emoji">
                  {achievement.icon || '🏆'}
                </span>
              </div>

              <h2 className="achievement-title">
                {achievement.name || 'Achievement'}
              </h2>

              <p className="description">
                {achievement.description ||
                  'Achievement unlocked through QuestForge progress.'}
              </p>

              <div className="challenge-footer">
                <span className="achievement-code-badge">
                  {achievement.code || 'ACHIEVEMENT'}
                </span>
                <span className="brand-name">Unlocked</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Achievements;