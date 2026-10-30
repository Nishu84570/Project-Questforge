import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { apiRequest } from '../services/api';

function SolveChallenge() {
  const { challengeId } = useParams();
  const navigate = useNavigate();

  const [challenge, setChallenge] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [options, setOptions] = useState([]);

  const [selectedAnswers, setSelectedAnswers] =
    useState({});

  const [currentQuestion, setCurrentQuestion] =
    useState(0);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // =========================================================
  // FETCH CHALLENGE
  // =========================================================

  const fetchChallenge = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const challengeResult = await apiRequest(
        `/challenges/${challengeId}`,
        {
          method: 'GET',
        }
      );

      if (!challengeResult.response.ok) {
        setError(
          challengeResult.data?.message ||
            'Challenge not found.'
        );

        return;
      }

      setChallenge(challengeResult.data);

      const questionResult = await apiRequest(
        `/questions/challenge/${challengeId}`,
        {
          method: 'GET',
        }
      );

      if (!questionResult.response.ok) {
        setError(
          questionResult.data?.message ||
            'Failed to load questions.'
        );

        return;
      }

      const loadedQuestions =
        questionResult.data || [];

      setQuestions(loadedQuestions);

      const optionResults =
        await Promise.all(
          loadedQuestions.map(
            (question) =>
              apiRequest(
                `/options/question/${question.id}`,
                {
                  method: 'GET',
                }
              )
          )
        );

      const allOptions = [];

      optionResults.forEach(
        (result, index) => {
          if (!result.response.ok) {
            return;
          }

          const questionOptions =
            result.data || [];

          questionOptions.forEach(
            (option) => {
              allOptions.push({
                ...option,
                questionId:
                  loadedQuestions[index].id,
              });
            }
          );
        }
      );

      setOptions(allOptions);

    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchChallenge();
  }, [challengeId]);

  // =========================================================
  // PREVENT ACCIDENTAL PAGE REFRESH
  // =========================================================

  useEffect(() => {
    const handleBeforeUnload = (event) => {
      if (
        questions.length > 0 &&
        Object.keys(selectedAnswers).length > 0 &&
        !submitting
      ) {
        event.preventDefault();
        event.returnValue = '';
      }
    };

    window.addEventListener(
      'beforeunload',
      handleBeforeUnload
    );

    return () => {
      window.removeEventListener(
        'beforeunload',
        handleBeforeUnload
      );
    };
  }, [
    questions.length,
    selectedAnswers,
    submitting,
  ]);

  // =========================================================
  // SELECT OPTION
  // =========================================================

  const handleOptionSelect = (
    questionId,
    optionId
  ) => {
    setSelectedAnswers(
      (currentAnswers) => ({
        ...currentAnswers,
        [questionId]: optionId,
      })
    );

    setError('');
    setSuccess('');
  };

  // =========================================================
  // GET OPTIONS FOR QUESTION
  // =========================================================

  const getQuestionOptions = (
    questionId
  ) => {
    return options.filter(
      (option) =>
        option.questionId === questionId
    );
  };

  // =========================================================
  // QUESTION NAVIGATION
  // =========================================================

  const goToQuestion = (index) => {
    if (
      index < 0 ||
      index >= questions.length
    ) {
      return;
    }

    setCurrentQuestion(index);

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  const goToPreviousQuestion = () => {
    goToQuestion(
      currentQuestion - 1
    );
  };

  const goToNextQuestion = () => {
    goToQuestion(
      currentQuestion + 1
    );
  };

  // =========================================================
  // RESET ANSWERS
  // =========================================================

  const handleReset = () => {
    const shouldReset =
      window.confirm(
        'Are you sure you want to clear all your answers?'
      );

    if (!shouldReset) {
      return;
    }

    setSelectedAnswers({});
    setCurrentQuestion(0);
    setError('');
    setSuccess(
      'All answers have been cleared.'
    );

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  // =========================================================
  // SUBMIT CHALLENGE
  // =========================================================

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError('');
    setSuccess('');

    const unansweredQuestions =
      questions.filter(
        (question) =>
          !selectedAnswers[question.id]
      );

    if (
      unansweredQuestions.length > 0
    ) {
      setError(
        `Please answer all questions. ${
          unansweredQuestions.length
        } question${
          unansweredQuestions.length !== 1
            ? 's are'
            : ' is'
        } unanswered.`
      );

      const firstUnansweredIndex =
        questions.findIndex(
          (question) =>
            !selectedAnswers[
              question.id
            ]
        );

      if (firstUnansweredIndex >= 0) {
        setCurrentQuestion(
          firstUnansweredIndex
        );
      }

      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      });

      return;
    }

    const shouldSubmit =
      window.confirm(
        'Are you sure you want to submit this challenge? You cannot change your answers after submission.'
      );

    if (!shouldSubmit) {
      return;
    }

    setSubmitting(true);

    try {
      const answers =
        questions.map(
          (question) => ({
            questionId:
              question.id,

            optionId:
              selectedAnswers[
                question.id
              ],
          })
        );

      const { response, data } =
        await apiRequest(
          '/submissions',
          {
            method: 'POST',

            body: JSON.stringify({
              challengeId:
                Number(challengeId),

              answers,
            }),
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to submit challenge.'
        );

        window.scrollTo({
          top: 0,
          behavior: 'smooth',
        });

        return;
      }

      navigate(
        `/submission-result/${data.id}`
      );

    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );

      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      });
    } finally {
      setSubmitting(false);
    }
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="page solve-page">

        <div className="empty-card solve-loading-card">

          <div className="loading-spinner"></div>

          <h2>
            Loading Challenge
          </h2>

          <p>
            Preparing your questions...
          </p>

        </div>

      </div>
    );
  }

  // =========================================================
  // CHALLENGE NOT FOUND
  // =========================================================

  if (!challenge) {
    return (
      <div className="page">

        <div className="empty-card">

          <h2>
            Challenge not found
          </h2>

          <p>
            {error ||
              'The requested challenge does not exist.'}
          </p>

          <Link
            className="primary-button"
            to="/challenges"
          >
            Back to Challenges
          </Link>

        </div>

      </div>
    );
  }

  // =========================================================
  // NO QUESTIONS
  // =========================================================

  if (questions.length === 0) {
    return (
      <div className="page">

        <div className="challenges-header">

          <div>

            <p className="eyebrow">
              CHALLENGE
            </p>

            <h1>
              {challenge.title}
            </h1>

            <p>
              {challenge.description}
            </p>

          </div>

          <Link
            className="secondary-button"
            to="/challenges"
          >
            Back
          </Link>

        </div>

        <div className="empty-card">

          <h2>
            No questions yet
          </h2>

          <p>
            This challenge does not have
            any questions yet.
          </p>

        </div>

      </div>
    );
  }

  // =========================================================
  // PROGRESS
  // =========================================================

  const answeredCount =
    Object.keys(selectedAnswers).length;

  const progress =
    Math.round(
      (answeredCount /
        questions.length) *
        100
    );

  const currentQuestionData =
    questions[currentQuestion];

  const currentOptions =
    getQuestionOptions(
      currentQuestionData.id
    );

  const selectedOptionId =
    selectedAnswers[
      currentQuestionData.id
    ];

  const isFirstQuestion =
    currentQuestion === 0;

  const isLastQuestion =
    currentQuestion ===
    questions.length - 1;

  // =========================================================
  // MAIN UI
  // =========================================================

  return (
    <div className="page solve-page">

      {/* =====================================================
          CHALLENGE HEADER
      ===================================================== */}

      <div className="solve-header">

        <div className="solve-header-content">

          <p className="eyebrow">
            QUESTFORGE CHALLENGE
          </p>

          <h1>
            {challenge.title}
          </h1>

          <p className="solve-description">
            {challenge.description}
          </p>

          <div className="solve-meta">

            <span className="difficulty">
              {challenge.difficulty}
            </span>

            <span className="category">
              {challenge.category}
            </span>

            <span className="solve-question-total">
              {questions.length}{' '}
              {questions.length === 1
                ? 'Question'
                : 'Questions'}
            </span>

          </div>

        </div>

        <Link
          className="secondary-button"
          to="/challenges"
        >
          ← Back
        </Link>

      </div>

      {/* =====================================================
          ERROR
      ===================================================== */}

      {error && (
        <div className="solve-error">

          <span>⚠</span>

          <p>
            {error}
          </p>

        </div>
      )}

      {success && (
        <div className="solve-success">

          <span>✓</span>

          <p>
            {success}
          </p>

        </div>
      )}

      {/* =====================================================
          PROGRESS CARD
      ===================================================== */}

      <div className="solve-progress-card">

        <div className="solve-progress-top">

          <div>

            <span className="progress-label">
              Your Progress
            </span>

            <strong>
              {answeredCount} /{' '}
              {questions.length}
            </strong>

          </div>

          <span className="progress-percentage">
            {progress}%
          </span>

        </div>

        <div className="progress-bar">

          <div
            className="progress-bar-fill"
            style={{
              width: `${progress}%`,
            }}
          ></div>

        </div>

        <p>
          {answeredCount ===
          questions.length
            ? 'All questions answered. You are ready to submit! 🎉'
            : 'Answer every question before submitting.'}
        </p>

      </div>

      {/* =====================================================
          QUESTION NAVIGATION
      ===================================================== */}

      <div className="solve-question-navigation">

        <div className="question-navigation-header">

          <strong>
            Questions
          </strong>

          <span>
            {answeredCount}/{questions.length}{' '}
            answered
          </span>

        </div>

        <div className="question-number-list">

          {questions.map(
            (question, index) => {

              const answered =
                Boolean(
                  selectedAnswers[
                    question.id
                  ]
                );

              const active =
                index === currentQuestion;

              return (
                <button
                  type="button"
                  key={question.id}
                  className={`question-number ${
                    active
                      ? 'active'
                      : ''
                  } ${
                    answered
                      ? 'answered'
                      : ''
                  }`}
                  onClick={() =>
                    goToQuestion(index)
                  }
                >
                  {index + 1}
                </button>
              );
            }
          )}

        </div>

      </div>

      {/* =====================================================
          QUIZ FORM
      ===================================================== */}

      <form
        className="solve-form"
        onSubmit={handleSubmit}
      >

        <div className="solve-question-list">

          <div
            className="solve-question-card"
            key={currentQuestionData.id}
          >

            {/* QUESTION HEADER */}

            <div className="solve-question-header">

              <div className="solve-question-number">
                Q{currentQuestion + 1}
              </div>

              <div className="solve-question-info">

                <span>
                  Question {currentQuestion + 1}{' '}
                  of {questions.length}
                </span>

              </div>

            </div>

            {/* QUESTION */}

            <h2 className="solve-question-text">
              {currentQuestionData.questionText}
            </h2>

            {/* OPTIONS */}

            {currentOptions.length === 0 ? (

              <div className="option-empty">

                <p>
                  No options available
                  for this question.
                </p>

              </div>

            ) : (

              <div className="solve-options">

                {currentOptions.map(
                  (
                    option,
                    optionIndex
                  ) => {

                    const selected =
                      selectedOptionId ===
                      option.id;

                    const letter =
                      String.fromCharCode(
                        65 +
                          optionIndex
                      );

                    return (
                      <label
                        className={`solve-option ${
                          selected
                            ? 'selected'
                            : ''
                        }`}
                        key={
                          option.id
                        }
                      >

                        <input
                          type="radio"
                          name={`question-${currentQuestionData.id}`}
                          value={
                            option.id
                          }
                          checked={
                            selected
                          }
                          onChange={() =>
                            handleOptionSelect(
                              currentQuestionData.id,
                              option.id
                            )
                          }
                        />

                        <span className="solve-option-letter">
                          {letter}
                        </span>

                        <span className="solve-option-text">
                          {
                            option.optionText
                          }
                        </span>

                        {selected && (
                          <span className="selected-check">
                            ✓
                          </span>
                        )}

                      </label>
                    );
                  }
                )}

              </div>

            )}

          </div>

        </div>

        {/* ===================================================
            QUESTION CONTROLS
        =================================================== */}

        <div className="solve-question-controls">

          <button
            type="button"
            className="secondary-button"
            onClick={
              goToPreviousQuestion
            }
            disabled={
              isFirstQuestion
            }
          >
            ← Previous
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={
              goToNextQuestion
            }
            disabled={
              isLastQuestion
            }
          >
            Next →
          </button>

        </div>

        {/* ===================================================
            SUBMIT SECTION
        =================================================== */}

        <div className="solve-submit-section">

          <div className="submit-info">

            <div className="submit-icon">
              ✓
            </div>

            <div>

              <h3>
                Ready to submit?
              </h3>

              <p>
                You have answered{' '}
                <strong>
                  {answeredCount}
                </strong>{' '}
                out of{' '}
                <strong>
                  {questions.length}
                </strong>{' '}
                questions.
              </p>

            </div>

          </div>

          <div className="solve-submit-actions">

            <button
              type="button"
              className="secondary-button"
              onClick={handleReset}
              disabled={
                submitting ||
                answeredCount === 0
              }
            >
              Reset Answers
            </button>

            <button
              type="submit"
              className="primary-button solve-submit-button"
              disabled={
                submitting
              }
            >
              {submitting
                ? 'Submitting...'
                : 'Submit Challenge →'}
            </button>

          </div>

        </div>

      </form>

    </div>
  );
}

export default SolveChallenge;