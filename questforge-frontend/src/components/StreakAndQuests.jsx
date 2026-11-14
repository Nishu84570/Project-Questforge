
import React, { useCallback, useEffect, useState } from 'react';
import { apiRequest } from '../services/api';
import '../styles/Streak.css';

const ENDPOINTS = {
  streak: '/streak',
  daily: '/daily-quest',
  weekly: '/weekly-quest',
};

const getProgress = (progress, target, completed = false) => {
  const current = Math.max(0, Number(progress) || 0);
  const total = Math.max(0, Number(target) || 0);

  const percentage = completed
    ? 100
    : total > 0
      ? Math.min(100, Math.round((current / total) * 100))
      : 0;

  return { current, total, percentage };
};

const formatDate = (date) => {
  if (!date) return 'Not available';

  const parsed = new Date(date);

  if (Number.isNaN(parsed.getTime())) return String(date);

  return parsed.toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });
};

function Icon({ name, size = 20 }) {
  const props = {
    width: size,
    height: size,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    'aria-hidden': true,
  };

  const paths = {
    refresh: (
      <>
        <path d="M20 7v5h-5" />
        <path d="M4 17v-5h5" />
        <path d="M5.6 9a7 7 0 0 1 11.6-2L20 12" />
        <path d="M18.4 15a7 7 0 0 1-11.6 2L4 12" />
      </>
    ),
    flame: (
      <path d="M12 22c4.5 0 7-3.1 7-7 0-2.5-1.2-4.5-3.5-7-.3 2-1.3 3-2.5 3.5C13.5 7.5 11 3.5 8 2c.5 4-1 6-2.5 8C4.2 12 4 13.5 4 15c0 4 3 7 8 7Z" />
    ),
    trophy: (
      <>
        <path d="M8 21h8" />
        <path d="M12 17v4" />
        <path d="M7 4h10v5a5 5 0 0 1-10 0V4Z" />
        <path d="M7 6H4v2a4 4 0 0 0 4 4" />
        <path d="M17 6h3v2a4 4 0 0 1-4 4" />
      </>
    ),
    target: (
      <>
        <circle cx="12" cy="12" r="9" />
        <circle cx="12" cy="12" r="5" />
        <circle cx="12" cy="12" r="1" />
      </>
    ),
    calendar: (
      <>
        <rect x="3" y="5" width="18" height="16" rx="2" />
        <path d="M16 3v4M8 3v4M3 11h18" />
      </>
    ),
    zap: (
      <path d="m13 2-3 8h7l-6 12 2-9H6l7-11Z" />
    ),
    check: <path d="m5 12 4 4L19 6" />,
    clock: (
      <>
        <circle cx="12" cy="12" r="9" />
        <path d="M12 7v5l3 2" />
      </>
    ),
  };

  return <svg {...props}>{paths[name] || null}</svg>;
}

function ProgressBar({ progress, target, completed }) {
  const { current, total, percentage } = getProgress(
    progress,
    target,
    completed
  );

  return (
    <div className="quest-progress">
      <div className="quest-progress-info">
        <span>Completion progress</span>
        <strong>{percentage}%</strong>
      </div>

      <div
        className="progress-track"
        role="progressbar"
        aria-valuenow={percentage}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-label="Quest completion"
      >
        <div
          className={`progress-fill ${completed ? 'progress-complete' : ''}`}
          style={{ width: `${percentage}%` }}
        />
      </div>

      <div className="progress-footer">
        <span>{current} of {total} completed</span>
        <span>
          {completed
            ? 'Goal achieved!'
            : `${Math.max(0, total - current)} remaining`}
        </span>
      </div>
    </div>
  );
}

