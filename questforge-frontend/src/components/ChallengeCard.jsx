import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/ChallengeCard.css';

function ChallengeCard({ challenge }) {
  if (!challenge) {
    return null;
  }

  const {
    id = '#',
    title = 'Untitled Challenge',
    description = 'No description available.',
    difficulty = 'UNKNOWN',
    category = 'General',
  } = challenge;

  const difficultyClass = difficulty.toLowerCase().replace(/\s+/g, '-');

  return (
    <div className="challenge-card">
      <div className="challenge-top">
        <span className="challenge-id">
          Challenge #{id}
        </span>

        <span className={`difficulty difficulty-${difficultyClass}`}>
          {difficulty}
        </span>
      </div>

      <h2 className="challenge-title">
        {title}
      </h2>

      <p className="description">
        {description}
      </p>

      <div className="challenge-footer">
        <span className="category">
          {category}
        </span>

        <Link
          className="solve-button"
          to={`/solve/${id}`}
        >
          Solve Challenge →
        </Link>
      </div>
    </div>
  );
}

export default ChallengeCard;