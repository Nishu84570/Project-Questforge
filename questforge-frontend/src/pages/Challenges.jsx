
import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import ChallengeCard from '../components/ChallengeCard';
import '../styles/Challenges.css';

function Challenges() {
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [difficultyFilter, setDifficultyFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('DEFAULT');

  useEffect(() => {
    const fetchChallenges = async () => {
      try {
        const { response, data } = await apiRequest('/challenges', {
          method: 'GET',
        });

        if (!response.ok) {
          setError(data?.message || 'Failed to load challenges.');
          return;
        }

        setChallenges(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error('Challenges loading error:', err);
        setError('Unable to connect to QuestForge backend.');
      } finally {
        setLoading(false);
      }
    };

    fetchChallenges();
  }, []);

  const difficulties = useMemo(() => {
    const values = challenges
      .map((challenge) => challenge.difficulty)
      .filter(Boolean);

    return [...new Set(values)].sort();
  }, [challenges]);

  const categories = useMemo(() => {
    const values = challenges
      .map((challenge) => challenge.category)
      .filter(Boolean);

    return [...new Set(values)].sort();
  }, [challenges]);

  const filteredChallenges = useMemo(() => {
    let result = [...challenges];

    const search = searchTerm.trim().toLowerCase();

    if (search) {
      result = result.filter((challenge) => {
        const title = challenge.title?.toLowerCase() || '';
        const description = challenge.description?.toLowerCase() || '';
        const category = challenge.category?.toLowerCase() || '';

        return (
          title.includes(search) ||
          description.includes(search) ||
          category.includes(search)
        );
      });
    }

    if (difficultyFilter !== 'ALL') {
      result = result.filter(
        (challenge) => challenge.difficulty === difficultyFilter
      );
    }

    if (categoryFilter !== 'ALL') {
      result = result.filter(
        (challenge) => challenge.category === categoryFilter
      );
    }

    const difficultyOrder = {
      EASY: 1,
      MEDIUM: 2,
      HARD: 3,
    };

    if (sortBy === 'TITLE_ASC') {
      result.sort((a, b) =>
        (a.title || '').localeCompare(b.title || '')
      );
    } else if (sortBy === 'TITLE_DESC') {
      result.sort((a, b) =>
        (b.title || '').localeCompare(a.title || '')
      );
    } else if (sortBy === 'EASY_FIRST') {
      result.sort(
        (a, b) =>
          (difficultyOrder[a.difficulty] || 99) -
          (difficultyOrder[b.difficulty] || 99)
      );
    } else if (sortBy === 'HARD_FIRST') {
      result.sort(
        (a, b) =>
          (difficultyOrder[b.difficulty] || 0) -
          (difficultyOrder[a.difficulty] || 0)
      );
    }

    return result;
  }, [
    challenges,
    searchTerm,
    difficultyFilter,
    categoryFilter,
    sortBy,
  ]);

  const clearFilters = () => {
    setSearchTerm('');
    setDifficultyFilter('ALL');
    setCategoryFilter('ALL');
    setSortBy('DEFAULT');
  };

  const hasActiveFilters =
    searchTerm.trim() !== '' ||
    difficultyFilter !== 'ALL' ||
    categoryFilter !== 'ALL' ||
    sortBy !== 'DEFAULT';

  if (loading) {
    return (
      <div className="page challenges-page">
        <div className="ch-loading-state">
          <div className="ch-loader">
            <span />
            <span />
            <span />
          </div>

          <h2>Loading Challenges</h2>
          <p>Preparing your next coding adventure...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page challenges-page">
        <div className="ch-state-card ch-error-state">
          <div className="ch-state-icon">!</div>
          <span className="ch-eyebrow">CONNECTION ISSUE</span>
          <h2>Challenges Unavailable</h2>
          <p>{error}</p>

          <button
            type="button"
            className="ch-button ch-button-primary"
            onClick={() => window.location.reload()}
          >
            Try Again <span>↻</span>
          </button>
        </div>
      </div>
    );
  }

  return (
    <main className="page challenges-page">

      {/* HERO SECTION */}
      <section className="ch-hero">
        <div className="ch-hero-content">
          <div className="ch-hero-badge">
            <span className="ch-hero-badge-dot" />
            QUESTFORGE CHALLENGE ARENA
          </div>

          <h1>
            Level Up Your
            <br />
            <span>Coding Skills.</span>
          </h1>

          <p className="ch-hero-description">
            Explore coding challenges, sharpen your problem-solving skills,
            and prove what you're capable of.
          </p>

          <div className="ch-hero-actions">
            <a href="#challenge-explorer" className="ch-button ch-button-primary">
              Explore Challenges <span>↓</span>
            </a>

            <Link to="/leaderboard" className="ch-button ch-button-glass">
              🏆 Leaderboard
            </Link>
          </div>
        </div>

        <div className="ch-hero-visual" aria-hidden="true">
          <div className="ch-orbit ch-orbit-one" />
          <div className="ch-orbit ch-orbit-two" />

          <div className="ch-code-window">
            <div className="ch-code-header">
              <div className="ch-window-dots">
                <span />
                <span />
                <span />
              </div>
              <span>questforge.java</span>
            </div>

            <div className="ch-code-body">
              <p><span>01</span> <i>public class</i> QuestForge {'{'}</p>
              <p><span>02</span> &nbsp; <i>public static void</i> levelUp() {'{'}</p>
              <p><span>03</span> &nbsp;&nbsp; skills.improve();</p>
              <p><span>04</span> &nbsp;&nbsp; challenges.solve();</p>
              <p><span>05</span> &nbsp;&nbsp; <b>return</b> SUCCESS;</p>
              <p><span>06</span> &nbsp; {'}'}</p>
              <p><span>07</span> {'}'}</p>
            </div>

            <div className="ch-code-footer">
              <span className="ch-code-status-dot" />
              READY TO CODE
            </div>
          </div>

          <div className="ch-floating-symbol ch-symbol-one">⌘</div>
          <div className="ch-floating-symbol ch-symbol-two">✦</div>
          <div className="ch-floating-symbol ch-symbol-three">{'</>'}</div>
        </div>

        <div className="ch-hero-bottom">
          <span>THINK</span>
          <span className="ch-hero-line" />
          <span>CODE</span>
          <span className="ch-hero-line" />
          <span>CONQUER</span>
        </div>
      </section>

      {/* QUICK OVERVIEW */}
      <section className="ch-overview">
        <div className="ch-overview-item">
          <span className="ch-overview-icon">◈</span>
          <div>
            <strong>{challenges.length}</strong>
            <span>Available Challenges</span>
          </div>
        </div>

        <div className="ch-overview-divider" />

        <div className="ch-overview-item">
          <span className="ch-overview-icon ch-icon-blue">⌘</span>
          <div>
            <strong>{categories.length}</strong>
            <span>Categories</span>
          </div>
        </div>

        <div className="ch-overview-divider" />

        <div className="ch-overview-item">
          <span className="ch-overview-icon ch-icon-green">✧</span>
          <div>
            <strong>{difficulties.length}</strong>
            <span>Difficulty Levels</span>
          </div>
        </div>

        <div className="ch-overview-divider" />

        <div className="ch-overview-item">
          <span className="ch-overview-icon ch-icon-orange">⚡</span>
          <div>
            <strong>∞</strong>
            <span>Room to Improve</span>
          </div>
        </div>
      </section>

      {/* CHALLENGE EXPLORER */}
      <section className="ch-explorer" id="challenge-explorer">

        <div className="ch-section-header">
          <div>
            <span className="ch-eyebrow">YOUR NEXT ADVENTURE</span>
            <h2>Explore Challenges</h2>
            <p>Find the right challenge and push your limits.</p>
          </div>

          <Link to="/submissions" className="ch-submissions-link">
            My Submissions <span>↗</span>
          </Link>
        </div>

        {/* FILTER PANEL */}
        {challenges.length > 0 && (
          <div className="ch-filter-panel">
            <div className="ch-filter-heading">
              <div>
                <span className="ch-filter-icon">⌕</span>
                <strong>Find Your Challenge</strong>
              </div>

              {hasActiveFilters && (
                <button
                  type="button"
                  className="ch-clear-button"
                  onClick={clearFilters}
                >
                  Reset Filters <span>↻</span>
                </button>
              )}
            </div>

            <div className="ch-filter-controls">
              <div className="ch-search-wrapper">
                <span className="ch-search-icon">⌕</span>

                <input
                  type="search"
                  placeholder="Search by title, description or category..."
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  className="ch-search-input"
                  aria-label="Search challenges"
                />

                {searchTerm && (
                  <button
                    type="button"
                    className="ch-search-clear"
                    onClick={() => setSearchTerm('')}
                    aria-label="Clear search"
                  >
                    ×
                  </button>
                )}
              </div>

              <select
                value={difficultyFilter}
                onChange={(event) => setDifficultyFilter(event.target.value)}
                className="ch-select"
                aria-label="Filter by difficulty"
              >
                <option value="ALL">All Difficulties</option>
                {difficulties.map((difficulty) => (
                  <option key={difficulty} value={difficulty}>
                    {difficulty}
                  </option>
                ))}
              </select>

              <select
                value={categoryFilter}
                onChange={(event) => setCategoryFilter(event.target.value)}
                className="ch-select"
                aria-label="Filter by category"
              >
                <option value="ALL">All Categories</option>
                {categories.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>

              <select
                value={sortBy}
                onChange={(event) => setSortBy(event.target.value)}
                className="ch-select"
                aria-label="Sort challenges"
              >
                <option value="DEFAULT">Recommended</option>
                <option value="TITLE_ASC">Name: A – Z</option>
                <option value="TITLE_DESC">Name: Z – A</option>
                <option value="EASY_FIRST">Easy to Hard</option>
                <option value="HARD_FIRST">Hard to Easy</option>
              </select>
            </div>

            <div className="ch-filter-footer">
              <span>
                Showing <strong>{filteredChallenges.length}</strong> of{' '}
                <strong>{challenges.length}</strong> challenges
              </span>

              {hasActiveFilters && (
                <span className="ch-active-filter-label">
                  Filters Applied
                </span>
              )}
            </div>
          </div>
        )}

        {/* CHALLENGE CONTENT */}
        {challenges.length === 0 ? (
          <div className="ch-state-card">
            <div className="ch-state-icon">⌘</div>
            <h2>No Challenges Yet</h2>
            <p>New coding challenges will appear here when they become available.</p>
          </div>
        ) : filteredChallenges.length === 0 ? (
          <div className="ch-state-card">
            <div className="ch-state-icon">⌕</div>
            <h2>No Matching Challenges</h2>
            <p>We couldn't find anything matching your current filters.</p>

            <button
              type="button"
              className="ch-button ch-button-primary"
              onClick={clearFilters}
            >
              Clear All Filters <span>↻</span>
            </button>
          </div>
        ) : (
          <div className="ch-challenge-grid">
            {filteredChallenges.map((challenge) => (
              <article
                key={challenge.id}
                className="ch-challenge-wrapper"
              >
                <ChallengeCard challenge={challenge} />

                <Link
                  className="ch-leaderboard-button"
                  to={`/leaderboard?challengeId=${challenge.id}`}
                >
                  <span>🏆</span>
                  View Challenge Leaderboard
                  <span className="ch-leaderboard-arrow">↗</span>
                </Link>
              </article>
            ))}
          </div>
        )}

      </section>

      {/* BOTTOM CTA */}
      <section className="ch-bottom-cta">
        <div className="ch-cta-decoration">✦</div>

        <div>
          <span className="ch-eyebrow">KEEP THE MOMENTUM</span>
          <h2>Your progress starts with one challenge.</h2>
          <p>Keep learning, keep solving, and keep getting better.</p>
        </div>

        <Link to="/submissions" className="ch-button ch-button-primary">
          Track My Progress <span>→</span>
        </Link>
      </section>

    </main>
  );
}

export default Challenges;
