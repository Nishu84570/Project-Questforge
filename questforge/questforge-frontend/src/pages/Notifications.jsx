import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  // ============================================================
  // FETCH NOTIFICATIONS
  // ============================================================

  const fetchNotifications = async () => {
    setLoading(true);
    setError('');

    try {
      const { response, data } = await apiRequest(
        '/notifications',
        {
          method: 'GET',
        }
      );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to load notifications.'
        );
        return;
      }

      setNotifications(
        Array.isArray(data) ? data : []
      );
    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  // ============================================================
  // UNREAD COUNT
  // ============================================================

  const unreadCount = useMemo(
    () =>
      notifications.filter(
        (notification) => !notification.read
      ).length,
    [notifications]
  );

  const readCount =
    notifications.length - unreadCount;

  // ============================================================
  // FILTER + SEARCH
  // ============================================================

  const filteredNotifications = useMemo(() => {
    const search = searchTerm
      .trim()
      .toLowerCase();

    return notifications.filter(
      (notification) => {
        const matchesFilter =
          filter === 'ALL' ||
          (filter === 'UNREAD' &&
            !notification.read) ||
          (filter === 'READ' &&
            notification.read);

        if (!matchesFilter) {
          return false;
        }

        if (!search) {
          return true;
        }

        return (
          notification.title
            ?.toLowerCase()
            .includes(search) ||
          notification.message
            ?.toLowerCase()
            .includes(search) ||
          notification.type
            ?.toLowerCase()
            .includes(search)
        );
      }
    );
  }, [
    notifications,
    filter,
    searchTerm,
  ]);

  // ============================================================
  // MARK AS READ
  // ============================================================

  const markAsRead = async (notificationId) => {
    setActionLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/notifications/${notificationId}/read`,
          {
            method: 'PUT',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to mark notification as read.'
        );
        return;
      }

      setNotifications((current) =>
        current.map((notification) =>
          notification.id === notificationId
            ? {
                ...notification,
                ...data,
                read: true,
              }
            : notification
        )
      );

      window.dispatchEvent(
        new Event(
          'questforge-notification-change'
        )
      );
    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  // ============================================================
  // MARK ALL AS READ
  // ============================================================

  const markAllAsRead = async () => {
    if (unreadCount === 0) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          '/notifications/read-all',
          {
            method: 'PUT',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to mark all notifications as read.'
        );
        return;
      }

      setNotifications((current) =>
        current.map((notification) => ({
          ...notification,
          read: true,
        }))
      );

      window.dispatchEvent(
        new Event(
          'questforge-notification-change'
        )
      );
    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  // ============================================================
  // DELETE
  // ============================================================

  const deleteNotification = async (
    notificationId
  ) => {
    const confirmed = window.confirm(
      'Delete this notification?'
    );

    if (!confirmed) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const { response, data } =
        await apiRequest(
          `/notifications/${notificationId}`,
          {
            method: 'DELETE',
          }
        );

      if (!response.ok) {
        setError(
          data?.message ||
            'Failed to delete notification.'
        );
        return;
      }

      setNotifications((current) =>
        current.filter(
          (notification) =>
            notification.id !== notificationId
        )
      );

      window.dispatchEvent(
        new Event(
          'questforge-notification-change'
        )
      );
    } catch (error) {
      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  // ============================================================
  // CLEAR FILTERS
  // ============================================================

  const clearFilters = () => {
    setFilter('ALL');
    setSearchTerm('');
  };

  // ============================================================
  // ICON
  // ============================================================

  const getNotificationIcon = (type) => {
    const normalized =
      type?.toLowerCase().trim();

    if (normalized?.includes('success')) {
      return '✓';
    }

    if (normalized?.includes('warning')) {
      return '⚠';
    }

    if (normalized?.includes('error')) {
      return '!';
    }

    if (normalized?.includes('challenge')) {
      return '⚔';
    }

    if (normalized?.includes('submission')) {
      return '⌁';
    }

    if (normalized?.includes('leaderboard')) {
      return '🏆';
    }

    return '🔔';
  };

  // ============================================================
  // TYPE
  // ============================================================

  const getNotificationType = (type) => {
    if (!type) {
      return 'Update';
    }

    return type
      .replaceAll('_', ' ')
      .replaceAll('-', ' ')
      .toLowerCase()
      .replace(
        /\b\w/g,
        (letter) => letter.toUpperCase()
      );
  };

  // ============================================================
  // LOADING
  // ============================================================

  if (loading) {
    return (
      <div className="page notifications-page">
        <div className="notifications-empty-card">
          <div className="notification-empty-icon">
            🔔
          </div>

          <div className="loading-spinner"></div>

          <h2>
            Loading Notifications
          </h2>

          <p>
            Checking your latest QuestForge updates...
          </p>
        </div>
      </div>
    );
  }

  // ============================================================
  // ERROR
  // ============================================================

  if (
    error &&
    notifications.length === 0
  ) {
    return (
      <div className="page notifications-page">
        <div className="notifications-empty-card">
          <div className="notification-empty-icon error">
            !
          </div>

          <h2>
            Something went wrong
          </h2>

          <p className="error-text">
            {error}
          </p>

          <button
            type="button"
            className="primary-button"
            onClick={fetchNotifications}
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  // ============================================================
  // MAIN
  // ============================================================

  return (
    <div className="page notifications-page">

      {/* HEADER */}

      <div className="notifications-header">

        <div className="notifications-title">

          <div className="notifications-title-icon">
            🔔
          </div>

          <div>
            <p className="eyebrow">
              QUESTFORGE
            </p>

            <h1>
              Notifications
            </h1>

            <p>
              Stay updated with your latest
              QuestForge activity.
            </p>
          </div>

        </div>

        <div className="notifications-header-actions">

          <Link
            className="secondary-button"
            to="/profile"
          >
            Profile
          </Link>

          <button
            type="button"
            className="primary-button"
            onClick={markAllAsRead}
            disabled={
              unreadCount === 0 ||
              actionLoading
            }
          >
            ✓ Mark All Read
          </button>

        </div>

      </div>


      {/* ERROR */}

      {error && (
        <div className="notification-error-banner">
          <span>⚠</span>
          {error}
        </div>
      )}


      {/* SUMMARY */}

      <div className="notification-summary">

        <div className="notification-summary-card">

          <div className="notification-summary-icon">
            🔔
          </div>

          <div>
            <span>Total</span>
            <strong>
              {notifications.length}
            </strong>
          </div>

        </div>


        <div className="notification-summary-card unread-summary">

          <div className="notification-summary-icon">
            ●
          </div>

          <div>
            <span>Unread</span>
            <strong>
              {unreadCount}
            </strong>
          </div>

        </div>


        <div className="notification-summary-card">

          <div className="notification-summary-icon">
            ✓
          </div>

          <div>
            <span>Read</span>
            <strong>
              {readCount}
            </strong>
          </div>

        </div>

      </div>


      {/* TOOLBAR */}

      {notifications.length > 0 && (
        <div className="notifications-toolbar">

          <div className="notification-search">

            <span>⌕</span>

            <input
              type="text"
              placeholder="Search notifications..."
              value={searchTerm}
              onChange={(event) =>
                setSearchTerm(
                  event.target.value
                )
              }
            />

          </div>


          <div className="notification-filter">

            <button
              type="button"
              className={
                filter === 'ALL'
                  ? 'active'
                  : ''
              }
              onClick={() =>
                setFilter('ALL')
              }
            >
              All
            </button>

            <button
              type="button"
              className={
                filter === 'UNREAD'
                  ? 'active'
                  : ''
              }
              onClick={() =>
                setFilter('UNREAD')
              }
            >
              Unread

              {unreadCount > 0 && (
                <span>
                  {unreadCount}
                </span>
              )}
            </button>

            <button
              type="button"
              className={
                filter === 'READ'
                  ? 'active'
                  : ''
              }
              onClick={() =>
                setFilter('READ')
              }
            >
              Read
            </button>

          </div>


          {(filter !== 'ALL' ||
            searchTerm) && (
            <button
              type="button"
              className="notification-clear-button"
              onClick={clearFilters}
            >
              Clear
            </button>
          )}

        </div>
      )}


      {/* EMPTY */}

      {notifications.length === 0 ? (

        <div className="notifications-empty-card">

          <div className="notification-empty-icon">
            🎉
          </div>

          <h2>
            You're all caught up
          </h2>

          <p>
            You don't have any notifications yet.
            We'll let you know when something happens.
          </p>

        </div>

      ) : filteredNotifications.length === 0 ? (

        <div className="notifications-empty-card">

          <div className="notification-empty-icon">
            🔎
          </div>

          <h2>
            No matching notifications
          </h2>

          <p>
            Try changing your search or filter.
          </p>

          <button
            type="button"
            className="secondary-button"
            onClick={clearFilters}
          >
            Reset Filters
          </button>

        </div>

      ) : (

        /* NOTIFICATION LIST */

        <div className="notification-list">

          {filteredNotifications.map(
            (notification) => (

              <article
                key={notification.id}
                className={`notification-item ${
                  notification.read
                    ? 'notification-read'
                    : 'notification-unread'
                }`}
              >

                <div className="notification-icon">
                  {getNotificationIcon(
                    notification.type
                  )}
                </div>


                <div className="notification-content">

                  <div className="notification-meta">

                    <span className="notification-type">
                      {getNotificationType(
                        notification.type
                      )}
                    </span>

                    {!notification.read && (
                      <span className="notification-new-badge">
                        NEW
                      </span>
                    )}

                  </div>


                  <h3>
                    {notification.title ||
                      'QuestForge Notification'}
                  </h3>


                  <p>
                    {notification.message ||
                      'You have a new QuestForge update.'}
                  </p>


                  <div className="notification-time">

                    <span>◷</span>

                    <span>
                      {notification.createdAt
                        ? new Date(
                            notification.createdAt
                          ).toLocaleString()
                        : 'Unknown'}
                    </span>

                  </div>

                </div>


                <div className="notification-actions">

                  {!notification.read && (
                    <button
                      type="button"
                      className="notification-action-button mark-read-button"
                      onClick={() =>
                        markAsRead(
                          notification.id
                        )
                      }
                      disabled={actionLoading}
                      title="Mark as read"
                    >
                      ✓
                    </button>
                  )}

                  <button
                    type="button"
                    className="notification-action-button delete-button"
                    onClick={() =>
                      deleteNotification(
                        notification.id
                      )
                    }
                    disabled={actionLoading}
                    title="Delete notification"
                  >
                    ×
                  </button>

                </div>

              </article>

            )
          )}

        </div>

      )}

    </div>
  );
}

export default Notifications;