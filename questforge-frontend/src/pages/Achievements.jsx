
import React, {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';

import '../styles/Achievements.css';

function Achievements() {
  const [achievements, setAchievements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');

  // ==========================================
  // FETCH ACHIEVEMENTS
  // ==========================================

  const fetchAchievements = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest(
        '/achievements/me',
        {
          method: 'GET',
        }
      );

      if (!response.ok) {
        throw new Error(
          data?.message || 'Failed to load achievements.'
        );
      }

      setAchievements(
        Array.isArray(data) ? data : []
      );

    } catch (err) {
      setError(
        err?.message ||
        'Unable to connect to QuestForge backend.'
      );

    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAchievements();
  }, [fetchAchievements]);

  // ==========================================
  // SEARCH AND SORT
  // ==========================================

  const filteredAchievements = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    let result = achievements.filter((achievement) => {
      const name = String(achievement.name || '').toLowerCase();
      const description = String(
        achievement.description || ''
      ).toLowerCase();

      const code = String(
        achievement.code || ''
      ).toLowerCase();

      return (
        !search ||
        name.includes(search) ||
        description.includes(search) ||
        code.includes(search)
      );
    });

    result = [...result];

    switch (sortBy) {
      case 'name':
        result.sort((a, b) =>
          String(a.name || '').localeCompare(
            String(b.name || '')
          )
        );
        break;

      case 'name-desc':
        result.sort((a, b) =>
          String(b.name || '').localeCompare(
            String(a.name || '')
          )
        );
        break;

      case 'latest':
        result.sort((a, b) =>
          Number(b.id || 0) - Number(a.id || 0)
        );
        break;

      case 'oldest':
        result.sort((a, b) =>
          Number(a.id || 0) - Number(b.id || 0)
        );
        break;

      default:
        break;
    }

    return result;
  }, [achievements, searchTerm, sortBy]);

  // ==========================================
  // STATISTICS
  // ==========================================

  const stats = useMemo(() => {
    const total = achievements.length;

    return {
      total,
      matching: filteredAchievements.length,
      status:
        total === 0
          ? 'Not Started'
          : total < 5
            ? 'Rising Star'
            : total < 10
              ? 'Achiever'
              : 'Champion',
    };
  }, [achievements, filteredAchievements]);

  // ==========================================
  // CLEAR FILTERS
  // ==========================================

  const clearFilters = () => {
    setSearchTerm('');
    setSortBy('name');
  };

  const hasActiveFilters =
    searchTerm.trim() !== '' || sortBy !== 'name';

  // ==========================================
  // LOADING STATE
  // ==========================================

  if (loading) {
    return (
      <div className="page achievements-page">
        <div className="empty-card loading-state">
          <div className="loading-spinner"></div>

          <h2>Loading Achievements</h2>

          <p>
            Discovering your QuestForge milestones...
          </p>
        </div>
      </div>
    );
  }

  // ==========================================
  // ERROR STATE
  // ==========================================

  if (error) {
    return (
      <div className="page achievements-page">
        <div className="empty-card error-state">

          <div className="empty-icon">
            ⚠️
          </div>

          <h2>Something went wrong</h2>

          <p className="error-text">
            {error}
          </p>

          <div className="empty-actions">
            <button
              type="button"
              className="primary-button"
              onClick={fetchAchievements}
            >
              Try Again
            </button>

            <Link
              className="secondary-button"
              to="/profile"
            >
              Back to Profile
            </Link>
          </div>

        </div>
      </div>
    );
  }

  // ==========================================
  // MAIN PAGE
  // ==========================================

  return (
    <div className="page achievements-page">

      {/* ======================================
          HERO HEADER
      ====================================== */}

      <section className="challenges-header">

        <div className="achievement-hero-content">

          <span className="hero-icon">
            🏆
          </span>

          <p className="eyebrow">
            QUESTFORGE / YOUR JOURNEY
          </p>

          <h1>
            My <span>Achievements</span>
          </h1>

          <p className="hero-description">
            Every challenge completed, every milestone
            conquered, and every badge earned.
            Your journey deserves to be celebrated.
          </p>

          <div className="hero-meta">
            <span className="hero-meta-item">
              <span className="meta-dot"></span>
              Achievement Collection
            </span>

            <span className="hero-meta-divider">
              /
            </span>

            <span className="hero-meta-item">
              {stats.total} Badges Earned
            </span>
          </div>

        </div>

        <div className="hero-side">

          <div className="hero-trophy">
            🏆
          </div>

          <Link
            className="secondary-button"
            to="/profile"
          >
            ← Back to Profile
          </Link>

        </div>

      </section>

      {/* ======================================
          STATISTICS
      ====================================== */}

      <section className="result-stats">

        <div className="result-stat-card">

          <div className="stat-icon">
            🏆
          </div>

          <span className="result-stat-label">
            Total Unlocked
          </span>

          <strong>{stats.total}</strong>

          <span className="stat-caption">
            Your earned achievements
          </span>

        </div>

        <div className="result-stat-card">

          <div className="stat-icon">
            ✨
          </div>

          <span className="result-stat-label">
            Collection Status
          </span>

          <strong className="stat-status">
            {stats.status}
          </strong>

          <span className="stat-caption">
            Keep pushing your limits
          </span>

        </div>

        <div className="result-stat-card">

          <div className="stat-icon">
            🔍
          </div>

          <span className="result-stat-label">
            Current Results
          </span>

          <strong>
            {stats.matching}
            <span className="stat-total">
              /{stats.total}
            </span>
          </strong>

          <span className="stat-caption">
            Matching achievements
          </span>

        </div>

      </section>

      {/* ======================================
          SEARCH AND FILTERS
      ====================================== */}

      {stats.total > 0 && (

        <section className="filter-card">

          <div className="filter-heading">

            <div>
              <h2>My Collection</h2>

              <p>
                Search and organize your earned badges.
              </p>
            </div>

            {hasActiveFilters && (
              <button
                type="button"
                className="clear-filters-button"
                onClick={clearFilters}
              >
                Reset Filters ↺
              </button>
            )}

          </div>

          <div className="filter-controls-wrapper">

            <div className="search-wrapper">

              <span className="search-icon">
                ⌕
              </span>

              <input
                type="search"
                placeholder="Search by name, description or code..."
                value={searchTerm}
                onChange={(e) =>
                  setSearchTerm(e.target.value)
                }
                className="achievement-search-input"
                aria-label="Search achievements"
              />

              {searchTerm && (
                <button
                  type="button"
                  className="search-clear"
                  onClick={() => setSearchTerm('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}

            </div>

            <div className="sort-wrapper">

              <label htmlFor="achievement-sort">
                Sort by
              </label>

              <select
                id="achievement-sort"
                value={sortBy}
                onChange={(e) =>
                  setSortBy(e.target.value)
                }
                className="achievement-sort-select"
              >
                <option value="name">Name A-Z</option>

                <option value="name-desc">
                  Name Z-A
                </option>

                <option value="latest">
                  Latest Unlocked
                </option>

                <option value="oldest">
                  Oldest Unlocked
                </option>

              </select>

            </div>

          </div>

          <div className="filter-bottom">

            <span>
              Showing <strong>{stats.matching}</strong>
              {' '}of <strong>{stats.total}</strong> achievements
            </span>

            <span className="collection-status">
              ● Collection Active
            </span>

          </div>

        </section>

      )}

      {/* ======================================
          ACHIEVEMENT CONTENT
      ====================================== */}

      {stats.total === 0 ? (

        <section className="empty-card">

          <div className="empty-icon">
            🏆
          </div>

          <span className="empty-eyebrow">
            YOUR JOURNEY STARTS HERE
          </span>

          <h2>No achievements yet</h2>

          <p>
            Your trophy collection is waiting!
            Complete challenges, sharpen your skills,
            and unlock your first QuestForge badge.
          </p>

          <Link
            className="primary-button"
            to="/challenges"
          >
            Explore Challenges →
          </Link>

        </section>

      ) : stats.matching === 0 ? (

        <section className="empty-card">

          <div className="empty-icon">
            🔎
          </div>

          <h2>No matching achievements</h2>

          <p>
            We couldn't find any badges matching
            "{searchTerm}". Try another keyword.
          </p>

          <button
            type="button"
            className="primary-button"
            onClick={clearFilters}
          >
            Clear All Filters
          </button>

        </section>

      ) : (

        <section className="challenge-grid">

          {filteredAchievements.map(
            (achievement, index) => (

              <article
                className="challenge-card achievement-card"
                key={achievement.id ?? achievement.code ?? index}
                style={{
                  '--card-index': index,
                }}
              >

                {/* CARD TOP */}

                <div className="challenge-top">

                  <span className="challenge-id">
                    #{achievement.id ?? 'N/A'}
                  </span>

                  <span className="submission-status status-approved">
                    <span className="status-check">
                      ✓
                    </span>

                    Unlocked
                  </span>

                </div>

                {/* ACHIEVEMENT ICON */}

                <div className="achievement-icon-wrapper">

                  <div className="icon-glow"></div>

                  <div className="icon-orbit"></div>

                  <span className="achievement-emoji">
                    {achievement.icon || '🏆'}
                  </span>

                </div>

                {/* CARD INFORMATION */}

                <div className="achievement-content">

                  <h2 className="achievement-title">
                    {achievement.name || 'Achievement'}
                  </h2>

                  <p className="description">
                    {achievement.description ||
                      'Achievement unlocked through your QuestForge progress.'}
                  </p>

                </div>

                {/* CARD FOOTER */}

                <div className="challenge-footer">

                  <span className="achievement-code-badge">
                    {achievement.code || 'ACHIEVEMENT'}
                  </span>

                  <span className="brand-name">
                    QUESTFORGE
                  </span>

                </div>

              </article>

            )
          )}

        </section>

      )}

      {/* ======================================
          BOTTOM FOOTER
      ====================================== */}

      {stats.total > 0 && (

        <div className="achievements-bottom">

          <span>
            ✦ Every achievement tells a story.
          </span>

          <span>
            Crafted with QuestForge
          </span>

        </div>

      )}

    </div>
  );
}

export default Achievements;
