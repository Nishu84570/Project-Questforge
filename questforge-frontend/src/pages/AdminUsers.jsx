
import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminUsers.css';

const ROLE_OPTIONS = [
  { value: 'ALL', label: 'All Roles' },
  { value: 'USER', label: 'USER' },
  { value: 'ADMIN', label: 'ADMIN' },
];

const getUserId = (user) => user?.userId;

const getRole = (role) =>
  String(role || 'USER').trim().toUpperCase();

const getSubmissionId = (submission) =>
  submission?.submissionId ?? submission?.id;

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);

  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  const [selectedUser, setSelectedUser] = useState(null);
  const [userSubmissions, setUserSubmissions] = useState([]);
  const [submissionsLoading, setSubmissionsLoading] = useState(false);
  const [submissionsLoaded, setSubmissionsLoaded] = useState(false);

  const [xpAmount, setXpAmount] = useState('');
  const [roleValue, setRoleValue] = useState('');

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const usersRequestRef = useRef(0);
  const submissionsRequestRef = useRef(0);

  // =========================================
  // LOAD USERS
  // =========================================

  const loadUsers = useCallback(async (query = '') => {
    const requestId = ++usersRequestRef.current;

    setLoading(true);
    setError('');
    setMessage('');

    try {
      const endpoint = query.trim()
        ? `/admin/users/search?query=${encodeURIComponent(query.trim())}`
        : '/admin/users';

      const { response, data } = await apiRequest(endpoint, {
        method: 'GET',
      });

      // Ignore outdated search/refresh responses.
      if (requestId !== usersRequestRef.current) return;

      if (!response.ok) {
        setError(data?.message || 'Failed to load admin users.');
        return;
      }

      setUsers(Array.isArray(data) ? data : []);
    } catch (err) {
      if (requestId === usersRequestRef.current) {
        console.error('Admin users error:', err);
        setError('Unable to connect to QuestForge backend.');
      }
    } finally {
      if (requestId === usersRequestRef.current) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    loadUsers();

    return () => {
      usersRequestRef.current += 1;
      submissionsRequestRef.current += 1;
    };
  }, [loadUsers]);

  // =========================================
  // FILTER USERS
  // =========================================

  const filteredUsers = useMemo(() => {
    return users.filter((user) => {
      const role = getRole(user.role);

      return roleFilter === 'ALL' || role === roleFilter;
    });
  }, [users, roleFilter]);

  // =========================================
  // USER STATISTICS
  // =========================================

  const stats = useMemo(() => {
    return users.reduce(
      (result, user) => {
        result.total += 1;

        if (getRole(user.role) === 'ADMIN') {
          result.admins += 1;
        }

        if (getRole(user.role) === 'USER') {
          result.members += 1;
        }

        result.xp += Number(user.xp) || 0;

        result.completed += Number(user.challengesCompleted) || 0;
        result.attempted += Number(user.challengesAttempted) || 0;

        return result;
      },
      {
        total: 0,
        admins: 0,
        members: 0,
        xp: 0,
        completed: 0,
        attempted: 0,
      }
    );
  }, [users]);

  const completionRate = stats.attempted > 0
    ? Math.round((stats.completed / stats.attempted) * 100)
    : 0;

  // =========================================
  // SEARCH
  // =========================================

  const handleSearch = async (event) => {
    event.preventDefault();
    await loadUsers(search);
  };

  const clearSearch = async () => {
    setSearch('');
    setRoleFilter('ALL');
    await loadUsers('');
  };

  // =========================================
  // SELECT USER
  // =========================================

  const selectUser = (user) => {
    submissionsRequestRef.current += 1;

    setSelectedUser(user);
    setRoleValue(getRole(user.role));
    setXpAmount('');

    setUserSubmissions([]);
    setSubmissionsLoaded(false);
    setSubmissionsLoading(false);

    setMessage('');
    setError('');
  };

  // =========================================
  // LOAD USER SUBMISSIONS
  // =========================================

  const loadUserSubmissions = async (userId) => {
    if (userId === null || userId === undefined || userId === '') {
      setError('User ID is missing.');
      return;
    }

    const requestId = ++submissionsRequestRef.current;

    setSubmissionsLoading(true);
    setError('');
    setMessage('');

    try {
      const { response, data } = await apiRequest(
        `/admin/users/${encodeURIComponent(userId)}/submissions`,
        {
          method: 'GET',
        }
      );

      if (requestId !== submissionsRequestRef.current) return;

      if (!response.ok) {
        setError(data?.message || 'Failed to load user submissions.');
        return;
      }

      setUserSubmissions(Array.isArray(data) ? data : []);
      setSubmissionsLoaded(true);
    } catch (err) {
      if (requestId === submissionsRequestRef.current) {
        console.error('User submissions error:', err);
        setError('Unable to load user submissions.');
      }
    } finally {
      if (requestId === submissionsRequestRef.current) {
        setSubmissionsLoading(false);
      }
    }
  };

  // =========================================
  // UPDATE ROLE
  // =========================================

  const updateRole = async () => {
    if (!selectedUser) return;

    const userId = getUserId(selectedUser);

    if (userId === null || userId === undefined) {
      setError('User ID is missing.');
      return;
    }

    if (!roleValue) {
      setError('Please select a role.');
      return;
    }

    if (getRole(selectedUser.role) === roleValue) {
      setMessage('This user already has the selected role.');
      setError('');
      return;
    }

    if (actionLoading !== null) return;

    setActionLoading(`role-${userId}`);
    setMessage('');
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/admin/users/${encodeURIComponent(userId)}/role`,
        {
          method: 'PUT',
          body: JSON.stringify({ role: roleValue }),
        }
      );

      if (!response.ok) {
        setError(data?.message || 'Failed to update user role.');
        return;
      }

      const updatedRole = data?.role || roleValue;

      setSelectedUser((current) => {
        if (String(getUserId(current)) !== String(userId)) {
          return current;
        }

        return {
          ...current,
          role: updatedRole,
        };
      });

      setUsers((currentUsers) =>
        currentUsers.map((user) =>
          String(getUserId(user)) === String(userId)
            ? { ...user, role: updatedRole }
            : user
        )
      );

      setRoleValue(getRole(updatedRole));
      setMessage('User role updated successfully.');
    } catch (err) {
      console.error('Update role error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================
  // ADD XP
  // =========================================

  const addXp = async () => {
    if (!selectedUser) return;

    const userId = getUserId(selectedUser);
    const xp = Number(xpAmount);

    if (userId === null || userId === undefined) {
      setError('User ID is missing.');
      return;
    }

    if (!Number.isSafeInteger(xp) || xp < 1) {
      setError('XP amount must be a positive whole number.');
      return;
    }

    if (actionLoading !== null) return;

    setActionLoading(`xp-${userId}`);
    setMessage('');
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/admin/users/${encodeURIComponent(userId)}/xp`,
        {
          method: 'POST',
          body: JSON.stringify({ xp }),
        }
      );

      if (!response.ok) {
        setError(data?.message || 'Failed to add XP.');
        return;
      }

      const existingXp = Number(selectedUser.xp) || 0;

      const newXp =
        data?.xp ??
        data?.totalXp ??
        existingXp + xp;

      setSelectedUser((current) => {
        if (String(getUserId(current)) !== String(userId)) {
          return current;
        }

        return {
          ...current,
          xp: newXp,
        };
      });

      setUsers((currentUsers) =>
        currentUsers.map((user) =>
          String(getUserId(user)) === String(userId)
            ? { ...user, xp: newXp }
            : user
        )
      );

      setXpAmount('');
      setMessage(`${xp.toLocaleString()} XP added successfully.`);
    } catch (err) {
      console.error('Add XP error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================
  // HELPERS
  // =========================================

  const formatNumber = (value) =>
    Number(value || 0).toLocaleString();

  const formatDate = (value) => {
    if (!value) return '—';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return String(value);
    }

    return date.toLocaleString();
  };

  const getRoleClass = (role) => {
    return `aum-role-${getRole(role).toLowerCase()}`;
  };

  const closeUserPanel = () => {
    submissionsRequestRef.current += 1;

    setSelectedUser(null);
    setUserSubmissions([]);
    setSubmissionsLoaded(false);
    setSubmissionsLoading(false);

    setMessage('');
    setError('');
    setXpAmount('');
    setRoleValue('');
  };

  const selectedUserId = selectedUser ? getUserId(selectedUser) : null;

  // =========================================
  // RENDER
  // =========================================

  return (
    <main className="page aum-page">
      <div className="aum-shell">

        {/* HEADER */}
        <header className="aum-header">
          <div className="aum-header-content">
            <div className="aum-breadcrumb">
              <span />
              QUESTFORGE / ADMIN / USER MANAGEMENT
            </div>

            <div className="aum-title-row">
              <div>
                <h1>User Management</h1>
                <p>
                  Manage your community, assign roles, reward XP and
                  monitor user activity.
                </p>
              </div>

              <div className="aum-header-icon" aria-hidden="true">
                👥
              </div>
            </div>

            <div className="aum-header-status">
              <span />
              Community administration workspace
            </div>
          </div>

          <div className="aum-header-actions">
            <Link to="/admin" className="aum-secondary-button">
              ← Admin Dashboard
            </Link>

            <button
              type="button"
              className="aum-primary-button"
              onClick={() => loadUsers(search)}
              disabled={loading}
            >
              <span className={loading ? 'aum-refresh-spinning' : ''}>
                ↻
              </span>
              {loading ? 'Refreshing...' : 'Refresh Users'}
            </button>
          </div>
        </header>

        {/* FEEDBACK */}
        {message && (
          <div className="aum-alert aum-alert-success" role="status">
            <span>✓</span>
            <p>{message}</p>
            <button
              type="button"
              onClick={() => setMessage('')}
              aria-label="Dismiss success message"
            >
              ×
            </button>
          </div>
        )}

        {error && (
          <div className="aum-alert aum-alert-error" role="alert">
            <span>!</span>
            <p>{error}</p>
            <button
              type="button"
              onClick={() => setError('')}
              aria-label="Dismiss error message"
            >
              ×
            </button>
          </div>
        )}

        {/* STATISTICS */}
        <section className="aum-stats-grid" aria-label="User statistics">
          <article className="aum-stat-card aum-stat-total">
            <div className="aum-stat-top">
              <span>Total Loaded Users</span>
              <span className="aum-stat-icon">👥</span>
            </div>
            <strong>{formatNumber(stats.total)}</strong>
            <small>Users in current result</small>
          </article>

          <article className="aum-stat-card aum-stat-admin">
            <div className="aum-stat-top">
              <span>Administrators</span>
              <span className="aum-stat-icon">🛡️</span>
            </div>
            <strong>{formatNumber(stats.admins)}</strong>
            <small>Users with admin role</small>
          </article>

          <article className="aum-stat-card aum-stat-xp">
            <div className="aum-stat-top">
              <span>Total XP</span>
              <span className="aum-stat-icon">⚡</span>
            </div>
            <strong>{formatNumber(stats.xp)}</strong>
            <small>Combined XP in current result</small>
          </article>

          <article className="aum-stat-card aum-stat-completed">
            <div className="aum-stat-top">
              <span>Challenges Completed</span>
              <span className="aum-stat-icon">🏆</span>
            </div>
            <strong>{formatNumber(stats.completed)}</strong>
            <small>Combined completed challenges</small>
          </article>
        </section>

        {/* COMMUNITY OVERVIEW */}
        <section className="aum-overview-card">
          <div className="aum-overview-heading">
            <div>
              <span className="aum-section-kicker">
                COMMUNITY OVERVIEW
              </span>
              <h2>Challenge Completion</h2>
              <p>
                Completed challenges compared with recorded attempts.
              </p>
            </div>

            <strong>{completionRate}%</strong>
          </div>

          <div
            className="aum-progress-track"
            role="progressbar"
            aria-label="Challenge completion ratio"
            aria-valuemin={0}
            aria-valuemax={100}
            aria-valuenow={Math.min(completionRate, 100)}
          >
            <div
              className="aum-progress-fill"
              style={{ width: `${Math.min(completionRate, 100)}%` }}
            />
          </div>

          <div className="aum-progress-footer">
            <span>
              {formatNumber(stats.completed)} completed
            </span>
            <span>
              {formatNumber(stats.attempted)} attempts
            </span>
          </div>
        </section>

        {/* SEARCH AND FILTER */}
        <section className="aum-filter-card">
          <div className="aum-filter-heading">
            <div>
              <span className="aum-section-kicker">
                COMMUNITY DIRECTORY
              </span>
              <h2>Find Users</h2>
              <p>Search the directory and filter users by role.</p>
            </div>

            {(search || roleFilter !== 'ALL') && (
              <button
                type="button"
                className="aum-text-button"
                onClick={clearSearch}
              >
                Reset filters ↺
              </button>
            )}
          </div>

          <form onSubmit={handleSearch} className="aum-filter-controls">
            <div className="aum-search-wrapper">
              <span aria-hidden="true">⌕</span>

              <input
                type="search"
                placeholder="Search by name or email..."
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                aria-label="Search users by name or email"
              />

              {search && (
                <button
                  type="button"
                  onClick={() => setSearch('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}
            </div>

            <select
              value={roleFilter}
              onChange={(event) => setRoleFilter(event.target.value)}
              className="aum-role-select"
              aria-label="Filter users by role"
            >
              {ROLE_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>

            <button
              type="submit"
              className="aum-primary-button"
              disabled={loading}
            >
              Search Users
            </button>
          </form>

          <div className="aum-filter-result">
            Showing <strong>{filteredUsers.length}</strong> of{' '}
            <strong>{users.length}</strong> loaded users
          </div>
        </section>

        {/* USERS TABLE */}
        <section className="aum-table-card">
          <div className="aum-table-heading">
            <div>
              <span className="aum-section-kicker">
                USER DIRECTORY
              </span>
              <h2>QuestForge Community</h2>
              <p>Manage accounts and inspect user activity.</p>
            </div>

            <span className="aum-table-count">
              {filteredUsers.length} Results
            </span>
          </div>

          {loading ? (
            <div className="aum-empty-state">
              <span className="aum-loader" />
              <h3>Loading users</h3>
              <p>Fetching community information...</p>
            </div>
          ) : filteredUsers.length === 0 ? (
            <div className="aum-empty-state">
              <span className="aum-empty-icon">👤</span>
              <h3>No users found</h3>
              <p>
                No accounts match your current search or role filter.
              </p>
              <button
                type="button"
                className="aum-secondary-button"
                onClick={clearSearch}
              >
                Reset Search
              </button>
            </div>
          ) : (
            <div className="aum-table-wrapper">
              <table className="aum-table">
                <thead>
                  <tr>
                    <th>User</th>
                    <th>Role</th>
                    <th>XP</th>
                    <th>Level</th>
                    <th>Attempted</th>
                    <th>Completed</th>
                    <th>Streak</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredUsers.map((user, index) => {
                    const role = getRole(user.role);
                    const isSelected =
                      String(selectedUserId) === String(user.userId);

                    return (
                      <tr
                        key={user.userId ?? `user-${index}`}
                        className={isSelected ? 'aum-row-selected' : ''}
                      >
                        <td>
                          <div className="aum-user-cell">
                            <span className="aum-avatar">
                              {(user.name || 'U')
                                .trim()
                                .charAt(0)
                                .toUpperCase()}
                            </span>

                            <div>
                              <strong>{user.name || 'Unknown User'}</strong>
                              <small>{user.email || '—'}</small>
                            </div>
                          </div>
                        </td>

                        <td>
                          <span className={`aum-role-badge ${getRoleClass(role)}`}>
                            <span />
                            {role}
                          </span>
                        </td>

                        <td>
                          <strong className="aum-xp-value">
                            {formatNumber(user.xp)}
                          </strong>
                        </td>

                        <td>
                          <span className="aum-level-value">
                            {user.level ?? 1}
                          </span>
                        </td>

                        <td>{formatNumber(user.challengesAttempted)}</td>

                        <td>{formatNumber(user.challengesCompleted)}</td>

                        <td>
                          <span className="aum-streak-value">
                            🔥 {user.currentStreak ?? 0}
                          </span>
                        </td>

                        <td>
                          <button
                            type="button"
                            className={`aum-manage-button ${
                              isSelected ? 'aum-manage-active' : ''
                            }`}
                            onClick={() => selectUser(user)}
                          >
                            Manage <span>→</span>
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* USER MANAGEMENT PANEL */}
        {selectedUser && (
          <section
            className="aum-management-panel"
            aria-label="Selected user management"
          >
            <div className="aum-panel-header">
              <div className="aum-profile-heading">
                <span className="aum-profile-avatar">
                  {(selectedUser.name || 'U')
                    .trim()
                    .charAt(0)
                    .toUpperCase()}
                </span>

                <div>
                  <span className="aum-section-kicker">
                    USER CONTROL CENTER
                  </span>
                  <h2>{selectedUser.name || 'Unknown User'}</h2>
                  <p>{selectedUser.email || '—'}</p>
                </div>
              </div>

              <button
                type="button"
                className="aum-close-button"
                onClick={closeUserPanel}
                aria-label="Close user management"
              >
                ×
              </button>
            </div>

            {/* USER OVERVIEW */}
            <div className="aum-profile-stats">
              <div>
                <span>User ID</span>
                <strong>{selectedUser.userId ?? '—'}</strong>
              </div>

              <div>
                <span>Total XP</span>
                <strong>{formatNumber(selectedUser.xp)}</strong>
              </div>

              <div>
                <span>Level</span>
                <strong>{selectedUser.level ?? 1}</strong>
              </div>

              <div>
                <span>Current Streak</span>
                <strong>🔥 {selectedUser.currentStreak ?? 0}</strong>
              </div>

              <div>
                <span>Longest Streak</span>
                <strong>{selectedUser.longestStreak ?? 0}</strong>
              </div>
            </div>

            {/* MANAGEMENT CONTROLS */}
            <div className="aum-management-grid">

              {/* ROLE MANAGEMENT */}
              <article className="aum-action-card">
                <div className="aum-action-card-heading">
                  <span className="aum-action-icon aum-role-icon">
                    🛡️
                  </span>

                  <div>
                    <h3>Role Management</h3>
                    <p>Change this account's access role.</p>
                  </div>
                </div>

                <label htmlFor="aum-role-value">
                  Account Role
                </label>

                <select
                  id="aum-role-value"
                  value={roleValue}
                  onChange={(event) => setRoleValue(event.target.value)}
                  className="aum-control-input"
                  disabled={actionLoading !== null}
                >
                  <option value="USER">USER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>

                <button
                  type="button"
                  className="aum-action-button aum-role-action"
                  onClick={updateRole}
                  disabled={
                    actionLoading !== null ||
                    roleValue === getRole(selectedUser.role)
                  }
                >
                  {actionLoading === `role-${selectedUser.userId}`
                    ? 'Updating Role...'
                    : 'Update Role →'}
                </button>
              </article>

              {/* XP MANAGEMENT */}
              <article className="aum-action-card">
                <div className="aum-action-card-heading">
                  <span className="aum-action-icon aum-xp-icon">
                    ⚡
                  </span>

                  <div>
                    <h3>Reward XP</h3>
                    <p>Grant experience points to this user.</p>
                  </div>
                </div>

                <label htmlFor="aum-xp-amount">
                  XP Amount
                </label>

                <input
                  id="aum-xp-amount"
                  type="number"
                  min="1"
                  step="1"
                  inputMode="numeric"
                  placeholder="Enter XP amount"
                  value={xpAmount}
                  onChange={(event) => setXpAmount(event.target.value)}
                  className="aum-control-input"
                  disabled={actionLoading !== null}
                />

                <button
                  type="button"
                  className="aum-action-button aum-xp-action"
                  onClick={addXp}
                  disabled={
                    actionLoading !== null ||
                    !Number.isSafeInteger(Number(xpAmount)) ||
                    Number(xpAmount) < 1
                  }
                >
                  {actionLoading === `xp-${selectedUser.userId}`
                    ? 'Adding XP...'
                    : 'Grant XP →'}
                </button>
              </article>
            </div>

            {/* USER SUBMISSIONS */}
            <section className="aum-submissions-section">
              <div className="aum-submissions-heading">
                <div>
                  <span className="aum-section-kicker">
                    ACTIVITY HISTORY
                  </span>
                  <h3>User Submissions</h3>
                  <p>Inspect coding submissions made by this account.</p>
                </div>

                <button
                  type="button"
                  className="aum-secondary-button"
                  onClick={() => loadUserSubmissions(selectedUser.userId)}
                  disabled={submissionsLoading}
                >
                  {submissionsLoading
                    ? 'Loading...'
                    : submissionsLoaded
                      ? '↻ Refresh Submissions'
                      : 'View Submissions →'}
                </button>
              </div>

              {submissionsLoading ? (
                <div className="aum-submissions-empty">
                  <span className="aum-mini-loader" />
                  Loading user submissions...
                </div>
              ) : submissionsLoaded && userSubmissions.length === 0 ? (
                <div className="aum-submissions-empty">
                  <span>📭</span>
                  <strong>No submissions found</strong>
                  <p>This user has no recorded submissions.</p>
                </div>
              ) : submissionsLoaded ? (
                <div className="aum-table-wrapper aum-submissions-table">
                  <table className="aum-table">
                    <thead>
                      <tr>
                        <th>Submission</th>
                        <th>Challenge</th>
                        <th>Status</th>
                        <th>Score</th>
                        <th>Submitted</th>
                      </tr>
                    </thead>

                    <tbody>
                      {userSubmissions.map((submission, index) => {
                        const submissionId = getSubmissionId(submission);
                        const status = getRole(submission.status);

                        return (
                          <tr
                            key={
                              submissionId ??
                              `submission-${index}`
                            }
                          >
                            <td>
                              #{submissionId ?? '—'}
                            </td>

                            <td>
                              {submission.challengeTitle ||
                                `Challenge #${submission.challengeId ?? '—'}`}
                            </td>

                            <td>
                              <span
                                className={`aum-submission-status aum-submission-${status.toLowerCase()}`}
                              >
                                <span />
                                {status}
                              </span>
                            </td>

                            <td>{submission.score ?? 0}</td>

                            <td>
                              {formatDate(submission.submittedAt)}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="aum-submissions-empty">
                  <span>📋</span>
                  <strong>Submission history not loaded</strong>
                  <p>
                    Select View Submissions to retrieve this user's
                    submission records.
                  </p>
                </div>
              )}
            </section>
          </section>
        )}

        {/* FOOTER */}
        <footer className="aum-footer">
          <span>
            <span />
            QuestForge Administration
          </span>
          <span>User Management Workspace</span>
        </footer>
      </div>
    </main>
  );
}

export default AdminUsers;
