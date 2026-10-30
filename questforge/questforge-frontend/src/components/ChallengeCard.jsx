import { Link } from 'react-router-dom';

function ChallengeCard({ challenge }) {

  if (!challenge) {
    return null;
  }

  const difficulty =
    challenge.difficulty || 'UNKNOWN';

  const difficultyClass =
    difficulty.toLowerCase().replace(/\s+/g, '-');

  return (
    <div className="challenge-card">

      <div className="challenge-top">

        <span className="challenge-id">
          Challenge #{challenge.id}
        </span>

        <span
          className={`difficulty difficulty-${difficultyClass}`}
        >
          {difficulty}
        </span>

      </div>

      <h2>
        {challenge.title || 'Untitled Challenge'}
      </h2>

      <p className="description">
        {challenge.description ||
          'No description available.'}
      </p>

      <div className="challenge-footer">

        <span className="category">
          {challenge.category || 'General'}
        </span>

        <Link
          className="solve-button"
          to={`/solve/${challenge.id}`}
        >
          Solve Challenge →
        </Link>

      </div>

    </div>
  );
}

export default ChallengeCard;