import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/SubmissionCard.css';

function SubmissionCard({
  submission,
  admin = false,
  onStatusUpdate,
  updating = false,
  loading = false,
}) {
  // Skeleton Loading State
  if (loading) {
    return (
      <article className="challenge-card submission-card submission-skeleton" aria-hidden="true">
        <div className="skeleton-header shimmer" />
        <div className="skeleton-content shimmer" />
        <div className="skeleton-footer shimmer" />
      </article>
    );
  }

  if (!submission) return null;

  const status = submission.status?.toUpperCase() || 'UNKNOWN';

  const submittedDate = submission.submittedAt
    ? new Date(submission.submittedAt).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : 'Unknown';

  const totalQuestions = submission.totalQuestions ?? 0;
  const correctAnswers = submission.correctAnswers ?? 0;
  const score = submission.score ?? 0;
  const challengeId = submission.challengeId ?? 'Unknown';
  const percentage = Math.max(0, Math.min(100, Number(score) || 0));

  return (
    <article 
      className="challenge-card submission-card"
      tabIndex="0"
      aria-label={`Submission #${submission.id} for Challenge ${challengeId}, Status: ${status}`}
    >
      <div className="submission-card-glow" />
      <div className="submission-card-shimmer" />

      {/* HEADER */}
      <div className="challenge-top submission-card-header">
        <div className="submission-id-group">
          <span className="submission-icon" aria-hidden="true">⌘</span>
          <span className="challenge-id">
            SUBMISSION #{submission.id}
          </span>
        </div>

        <span className={`submission-status status-${status.toLowerCase()}`}>
          <span className="status-dot" aria-hidden="true" />
          {status}
        </span>
      </div>

      {/* CONTENT */}
      <div className="submission-card-content">
        <span className="submission-overline">
          CODING CHALLENGE
        </span>

        <h2 className="challenge-title">
          Challenge #{challengeId}
        </h2>

        {/* ANSWERS */}
        <div className="submission-answer">
          <div>
            <span className="submission-label">
              Correct Answers
            </span>
            <p className="submission-answer-value">
              {correctAnswers}
              <span> / {totalQuestions}</span>
            </p>
          </div>

          <div className="submission-answer-icon" aria-hidden="true">
            ✓
          </div>
        </div>

        {/* SCORE */}
        <div className="submission-score">
          <div className="submission-score-top">
            <span className="submission-label">Your Score</span>
            <strong className="score-value">{score}%</strong>
          </div>

          <div 
            className="submission-progress-track"
            role="progressbar"
            aria-valuenow={percentage}
            aria-valuemin="0"
            aria-valuemax="100"
          >
            <div
              className="submission-progress-fill"
              style={{ width: `${percentage}%` }}
            />
          </div>
        </div>
      </div>

      {/* FOOTER */}
      <div className="challenge-footer submission-card-footer">
        <span>
          {admin ? `User #${submission.userId}` : '◷ Submitted'}
        </span>
        <span>{submittedDate}</span>
      </div>

      {/* ACTIONS */}
      {admin ? (
        <div className="submission-actions">
          <button
            type="button"
            className="primary-button approve-btn"
            onClick={() => onStatusUpdate?.(submission.id, 'APPROVED')}
            disabled={updating}
            aria-label={`Approve submission ${submission.id}`}
          >
            {updating ? (
              <span className="btn-spinner-text">
                <span className="mini-spinner" /> Updating...
              </span>
            ) : (
              '✓ Approve'
            )}
          </button>

          <button
            type="button"
            className="secondary-button reject-btn"
            onClick={() => onStatusUpdate?.(submission.id, 'REJECTED')}
            disabled={updating}
            aria-label={`Reject submission ${submission.id}`}
          >
            {updating ? (
              <span className="btn-spinner-text">
                <span className="mini-spinner" /> Updating...
              </span>
            ) : (
              '✕ Reject'
            )}
          </button>
        </div>
      ) : (
        <Link
          to={`/submission-result/${submission.id}`}
          className="view-result-button submission-view-link"
          aria-label={`View result for submission ${submission.id}`}
        >
          View Result
          <span aria-hidden="true">→</span>
        </Link>
      )}
    </article>
  );
}

export default SubmissionCard;