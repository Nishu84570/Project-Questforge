
import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';
import { getToken, getUser } from '../utils/auth';

import '../styles/Profile.css';

// ======================================================
// CONFIGURATION
// ======================================================

const MAX_AVATAR_SIZE = 5 * 1024 * 1024;
const MAX_NAME_LENGTH = 100;
const XP_PER_LEVEL = 500;

const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
).replace(/\/+$/, '');

const ALLOWED_IMAGE_TYPES = [
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif',
];

// ======================================================
// UTILITY FUNCTIONS
// ======================================================

const getSafeNumber = (value, fallback = 0) => {
  const number = Number(value);

  return Number.isFinite(number) ? number : fallback;
};

const getInitials = (name) => {
  const normalizedName = String(name || '').trim();

  if (!normalizedName) return 'U';

  return normalizedName.charAt(0).toUpperCase();
};

const getProfileError = (data, fallback) => {
  return data?.message || fallback;
};

const calculateProfileStats = (profile) => {
  const totalAttempts = Math.max(
    0,
    getSafeNumber(profile.totalAttempts)
  );

  const correctAnswers = Math.max(
    0,
    getSafeNumber(profile.totalCorrectAnswers)
  );

  const questionsAttempted = Math.max(
    0,
    getSafeNumber(profile.totalQuestionsAttempted)
  );

  const averageScore = getSafeNumber(profile.averageScore);
  const bestScore = getSafeNumber(profile.bestScore);

  const accuracy = questionsAttempted > 0
    ? Math.min(
        100,
        Math.round((correctAnswers / questionsAttempted) * 100)
      )
    : 0;

  // Preserves the existing QuestForge XP calculation.
  const xp = Math.max(0, totalAttempts * 25 + bestScore * 2);

  const level = Math.floor(xp / XP_PER_LEVEL) + 1;
  const levelXp = xp % XP_PER_LEVEL;

  const xpPercentage = (levelXp / XP_PER_LEVEL) * 100;

  return {
    totalAttempts,
    correctAnswers,
    questionsAttempted,
    averageScore,
    bestScore,
    accuracy,
    xp,
    level,
    levelXp,
    xpPercentage,
    xpRemaining: XP_PER_LEVEL - levelXp,
    firstAttempts: Math.max(
      0,
      getSafeNumber(profile.firstAttempts)
    ),
    practiceAttempts: Math.max(
      0,
      getSafeNumber(profile.practiceAttempts)
    ),
  };
};

const createAchievements = (stats) => [
  {
    id: 'first-mission',
    icon: '🚀',
    title: 'First Mission',
    description: 'Completed your first challenge.',
    unlocked: stats.totalAttempts >= 1,
  },
  {
    id: 'persistent-coder',
    icon: '🎯',
    title: 'Persistent Coder',
    description: 'Made at least two challenge attempts.',
    unlocked: stats.totalAttempts >= 2,
  },
  {
    id: 'score-hunter',
    icon: '🏆',
    title: 'Score Hunter',
    description: 'Earned a score on a challenge.',
    unlocked: stats.bestScore > 0,
  },
];

// ======================================================
// PROFILE COMPONENT
// ======================================================

