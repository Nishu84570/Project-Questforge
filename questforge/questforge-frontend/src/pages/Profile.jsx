
import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';
import { getToken, getUser } from '../utils/auth';

function Profile() {
  const [profile, setProfile] = useState(null);

  const [loading, setLoading] = useState(true);
  const [savingName, setSavingName] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [removingImage, setRemovingImage] = useState(false);

  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const [editMode, setEditMode] = useState(false);
  const [name, setName] = useState('');

  const [avatarUrl, setAvatarUrl] = useState('');

  const fileInputRef = useRef(null);

  const user = getUser();

  // ==========================================
  // FETCH PROFILE
  // ==========================================

  const fetchProfile = async () => {
    try {
      setError('');

      const { response, data } =
        await apiRequest('/profile', {
          method: 'GET',
        });

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load profile.'
        );

        return;
      }

      setProfile(data);
      setName(data.name || '');

      if (data.profileImagePresent) {
        setAvatarUrl(
          `${window.location.origin}/api-profile-avatar`
        );
      } else {
        setAvatarUrl('');
      }
    } catch (err) {
      console.error(
        'Profile loading error:',
        err
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  // ==========================================
  // PROFILE IMAGE URL
  // ==========================================

  useEffect(() => {
    if (!profile?.profileImagePresent) {
      setAvatarUrl('');
      return;
    }

    const token = getToken();

    if (!token) {
      return;
    }

    const controller =
      new AbortController();

    const loadAvatar = async () => {
      try {
        const response = await fetch(
          'http://localhost:8080/profile/avatar',
          {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
            signal: controller.signal,
          }
        );

        if (!response.ok) {
          setAvatarUrl('');
          return;
        }

        const blob =
          await response.blob();

        const objectUrl =
          URL.createObjectURL(blob);

        setAvatarUrl(objectUrl);
      } catch (err) {
        if (
          err.name !== 'AbortError'
        ) {
          console.error(
            'Avatar loading error:',
            err
          );
        }
      }
    };

    loadAvatar();

    return () => {
      controller.abort();
    };
  }, [profile?.profileImagePresent]);

  // ==========================================
  // SAVE NAME
  // ==========================================

  const handleSaveName = async (event) => {
    event.preventDefault();

    const trimmedName =
      name.trim();

    if (!trimmedName) {
      setError(
        'Name cannot be empty.'
      );

      return;
    }

    if (trimmedName.length > 100) {
      setError(
        'Name cannot exceed 100 characters.'
      );

      return;
    }

    setSavingName(true);
    setError('');
    setMessage('');

    try {
      const { response, data } =
        await apiRequest('/profile', {
          method: 'PUT',
          body: JSON.stringify({
            name: trimmedName,
          }),
        });

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to update your name.'
        );

        return;
      }

      setProfile(data);
      setName(data.name || '');

      // Keep navbar/user data synchronized.
      const currentUser =
        getUser();

      if (currentUser) {
        localStorage.setItem(
          'user',
          JSON.stringify({
            ...currentUser,
            name: data.name,
          })
        );

        window.dispatchEvent(
          new Event(
            'questforge-auth-change'
          )
        );
      }

      setEditMode(false);

      setMessage(
        'Profile name updated successfully.'
      );
    } catch (err) {
      console.error(
        'Profile update error:',
        err
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setSavingName(false);
    }
  };

  // ==========================================
  // SELECT IMAGE
  // ==========================================

  const handleAvatarButtonClick = () => {
    if (uploadingImage) {
      return;
    }

    fileInputRef.current?.click();
  };

  // ==========================================
  // UPLOAD IMAGE
  // ==========================================

  const handleImageChange = async (
    event
  ) => {
    const file =
      event.target.files?.[0];

    if (!file) {
      return;
    }

    setError('');
    setMessage('');

    // Frontend validation
    if (!file.type.startsWith('image/')) {
      setError(
        'Please select a valid image file.'
      );

      event.target.value = '';
      return;
    }

    if (
      file.size >
      5 * 1024 * 1024
    ) {
      setError(
        'Profile image must be smaller than 5 MB.'
      );

      event.target.value = '';
      return;
    }

    setUploadingImage(true);

    try {
      const formData =
        new FormData();

      formData.append(
        'file',
        file
      );

      const { response, data } =
        await apiRequest(
          '/profile/avatar',
          {
            method: 'POST',
            body: formData,
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to upload profile image.'
        );

        return;
      }

      setProfile(data);

      setMessage(
        'Profile image updated successfully.'
      );
    } catch (err) {
      console.error(
        'Avatar upload error:',
        err
      );

      setError(
        'Unable to upload profile image.'
      );
    } finally {
      setUploadingImage(false);

      if (fileInputRef.current) {
        fileInputRef.current.value =
          '';
      }
    }
  };

  // ==========================================
  // REMOVE IMAGE
  // ==========================================

  const handleRemoveImage = async () => {
    if (!profile?.profileImagePresent) {
      return;
    }

    const confirmed =
      window.confirm(
        'Are you sure you want to remove your profile image?'
      );

    if (!confirmed) {
      return;
    }

    setRemovingImage(true);
    setError('');
    setMessage('');

    try {
      const { response, data } =
        await apiRequest(
          '/profile/avatar',
          {
            method: 'DELETE',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to remove profile image.'
        );

        return;
      }

      setProfile(data);
      setAvatarUrl('');

      setMessage(
        'Profile image removed successfully.'
      );
    } catch (err) {
      console.error(
        'Avatar removal error:',
        err
      );

      setError(
        'Unable to remove profile image.'
      );
    } finally {
      setRemovingImage(false);
    }
  };

  // ==========================================
  // CANCEL EDIT
  // ==========================================

  const handleCancelEdit = () => {
    setName(
      profile?.name || ''
    );

    setEditMode(false);
    setError('');
  };

  // ==========================================
  // LOADING
  // ==========================================

  if (loading) {
    return (
      <div className="page profile-page">
        <div className="profile-loading-card">
          <div className="loading-spinner">
            ⟳
          </div>

          <h2>
            Loading your profile...
          </h2>

          <p>
            Preparing your QuestForge
            stats and achievements.
          </p>
        </div>
      </div>
    );
  }

  // ==========================================
  // ERROR
  // ==========================================

  if (error && !profile) {
    return (
      <div className="page profile-page">
        <div className="empty-card">
          <h2>
            Unable to Load Profile
          </h2>

          <p className="error-text">
            {error}
          </p>

          <button
            className="primary-button"
            onClick={() => {
              setLoading(true);
              fetchProfile();
            }}
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="page profile-page">
        <div className="empty-card">
          <h2>
            Profile Not Found
          </h2>

          <p>
            We could not load your
            QuestForge profile.
          </p>
        </div>
      </div>
    );
  }

  // ==========================================
  // DERIVED DATA
  // ==========================================

  const averageScore =
    Number(profile.averageScore) || 0;

  const bestScore =
    Number(profile.bestScore) || 0;

  const totalAttempts =
    Number(profile.totalAttempts) || 0;

  const correctAnswers =
    Number(profile.totalCorrectAnswers) || 0;

  const questionsAttempted =
    Number(profile.totalQuestionsAttempted) || 0;

  const accuracy =
    questionsAttempted > 0
      ? Math.round(
          (correctAnswers /
            questionsAttempted) *
            100
        )
      : 0;

  const xp =
    totalAttempts * 25 +
    bestScore * 2;

  const level =
    Math.floor(xp / 500) + 1;

  const levelXp =
    xp % 500;

  const xpPercentage =
    Math.min(
      (levelXp / 500) * 100,
      100
    );

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="page profile-page">

      {/* =====================================
          PAGE HEADER
      ====================================== */}

      <div className="challenges-header profile-header-actions">
        <div>
          <p className="eyebrow">
            QUESTFORGE
          </p>

          <h1>
            My Profile
          </h1>

          <p>
            Your coding journey,
            progress and achievements
            in one place.
          </p>
        </div>

        <div className="navbar-links">
          <Link
            to="/challenges"
            className="secondary-button"
          >
            🚀 Challenges
          </Link>

          <Link
            to="/leaderboard"
            className="secondary-button"
          >
            🏆 Leaderboard
          </Link>
        </div>
      </div>

      {/* =====================================
          ALERTS
      ====================================== */}

      {error && (
        <div className="profile-message profile-message-error">
          ⚠️ {error}
        </div>
      )}

      {message && (
        <div className="profile-message">
          ✓ {message}
        </div>
      )}

      {/* =====================================
          PROFILE HERO
      ====================================== */}

      <section className="profile-hero-card">

        <div className="profile-hero-glow" />

        <div className="profile-avatar-section">

          <button
            type="button"
            className="profile-avatar-button"
            onClick={
              handleAvatarButtonClick
            }
            disabled={
              uploadingImage
            }
            title="Change profile image"
          >
            {avatarUrl ? (
              <img
                src={avatarUrl}
                alt="Profile"
                className="profile-avatar-image"
              />
            ) : (
              <div className="profile-avatar-placeholder">
                {(
                  profile.name ||
                  user?.name ||
                  'U'
                )
                  .charAt(0)
                  .toUpperCase()}
              </div>
            )}

            <span className="profile-avatar-edit">
              ✎
            </span>
          </button>

          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={
              handleImageChange
            }
            hidden
          />

          <button
            type="button"
            className="avatar-upload-button"
            onClick={
              handleAvatarButtonClick
            }
            disabled={
              uploadingImage
            }
          >
            {uploadingImage
              ? 'Uploading...'
              : '📷 Change Photo'}
          </button>

          {profile.profileImagePresent && (
            <button
              type="button"
              className="avatar-remove-button"
              onClick={
                handleRemoveImage
              }
              disabled={
                removingImage
              }
            >
              {removingImage
                ? 'Removing...'
                : 'Remove Photo'}
            </button>
          )}

          <p className="avatar-help-text">
            JPG, PNG, WEBP • Max 5 MB
          </p>

        </div>

        <div className="profile-hero-info">

          <div className="profile-name-row">

            <div>
              <h2>
                {profile.name}
              </h2>

              <p>
                {profile.email}
              </p>
            </div>

            <span className="profile-role-badge">
              {profile.role === 'ADMIN'
                ? '👑 ADMIN'
                : '⚡ CODER'}
            </span>

          </div>

          <div className="profile-level-row">

            <span className="profile-level-badge">
              LEVEL {level}
            </span>

            <div className="profile-xp-area">

              <div className="profile-xp-top">
                <span>
                  {levelXp} / 500 XP
                </span>

                <span>
                  {xp} Total XP
                </span>
              </div>

              <div className="profile-xp-bar">
                <span
                  style={{
                    width: `${xpPercentage}%`,
                  }}
                />
              </div>

            </div>

          </div>

        </div>

      </section>

      {/* =====================================
          EDIT PROFILE
      ====================================== */}

      <section className="profile-edit-card">

        <div className="profile-section-heading">

          <div>
            <span className="section-icon">
              ✨
            </span>

            <div>
              <h2>
                Personal Information
              </h2>

              <p>
                Keep your QuestForge
                profile up to date.
              </p>
            </div>
          </div>

          {!editMode && (
            <button
              type="button"
              className="secondary-button"
              onClick={() => {
                setName(
                  profile.name || ''
                );

                setError('');
                setMessage('');
                setEditMode(true);
              }}
            >
              ✎ Edit Profile
            </button>
          )}

        </div>

        {editMode ? (
          <form
            className="profile-edit-form"
            onSubmit={
              handleSaveName
            }
          >

            <div className="profile-form-field">

              <label htmlFor="profile-name">
                Display Name
              </label>

              <input
                id="profile-name"
                type="text"
                value={name}
                onChange={(event) =>
                  setName(
                    event.target.value
                  )
                }
                maxLength={100}
                placeholder="Enter your name"
                autoFocus
              />

              <small>
                Your name is visible
                across QuestForge.
              </small>

            </div>

            <div className="profile-form-field">

              <label>
                Email Address
              </label>

              <input
                type="email"
                value={
                  profile.email || ''
                }
                disabled
              />

              <small>
                Email cannot be changed
                from the profile page.
              </small>

            </div>

            <div className="profile-edit-actions">

              <button
                type="button"
                className="secondary-button"
                onClick={
                  handleCancelEdit
                }
                disabled={
                  savingName
                }
              >
                Cancel
              </button>

              <button
                type="submit"
                className="primary-button"
                disabled={
                  savingName
                }
              >
                {savingName
                  ? 'Saving...'
                  : '✓ Save Changes'}
              </button>

            </div>

          </form>
        ) : (
          <div className="profile-readonly-grid">

            <div className="profile-readonly-field">
              <span>
                Display Name
              </span>

              <strong>
                {profile.name}
              </strong>
            </div>

            <div className="profile-readonly-field">
              <span>
                Email
              </span>

              <strong>
                {profile.email}
              </strong>
            </div>

            <div className="profile-readonly-field">
              <span>
                Account Type
              </span>

              <strong>
                {profile.role}
              </strong>
            </div>

          </div>
        )}

      </section>

      {/* =====================================
          STATS
      ====================================== */}

      <section className="profile-stats-grid">

        <div className="profile-stat-card">
          <span className="stat-icon">
            🎯
          </span>

          <span>
            Total Attempts
          </span>

          <strong>
            {totalAttempts}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            🚀
          </span>

          <span>
            First Attempts
          </span>

          <strong>
            {profile.firstAttempts || 0}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            🔁
          </span>

          <span>
            Practice Attempts
          </span>

          <strong>
            {profile.practiceAttempts || 0}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            📊
          </span>

          <span>
            Average Score
          </span>

          <strong>
            {averageScore}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            🏆
          </span>

          <span>
            Best Score
          </span>

          <strong>
            {bestScore}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            ✅
          </span>

          <span>
            Correct Answers
          </span>

          <strong>
            {correctAnswers}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            🧠
          </span>

          <span>
            Questions Attempted
          </span>

          <strong>
            {questionsAttempted}
          </strong>
        </div>

        <div className="profile-stat-card">
          <span className="stat-icon">
            🎯
          </span>

          <span>
            Accuracy
          </span>

          <strong>
            {accuracy}%
          </strong>
        </div>

      </section>

      {/* =====================================
          STREAK
      ====================================== */}

      <section className="profile-streak-card">

        <div>
          <span className="streak-icon">
            🔥
          </span>

          <div>
            <h2>
              Keep Your Streak Alive!
            </h2>

            <p>
              Solve challenges regularly
              to keep improving your
              coding skills.
            </p>
          </div>
        </div>

        <Link
          to="/challenges"
          className="primary-button"
        >
          Start Solving →
        </Link>

      </section>

      {/* =====================================
          ACHIEVEMENTS
      ====================================== */}

      <section className="profile-achievements-section">

        <div className="profile-section-heading">

          <div>
            <span className="section-icon">
              🏅
            </span>

            <div>
              <h2>
                Achievements
              </h2>

              <p>
                Milestones from your
                QuestForge journey.
              </p>
            </div>
          </div>

          <span className="achievement-count">
            {totalAttempts > 0
              ? '3 unlocked'
              : '0 unlocked'}
          </span>

        </div>

        {totalAttempts === 0 ? (
          <div className="profile-achievements-empty">

            <span>
              🏅
            </span>

            <h3>
              Your first achievement
              is waiting!
            </h3>

            <p>
              Complete your first
              challenge to begin
              your QuestForge journey.
            </p>

            <Link
              to="/challenges"
              className="primary-button"
            >
              Explore Challenges
            </Link>

          </div>
        ) : (
          <div className="achievement-grid">

            <div className="achievement-card">

              <span className="achievement-icon">
                🚀
              </span>

              <div>
                <h3>
                  First Mission
                </h3>

                <p>
                  Completed your first
                  challenge.
                </p>
              </div>

              <span className="achievement-unlocked">
                ✓
              </span>

            </div>

            <div className="achievement-card">

              <span className="achievement-icon">
                🎯
              </span>

              <div>
                <h3>
                  Persistent Coder
                </h3>

                <p>
                  Made multiple challenge
                  attempts.
                </p>
              </div>

              <span className="achievement-unlocked">
                ✓
              </span>

            </div>

            <div className="achievement-card">

              <span className="achievement-icon">
                🏆
              </span>

              <div>
                <h3>
                  Score Hunter
                </h3>

                <p>
                  Built your QuestForge
                  score.
                </p>
              </div>

              <span className="achievement-unlocked">
                ✓
              </span>

            </div>

          </div>
        )}

      </section>

      {/* =====================================
          FOOTER
      ====================================== */}

      <section className="profile-footer-card">

        <div>
          <span>
            💡
          </span>

          <div>
            <h2>
              Ready for the next quest?
            </h2>

            <p>
              Every challenge you solve
              makes you a better developer.
            </p>
          </div>
        </div>

        <div className="profile-footer-actions">

          <Link
            to="/challenges"
            className="primary-button"
          >
            🚀 Solve Challenge
          </Link>

          <Link
            to="/submissions"
            className="secondary-button"
          >
            View Submissions
          </Link>

        </div>

      </section>

    </div>
  );
}

export default Profile;
``
