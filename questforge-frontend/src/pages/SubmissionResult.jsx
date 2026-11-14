import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/SubmissionResult.css';

function SubmissionResult() {
  const { submissionId } = useParams();

  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;

    const fetchSubmission = async () => {
      if (!submissionId || !/^\d+$/.test(submissionId)) {
        setError('Invalid submission ID.');
        setLoading(false);
        return;
      }

      setLoading(true);
      setError('');
      setSubmission(null);

      try {
        const { response, data } = await apiRequest(
          `/submissions/${submissionId}`,
          { method: 'GET' }
        );

        if (!isMounted) return;

        if (!response.ok) {
          const message =
            data && typeof data === 'object'
              ? data.message || data.error
              : null;

          setError(
            message ||
            `Unable to load this submission. (HTTP ${response.status})`
          );
          return;
        }

        if (!data || typeof data !== 'object' || data.id == null) {
          setError('The server returned an invalid submission response.');
          return;
        }

        setSubmission(data);
      } catch (err) {
        console.error('Failed to fetch submission:', err);

        if (isMounted) {
          setError(
            'Could not connect to the QuestForge backend. Please check your connection and try again.'
          );
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchSubmission();

    return () => {
      isMounted = false;
    };
  }, [submissionId]);

  if (loading) {
    return (
      <main className="page submission-result-page">
        <div className="sr-state-card">
          <div className="sr-spinner" />
          <h2>Loading submission</h2>
          <p>Fetching your challenge performance...</p>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="page submission-result-page">
        <div className="sr-state-card sr-error-card">
          <div className="sr-state-icon">!</div>
          <h2>Unable to load result</h2>
          <p>{error}</p>

          <div className="sr-state-actions">
            <button
              type="button"
              className="sr-button sr-button-primary"
              onClick={() => window.location.reload()}
            >
              Try Again
            </button>

            <Link
              to="/submissions"
              className="sr-button sr-button-secondary"
            >
              Back to submissions
            </Link>
          </div>
        </div>
      </main>
    );
  }

  if (!submission) {
    return (
      <main className="page submission-result-page">
        <div className="sr-state-card">
          <div className="sr-state-icon">?</div>
          <h2>Submission not found</h2>
          <p>We could not find the requested submission.</p>
          <Link to="/submissions" className="sr-button sr-button-primary">
            Back to submissions
          </Link>
        </div>
      </main>
    );
  }

  const totalQuestions = Number(submission.totalQuestions) || 0;
  const correctAnswers = Number(submission.correctAnswers) || 0;
  const score = Math.min(100, Math.max(0, Number(submission.score) || 0));

  const accuracy =
    totalQuestions > 0
      ? Math.round((correctAnswers / totalQuestions) * 100)
      : 0;

  const incorrectAnswers = Math.max(
    0,
    totalQuestions - correctAnswers
  );

  const status = String(submission.status || 'PENDING').toUpperCase();
  const firstSubmission = submission.firstSubmission === true;

  const submittedDate = submission.submittedAt
    ? new Date(submission.submittedAt).toLocaleString()
    : 'Not available';

  const scoreMessage =
    score === 100
      ? 'Perfect performance!'
      : score >= 75
        ? 'Excellent work!'
        : score >= 50
          ? 'Good effort. Keep improving!'
          : 'Keep practicing. You can do better!';

  return (
    <main className="page submission-result-page">
      <div className="sr-container">

        {/* Page heading */}
        <header className="sr-page-header">
          <Link to="/submissions" className="sr-back-link">
            <span>←</span> My Submissions
          </Link>

          <div className="sr-heading-content">
            <span className="sr-overline">QUESTFORGE / PERFORMANCE</span>
            <h1>Submission Result</h1>
            <p>Your challenge performance at a glance.</p>
          </div>

          <span className="sr-submission-id">
            ID #{submission.id}
          </span>
        </header>

        {/* Main score card */}
        <section className="sr-score-panel">
          <div className="sr-score-content">
            <span className="sr-overline">YOUR FINAL SCORE</span>

            <div className="sr-score-value">
              {score}
              <span>%</span>
            </div>

            <h2>{scoreMessage}</h2>

            <p>
              You answered {correctAnswers} out of {totalQuestions} questions
              correctly.
            </p>

            <div
              className="sr-progress-track"
              role="progressbar"
              aria-label="Final score"
              aria-valuenow={score}
              aria-valuemin={0}
              aria-valuemax={100}
            >
              <div
                className="sr-progress-fill"
                style={{ width: `${score}%` }}
              />
            </div>
          </div>

          <div className="sr-score-side">
            <div
              className="sr-score-circle"
              style={{
              background: `conic-gradient(
              #8b7cf6 ${accuracy * 3.6}deg,
              rgba(255, 255, 255, 0.09) ${accuracy * 3.6}deg
             )`,
            }}>
            <div>
                <strong>{accuracy}%</strong>
                <span>Accuracy</span>
              </div>
            </div>
          </div>
        </section>

        {/* Statistics */}
        <section className="sr-section">
          <div className="sr-section-heading">
            <div>
              <span className="sr-overline">OVERVIEW</span>
              <h2>Performance breakdown</h2>
            </div>
          </div>

          <div className="sr-stat-grid">
            <article className="sr-stat-card">
              <div className="sr-stat-top">
                <span className="sr-stat-icon sr-purple">Q</span>
                <span className="sr-stat-label">Total questions</span>
              </div>
              <strong>{totalQuestions}</strong>
              <small>Questions in this challenge</small>
            </article>

            <article className="sr-stat-card">
              <div className="sr-stat-top">
                <span className="sr-stat-icon sr-green">✓</span>
                <span className="sr-stat-label">Correct answers</span>
              </div>
              <strong>{correctAnswers}</strong>
              <small>Questions answered correctly</small>
            </article>

            <article className="sr-stat-card">
              <div className="sr-stat-top">
                <span className="sr-stat-icon sr-red">×</span>
                <span className="sr-stat-label">Incorrect answers</span>
              </div>
              <strong>{incorrectAnswers}</strong>
              <small>Questions not answered correctly</small>
            </article>

            <article className="sr-stat-card">
              <div className="sr-stat-top">
                <span className="sr-stat-icon sr-blue">%</span>
                <span className="sr-stat-label">Accuracy</span>
              </div>
              <strong>{accuracy}%</strong>
              <small>Correct answers percentage</small>
            </article>
          </div>
        </section>

        {/* Submission details */}
        <section className="sr-section">
          <div className="sr-section-heading">
            <div>
              <span className="sr-overline">INFORMATION</span>
              <h2>Submission details</h2>
            </div>
          </div>

          <div className="sr-details-card">
            <div className="sr-detail-row">
              <span>Submission ID</span>
              <strong>#{submission.id}</strong>
            </div>

            <div className="sr-detail-row">
              <span>Challenge ID</span>
              <strong>
                {submission.challengeId != null
                  ? `#${submission.challengeId}`
                  : 'Not available'}
              </strong>
            </div>

            <div className="sr-detail-row">
              <span>Submitted at</span>
              <strong>{submittedDate}</strong>
            </div>

            <div className="sr-detail-row">
              <span>Review status</span>
              <strong className={`sr-status sr-status-${status.toLowerCase()}`}>
                <span />
                {status}
              </strong>
            </div>

            <div className="sr-detail-row">
              <span>Attempt type</span>
              <strong>
                {firstSubmission ? 'First attempt' : 'Practice attempt'}
              </strong>
            </div>

            <div className="sr-detail-row">
              <span>Leaderboard eligibility</span>
              <strong className={firstSubmission ? 'sr-eligible' : 'sr-not-eligible'}>
                {firstSubmission ? 'Eligible' : 'Not eligible'}
              </strong>
            </div>
          </div>
        </section>

        {/* Submitted answers */}
        {submission.answer && (
          <section className="sr-section">
            <div className="sr-section-heading">
              <div>
                <span className="sr-overline">YOUR RESPONSE</span>
                <h2>Submitted answers</h2>
              </div>
            </div>

            <div className="sr-answer-card">
              <pre>{submission.answer}</pre>
            </div>
          </section>
        )}

        {/* Actions */}
        <section className="sr-actions">
          {submission.challengeId != null && (
            <Link
              to={`/solve/${submission.challengeId}`}
              className="sr-button sr-button-primary"
            >
              Try challenge again <span>→</span>
            </Link>
          )}

          <Link
            to="/submissions"
            className="sr-button sr-button-secondary"
          >
            All submissions
          </Link>

          {firstSubmission && (
            <Link
              to={`/leaderboard?challengeId=${submission.challengeId}`}
              className="sr-button sr-button-secondary"
            >
              View leaderboard
            </Link>
          )}
        </section>

        <footer className="sr-footer">
          Keep learning. Keep building. Keep leveling up.
        </footer>
      </div>
    </main>
  );
}

export default SubmissionResult;