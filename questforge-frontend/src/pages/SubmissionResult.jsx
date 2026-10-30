import React, { useEffect, useState } from 'react';
import { Link, Navigate, useParams } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/SubmissionResult.css';

function SubmissionResult() {
  const { submissionId } = useParams();

  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSubmission = async () => {
      setLoading(true);
      setError('');

      try {
        const { response, data } = await apiRequest(
          `/submissions/${submissionId}`,
          {
            method: 'GET',
          }
        );

        if (!response.ok) {
          setError(
            data?.message || 'Failed to load submission result.'
          );
          return;
        }

        setSubmission(data);
      } catch (err) {
        console.error('Error loading submission result:', err);
        setError('Unable to connect to QuestForge backend.');
      } finally {
        setLoading(false);
      }
    };

    fetchSubmission();
  }, [submissionId]);

  if (loading) {
    return (
      <div className="page submission-result-page">
        <div className="empty-card">
          <div className="loading-spinner"></div>
          <h2>Loading Result...</h2>
          <p>Please wait while we fetch your result.</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page submission-result-page">
        <div className="empty-card error-card-container">
          <h2>Unable to Load Result</h2>
          <p className="error-text">{error}</p>
          <Link to="/submissions" className="primary-button" style={{ marginTop: '16px' }}>
            Back to My Submissions
          </Link>
        </div>
      </div>
    );
  }

  if (!submission) {
    return <Navigate to="/submissions" replace />;
  }

  const totalQuestions = submission.totalQuestions ?? 0;
  const correctAnswers = submission.correctAnswers ?? 0;
  const score = submission.score ?? 0;

  const percentage =
    totalQuestions > 0
      ? Math.round((correctAnswers / totalQuestions) * 100)
      : score;

  const status = submission.status?.toUpperCase() || 'PENDING';
  const isFirstSubmission = submission.firstSubmission === true;

  return (
    <div className="page submission-result-page">
      <div className="result-page">
        {/* HEADER */}
        <div className="result-header">
          <p className="eyebrow">QUESTFORGE</p>
          <h1>Challenge Result 🎯</h1>
          <p>Submission #{submission.id}</p>
        </div>

        {/* SCORE CARD */}
        <div className="result-score-card">
          <p className="result-score-label">YOUR SCORE</p>
          <div className="result-score">{score}%</div>
          <p className="result-score-subtitle">
            {isFirstSubmission ? 'First attempt' : 'Practice attempt'}
          </p>
        </div>

        {/* STATS */}
        <div className="result-stats">
          <div className="result-stat-card">
            <span>Total Questions</span>
            <strong>{totalQuestions}</strong>
          </div>

          <div className="result-stat-card">
            <span>Correct Answers</span>
            <strong>{correctAnswers}</strong>
          </div>

          <div className="result-stat-card">
            <span>Percentage</span>
            <strong>{percentage}%</strong>
          </div>
        </div>

        {/* DETAILS */}
        <div className="result-details">
          <div className="result-detail-card">
            <span>Challenge</span>
            <strong>#{submission.challengeId}</strong>
          </div>

          <div className="result-detail-card">
            <span>Status</span>
            <strong className={`submission-status status-${status.toLowerCase()}`}>
              {status}
            </strong>
          </div>

          <div className="result-detail-card">
            <span>Leaderboard</span>
            <strong>{isFirstSubmission ? 'Counts' : 'Does not count'}</strong>
          </div>
        </div>

        {/* ACTIONS */}
        <div className="result-actions">
          <Link className="primary-button" to={`/solve/${submission.challengeId}`}>
            Try Again
          </Link>

          {isFirstSubmission && (
            <Link
              className="secondary-button"
              to={`/leaderboard?challengeId=${submission.challengeId}`}
            >
              🏆 View Leaderboard
            </Link>
          )}

          <Link className="secondary-button" to="/submissions">
            My Submissions
          </Link>
        </div>
      </div>
    </div>
  );
}

export default SubmissionResult;