function LoadingSkeleton() {
  return (
    <section className="streak-container" aria-label="Loading quest progress">
      <div className="streak-header">
        <div className="skeleton-group">
          <div className="skeleton skeleton-eyebrow" />
          <div className="skeleton skeleton-heading" />
          <div className="skeleton skeleton-description" />
        </div>
      </div>

      <div className="skeleton streak-skeleton" />

      <div className="skeleton-quests">
        <div className="skeleton quest-skeleton" />
        <div className="skeleton quest-skeleton" />
      </div>
    </section>
  );
}

function QuestCard({ quest, type, error }) {
  const isDaily = type === 'daily';
  const completed = Boolean(quest?.completed);

  const title = isDaily ? 'Daily Quest' : 'Weekly Quest';
  const dateLabel = isDaily ? 'Quest Date' : 'Week Starting';

  if (!quest) {
    return (
      <article className="premium-card quest-card quest-unavailable">
        <div className="card-heading">
          <div className="card-icon">
            <Icon name={isDaily ? 'calendar' : 'trophy'} size={22} />
          </div>
          <div className="card-title-group">
            <span className="card-label">{title}</span>
            <h3>Quest unavailable</h3>
          </div>
        </div>

        <div className="empty-message">
          <Icon name="clock" size={18} />
          <p>
            {error || `No active ${type} quest available right now.`}
          </p>
        </div>
      </article>
    );
  }

  return (
    <article
      className={`premium-card quest-card ${
        completed ? 'completed-card' : ''
      }`}
    >
      <div className="card-top">
        <div className="card-heading">
          <div className={`card-icon ${isDaily ? 'daily-icon' : 'weekly-icon'}`}>
            <Icon name={isDaily ? 'calendar' : 'trophy'} size={22} />
          </div>

          <div className="card-title-group">
            <span className="card-label">{title}</span>
            <h3>{quest.title || 'Untitled Quest'}</h3>
          </div>
        </div>

        <span className={`quest-status ${completed ? 'completed' : 'pending'}`}>
          <span className="status-dot" />
          {completed ? 'Completed' : 'In Progress'}
        </span>
      </div>

      <div className="quest-date">
        <span>{dateLabel}</span>
        <strong>
          {formatDate(isDaily ? quest.questDate : quest.weekStart)}
        </strong>
      </div>

      <ProgressBar
        progress={quest.progress}
        target={quest.target}
        completed={completed}
      />

      <div className="reward-box">
        <div className="reward-icon">
          <Icon name="zap" size={21} />
        </div>

        <div className="reward-details">
          <span>Quest reward</span>
          <strong>+{Math.max(0, Number(quest.rewardXp) || 0)} XP</strong>
        </div>

        <span className="reward-tag">XP REWARD</span>
      </div>
    </article>
  );
}