function Profile() {
  // ----------------------------------------------------
  // STATE
  // ----------------------------------------------------

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
  const [avatarVersion, setAvatarVersion] = useState(0);

  // ----------------------------------------------------
  // REFS
  // ----------------------------------------------------

  const fileInputRef = useRef(null);
  const avatarObjectUrlRef = useRef(null);
  const profileRequestRef = useRef(null);

  const user = getUser();

  const isBusy = savingName || uploadingImage || removingImage;

  // ----------------------------------------------------
  // AVATAR URL CLEANUP
  // ----------------------------------------------------

  const clearAvatarObjectUrl = useCallback(() => {
    if (avatarObjectUrlRef.current) {
      URL.revokeObjectURL(avatarObjectUrlRef.current);
      avatarObjectUrlRef.current = null;
    }
  }, []);

  // ----------------------------------------------------
  // PROFILE FETCH
  // ----------------------------------------------------

  const fetchProfile = useCallback(async () => {
    profileRequestRef.current?.abort();

    const controller = new AbortController();
    profileRequestRef.current = controller;

    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest('/profile', {
        method: 'GET',
        signal: controller.signal,
      });

      if (controller.signal.aborted) return;

      if (!response.ok) {
        setError(
          getProfileError(data, 'Failed to load profile.')
        );
        return;
      }

      setProfile(data);
      setName(data?.name || '');

    } catch (err) {
      if (err.name === 'AbortError') return;

      console.error('Profile loading error:', err);

      setError('Unable to connect to QuestForge backend.');

    } finally {
      if (!controller.signal.aborted) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    fetchProfile();

    return () => {
      profileRequestRef.current?.abort();
      clearAvatarObjectUrl();
    };
  }, [fetchProfile, clearAvatarObjectUrl]);

  // ----------------------------------------------------
  // AVATAR FETCH
  // ----------------------------------------------------

  useEffect(() => {
    let cancelled = false;
    let currentObjectUrl = null;

    const controller = new AbortController();

    const loadAvatar = async () => {
      setAvatarUrl('');

      if (!profile?.profileImagePresent) {
        return;
      }

      const token = getToken();

      if (!token) {
        return;
      }

      try {
        const response = await fetch(
          `${API_BASE_URL}/profile/avatar`,
          {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
            signal: controller.signal,
          }
        );

        if (!response.ok) {
          if (!cancelled) {
            console.error(
              'Avatar request failed:',
              response.status
            );
          }

          return;
        }

        const blob = await response.blob();

        if (cancelled || !blob.size) return;

        currentObjectUrl = URL.createObjectURL(blob);

        avatarObjectUrlRef.current = currentObjectUrl;

        if (!cancelled) {
          setAvatarUrl(currentObjectUrl);
        }

      } catch (err) {
        if (err.name !== 'AbortError') {
          console.error('Avatar loading error:', err);
        }
      }
    };

    loadAvatar();

    return () => {
      cancelled = true;
      controller.abort();

      if (currentObjectUrl) {
        URL.revokeObjectURL(currentObjectUrl);

        if (avatarObjectUrlRef.current === currentObjectUrl) {
          avatarObjectUrlRef.current = null;
        }
      }
    };
  }, [profile?.profileImagePresent, avatarVersion]);

  // ----------------------------------------------------
  // PROFILE NAME UPDATE
  // ----------------------------------------------------

  const handleSaveName = async (event) => {
    event.preventDefault();

    if (isBusy) return;

    const trimmedName = name.trim();

    if (!trimmedName) {
      setError('Name cannot be empty.');
      return;
    }

    if (trimmedName.length > MAX_NAME_LENGTH) {
      setError(
        `Name cannot exceed ${MAX_NAME_LENGTH} characters.`
      );
      return;
    }

    setSavingName(true);
    setError('');
    setMessage('');

    try {
      const { response, data } = await apiRequest('/profile', {
        method: 'PUT',
        body: JSON.stringify({
          name: trimmedName,
        }),
      });

      if (!response.ok) {
        setError(
          getProfileError(data, 'Failed to update your name.')
        );
        return;
      }

      setProfile((previous) => ({
        ...previous,
        ...data,
      }));

      const updatedName = data?.name ?? trimmedName;

      setName(updatedName);

      // Synchronize cached authentication information.
      const currentUser = getUser();

      if (currentUser) {
        try {
          localStorage.setItem(
            'user',
            JSON.stringify({
              ...currentUser,
              name: updatedName,
            })
          );

          window.dispatchEvent(
            new Event('questforge-auth-change')
          );
        } catch (storageError) {
          console.error(
            'Authentication cache update error:',
            storageError
          );
        }
      }

      setEditMode(false);
      setMessage('Profile name updated successfully.');

    } catch (err) {
      console.error('Profile update error:', err);

      setError('Unable to connect to QuestForge backend.');

    } finally {
      setSavingName(false);
    }
  };

  // ----------------------------------------------------
  // AVATAR FILE SELECTION
  // ----------------------------------------------------

  const handleAvatarButtonClick = () => {
    if (isBusy) return;

    fileInputRef.current?.click();
  };

  // ----------------------------------------------------
  // AVATAR UPLOAD
  // ----------------------------------------------------

  const handleImageChange = async (event) => {
    const file = event.target.files?.[0];

    if (!file || isBusy) return;

    setError('');
    setMessage('');

    if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
      setError(
        'Please select a PNG, JPG, WEBP or GIF image.'
      );

      event.target.value = '';
      return;
    }

    if (file.size === 0) {
      setError('The selected image is empty.');

      event.target.value = '';
      return;
    }

    if (file.size > MAX_AVATAR_SIZE) {
      setError('Profile image must not exceed 5 MB.');

      event.target.value = '';
      return;
    }

    setUploadingImage(true);

    try {
      const formData = new FormData();

      formData.append('file', file);

      const { response, data } = await apiRequest(
        '/profile/avatar',
        {
          method: 'POST',
          body: formData,
        }
      );

      if (!response.ok) {
        setError(
          getProfileError(
            data,
            'Failed to upload profile image.'
          )
        );

        return;
      }

      setProfile((previous) => ({
        ...previous,
        ...data,
      }));

      // Force the avatar loading effect to fetch the new image.
      clearAvatarObjectUrl();
      setAvatarUrl('');
      setAvatarVersion((version) => version + 1);

      setMessage('Profile image updated successfully.');

    } catch (err) {
      console.error('Avatar upload error:', err);

      setError('Unable to upload profile image.');

    } finally {
      setUploadingImage(false);

      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  // ----------------------------------------------------
  // AVATAR REMOVAL
  // ----------------------------------------------------

  const handleRemoveImage = async () => {
    if (!profile?.profileImagePresent || isBusy) return;

    const confirmed = window.confirm(
      'Are you sure you want to remove your profile image?'
    );

    if (!confirmed) return;

    setRemovingImage(true);
    setError('');
    setMessage('');

    try {
      const { response, data } = await apiRequest(
        '/profile/avatar',
        {
          method: 'DELETE',
        }
      );

      if (!response.ok) {
        setError(
          getProfileError(
            data,
            'Failed to remove profile image.'
          )
        );

        return;
      }

      clearAvatarObjectUrl();
      setAvatarUrl('');

      setProfile((previous) => ({
        ...previous,
        ...data,
        profileImagePresent: false,
      }));

      setMessage('Profile image removed successfully.');

    } catch (err) {
      console.error('Avatar removal error:', err);

      setError('Unable to remove profile image.');

    } finally {
      setRemovingImage(false);
    }
  };

  // ----------------------------------------------------
  // EDIT HANDLERS
  // ----------------------------------------------------

  const handleCancelEdit = () => {
    setName(profile?.name || '');
    setEditMode(false);
    setError('');
  };

  const handleStartEdit = () => {
    setName(profile?.name || '');
    setError('');
    setMessage('');
    setEditMode(true);
  };

  // ----------------------------------------------------
  // DERIVED DATA
  // ----------------------------------------------------

  const stats = useMemo(() => {
    if (!profile) return null;

    return calculateProfileStats(profile);
  }, [profile]);

  const achievements = useMemo(() => {
    if (!stats) return [];

    return createAchievements(stats);
  }, [stats]);

  const unlockedAchievements = useMemo(
    () => achievements.filter((item) => item.unlocked).length,
    [achievements]
  );

  const displayName = (
    profile?.name ||
    user?.name ||
    'User'
  );

  const initials = getInitials(displayName);

  const statCards = useMemo(() => {
    if (!stats) return [];

    return [
      {
        icon: '🎯',
        label: 'Total Attempts',
        value: stats.totalAttempts,
      },
      {
        icon: '🚀',
        label: 'First Attempts',
        value: stats.firstAttempts,
      },
      {
        icon: '🔁',
        label: 'Practice Attempts',
        value: stats.practiceAttempts,
      },
      {
        icon: '📊',
        label: 'Average Score',
        value: stats.averageScore,
      },
      {
        icon: '🏆',
        label: 'Best Score',
        value: stats.bestScore,
      },
      {
        icon: '✅',
        label: 'Correct Answers',
        value: stats.correctAnswers,
      },
      {
        icon: '🧠',
        label: 'Questions Attempted',
        value: stats.questionsAttempted,
      },
      {
        icon: '🎯',
        label: 'Accuracy',
        value: `${stats.accuracy}%`,
      },
    ];
  }, [stats]);

  // ----------------------------------------------------
  // LOADING STATE
  // ----------------------------------------------------

  if (loading) {
    return (
      <main className="page profile-page">
        <section
          className="profile-state-card"
          aria-live="polite"
          aria-busy="true"
        >
          <span className="profile-state-icon profile-loading-icon">
            ✦
          </span>

          <h2>Loading your profile</h2>

          <p>
            Preparing your QuestForge stats and achievements.
          </p>

          <span className="profile-loading-track">
            <span />
          </span>
        </section>
      </main>
    );
  }

  // ----------------------------------------------------
  // ERROR STATE
  // ----------------------------------------------------

  if (error && !profile) {
    return (
      <main className="page profile-page">
        <section className="profile-state-card">
          <span className="profile-state-icon">⚠</span>

          <p className="profile-eyebrow">
            QUESTFORGE / PROFILE
          </p>

          <h2>Unable to load profile</h2>

          <p className="profile-error-copy">
            {error}
          </p>

          <button
            type="button"
            className="profile-button profile-button-primary"
            onClick={fetchProfile}
          >
            Try again <span>→</span>
          </button>
        </section>
      </main>
    );
  }

  // ----------------------------------------------------
  // EMPTY STATE
  // ----------------------------------------------------

  if (!profile || !stats) {
    return (
      <main className="page profile-page">
        <section className="profile-state-card">
          <span className="profile-state-icon">◈</span>

          <h2>Profile not found</h2>

          <p>
            We could not load your QuestForge profile.
          </p>

          <button
            type="button"
            className="profile-button profile-button-primary"
            onClick={fetchProfile}
          >
            Reload profile →
          </button>
        </section>
      </main>
    );
  }

  // ======================================================
  // MAIN UI
  // ======================================================

  return (
    <main className="page profile-page">

      {/* PAGE HEADER */}

      <header className="profile-page-header">
        <div className="profile-heading-copy">
          <span className="profile-eyebrow">
            <span /> QUESTFORGE / YOUR SPACE
          </span>

          <h1>
            My <span>Profile</span>
          </h1>

          <p>
            Your coding journey, progress and achievements — all in one place.
          </p>
        </div>

        <nav
          className="profile-header-nav"
          aria-label="Profile navigation"
        >
          <Link
            to="/challenges"
            className="profile-button profile-button-secondary"
          >
            <span>⌘</span> Challenges
          </Link>

          <Link
            to="/leaderboard"
            className="profile-button profile-button-secondary"
          >
            <span>♛</span> Leaderboard
          </Link>
        </nav>
      </header>

      {/* ALERTS */}

      {error && (
        <div
          className="profile-alert profile-alert-error"
          role="alert"
        >
          <span>⚠</span>

          {error}

          <button
            type="button"
            onClick={() => setError('')}
            aria-label="Dismiss error"
          >
            ×
          </button>
        </div>
      )}

      {message && (
        <div
          className="profile-alert profile-alert-success"
          role="status"
        >
          <span>✓</span>

          {message}

          <button
            type="button"
            onClick={() => setMessage('')}
            aria-label="Dismiss message"
          >
            ×
          </button>
        </div>
      )}

      {/* PROFILE HERO */}

      <section className="profile-hero-card">
        <div className="profile-hero-orb profile-hero-orb-one" />
        <div className="profile-hero-orb profile-hero-orb-two" />

        <div className="profile-avatar-column">
          <div className="profile-avatar-frame">
            <button
              type="button"
              className="profile-avatar-button"
              onClick={handleAvatarButtonClick}
              disabled={isBusy}
              title="Change profile image"
              aria-label="Change profile image"
            >
              {avatarUrl ? (
                <img
                  src={avatarUrl}
                  alt={`${displayName}'s profile`}
                  className="profile-avatar-image"
                />
              ) : (
                <span className="profile-avatar-placeholder">
                  {initials}
                </span>
              )}

              <span className="profile-avatar-edit">
                ✎
              </span>
            </button>

            <span className="profile-avatar-status" />
          </div>

          <input
            ref={fileInputRef}
            className="profile-file-input"
            type="file"
            accept={ALLOWED_IMAGE_TYPES.join(',')}
            onChange={handleImageChange}
            aria-label="Choose profile image"
          />

          <button
            type="button"
            className="profile-button profile-button-secondary profile-photo-button"
            onClick={handleAvatarButtonClick}
            disabled={isBusy}
          >
            {uploadingImage
              ? 'Uploading...'
              : '＋ Change photo'}
          </button>

          {profile.profileImagePresent && (
            <button
              type="button"
              className="profile-remove-photo"
              onClick={handleRemoveImage}
              disabled={isBusy}
            >
              {removingImage
                ? 'Removing...'
                : 'Remove photo'}
            </button>
          )}

          <span className="profile-avatar-hint">
            JPG, PNG, WEBP · Max 5 MB
          </span>
        </div>

        <div className="profile-hero-content">
          <div className="profile-identity-row">
            <div className="profile-identity">
              <span className="profile-card-kicker">
                WELCOME BACK
              </span>

              <h2>{displayName}</h2>

              <p>{profile.email}</p>
            </div>

            <span className="profile-role-badge">
              <span>
                {profile.role === 'ADMIN' ? '♛' : '⚡'}
              </span>

              {profile.role === 'ADMIN' ? 'ADMIN' : 'CODER'}
            </span>
          </div>

          {/* LEVEL AND XP */}

          <div className="profile-level-panel">
            <div className="profile-level-topline">
              <div>
                <span className="profile-level-label">
                  CURRENT RANK
                </span>

                <strong>LEVEL {stats.level}</strong>
              </div>

              <div className="profile-xp-total">
                <strong>
                  {stats.xp.toLocaleString()}
                </strong>

                <span>TOTAL XP</span>
              </div>
            </div>

            <div className="profile-xp-meta">
              <span>Level progress</span>

              <strong>
                {stats.levelXp}
                <span> / {XP_PER_LEVEL} XP</span>
              </strong>
            </div>

            <div
              className="profile-xp-bar"
              role="progressbar"
              aria-label="Level progress"
              aria-valuemin={0}
              aria-valuemax={XP_PER_LEVEL}
              aria-valuenow={stats.levelXp}
            >
              <span
                style={{
                  width: `${stats.xpPercentage}%`,
                }}
              />
            </div>

            <p className="profile-xp-next">
              {stats.xpRemaining} XP to reach Level {stats.level + 1}
            </p>
          </div>
        </div>
      </section>

      {/* PERSONAL INFORMATION */}

      <section className="profile-panel profile-personal-panel">
        <div className="profile-section-heading">
          <div className="profile-section-title">
            <span className="profile-section-icon">
              ✦
            </span>

            <div>
              <span className="profile-card-kicker">
                ACCOUNT DETAILS
              </span>

              <h2>Personal Information</h2>

              <p>
                Manage the details associated with your account.
              </p>
            </div>
          </div>

          {!editMode && (
            <button
              type="button"
              className="profile-button profile-button-secondary"
              onClick={handleStartEdit}
              disabled={isBusy}
            >
              ✎ Edit profile
            </button>
          )}
        </div>

        {editMode ? (
          <form
            className="profile-edit-form"
            onSubmit={handleSaveName}
          >
            <div className="profile-form-grid">
              <div className="profile-form-field">
                <label htmlFor="profile-name">
                  Display name
                </label>

                <input
                  id="profile-name"
                  type="text"
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  maxLength={MAX_NAME_LENGTH}
                  placeholder="Enter your name"
                  autoFocus
                  autoComplete="name"
                  className="profile-input"
                  disabled={savingName}
                  required
                />

                <small>
                  {name.length}/{MAX_NAME_LENGTH} characters.
                  Your name is visible across QuestForge.
                </small>
              </div>

              <div className="profile-form-field">
                <label htmlFor="profile-email">
                  Email address
                </label>

                <input
                  id="profile-email"
                  type="email"
                  value={profile.email || ''}
                  disabled
                  className="profile-input"
                />

                <small>
                  Email cannot be changed from this page.
                </small>
              </div>
            </div>

            <div className="profile-edit-actions">
              <button
                type="button"
                className="profile-button profile-button-secondary"
                onClick={handleCancelEdit}
                disabled={savingName}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="profile-button profile-button-primary"
                disabled={
                  savingName ||
                  !name.trim() ||
                  name.trim().length > MAX_NAME_LENGTH
                }
              >
                {savingName
                  ? 'Saving...'
                  : '✓ Save changes'}
              </button>
            </div>
          </form>
        ) : (
          <div className="profile-details-grid">
            <div className="profile-detail-item">
              <span>DISPLAY NAME</span>

              <strong>
                {profile.name || 'Not set'}
              </strong>
            </div>

            <div className="profile-detail-item">
              <span>EMAIL ADDRESS</span>

              <strong>
                {profile.email || 'Not available'}
              </strong>
            </div>

            <div className="profile-detail-item">
              <span>ACCOUNT TYPE</span>

              <strong>
                {profile.role || 'USER'}
              </strong>
            </div>
          </div>
        )}
      </section>

      {/* PERFORMANCE ANALYTICS */}

      <section className="profile-stats-section">
        <div className="profile-section-heading profile-stats-heading">
          <div className="profile-section-title">
            <span className="profile-section-icon">
              ▦
            </span>

            <div>
              <span className="profile-card-kicker">
                YOUR PERFORMANCE
              </span>

              <h2>Stats &amp; Analytics</h2>

              <p>
                A snapshot of your coding activity so far.
              </p>
            </div>
          </div>

          <span className="profile-section-note">
            LIVE PROFILE DATA
          </span>
        </div>

        <div className="profile-stats-grid">
          {statCards.map((stat, index) => (
            <article
              className="profile-stat-card"
              key={stat.label}
            >
              <div className="profile-stat-top">
                <span className="profile-stat-icon">
                  {stat.icon}
                </span>

                <span className="profile-stat-index">
                  {String(index + 1).padStart(2, '0')}
                </span>
              </div>

              <strong className="profile-stat-value">
                {stat.value}
              </strong>

              <span className="profile-stat-label">
                {stat.label}
              </span>
            </article>
          ))}
        </div>
      </section>

      {/* STREAK / MOTIVATION */}

      <section className="profile-streak-card">
        <div className="profile-streak-art">
          🔥
        </div>

        <div className="profile-streak-copy">
          <span className="profile-card-kicker">
            STAY CONSISTENT
          </span>

          <h2>
            Keep your momentum alive.
          </h2>

          <p>
            Every challenge is a chance to sharpen your problem-solving skills.
            Your next milestone starts here.
          </p>
        </div>

        <Link
          to="/challenges"
          className="profile-button profile-button-primary"
        >
          Start solving →
        </Link>
      </section>

      {/* ACHIEVEMENTS */}

      <section className="profile-panel profile-achievements-section">
        <div className="profile-section-heading">
          <div className="profile-section-title">
            <span className="profile-section-icon">
              ♛
            </span>

            <div>
              <span className="profile-card-kicker">
                MILESTONES
              </span>

              <h2>Achievements</h2>

              <p>
                Milestones earned through your QuestForge activity.
              </p>
            </div>
          </div>

          <span className="profile-achievement-count">
            {unlockedAchievements} / {achievements.length} UNLOCKED
          </span>
        </div>

        <div className="profile-achievement-grid">
          {achievements.map((achievement) => (
            <article
              className={`profile-achievement-card ${
                achievement.unlocked
                  ? 'is-unlocked'
                  : 'is-locked'
              }`}
              key={achievement.id}
            >
              <span className="profile-achievement-icon">
                {achievement.icon}
              </span>

              <div className="profile-achievement-copy">
                <h3>{achievement.title}</h3>

                <p>{achievement.description}</p>
              </div>

              <span className="profile-achievement-status">
                {achievement.unlocked ? '✓' : '⌑'}
              </span>
            </article>
          ))}
        </div>
      </section>

      {/* FOOTER */}

      <footer className="profile-footer-card">
        <div className="profile-footer-mark">
          ✧
        </div>

        <div className="profile-footer-copy">
          <span className="profile-card-kicker">
            YOUR NEXT CHAPTER
          </span>

          <h2>
            Ready for your next quest?
          </h2>

          <p>
            Keep learning, keep building, and make every attempt count.
          </p>
        </div>

        <div className="profile-footer-actions">
          <Link
            to="/challenges"
            className="profile-button profile-button-primary"
          >
            🚀 Solve challenge
          </Link>

          <Link
            to="/submissions"
            className="profile-button profile-button-secondary"
          >
            View submissions
          </Link>
        </div>
      </footer>

    </main>
  );
}

export default Profile;
