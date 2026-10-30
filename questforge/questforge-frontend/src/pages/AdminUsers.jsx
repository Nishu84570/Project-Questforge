import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);

  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  const [selectedUser, setSelectedUser] = useState(null);
  const [userSubmissions, setUserSubmissions] = useState([]);
  const [submissionsLoading, setSubmissionsLoading] =
    useState(false);

  const [xpAmount, setXpAmount] = useState('');
  const [roleValue, setRoleValue] = useState('');

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  // =========================
  // LOAD USERS
  // =========================

  const loadUsers = async (query = '') => {
    setLoading(true);
    setError('');

    try {
      const endpoint = query.trim()
        ? `/admin/users/search?query=${encodeURIComponent(
            query.trim()
          )}`
        : '/admin/users';

      const { response, data } =
        await apiRequest(endpoint, {
          method: 'GET',
        });

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load admin users.'
        );
        return;
      }

      setUsers(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Admin users error:', error);

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  // =========================
  // FILTER USERS
  // =========================

  const filteredUsers = useMemo(() => {
    return users.filter((user) => {
      const role =
        String(user.role || '').toUpperCase();

      if (
        roleFilter !== 'ALL' &&
        role !== roleFilter
      ) {
        return false;
      }

      return true;
    });
  }, [users, roleFilter]);

  // =========================
  // SEARCH
  // =========================

  const handleSearch = async (event) => {
    event.preventDefault();

    await loadUsers(search);
  };

  // =========================
  // CLEAR SEARCH
  // =========================

  const clearSearch = async () => {
    setSearch('');
    setRoleFilter('ALL');

    await loadUsers('');
  };

  // =========================
  // SELECT USER
  // =========================

  const selectUser = (user) => {
    setSelectedUser(user);

    setRoleValue(
      user.role || 'USER'
    );

    setXpAmount('');

    setMessage('');
    setError('');

    setUserSubmissions([]);
  };

  // =========================
  // LOAD USER SUBMISSIONS
  // =========================

  const loadUserSubmissions = async (
    userId
  ) => {
    setSubmissionsLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/admin/users/${userId}/submissions`,
          {
            method: 'GET',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load user submissions.'
        );
        return;
      }

      setUserSubmissions(
        Array.isArray(data) ? data : []
      );
    } catch (error) {
      console.error(
        'User submissions error:',
        error
      );

      setError(
        'Unable to load user submissions.'
      );
    } finally {
      setSubmissionsLoading(false);
    }
  };

  // =========================
  // UPDATE ROLE
  // =========================

  const updateRole = async () => {
    if (!selectedUser) {
      return;
    }

    if (!roleValue) {
      setError('Please select a role.');
      return;
    }

    setActionLoading(
      `role-${selectedUser.userId}`
    );

    setMessage('');
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/admin/users/${selectedUser.userId}/role`,
          {
            method: 'PUT',
            body: JSON.stringify({
              role: roleValue,
            }),
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to update user role.'
        );
        return;
      }

      const updatedUser = {
        ...selectedUser,
        role:
          data?.role ||
          roleValue,
      };

      setSelectedUser(updatedUser);

      setUsers((currentUsers) =>
        currentUsers.map((user) =>
          user.userId ===
          selectedUser.userId
            ? {
                ...user,
                role:
                  data?.role ||
                  roleValue,
              }
            : user
        )
      );

      setMessage(
        'User role updated successfully.'
      );
    } catch (error) {
      console.error(
        'Update role error:',
        error
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================
  // ADD XP
  // =========================

  const addXp = async () => {
    if (!selectedUser) {
      return;
    }

    const xp = Number(xpAmount);

    if (
      !Number.isFinite(xp) ||
      xp < 1
    ) {
      setError(
        'XP amount must be at least 1.'
      );
      return;
    }

    setActionLoading(
      `xp-${selectedUser.userId}`
    );

    setMessage('');
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/admin/users/${selectedUser.userId}/xp`,
          {
            method: 'POST',
            body: JSON.stringify({
              xp,
            }),
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to add XP.'
        );
        return;
      }

      const newXp =
        data?.xp ??
        data?.totalXp ??
        selectedUser.xp + xp;

      const updatedUser = {
        ...selectedUser,
        xp: newXp,
      };

      setSelectedUser(updatedUser);

      setUsers((currentUsers) =>
        currentUsers.map((user) =>
          user.userId ===
          selectedUser.userId
            ? {
                ...user,
                xp: newXp,
              }
            : user
        )
      );

      setXpAmount('');

      setMessage(
        `${xp} XP added successfully.`
      );
    } catch (error) {
      console.error(
        'Add XP error:',
        error
      );

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================
  // FORMAT DATE
  // =========================

  const formatDate = (value) => {
    if (!value) {
      return '—';
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString();
  };

  // =========================
  // CLOSE USER PANEL
  // =========================

  const closeUserPanel = () => {
    setSelectedUser(null);
    setUserSubmissions([]);
    setMessage('');
    setError('');
  };

  // =========================
  // RENDER
  // =========================

  return (
    <div className="page admin-page">

      <div className="page-header">

        <div>
          <span className="eyebrow">
            ADMIN
          </span>

          <h1>
            User Management
          </h1>

          <p>
            Manage QuestForge users, roles,
            XP and submission activity.
          </p>
        </div>

        <div
          style={{
            display: 'flex',
            gap: '10px',
            flexWrap: 'wrap',
          }}
        >
          <Link
            to="/admin"
            className="secondary-button"
          >
            ← Admin Dashboard
          </Link>

          <button
            className="secondary-button"
            onClick={() =>
              loadUsers(search)
            }
            disabled={loading}
          >
            {loading
              ? 'Loading...'
              : '↻ Refresh'}
          </button>
        </div>

      </div>

      {message && (
        <div className="success-message">
          {message}
        </div>
      )}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* =========================
          SEARCH + FILTER
      ========================= */}

      <div className="admin-form-card">

        <form
          onSubmit={handleSearch}
          style={{
            display: 'flex',
            gap: '12px',
            flexWrap: 'wrap',
            alignItems: 'center',
          }}
        >

          <input
            type="text"
            placeholder="Search by name or email..."
            value={search}
            onChange={(event) =>
              setSearch(
                event.target.value
              )
            }
            style={{
              flex: 1,
              minWidth: '240px',
            }}
          />

          <select
            value={roleFilter}
            onChange={(event) =>
              setRoleFilter(
                event.target.value
              )
            }
            style={{
              minWidth: '150px',
            }}
          >
            <option value="ALL">
              All Roles
            </option>

            <option value="USER">
              USER
            </option>

            <option value="ADMIN">
              ADMIN
            </option>
          </select>

          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            Search
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={clearSearch}
          >
            Clear
          </button>

        </form>

      </div>

      {/* =========================
          USER COUNT
      ========================= */}

      <div className="result-stats">

        <div className="result-stat-card">
          <span className="result-stat-label">
            Total Users
          </span>

          <strong>
            {users.length}
          </strong>
        </div>

        <div className="result-stat-card">
          <span className="result-stat-label">
            Showing
          </span>

          <strong>
            {filteredUsers.length}
          </strong>
        </div>

        <div className="result-stat-card">
          <span className="result-stat-label">
            Admins
          </span>

          <strong>
            {
              users.filter(
                (user) =>
                  String(
                    user.role || ''
                  ).toUpperCase() ===
                  'ADMIN'
              ).length
            }
          </strong>
        </div>

        <div className="result-stat-card">
          <span className="result-stat-label">
            Total XP
          </span>

          <strong>
            {users.reduce(
              (total, user) =>
                total +
                Number(user.xp || 0),
              0
            )}
          </strong>
        </div>

      </div>

      {/* =========================
          USER TABLE
      ========================= */}

      <div className="admin-table-card">

        <div className="section-heading">

          <div>
            <span className="eyebrow">
              USERS
            </span>

            <h2>
              QuestForge Users
            </h2>
          </div>

        </div>

        {loading ? (

          <div className="empty-card">
            <h2>
              Loading users...
            </h2>
          </div>

        ) : filteredUsers.length === 0 ? (

          <div className="empty-card">
            <h2>
              No users found
            </h2>

            <p>
              Try changing the search
              or role filter.
            </p>
          </div>

        ) : (

          <div
            style={{
              overflowX: 'auto',
            }}
          >

            <table className="admin-table">

              <thead>
                <tr>
                  <th>
                    User
                  </th>

                  <th>
                    Role
                  </th>

                  <th>
                    XP
                  </th>

                  <th>
                    Level
                  </th>

                  <th>
                    Attempted
                  </th>

                  <th>
                    Completed
                  </th>

                  <th>
                    Streak
                  </th>

                  <th>
                    Action
                  </th>
                </tr>
              </thead>

              <tbody>

                {filteredUsers.map(
                  (user) => (

                    <tr
                      key={
                        user.userId
                      }
                    >

                      <td>
                        <strong>
                          {user.name ||
                            'Unknown User'}
                        </strong>

                        <br />

                        <small>
                          {user.email ||
                            '—'}
                        </small>
                      </td>

                      <td>
                        <span
                          className={`status-badge status-${String(
                            user.role ||
                              'USER'
                          ).toLowerCase()}`}
                        >
                          {user.role ||
                            'USER'}
                        </span>
                      </td>

                      <td>
                        {user.xp ?? 0}
                      </td>

                      <td>
                        {user.level ?? 1}
                      </td>

                      <td>
                        {
                          user.challengesAttempted ??
                          0
                        }
                      </td>

                      <td>
                        {
                          user.challengesCompleted ??
                          0
                        }
                      </td>

                      <td>
                        🔥{' '}
                        {
                          user.currentStreak ??
                          0
                        }
                      </td>

                      <td>

                        <button
                          className="secondary-button"
                          onClick={() =>
                            selectUser(
                              user
                            )
                          }
                        >
                          Manage
                        </button>

                      </td>

                    </tr>

                  )
                )}

              </tbody>

            </table>

          </div>

        )}

      </div>

      {/* =========================
          USER MANAGEMENT PANEL
      ========================= */}

      {selectedUser && (

        <div className="admin-form-card">

          <div
            style={{
              display: 'flex',
              justifyContent:
                'space-between',
              gap: '15px',
              flexWrap: 'wrap',
              alignItems: 'flex-start',
            }}
          >

            <div>
              <span className="eyebrow">
                USER MANAGEMENT
              </span>

              <h2>
                {selectedUser.name ||
                  'Unknown User'}
              </h2>

              <p>
                {selectedUser.email ||
                  '—'}
              </p>
            </div>

            <button
              className="secondary-button"
              onClick={
                closeUserPanel
              }
            >
              ✕ Close
            </button>

          </div>

          <div
            className="result-stats"
            style={{
              marginTop: '20px',
            }}
          >

            <div className="result-stat-card">
              <span className="result-stat-label">
                User ID
              </span>

              <strong>
                {selectedUser.userId}
              </strong>
            </div>

            <div className="result-stat-card">
              <span className="result-stat-label">
                XP
              </span>

              <strong>
                {selectedUser.xp ?? 0}
              </strong>
            </div>

            <div className="result-stat-card">
              <span className="result-stat-label">
                Level
              </span>

              <strong>
                {selectedUser.level ?? 1}
              </strong>
            </div>

            <div className="result-stat-card">
              <span className="result-stat-label">
                Longest Streak
              </span>

              <strong>
                {selectedUser.longestStreak ??
                  0}
              </strong>
            </div>

          </div>

          {/* ROLE */}

          <div
            style={{
              marginTop: '25px',
            }}
          >

            <h3>
              Update Role
            </h3>

            <div
              style={{
                display: 'flex',
                gap: '12px',
                flexWrap: 'wrap',
              }}
            >

              <select
                value={roleValue}
                onChange={(event) =>
                  setRoleValue(
                    event.target.value
                  )
                }
              >

                <option value="USER">
                  USER
                </option>

                <option value="ADMIN">
                  ADMIN
                </option>

              </select>

              <button
                className="primary-button"
                onClick={updateRole}
                disabled={
                  actionLoading ===
                  `role-${selectedUser.userId}`
                }
              >
                {actionLoading ===
                `role-${selectedUser.userId}`
                  ? 'Updating...'
                  : 'Update Role'}
              </button>

            </div>

          </div>

          {/* XP */}

          <div
            style={{
              marginTop: '25px',
            }}
          >

            <h3>
              Add XP
            </h3>

            <div
              style={{
                display: 'flex',
                gap: '12px',
                flexWrap: 'wrap',
              }}
            >

              <input
                type="number"
                min="1"
                placeholder="XP amount"
                value={xpAmount}
                onChange={(event) =>
                  setXpAmount(
                    event.target.value
                  )
                }
              />

              <button
                className="primary-button"
                onClick={addXp}
                disabled={
                  actionLoading ===
                  `xp-${selectedUser.userId}`
                }
              >
                {actionLoading ===
                `xp-${selectedUser.userId}`
                  ? 'Adding...'
                  : 'Add XP'}
              </button>

            </div>

          </div>

          {/* SUBMISSIONS */}

          <div
            style={{
              marginTop: '30px',
            }}
          >

            <div
              style={{
                display: 'flex',
                justifyContent:
                  'space-between',
                gap: '12px',
                flexWrap: 'wrap',
                alignItems: 'center',
              }}
            >

              <div>
                <h3>
                  User Submissions
                </h3>

                <p>
                  Review submissions
                  made by this user.
                </p>
              </div>

              <button
                className="secondary-button"
                onClick={() =>
                  loadUserSubmissions(
                    selectedUser.userId
                  )
                }
                disabled={
                  submissionsLoading
                }
              >
                {submissionsLoading
                  ? 'Loading...'
                  : 'View Submissions'}
              </button>

            </div>

            {userSubmissions.length >
              0 && (

              <div
                style={{
                  overflowX: 'auto',
                  marginTop: '15px',
                }}
              >

                <table className="admin-table">

                  <thead>
                    <tr>
                      <th>
                        Submission
                      </th>

                      <th>
                        Challenge
                      </th>

                      <th>
                        Status
                      </th>

                      <th>
                        Score
                      </th>

                      <th>
                        Submitted
                      </th>
                    </tr>
                  </thead>

                  <tbody>

                    {userSubmissions.map(
                      (submission) => (

                        <tr
                          key={
                            submission.id ||
                            submission.submissionId
                          }
                        >

                          <td>
                            #
                            {submission.id ||
                              submission.submissionId}
                          </td>

                          <td>
                            {submission.challengeTitle ||
                              `Challenge #${
                                submission.challengeId ??
                                '—'
                              }`}
                          </td>

                          <td>
                            {submission.status ||
                              'PENDING'}
                          </td>

                          <td>
                            {submission.score ??
                              0}
                          </td>

                          <td>
                            {formatDate(
                              submission.submittedAt
                            )}
                          </td>

                        </tr>

                      )
                    )}

                  </tbody>

                </table>

              </div>

            )}

            {!submissionsLoading &&
              userSubmissions.length ===
                0 && (

              <div
                className="empty-card"
                style={{
                  marginTop: '15px',
                }}
              >
                <p>
                  No submissions loaded.
                  Click "View Submissions"
                  to check this user's
                  activity.
                </p>
              </div>

            )}

          </div>

        </div>

      )}

    </div>
  );
}

export default AdminUsers;