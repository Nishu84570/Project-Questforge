import React from 'react';
import '../styles/SubmissionCard.css';

function SubmissionCard({
  submission,
  admin = false,
  onStatusUpdate,
  updating = false,
}) {
  if (!submission) return null;

  const status = submission.status?.toUpperCase() || 'UNKNOWN';
  const submittedDate = submission.submittedAt
    ? new Date(submission.submittedAt).toLocaleString()
    : 'Unknown';

  const totalQuestions = submission.totalQuestions ?? 0;
  const correctAnswers = submission.correctAnswers ?? 0;
  const score = submission.score ?? 0;
  const challengeId = submission.challengeId ?? 'Unknown';

  return (
    <div className="challenge-card submission-card">
      {/* HEADER */}
      <div className="challenge-top">
        <span className="challenge-id">Submission #{submission.id}</span>
        <span className={`submission-status status-${status.toLowerCase()}`}>
          {status}
        </span>
      </div>

      {/* CHALLENGE TITLE */}
      <h2 className="challenge-title">Challenge #{challengeId}</h2>

      {/* ANSWERS RESULT */}
      <div className="submission-answer">
        <span className="submission-label">Result</span>
        <p className="description">
          {correctAnswers} / {totalQuestions} correct answers
        </p>
      </div>

      {/* SCORE */}
      <div className="submission-score">
        <span className="submission-label">Score</span>
        <strong className="score-value">{score}%</strong>
      </div>

      {/* FOOTER */}
      <div className="challenge-footer">
        <span>{admin ? `User #${submission.userId}` : 'Submitted'}</span>
        <span>{submittedDate}</span>
      </div>

      {/* ADMIN ACTIONS */}
      {admin && (
        <div className="submission-actions">
          <button
            type="button"
            className="primary-button approve-btn"
            onClick={() => onStatusUpdate(submission.id, 'APPROVED')}
            disabled={updating}
          >
            {updating ? 'Updating...' : 'Approve'}
          </button>

          <button
            type="button"
            className="secondary-button reject-btn"
            onClick={() => onStatusUpdate(submission.id, 'REJECTED')}
            disabled={updating}
          >
            {updating ? 'Updating...' : 'Reject'}
          </button>
        </div>
      )}
    </div>
  );
}

export default SubmissionCard;