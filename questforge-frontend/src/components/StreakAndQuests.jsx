import React, { useEffect, useState } from 'react';
import { apiRequest } from '../services/api';
import '../styles/Streak.css';

function StreakAndQuests() {
  const [streakData, setStreakData] = useState(null);
  const [dailyQuest, setDailyQuest] = useState(null);
  const [weeklyQuest, setWeeklyQuest] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const streakRes = await apiRequest('/streak', { method: 'GET' });
        if (streakRes.response.ok) {
          setStreakData(streakRes.data);
        }

        const dailyRes = await apiRequest('/daily-quest', { method: 'GET' });
        if (dailyRes.response.ok) {
          setDailyQuest(dailyRes.data);
        }

        const weeklyRes = await apiRequest('/weekly-quest', { method: 'GET' });
        if (weeklyRes.response.ok) {
          setWeeklyQuest(weeklyRes.data);
        }
      } catch (err) {
        console.error('Error fetching dashboard progress:', err);
        setError('Failed to load quest and streak progress.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div className="streak-loading">Loading your quest progress...</div>;
  }

  if (error) {
    return <div className="streak-error">{error}</div>;
  }

  return (
    <div className="streak-container">
      <h2>🔥 Your Quest Progress</h2>

      {/* STREAK SECTION */}
      <div className="streak-card">
        <h3>Streak Info</h3>
        {streakData ? (
          <div>
            <p>Current Streak: <strong>{streakData.currentStreak || 0} days</strong></p>
            <p>Longest Streak: <strong>{streakData.longestStreak || 0} days</strong></p>
          </div>
        ) : (
          <p>No streak data available.</p>
        )}
      </div>

      {/* DAILY QUEST SECTION */}
      <div className="quest-card">
        <h3>📅 Daily Quest</h3>
        {dailyQuest ? (
          <div>
            <p><strong>{dailyQuest.title}</strong></p>
            <p>Date: {dailyQuest.questDate}</p>
            <p>
              Progress: <strong>{dailyQuest.progress} / {dailyQuest.target}</strong>
            </p>
            <p>Reward: 🎁 <strong>{dailyQuest.rewardXp} XP</strong></p>
            <p>
              Status:{' '}
              <span className={dailyQuest.completed ? 'status-completed' : 'status-pending'}>
                {dailyQuest.completed ? '✅ Completed' : '⏳ In Progress'}
              </span>
            </p>
          </div>
        ) : (
          <p>No active daily quest for today.</p>
        )}
      </div>

      {/* WEEKLY QUEST SECTION */}
      <div className="quest-card">
        <h3>🏆 Weekly Quest</h3>
        {weeklyQuest ? (
          <div>
            <p><strong>{weeklyQuest.title}</strong></p>
            <p>Week Starting: {weeklyQuest.weekStart}</p>
            <p>
              Progress: <strong>{weeklyQuest.progress} / {weeklyQuest.target}</strong>
            </p>
            <p>Reward: 🎁 <strong>{weeklyQuest.rewardXp} XP</strong></p>
            <p>
              Status:{' '}
              <span className={weeklyQuest.completed ? 'status-completed' : 'status-pending'}>
                {weeklyQuest.completed ? '✅ Completed' : '⏳ In Progress'}
              </span>
            </p>
          </div>
        ) : (
          <p>No active weekly quest for this week.</p>
        )}
      </div>
    </div>
  );
}

export default StreakAndQuests;