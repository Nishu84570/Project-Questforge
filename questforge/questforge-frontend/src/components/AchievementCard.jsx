function AchievementCard({
  achievement,
}) {
  if (!achievement) {
    return null;
  }

  const icon =
    achievement.icon || '🏆';

  const name =
    achievement.name ||
    'Achievement';

  const description =
    achievement.description ||
    'Achievement unlocked!';

  const code =
    achievement.code ||
    'ACHIEVEMENT';

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

      <div
        style={{
          fontSize: '3rem',
          margin: '18px 0',
        }}
      >
        {icon}
      </div>

      <h2>
        {name}
      </h2>

      <p className="description">
        {description}
      </p>

      <div className="challenge-footer">

        <span>
          {code}
        </span>

        <span>
          QuestForge
        </span>

      </div>

    </div>
  );
}

export default AchievementCard;