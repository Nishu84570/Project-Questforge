import { useEffect, useMemo, useState } from 'react';
import {
  Link,
  useSearchParams,
} from 'react-router-dom';

import { apiRequest } from '../services/api';

function Leaderboard() {
  const [searchParams] = useSearchParams();

  const challengeId =
    searchParams.get('challengeId');

  const [leaderboard, setLeaderboard] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState('');

  const [challengeTitle, setChallengeTitle] =
    useState('');

  const [searchTerm, setSearchTerm] =
    useState('');

  const [sortBy, setSortBy] =
    useState('rank');

  const isGlobal = !challengeId;

  // ============================================================
  // FETCH LEADERBOARD
  // ============================================================

  useEffect(() => {
    const fetchLeaderboard = async () => {
      setLoading(true);
      setError('');
      setSearchTerm('');
      setSortBy('rank');

      try {
        const endpoint = challengeId
          ? `/leaderboard/challenge/${challengeId}`
          : '/leaderboard/global';

        const {
          response,
          data,
        } = await apiRequest(
          endpoint,
          {
            method: 'GET',
          }
        );

        if (!response.ok) {
          setError(
            data?.message ||
              'Failed to load leaderboard.'
          );

          setLeaderboard([]);
          return;
        }

        const safeData =
          Array.isArray(data)
            ? data
            : [];

        setLeaderboard(safeData);

        if (
          challengeId &&
          safeData.length > 0
        ) {
          setChallengeTitle(
            safeData[0]?.challengeTitle ||
              `Challenge #${challengeId}`
          );
        } else if (challengeId) {
          setChallengeTitle(
            `Challenge #${challengeId}`
          );
        } else {
          setChallengeTitle('');
        }
      } catch (error) {
        setError(
          'Unable to connect to QuestForge backend.'
        );

        setLeaderboard([]);
      } finally {
        setLoading(false);
      }
    };

    fetchLeaderboard();
  }, [challengeId]);

  // ============================================================
  // FILTER + SORT
  // ============================================================

  const filteredLeaderboard = useMemo(() => {
    const search =
      searchTerm
        .trim()
        .toLowerCase();

    let result =
      leaderboard.filter((entry) => {
        if (!search) {
          return true;
        }

        const userName =
          entry.userName ||
          entry.name ||
          '';

        const challengeName =
          entry.challengeTitle ||
          '';

        return (
          userName
            .toLowerCase()
            .includes(search) ||
          challengeName
            .toLowerCase()
            .includes(search)
        );
      });

    result = [...result];

    if (sortBy === 'score') {
      result.sort(
        (a, b) =>
          (b.score ?? 0) -
          (a.score ?? 0)
      );
    }

    if (sortBy === 'xp') {
      result.sort(
        (a, b) =>
          (b.xp ?? 0) -
          (a.xp ?? 0)
      );
    }

    if (sortBy === 'level') {
      result.sort(
        (a, b) =>
          (b.level ?? 0) -
          (a.level ?? 0)
      );
    }

    if (sortBy === 'rank') {
      result.sort(
        (a, b) =>
          (a.rank ?? 0) -
          (b.rank ?? 0)
      );
    }

    return result;
  }, [
    leaderboard,
    searchTerm,
    sortBy,
  ]);

  // ============================================================
  // HELPERS
  // ============================================================

  const getRankIcon = (rank) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';

    return `#${rank}`;
  };

  const getPlayerInitial = (entry) => {
    const name =
      entry.userName ||
      entry.name ||
      'U';

    return name
      .charAt(0)
      .toUpperCase();
  };

  // ============================================================
  // LOADING
  // ============================================================

  if (loading) {
    return (
      <div className="page leaderboard-page">

        <div className="leaderboard-loading-card">

          <div className="leaderboard-loading-icon">
            🏆
          </div>

          <div className="loading-spinner"></div>

          <h2>
            Loading Leaderboard
          </h2>

          <p>
            Fetching the latest rankings...
          </p>

        </div>

      </div>
    );
  }

  // ============================================================
  // ERROR
  // ============================================================

  if (!loading && error) {
    return (
      <div className="page leaderboard-page">

        <div className="leaderboard-empty-card">

          <div className="leaderboard-empty-icon">
            ⚠️
          </div>

          <h2>
            Leaderboard unavailable
          </h2>

          <p className="error-text">
            {error}
          </p>

          <Link
            className="primary-button"
            to="/leaderboard"
          >
            🔄 Try Again
          </Link>

        </div>

      </div>
    );
  }

  // ============================================================
  // MAIN
  // ============================================================

  return (
    <div className="page leaderboard-page">

      {/* ======================================================
          HERO HEADER
      ====================================================== */}

      <section className="leaderboard-hero">

        <div className="leaderboard-hero-content">

          <div className="leaderboard-hero-icon">
            🏆
          </div>

          <div>

            <p className="eyebrow">
              QUESTFORGE
            </p>

            <h1>
              Leaderboard
            </h1>

            <p>
              {isGlobal
                ? 'Compete with other players and climb the XP rankings.'
                : 'Compare first-attempt scores with other challengers.'}
            </p>

          </div>

        </div>

        <div className="leaderboard-hero-actions">

          {!isGlobal && (
            <Link
              className="secondary-button"
              to="/leaderboard"
            >
              🌎 Global
            </Link>
          )}

          <Link
            className="secondary-button"
            to="/challenges"
          >
            ⚔ Challenges
          </Link>

        </div>

      </section>


      {/* ======================================================
          TOP PLAYERS PODIUM
      ====================================================== */}

      {leaderboard.length >= 1 && (
        <section className="leaderboard-podium">

          <div className="podium-heading">

            <span>🔥</span>

            <div>
              <h2>
                Top Performers
              </h2>

              <p>
                The champions leading the quest.
              </p>
            </div>

          </div>

          <div className="podium-grid">

            {leaderboard
              .slice(0, 3)
              .map((entry, index) => {

                const rank =
                  entry.rank ??
                  index + 1;

                const playerName =
                  entry.userName ||
                  entry.name ||
                  'Unknown User';

                return (
                  <div
                    key={
                      isGlobal
                        ? entry.userId
                        : `${entry.userId}-${entry.challengeId}`
                    }
                    className={`podium-card podium-rank-${rank}`}
                  >

                    <div className="podium-rank">
                      {getRankIcon(rank)}
                    </div>

                    <div className="podium-avatar">
                      {getPlayerInitial(entry)}
                    </div>

                    <h3>
                      {playerName}
                    </h3>

                    {isGlobal ? (
                      <>
                        <strong className="podium-score">
                          {entry.xp ?? 0} XP
                        </strong>

                        <span className="podium-detail">
                          ⭐ Level {entry.level ?? 1}
                        </span>
                      </>
                    ) : (
                      <>
                        <strong className="podium-score">
                          {entry.score ?? 0}%
                        </strong>

                        <span className="podium-detail">
                          ✓ {entry.correctAnswers ?? 0}
                          {' / '}
                          {entry.totalQuestions ?? 0}
                        </span>
                      </>
                    )}

                  </div>
                );
              })}

          </div>

        </section>
      )}


      {/* ======================================================
          LEADERBOARD CONTAINER
      ====================================================== */}

      <div className="leaderboard-container">

        {/* HEADER */}

        <div className="leaderboard-header-card">

          <div className="leaderboard-header-info">

            <div className="leaderboard-section-icon">
              {isGlobal ? '🌎' : '⚔️'}
            </div>

            <div>

              <p className="eyebrow">
                {isGlobal
                  ? 'GLOBAL LEADERBOARD'
                  : 'CHALLENGE LEADERBOARD'}
              </p>

              <h2>
                {isGlobal
                  ? 'Top Players'
                  : challengeTitle}
              </h2>

            </div>

          </div>

          <div className="leaderboard-count">

            <strong>
              {filteredLeaderboard.length}
            </strong>

            <span>
              {filteredLeaderboard.length === 1
                ? 'Entry'
                : 'Entries'}
            </span>

          </div>

        </div>


        {/* ====================================================
            SEARCH + SORT
        ==================================================== */}

        {leaderboard.length > 0 && (
          <div className="leaderboard-toolbar">

            <div className="leaderboard-search">

              <span>
                🔎
              </span>

              <input
                type="text"
                placeholder="Search player..."
                value={searchTerm}
                onChange={(event) =>
                  setSearchTerm(
                    event.target.value
                  )
                }
              />

            </div>


            <div className="leaderboard-sort">

              <span>
                ↕
              </span>

              <select
                value={sortBy}
                onChange={(event) =>
                  setSortBy(
                    event.target.value
                  )
                }
              >

                <option value="rank">
                  Sort by Rank
                </option>

                {isGlobal ? (
                  <>
                    <option value="xp">
                      Sort by XP
                    </option>

                    <option value="level">
                      Sort by Level
                    </option>
                  </>
                ) : (
                  <option value="score">
                    Sort by Score
                  </option>
                )}

              </select>

            </div>


            {(searchTerm ||
              sortBy !== 'rank') && (
              <button
                type="button"
                className="leaderboard-clear"
                onClick={() => {
                  setSearchTerm('');
                  setSortBy('rank');
                }}
              >
                ✕ Clear
              </button>
            )}

          </div>
        )}


        {/* ====================================================
            NO DATA
        ==================================================== */}

        {leaderboard.length === 0 ? (

          <div className="leaderboard-empty-card">

            <div className="leaderboard-empty-icon">
              🏆
            </div>

            <h2>
              No scores yet
            </h2>

            <p>
              {isGlobal
                ? 'Complete a challenge to start climbing the leaderboard!'
                : 'Be the first person to complete this challenge!'}
            </p>

            <Link
              className="primary-button"
              to="/challenges"
            >
              ⚔ View Challenges
            </Link>

          </div>

        ) : filteredLeaderboard.length === 0 ? (

          <div className="leaderboard-empty-card">

            <div className="leaderboard-empty-icon">
              🔎
            </div>

            <h2>
              No players found
            </h2>

            <p>
              Try a different player name.
            </p>

            <button
              type="button"
              className="secondary-button"
              onClick={() =>
                setSearchTerm('')
              }
            >
              ✕ Clear Search
            </button>

          </div>

        ) : (

          /* ==================================================
             TABLE
          ================================================== */

          <div className="leaderboard-table-wrapper">

            <div className="leaderboard-table-top">

              <div>

                <span className="leaderboard-live-dot">
                  ●
                </span>

                Live Rankings

              </div>

              <span>
                {filteredLeaderboard.length}{' '}
                {filteredLeaderboard.length === 1
                  ? 'player'
                  : 'players'}
              </span>

            </div>


            <table className="leaderboard-table">

              <thead>

                <tr>

                  <th>
                    Rank
                  </th>

                  <th>
                    Player
                  </th>

                  {isGlobal ? (
                    <>
                      <th>
                        XP
                      </th>

                      <th>
                        Level
                      </th>
                    </>
                  ) : (
                    <>
                      <th>
                        Score
                      </th>

                      <th>
                        Correct
                      </th>

                      <th>
                        Submitted
                      </th>
                    </>
                  )}

                </tr>

              </thead>


              <tbody>

                {filteredLeaderboard.map(
                  (entry, index) => {

                    const rank =
                      entry.rank ??
                      index + 1;

                    const playerName =
                      entry.userName ||
                      entry.name ||
                      'Unknown User';

                    return (
                      <tr
                        key={
                          isGlobal
                            ? entry.userId
                            : `${entry.userId}-${entry.challengeId}`
                        }
                        className={
                          rank <= 3
                            ? `leaderboard-top-${rank}`
                            : ''
                        }
                      >

                        <td>

                          <span className="leaderboard-rank">

                            {getRankIcon(rank)}

                          </span>

                        </td>


                        <td>

                          <div className="leaderboard-player">

                            <div className="leaderboard-avatar">
                              {getPlayerInitial(entry)}
                            </div>

                            <div>

                              <strong>
                                {playerName}
                              </strong>

                              {rank <= 3 && (
                                <span className="leaderboard-champion-label">
                                  ⭐ Top Performer
                                </span>
                              )}

                            </div>

                          </div>

                        </td>


                        {isGlobal ? (

                          <>

                            <td>

                              <strong className="leaderboard-score">
                                {entry.xp ?? 0}
                                <span>
                                  XP
                                </span>
                              </strong>

                            </td>


                            <td>

                              <span className="leaderboard-level">
                                ✦ Level {entry.level ?? 1}
                              </span>

                            </td>

                          </>

                        ) : (

                          <>

                            <td>

                              <strong className="leaderboard-score">
                                {entry.score ?? 0}
                                <span>
                                  %
                                </span>
                              </strong>

                            </td>


                            <td>

                              <span className="leaderboard-correct">
                                ✓{' '}
                                {entry.correctAnswers ?? 0}
                                {' / '}
                                {entry.totalQuestions ?? 0}
                              </span>

                            </td>


                            <td>

                              <span className="leaderboard-date">
                                🕐{' '}
                                {entry.submittedAt
                                  ? new Date(
                                      entry.submittedAt
                                    ).toLocaleString()
                                  : 'Unknown'}
                              </span>

                            </td>

                          </>

                        )}

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

export default Leaderboard;