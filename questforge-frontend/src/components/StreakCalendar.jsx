import React, { useEffect, useState } from 'react';
import { apiRequest } from '../services/api';
import '../styles/StreakCalendar.css';

function StreakCalendar() {
  const [streakData, setStreakData] = useState(null);
  const [dailyQuest, setDailyQuest] = useState(null);
  const [weeklyQuest, setWeeklyQuest] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStreakData = async () => {
      try {
        setLoading(true);

        // Fetch Streak info
        const streakRes = await apiRequest('/streak', { method: 'GET' });
        if (streakRes.response.ok) {
          setStreakData(streakRes.data);
        }

        // Fetch Daily Quest
        const dailyRes = tab => apiRequest('/daily-quest', { method: 'GET' });
        const dailyData = await apiRequest('/daily-quest', { method: 'GET' });
        if (dailyData.response.ok) {
          setDailyQuest(dailyData.data);
        }

        // Fetch Weekly Quest
        const weeklyData = await apiRequest('/weekly-quest', { method: 'GET' });
        if (weeklyData.response.ok) {
          setWeeklyQuest(weeklyData.data);
        }
      } catch (err) {
        console.error('Error fetching streak calendar:', err);
        setError('Failed to load streak calendar data.');
      } finally {
        setLoading(false);
      }
    };

    fetchStreakData();
  }, []);

  // Generate last 30 days calendar grid for visual streak representation
  const renderCalendarGrid = (currentStreak = 0) => {
    const days = [];
    const totalDays = 30; // Last 30 days view
    for (let i = totalDays; i >= 1; i--) {
      // If i is less than or equal to currentStreak, mark it as active/completed
      const isActive = i <= currentStreak;
      days.push(
        <div
          key={i}
          className={`calendar-day ${isActive ? 'active-streak' : 'inactive-streak'}`}
          title={`Day ${i}: ${isActive ? 'Streak Maintained 🔥' : 'Missed'}`}
        >
          <span>{i}</span>
        </div>
      );
    }
    return days;
  };

  if (loading) {
    return <div className="streak-loading">Loading your streak calendar...</div>;
  }

  if (error) {
    return <div className="streak-error">{error}</div>;
  }

  const currentStreak = streakData?.currentStreak || 0;
  const longestStreak = streakData?.longestStreak || 0;

  return (
    <div className="streak-calendar-page">
      <div className="streak-calendar-container">
        <h2>🔥 Your Streak & Activity Calendar</h2>

        {/* STREAK STATS CARDS */}
        <div className="streak-stats-row">
          <div className="stat-card">
            <span className="stat-icon">⚡</span>
            <div>
              <p className="stat-title">Current Streak</p>
              <p className="stat-value">{currentStreak} Days</p>
            </div>
          </div>
          <div className="stat-card">
            <span className="stat-icon">🏆</span>
            <div>
              <p className="stat-title">Longest Streak</p>
              <p className="stat-value">{longestStreak} Days</p>
            </div>
          </div>
        </div>

        {/* CALENDAR GRID VIEW */}
        <div className="calendar-card">
          <h3>📅 30-Day Streak View</h3>
          <p className="calendar-subtitle">Visual representation of your recent consistency.</p>
          <div className="calendar-grid">
            {renderCalendarGrid(currentStreak)}
          </div>
        </div>

        {/* QUESTS SECTION */}
        <div className="quests-row">
          <div className="quest-box">
            <h4>📅 Daily Quest</h4>
            {dailyQuest ? (
              <div>
                <p><strong>{dailyQuest.title}</strong></p>
                <p>Progress: {dailyQuest.progress} / {dailyQuest.target}</p>
                <p>Reward: {dailyQuest.rewardXp} XP</p>
                <span className={`status-badge ${dailyQuest.completed ? 'completed' : 'pending'}`}>
                  {dailyQuest.completed ? '✅ Completed' : '⏳ In Progress'}
                </span>
              </div>
            ) : (
              <p>No active daily quest.</p>
            )}
          </div>

          <div className="quest-box">
            <h4>🏆 Weekly Quest</h4>
            {weeklyQuest ? (
              <div>
                <p><strong>{weeklyQuest.title}</strong></p>
                <p>Progress: {weeklyQuest.progress} / {weeklyQuest.target}</p>
                <p>Reward: {weeklyQuest.rewardXp} XP</p>
                <span className={`status-badge ${weeklyQuest.completed ? 'completed' : 'pending'}`}>
                  {weeklyQuest.completed ? '✅ Completed' : '⏳ In Progress'}
                </span>
              </div>
            ) : (
              <p>No active weekly quest.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default StreakCalendar;