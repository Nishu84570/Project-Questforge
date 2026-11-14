
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { apiRequest } from '../services/api';
import '../styles/StreakCalendar.css';

const API = {
  streak: '/streak',
  daily: '/daily-quest',
  weekly: '/weekly-quest',
};

const WEEKDAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

const getLocalDateKey = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};

const getCurrentMonth = () => {
  const today = new Date();

  return `${today.getFullYear()}-${String(
    today.getMonth() + 1
  ).padStart(2, '0')}`;
};

const formatDate = (date) => {
  if (!date) return 'Not available';

  return date.toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
};

const formatMonth = (year, month) => {
  return new Date(year, month - 1, 1).toLocaleDateString('en-IN', {
    month: 'long',
    year: 'numeric',
  });
};

const getProgress = (progress, target) => {
  const current = Math.max(0, Number(progress) || 0);
  const total = Math.max(0, Number(target) || 0);

  return {
    current,
    total,
    percentage: total > 0
      ? Math.min(100, Math.round((current / total) * 100))
      : 0,
  };
};

/* ==============================
   LOADING SKELETON
================================ */

function CalendarSkeleton() {
  return (
    <div className="sc-container">
      <div className="sc-skeleton sc-skeleton-header" />
      <div className="sc-skeleton sc-skeleton-hero" />
      <div className="sc-skeleton sc-skeleton-calendar" />
      <div className="sc-skeleton sc-skeleton-quests" />
    </div>
  );
}

/* ==============================
   STAT CARD
================================ */

function StatCard({ icon, title, value, description }) {
  return (
    <div className="sc-stat-card">
      <div className="sc-stat-icon">{icon}</div>

      <div className="sc-stat-info">
        <span>{title}</span>
        <strong>{value}</strong>
        <small>{description}</small>
      </div>
    </div>
  );
}

/* ==============================
   QUEST CARD
================================ */

function QuestCard({ quest, type, error }) {
  const isDaily = type === 'daily';
  const completed = Boolean(quest?.completed);

  const title = isDaily ? 'Daily Quest' : 'Weekly Quest';
  const icon = isDaily ? '📅' : '🏆';

  if (!quest) {
    return (
      <article className="sc-quest-card">
        <div className="sc-quest-heading">
          <div className="sc-quest-icon">{icon}</div>

          <div>
            <span className="sc-quest-label">{title}</span>
            <h3>Quest unavailable</h3>
          </div>
        </div>

        <p className="sc-empty-message">
          {error || `No active ${type} quest available.`}
        </p>
      </article>
    );
  }

  const progress = getProgress(quest.progress, quest.target);
  const percentage = completed ? 100 : progress.percentage;
  const reward = Math.max(0, Number(quest.rewardXp) || 0);

  return (
    <article className="sc-quest-card">

      <div className="sc-quest-top">
        <div className="sc-quest-heading">
          <div className="sc-quest-icon">{icon}</div>

          <div>
            <span className="sc-quest-label">{title}</span>
            <h3>{quest.title || 'Untitled Quest'}</h3>
          </div>
        </div>

        <span className={`sc-status ${completed ? 'is-completed' : 'is-pending'}`}>
          <span />
          {completed ? 'Completed' : 'In Progress'}
        </span>
      </div>

      <div className="sc-quest-meta">
        <span>{isDaily ? 'Quest Date' : 'Week Starting'}</span>

        <strong>
          {isDaily
            ? (quest.questDate || 'Not available')
            : (quest.weekStart || 'Not available')}
        </strong>
      </div>

      <div className="sc-progress-section">
        <div className="sc-progress-label">
          <span>Quest Progress</span>
          <strong>
            {progress.current} / {progress.total}
          </strong>
        </div>

        <div
          className="sc-progress-track"
          role="progressbar"
          aria-valuenow={percentage}
          aria-valuemin={0}
          aria-valuemax={100}
          aria-label={`${title} progress`}
        >
          <div
            className={`sc-progress-fill ${completed ? 'is-completed' : ''}`}
            style={{ width: `${percentage}%` }}
          />
        </div>

        <div className="sc-progress-footer">
          <span>{percentage}% completed</span>

          <span>
            {completed
              ? 'Goal achieved!'
              : `${Math.max(0, progress.total - progress.current)} remaining`}
          </span>
        </div>
      </div>

      <div className="sc-reward">
        <div className="sc-reward-icon">⚡</div>

        <div>
          <small>Quest Reward</small>
          <strong>+{reward} XP</strong>
        </div>

        <span className="sc-reward-tag">XP REWARD</span>
      </div>

    </article>
  );
}

