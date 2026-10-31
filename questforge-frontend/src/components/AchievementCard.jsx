import React from 'react';
import '../styles/AchievementCard.css';

function AchievementCard({ achievement }) {
  if (!achievement) {
    return null;
  }

  const icon = achievement.icon || '🏆';
  const name = achievement.name || 'Achievement';
  const description = achievement.description || 'Achievement unlocked!';
  const code = achievement.code || 'ACHIEVEMENT';

  return (
    <div className="challenge-card achievement-card">
      <div className="challenge-top">
        <span className="challenge-id">
          Badge #{achievement.id ?? '-'}
        </span>
        <span className="submission-status status-approved">
          ✓ Unlocked
        </span>
      </div>

      <div className="achievement-icon-wrapper">
        <div className="icon-glow"></div>
        <span className="achievement-emoji">{icon}</span>
      </div>

      <h2>
        {name}
      </h2>

      <p className="description">
        {description}
      </p>

      <div className="challenge-footer">
        <span className="achievement-code-badge">
          {code}
        </span>
        <span className="brand-name">
          QuestForge
        </span>
      </div>
    </div>
  );
}

export default AchievementCard;