function StreakAndQuests() {
  const [streakData, setStreakData] = useState(null);
  const [dailyQuest, setDailyQuest] = useState(null);
  const [weeklyQuest, setWeeklyQuest] = useState(null);

  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [errors, setErrors] = useState({});

  const fetchData = useCallback(async () => {
    setLoading(true);
    setRefreshing(true);
    setErrors({});

    const requests = [
      { key: 'streak', endpoint: ENDPOINTS.streak },
      { key: 'daily', endpoint: ENDPOINTS.daily },
      { key: 'weekly', endpoint: ENDPOINTS.weekly },
    ];

    try {
      const results = await Promise.allSettled(
        requests.map(async ({ key, endpoint }) => {
          const result = await apiRequest(endpoint, {
            method: 'GET',
          });

          if (!result?.response?.ok) {
            throw new Error(`Unable to load ${key} data.`);
          }

          return { key, data: result.data };
        })
      );

      const newErrors = {};

      results.forEach((result, index) => {
        const key = requests[index].key;

        if (result.status === 'fulfilled') {
          const data = result.value.data;

          if (key === 'streak') setStreakData(data);
          if (key === 'daily') setDailyQuest(data);
          if (key === 'weekly') setWeeklyQuest(data);
        } else {
          console.error(`Error loading ${key}:`, result.reason);
          newErrors[key] =
            result.reason?.message || 'Something went wrong.';
        }
      });

      setErrors(newErrors);
    } catch (error) {
      console.error('Unexpected quest loading error:', error);
      setErrors({ general: 'Unable to load quest information.' });
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData, refreshKey]);

  if (loading) {
    return <LoadingSkeleton />;
  }

  const currentStreak = Math.max(
    0,
    Number(streakData?.currentStreak) || 0
  );

  const longestStreak = Math.max(
    0,
    Number(streakData?.longestStreak) || 0
  );

  return (
    <section className="streak-container">
      {/* HEADER */}
      <header className="streak-header">
        <div className="streak-heading-content">
          <span className="section-eyebrow">YOUR ACHIEVEMENTS</span>
          <h2>Quest Progress</h2>
          <p>Build consistency. Complete quests. Earn rewards.</p>
        </div>

        <button
          type="button"
          className={`refresh-button ${refreshing ? 'is-refreshing' : ''}`}
          onClick={() => setRefreshKey((prev) => prev + 1)}
          disabled={refreshing}
          aria-label="Refresh quest progress"
        >
          <Icon name="refresh" size={17} />
          <span>Refresh</span>
        </button>
      </header>

      {/* API WARNING */}
      {(Object.keys(errors).length > 0) && (
        <div className="streak-warning" role="alert">
          <span className="warning-symbol">!</span>
          <p>
            Some progress data could not be loaded.
            Other sections are still available.
          </p>
        </div>
      )}

      {/* STREAK HERO */}
      <section className="streak-hero">
        <div className="streak-glow streak-glow-one" />
        <div className="streak-glow streak-glow-two" />

        <div className="streak-main">
          <div className="streak-flame">
            <Icon name="flame" size={43} />
          </div>

          <div className="streak-content">
            <span className="streak-label">CURRENT STREAK</span>

            <div className="streak-number">
              {errors.streak ? '--' : currentStreak}
              <span>days</span>
            </div>

            <p>
              {errors.streak
                ? 'Streak information is temporarily unavailable.'
                : currentStreak > 0
                  ? 'Amazing progress! Keep your momentum going.'
                  : 'Start your coding streak today!'}
            </p>
          </div>
        </div>

        <div className="streak-divider" />

        <div className="streak-stats">
          <div className="streak-stat">
            <div className="stat-icon trophy-stat">
              <Icon name="trophy" size={21} />
            </div>
            <div>
              <strong>{errors.streak ? '--' : longestStreak}</strong>
              <span>Longest Streak</span>
            </div>
          </div>

          <div className="streak-stat">
            <div className="stat-icon target-stat">
              <Icon name="target" size={21} />
            </div>
            <div>
              <strong>{errors.streak ? '--' : currentStreak}</strong>
              <span>Current Days</span>
            </div>
          </div>
        </div>
      </section>

      {/* QUEST SECTION */}
      <section className="quests-section">
        <div className="quests-section-heading">
          <div>
            <span className="section-eyebrow">YOUR MISSIONS</span>
            <h3>Active Quests</h3>
            <p>Small goals. Meaningful progress.</p>
          </div>

          <span className="quest-count">
            {Number(Boolean(dailyQuest)) + Number(Boolean(weeklyQuest))}
            {' '}AVAILABLE
          </span>
        </div>

        <div className="quests-grid">
          <QuestCard
            quest={dailyQuest}
            type="daily"
            error={errors.daily}
          />

          <QuestCard
            quest={weeklyQuest}
            type="weekly"
            error={errors.weekly}
          />
        </div>
      </section>

      {/* FOOTER */}
      <footer className="streak-footer">
        <span className="footer-icon">
          <Icon name="zap" size={19} />
        </span>
        <p>Consistency beats intensity. Keep showing up!</p>
      </footer>
    </section>
  );
}

export default StreakAndQuests;