/* ==============================
   MAIN COMPONENT
================================ */

function StreakCalendar() {
  const [streakData, setStreakData] = useState(null);
  const [dailyQuest, setDailyQuest] = useState(null);
  const [weeklyQuest, setWeeklyQuest] = useState(null);

  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState({});
  const [refreshKey, setRefreshKey] = useState(0);

  // Direct month/year navigation
  const [selectedMonth, setSelectedMonth] = useState(getCurrentMonth);

  // Selected calendar date
  const [selectedDate, setSelectedDate] = useState(null);

  /* ==============================
     FETCH API DATA
  ================================ */

  const fetchData = useCallback(async () => {
    setLoading(true);
    setErrors({});

    const requests = [
      { key: 'streak', endpoint: API.streak },
      { key: 'daily', endpoint: API.daily },
      { key: 'weekly', endpoint: API.weekly },
    ];

    const results = await Promise.allSettled(
      requests.map(async ({ endpoint }) => {
        const result = await apiRequest(endpoint, {
          method: 'GET',
        });

        if (!result?.response?.ok) {
          throw new Error('Unable to fetch data.');
        }

        return result.data;
      })
    );

    const newErrors = {};

    results.forEach((result, index) => {
      const key = requests[index].key;

      if (result.status === 'fulfilled') {
        if (key === 'streak') setStreakData(result.value);
        if (key === 'daily') setDailyQuest(result.value);
        if (key === 'weekly') setWeeklyQuest(result.value);
      } else {
        console.error(`Failed to load ${key}:`, result.reason);

        newErrors[key] =
          result.reason?.message || 'Failed to load information.';
      }
    });

    setErrors(newErrors);
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData, refreshKey]);

  /* ==============================
     STREAK STATISTICS
  ================================ */

  const currentStreak = Math.max(
    0,
    Number(streakData?.currentStreak) || 0
  );

  const longestStreak = Math.max(
    0,
    Number(streakData?.longestStreak) || 0
  );

  /* ==============================
     CALENDAR DATE CALCULATIONS
  ================================ */

  const { year, month } = useMemo(() => {
    const [selectedYear, selectedMonthValue] = selectedMonth.split('-');

    return {
      year: Number(selectedYear),
      month: Number(selectedMonthValue),
    };
  }, [selectedMonth]);

  const daysInMonth = useMemo(() => {
    return new Date(year, month, 0).getDate();
  }, [year, month]);

  const firstDayOfMonth = useMemo(() => {
    return new Date(year, month - 1, 1).getDay();
  }, [year, month]);

  const monthLabel = useMemo(() => {
    return formatMonth(year, month);
  }, [year, month]);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const todayKey = getLocalDateKey(today);

  const isCurrentMonth =
    year === today.getFullYear() &&
    month === today.getMonth() + 1;

  // Estimate current streak dates only.
  // Actual historical activity requires a backend history endpoint.
  const activeDateKeys = useMemo(() => {
    const activeDays = new Set();

    const todayDate = new Date();
    todayDate.setHours(0, 0, 0, 0);

    for (let i = 0; i < currentStreak; i++) {
      const date = new Date(todayDate);
      date.setDate(date.getDate() - i);

      activeDays.add(getLocalDateKey(date));
    }

    return activeDays;
  }, [currentStreak]);

  /* ==============================
     MONTH NAVIGATION
  ================================ */

  const changeMonth = (difference) => {
    const [currentYear, currentMonthValue] = selectedMonth.split('-');

    const newDate = new Date(
      Number(currentYear),
      Number(currentMonthValue) - 1 + difference,
      1
    );

    const newMonth = `${newDate.getFullYear()}-${String(
      newDate.getMonth() + 1
    ).padStart(2, '0')}`;

    setSelectedMonth(newMonth);
    setSelectedDate(null);
  };

  const handleMonthChange = (event) => {
    setSelectedMonth(event.target.value);
    setSelectedDate(null);
  };

  const goToToday = () => {
    setSelectedMonth(getCurrentMonth());
    setSelectedDate(todayKey);
  };

  /* ==============================
     DATE SELECTION
  ================================ */

  const handleDateClick = (day) => {
    const date = new Date(year, month - 1, day);
    const key = getLocalDateKey(date);

    setSelectedDate(key);
  };

  const selectedDateObject = useMemo(() => {
    if (!selectedDate) return null;

    const [selectedYear, selectedMonthValue, selectedDay] =
      selectedDate.split('-').map(Number);

    return new Date(selectedYear, selectedMonthValue - 1, selectedDay);
  }, [selectedDate]);

  if (loading) {
    return <CalendarSkeleton />;
  }

  /* ==============================
     RENDER
  ================================ */

  return (
    <section className="sc-container">

      {/* =================================
          ANIMATED MAIN HEADER
      ================================== */}

      <header className="sc-main-header">
        <div className="sc-header-decoration sc-decoration-one" />
        <div className="sc-header-decoration sc-decoration-two" />

        <div className="sc-header-content">
          <div className="sc-header-icon">🚀</div>

          <span className="sc-eyebrow">YOUR JOURNEY</span>

          <h1>Streak & Activity</h1>

          <p>
            Track your consistency and celebrate every achievement.
          </p>

          <div className="sc-header-line">
            <span />
          </div>
        </div>

        <button
          className="sc-refresh"
          onClick={() => setRefreshKey((key) => key + 1)}
          aria-label="Refresh streak data"
          title="Refresh data"
        >
          ↻ <span>Refresh</span>
        </button>
      </header>

      {Object.keys(errors).length > 0 && (
        <div className="sc-warning" role="alert">
          ⚠️ Some information could not be loaded. Other sections remain available.
        </div>
      )}

      {/* =================================
          STREAK HERO
      ================================== */}

      <section className="sc-hero">
        <div className="sc-hero-glow" />

        <div className="sc-hero-content">
          <div className="sc-hero-flame">🔥</div>

          <div>
            <span className="sc-hero-label">YOUR CURRENT STREAK</span>

            <div className="sc-hero-number">
              {errors.streak ? '--' : currentStreak}
              <span>Days</span>
            </div>

            <p>
              {currentStreak > 0
                ? 'You are building a great habit. Keep going!'
                : 'Every great journey starts with day one.'}
            </p>
          </div>
        </div>

        <div className="sc-hero-bottom">
          <StatCard
            icon="🏆"
            title="Longest Streak"
            value={errors.streak ? '--' : `${longestStreak} Days`}
            description="Your personal record"
          />

          <StatCard
            icon="🎯"
            title="Current Streak"
            value={errors.streak ? '--' : `${currentStreak} Days`}
            description="Your current progress"
          />
        </div>
      </section>

      {/* =================================
          CALENDAR SECTION HEADER
      ================================== */}

      <section className="sc-calendar-section">

        <div className="sc-calendar-title-box">
          <div className="sc-title-decoration" />

          <span className="sc-eyebrow">
            CONSISTENCY TRACKER
          </span>

          <div className="sc-calendar-title-icon">📅</div>

          <h2>Activity Calendar</h2>

          <p>
            Every day is another opportunity to make progress.
          </p>

          <div className="sc-title-underline">
            <span />
          </div>
        </div>

        {/* =================================
            CALENDAR CARD
        ================================== */}

        <div className="sc-calendar-card">

          {/* MONTH NAVIGATION */}

          <div className="sc-calendar-toolbar">

            <div className="sc-month-information">
              <span className="sc-month-icon">🗓️</span>

              <div>
                <span className="sc-toolbar-label">
                  SELECTED MONTH
                </span>

                <h3>{monthLabel}</h3>
              </div>
            </div>

            <div className="sc-calendar-controls">

              <button
                type="button"
                className="sc-nav-button"
                onClick={() => changeMonth(-1)}
                aria-label="Previous month"
                title="Previous month"
              >
                ‹
              </button>

              <button
                type="button"
                className="sc-nav-button"
                onClick={() => changeMonth(1)}
                aria-label="Next month"
                title="Next month"
              >
                ›
              </button>

              <button
                type="button"
                className="sc-today-button"
                onClick={goToToday}
              >
                Today
              </button>

            </div>
          </div>

          {/* DIRECT MONTH AND YEAR PICKER */}

          <div className="sc-date-jump">
            <div className="sc-date-jump-info">
              <span className="sc-date-jump-icon">✨</span>

              <div>
                <strong>Jump to any date</strong>
                <small>Select any month and year</small>
              </div>
            </div>

            <input
              type="month"
              className="sc-month-input"
              value={selectedMonth}
              onChange={handleMonthChange}
              aria-label="Choose month and year"
            />
          </div>

          {/* CALENDAR GRID */}

          <div className="sc-calendar-grid">

            {/* WEEKDAY HEADINGS */}

            {WEEKDAYS.map((day) => (
              <div key={day} className="sc-weekday">
                {day}
              </div>
            ))}

            {/* EMPTY CELLS BEFORE FIRST DAY */}

            {Array.from(
              { length: firstDayOfMonth },
              (_, index) => (
                <div
                  key={`empty-${index}`}
                  className="sc-calendar-empty"
                  aria-hidden="true"
                />
              )
            )}

            {/* ONLY SELECTED MONTH DATES */}

            {Array.from(
              { length: daysInMonth },
              (_, index) => {
                const day = index + 1;

                const date = new Date(year, month - 1, day);
                const dateKey = getLocalDateKey(date);

                const isToday = dateKey === todayKey;
                const isActive = activeDateKeys.has(dateKey);
                const isFuture = date > today;
                const isSelected = selectedDate === dateKey;

                let className = 'sc-day';

                if (isActive) className += ' sc-day-active';
                if (isFuture) className += ' sc-day-future';
                if (isToday) className += ' sc-day-today';
                if (isSelected) className += ' sc-day-selected';

                return (
                  <button
                    key={dateKey}
                    type="button"
                    className={className}
                    onClick={() => handleDateClick(day)}
                    aria-label={`${formatDate(date)}${
                      isToday ? ', Today' : ''
                    }`}
                    aria-pressed={isSelected}
                    title={`${formatDate(date)}${
                      isToday ? ' - Today' : ''
                    }`}
                  >
                    <span>{day}</span>

                    {isToday && (
                      <span className="sc-today-indicator">
                        TODAY
                      </span>
                    )}

                    {isActive && !isToday && (
                      <span className="sc-active-indicator" />
                    )}
                  </button>
                );
              }
            )}

          </div>

          {/* SELECTED DATE INFORMATION */}

          {selectedDate && selectedDateObject && (
            <div className="sc-selected-date">
              <div className="sc-selected-date-icon">📌</div>

              <div className="sc-selected-date-info">
                <span>SELECTED DATE</span>

                <strong>
                  {formatDate(selectedDateObject)}
                </strong>

                <p>
                  {selectedDate === todayKey
                    ? 'This is today!'
                    : activeDateKeys.has(selectedDate)
                      ? 'This date falls within your current streak estimate.'
                      : selectedDateObject > today
                        ? 'This date is in the future. No activity has been recorded yet.'
                        : 'Historical activity is not available from the current API.'}
                </p>
              </div>
            </div>
          )}

          {/* CALENDAR LEGEND */}

          <div className="sc-calendar-legend">

            <div>
              <span className="sc-legend-dot sc-legend-today" />
              Today
            </div>

            <div>
              <span className="sc-legend-dot sc-legend-active" />
              Current streak estimate
            </div>

            <div>
              <span className="sc-legend-dot sc-legend-normal" />
              No verified activity
            </div>

          </div>

          <div className="sc-calendar-note">
            <span>ℹ️</span>
            Streak dates are estimated from your current streak count.
            Actual historical activity requires a date-wise backend history API.
          </div>

        </div>
      </section>

      {/* =================================
          QUEST SECTION
      ================================== */}

      <section className="sc-quests-section">

        <div className="sc-section-heading">
          <div>
            <span className="sc-eyebrow">LEVEL UP</span>
            <h2>Your Active Quests</h2>
          </div>

          <span className="sc-quest-count">
            02 MISSIONS
          </span>
        </div>

        <div className="sc-quests-grid">
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

      {/* =================================
          FOOTER
      ================================== */}

      <footer className="sc-footer">
        <span>⚡</span>

        <p>
          Small steps every day lead to extraordinary progress.
        </p>
      </footer>

    </section>
  );
}

export default StreakCalendar;
