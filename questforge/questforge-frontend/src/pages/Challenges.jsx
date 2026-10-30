import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

import ChallengeCard from '../components/ChallengeCard';

function Challenges() {
  const [challenges, setChallenges] = useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState('');

  const [searchTerm, setSearchTerm] =
    useState('');

  const [difficultyFilter, setDifficultyFilter] =
    useState('ALL');

  const [categoryFilter, setCategoryFilter] =
    useState('ALL');

  const [sortBy, setSortBy] =
    useState('DEFAULT');

  useEffect(() => {

    const fetchChallenges = async () => {

      try {

        const {
          response,
          data,
        } = await apiRequest(
          '/challenges',
          {
            method: 'GET',
          }
        );

        if (!response.ok) {

          setError(
            data?.message ||
              'Failed to load challenges'
          );

          return;
        }

        setChallenges(data || []);

      } catch (error) {

        setError(
          'Unable to connect to QuestForge backend.'
        );

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

    return [
      ...new Set(values),
    ];

  }, [challenges]);

  const categories = useMemo(() => {

    const values = challenges
      .map((challenge) => challenge.category)
      .filter(Boolean);

    return [
      ...new Set(values),
    ];

  }, [challenges]);

  const filteredChallenges = useMemo(() => {

    let result = [...challenges];

    const search =
      searchTerm
        .trim()
        .toLowerCase();

    if (search) {

      result = result.filter(
        (challenge) => {

          const title =
            challenge.title
              ?.toLowerCase() || '';

          const description =
            challenge.description
              ?.toLowerCase() || '';

          const category =
            challenge.category
              ?.toLowerCase() || '';

          return (
            title.includes(search) ||
            description.includes(search) ||
            category.includes(search)
          );
        }
      );
    }

    if (difficultyFilter !== 'ALL') {

      result = result.filter(
        (challenge) =>
          challenge.difficulty ===
          difficultyFilter
      );
    }

    if (categoryFilter !== 'ALL') {

      result = result.filter(
        (challenge) =>
          challenge.category ===
          categoryFilter
      );
    }

    if (sortBy === 'TITLE_ASC') {

      result.sort(
        (a, b) =>
          (a.title || '').localeCompare(
            b.title || ''
          )
      );
    }

    if (sortBy === 'TITLE_DESC') {

      result.sort(
        (a, b) =>
          (b.title || '').localeCompare(
            a.title || ''
          )
      );
    }

    if (sortBy === 'EASY_FIRST') {

      const order = {
        EASY: 1,
        MEDIUM: 2,
        HARD: 3,
      };

      result.sort(
        (a, b) =>
          (order[a.difficulty] || 99) -
          (order[b.difficulty] || 99)
      );
    }

    if (sortBy === 'HARD_FIRST') {

      const order = {
        EASY: 1,
        MEDIUM: 2,
        HARD: 3,
      };

      result.sort(
        (a, b) =>
          (order[b.difficulty] || 99) -
          (order[a.difficulty] || 99)
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

  if (loading) {

    return (
      <div className="page">

        <p className="status-text">
          Loading challenges...
        </p>

      </div>
    );
  }

  if (error) {

    return (
      <div className="page">

        <p className="error-text">
          {error}
        </p>

      </div>
    );
  }

  return (
    <div className="page">

      <div className="challenges-header">

        <div>

          <p className="eyebrow">
            QUESTFORGE
          </p>

          <h1>
            Challenges
          </h1>

          <p>
            Choose a challenge and prove your skills.
          </p>

        </div>

        <div className="challenges-header-actions">

          <Link
            className="secondary-button"
            to="/leaderboard"
          >
            🏆 Leaderboard
          </Link>

          <Link
            className="secondary-button"
            to="/submissions"
          >
            My Submissions
          </Link>

        </div>

      </div>

      {challenges.length > 0 && (

        <div className="challenge-filters">

          <input
            type="text"
            placeholder="Search challenges..."
            value={searchTerm}
            onChange={(event) =>
              setSearchTerm(event.target.value)
            }
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

          <select
            value={sortBy}
            onChange={(event) =>
              setSortBy(event.target.value)
            }
          >
            <option value="DEFAULT">
              Default Order
            </option>

            <option value="TITLE_ASC">
              Title: A → Z
            </option>

            <option value="TITLE_DESC">
              Title: Z → A
            </option>

            <option value="EASY_FIRST">
              Easy → Hard
            </option>

            <option value="HARD_FIRST">
              Hard → Easy
            </option>

          </select>

          <button
            type="button"
            className="secondary-button"
            onClick={clearFilters}
          >
            Clear
          </button>

        </div>

      )}

      {challenges.length === 0 ? (

        <div className="empty-card">

          <h2>
            No challenges available
          </h2>

          <p>
            Check back later for new challenges.
          </p>

        </div>

      ) : filteredChallenges.length === 0 ? (

        <div className="empty-card">

          <h2>
            No matching challenges
          </h2>

          <p>
            Try changing your search or filters.
          </p>

          <button
            type="button"
            className="secondary-button"
            onClick={clearFilters}
          >
            Clear Filters
          </button>

        </div>

      ) : (

        <>
          <div className="challenge-results-info">

            Showing{' '}
            <strong>
              {filteredChallenges.length}
            </strong>{' '}
            of{' '}
            <strong>
              {challenges.length}
            </strong>{' '}
            challenges

          </div>

          <div className="challenge-grid">

            {filteredChallenges.map(
              (challenge) => (

                <div
                  key={challenge.id}
                  className="challenge-card-wrapper"
                >

                  <ChallengeCard
                    challenge={challenge}
                  />

                  <Link
                    className="leaderboard-card-button"
                    to={`/leaderboard?challengeId=${challenge.id}`}
                  >
                    🏆 View Leaderboard
                  </Link>

                </div>

              )
            )}

          </div>
        </>

      )}

    </div>
  );
}

export default Challenges;
