import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/Notifications.css';

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const [busyNotificationIds, setBusyNotificationIds] = useState([]);
  const [markingAll, setMarkingAll] = useState(false);

  const unreadCount = useMemo(
    () => notifications.filter((item) => !item.read).length,
    [notifications]
  );

  const readCount = notifications.length - unreadCount;

  const dispatchNotificationChange = () => {
    window.dispatchEvent(new Event('questforge-notification-change'));
  };

  const fetchNotifications = useCallback(async (showRefresh = false) => {
    if (showRefresh) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError('');

    try {
      const { response, data } = await apiRequest('/notifications', {
        method: 'GET',
      });

      if (!response.ok) {
        setError(data?.message || 'Failed to load notifications.');
        return;
      }

      if (!Array.isArray(data)) {
        setError('Unexpected response received from the server.');
        return;
      }

      setNotifications(data);
      dispatchNotificationChange();
    } catch (err) {
      console.error('Notification fetch error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchNotifications();

    const handleNotificationChange = () => {
      fetchNotifications(true);
    };

    window.addEventListener(
      'questforge-notification-change',
      handleNotificationChange
    );

    return () => {
      window.removeEventListener(
        'questforge-notification-change',
        handleNotificationChange
      );
    };
  }, [fetchNotifications]);

  const setNotificationBusy = (notificationId, busy) => {
    setBusyNotificationIds((current) => {
      if (busy) {
        return current.includes(notificationId)
          ? current
          : [...current, notificationId];
      }

      return current.filter((id) => id !== notificationId);
    });
  };

  const markAsRead = async (notificationId) => {
    if (busyNotificationIds.includes(notificationId)) return;

    setNotificationBusy(notificationId, true);
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/notifications/${notificationId}/read`,
        { method: 'PUT' }
      );

      if (!response.ok) {
        setError(data?.message || 'Failed to mark notification as read.');
        return;
      }

      setNotifications((current) =>
        current.map((notification) =>
          notification.id === notificationId
            ? { ...notification, ...data, read: true }
            : notification
        )
      );

      dispatchNotificationChange();
    } catch (err) {
      console.error('Mark notification as read error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setNotificationBusy(notificationId, false);
    }
  };

  const markAllAsRead = async () => {
    if (unreadCount === 0 || markingAll) return;

    setMarkingAll(true);
    setError('');

    try {
      const { response, data } = await apiRequest('/notifications/read-all', {
        method: 'PUT',
      });

      if (!response.ok) {
        setError(data?.message || 'Failed to mark all notifications as read.');
        return;
      }

      setNotifications((current) =>
        current.map((notification) => ({
          ...notification,
          read: true,
        }))
      );

      dispatchNotificationChange();
    } catch (err) {
      console.error('Mark all notifications as read error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setMarkingAll(false);
    }
  };

  const deleteNotification = async (notificationId) => {
    if (busyNotificationIds.includes(notificationId)) return;

    const confirmed = window.confirm(
      'Are you sure you want to delete this notification?'
    );

    if (!confirmed) return;

    setNotificationBusy(notificationId, true);
    setError('');

    try {
      const { response, data } = await apiRequest(
        `/notifications/${notificationId}`,
        { method: 'DELETE' }
      );

      if (!response.ok) {
        setError(data?.message || 'Failed to delete notification.');
        return;
      }

      setNotifications((current) =>
        current.filter((notification) => notification.id !== notificationId)
      );

      dispatchNotificationChange();
    } catch (err) {
      console.error('Delete notification error:', err);
      setError('Unable to connect to QuestForge backend.');
    } finally {
      setNotificationBusy(notificationId, false);
    }
  };

  const clearFilters = () => {
    setFilter('ALL');
    setSearchTerm('');
  };

  const filteredNotifications = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    return notifications.filter((notification) => {
      const matchesFilter =
        filter === 'ALL' ||
        (filter === 'UNREAD' && !notification.read) ||
        (filter === 'READ' && notification.read);

      if (!matchesFilter) return false;
      if (!search) return true;

      const title = String(notification.title || '').toLowerCase();
      const message = String(notification.message || '').toLowerCase();
      const type = String(notification.type || '').toLowerCase();

      return (
        title.includes(search) ||
        message.includes(search) ||
        type.includes(search)
      );
    });
  }, [notifications, filter, searchTerm]);

  const getNotificationIcon = (type) => {
    const normalized = String(type || '').toLowerCase().trim();

    if (normalized.includes('success')) return '✓';
    if (normalized.includes('warning')) return '⚠';
    if (normalized.includes('error')) return '!';
    if (normalized.includes('challenge')) return '⚔';
    if (normalized.includes('submission')) return '⌁';
    if (normalized.includes('leaderboard')) return '🏆';
    if (normalized.includes('achievement')) return '🏅';
    if (normalized.includes('streak')) return '🔥';
    if (normalized.includes('quest')) return '🎯';

    return '🔔';
  };

  const getNotificationType = (type) => {
    if (!type) return 'General Update';

    return String(type)
      .replace(/[_-]+/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());
  };

  const formatNotificationDate = (createdAt) => {
    if (!createdAt) return 'Date unavailable';

    const date = new Date(createdAt);

    if (Number.isNaN(date.getTime())) {
      return 'Date unavailable';
    }

    return date.toLocaleString(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  };

  if (loading) {
    return (
      <main className="page notifications-page">
        <section className="notifications-loading-card">
          <div className="notifications-loading-icon">🔔</div>
          <div className="notifications-spinner" />
          <h2>Loading your notifications</h2>
          <p>Getting your latest QuestForge updates ready...</p>

          <div className="notification-skeleton-list">
            <div className="notification-skeleton" />
            <div className="notification-skeleton" />
            <div className="notification-skeleton" />
          </div>
        </section>
      </main>
    );
  }

  if (error && notifications.length === 0) {
    return (
      <main className="page notifications-page">
        <section className="notifications-empty-card">
          <div className="notification-empty-icon error">!</div>
          <span className="notifications-eyebrow">QUESTFORGE</span>
          <h2>Unable to load notifications</h2>
          <p>{error}</p>

          <button
            type="button"
            className="notification-primary-button"
            onClick={() => fetchNotifications()}
          >
            <span>↻</span>
            Try Again
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="page notifications-page">
      {/* PAGE HEADER */}
      <section className="notifications-header">
        <div className="notifications-header-glow" />

        <div className="notifications-title">
          <div className="notifications-title-icon" aria-hidden="true">
            🔔
            {unreadCount > 0 && (
              <span className="header-unread-dot" />
            )}
          </div>

          <div className="notifications-heading-content">
            <span className="notifications-eyebrow">
              QUESTFORGE / YOUR ACTIVITY
            </span>

            <h1>Notifications</h1>

            <p>
              Stay connected with your coding journey and latest achievements.
            </p>
          </div>
        </div>

        <div className="notifications-header-actions">
          <button
            type="button"
            className="notification-secondary-button refresh-button"
            onClick={() => fetchNotifications(true)}
            disabled={refreshing}
          >
            <span className={refreshing ? 'refresh-icon is-spinning' : 'refresh-icon'}>
              ↻
            </span>
            {refreshing ? 'Refreshing' : 'Refresh'}
          </button>

          <Link className="notification-secondary-button" to="/profile">
            <span>◉</span>
            Profile
          </Link>

          <button
            type="button"
            className="notification-primary-button"
            onClick={markAllAsRead}
            disabled={unreadCount === 0 || markingAll}
          >
            <span>✓</span>
            {markingAll ? 'Updating...' : 'Mark All Read'}
          </button>
        </div>
      </section>

      {/* ERROR MESSAGE */}
      {error && (
        <div className="notification-error-banner" role="alert">
          <span className="error-banner-icon">⚠</span>
          <span>{error}</span>
          <button
            type="button"
            onClick={() => setError('')}
            aria-label="Dismiss error"
          >
            ×
          </button>
        </div>
      )}

      {/* SUMMARY CARDS */}
      <section className="notification-summary">
        <article className="notification-summary-card total-summary">
          <div className="notification-summary-icon">🔔</div>
          <div className="notification-summary-info">
            <span>Total Notifications</span>
            <strong>{notifications.length}</strong>
            <small>Your QuestForge updates</small>
          </div>
        </article>

        <article className="notification-summary-card unread-summary">
          <div className="notification-summary-icon">●</div>
          <div className="notification-summary-info">
            <span>Unread Notifications</span>
            <strong>{unreadCount}</strong>
            <small>Waiting for your attention</small>
          </div>
        </article>

        <article className="notification-summary-card read-summary">
          <div className="notification-summary-icon">✓</div>
          <div className="notification-summary-info">
            <span>Read Notifications</span>
            <strong>{readCount}</strong>
            <small>Already checked</small>
          </div>
        </article>
      </section>

      {/* TOOLBAR */}
      {notifications.length > 0 && (
        <section className="notifications-toolbar">
          <div className="notification-toolbar-heading">
            <div>
              <span className="notifications-eyebrow">YOUR INBOX</span>
              <h2>All Updates</h2>
            </div>

            <span className="notification-results-count">
              {filteredNotifications.length} of {notifications.length}
            </span>
          </div>

          <div className="notification-toolbar-controls">
            <label className="notification-search">
              <span className="notification-search-icon" aria-hidden="true">
                ⌕
              </span>

              <input
                type="search"
                placeholder="Search notifications..."
                value={searchTerm}
                onChange={(event) => setSearchTerm(event.target.value)}
                className="notification-search-input"
                aria-label="Search notifications"
              />

              {searchTerm && (
                <button
                  type="button"
                  className="notification-search-clear"
                  onClick={() => setSearchTerm('')}
                  aria-label="Clear search"
                >
                  ×
                </button>
              )}
            </label>

            <div className="notification-filter" aria-label="Filter notifications">
              <button
                type="button"
                className={filter === 'ALL' ? 'active' : ''}
                onClick={() => setFilter('ALL')}
                aria-pressed={filter === 'ALL'}
              >
                All
                <span className="filter-count">{notifications.length}</span>
              </button>

              <button
                type="button"
                className={filter === 'UNREAD' ? 'active' : ''}
                onClick={() => setFilter('UNREAD')}
                aria-pressed={filter === 'UNREAD'}
              >
                Unread
                {unreadCount > 0 && (
                  <span className="filter-count">{unreadCount}</span>
                )}
              </button>

              <button
                type="button"
                className={filter === 'READ' ? 'active' : ''}
                onClick={() => setFilter('READ')}
                aria-pressed={filter === 'READ'}
              >
                Read
              </button>
            </div>

            {(filter !== 'ALL' || searchTerm) && (
              <button
                type="button"
                className="notification-clear-button"
                onClick={clearFilters}
              >
                Clear filters
              </button>
            )}
          </div>
        </section>
      )}

      {/* NOTIFICATION CONTENT */}
      {notifications.length === 0 ? (
        <section className="notifications-empty-card">
          <div className="notification-empty-illustration">🎉</div>
          <span className="notifications-eyebrow">INBOX ZERO</span>
          <h2>You're all caught up!</h2>
          <p>
            You don't have any notifications yet. Your achievements and
            important QuestForge updates will appear here.
          </p>
          <Link to="/" className="notification-primary-button">
            Explore QuestForge
            <span>→</span>
          </Link>
        </section>
      ) : filteredNotifications.length === 0 ? (
        <section className="notifications-empty-card">
          <div className="notification-empty-illustration">⌕</div>
          <span className="notifications-eyebrow">NO RESULTS</span>
          <h2>No matching notifications</h2>
          <p>
            We couldn't find anything matching your current search or filter.
            Try adjusting your search.
          </p>

          <button
            type="button"
            className="notification-secondary-button"
            onClick={clearFilters}
          >
            Reset Filters
          </button>
        </section>
      ) : (
        <section className="notification-list" aria-label="Notification list">
          {filteredNotifications.map((notification) => {
            const isBusy = busyNotificationIds.includes(notification.id);

            return (
              <article
                key={notification.id}
                className={`notification-item ${
                  notification.read
                    ? 'notification-read'
                    : 'notification-unread'
                } ${isBusy ? 'notification-is-busy' : ''}`}
              >
                <div
                  className={`notification-icon ${
                    notification.read ? '' : 'notification-icon-unread'
                  }`}
                  aria-hidden="true"
                >
                  {getNotificationIcon(notification.type)}
                </div>

                <div className="notification-content">
                  <div className="notification-meta">
                    <span className="notification-type">
                      {getNotificationType(notification.type)}
                    </span>

                    {!notification.read && (
                      <span className="notification-new-badge">NEW</span>
                    )}

                    {notification.read && (
                      <span className="notification-read-label">
                        ✓ Read
                      </span>
                    )}
                  </div>

                  <h3>
                    {notification.title || 'QuestForge Notification'}
                  </h3>

                  <p>
                    {notification.message ||
                      'You have a new QuestForge update.'}
                  </p>

                  <div className="notification-time">
                    <span aria-hidden="true">◷</span>
                    <time
                      dateTime={
                        notification.createdAt
                          ? new Date(notification.createdAt).toISOString()
                          : undefined
                      }
                    >
                      {formatNotificationDate(notification.createdAt)}
                    </time>
                  </div>
                </div>

                <div className="notification-actions">
                  {!notification.read && (
                    <button
                      type="button"
                      className="notification-action-button mark-read-button"
                      onClick={() => markAsRead(notification.id)}
                      disabled={isBusy || markingAll}
                      title="Mark as read"
                      aria-label="Mark notification as read"
                    >
                      {isBusy ? '…' : '✓'}
                    </button>
                  )}

                  <button
                    type="button"
                    className="notification-action-button delete-button"
                    onClick={() => deleteNotification(notification.id)}
                    disabled={isBusy || markingAll}
                    title="Delete notification"
                    aria-label="Delete notification"
                  >
                    {isBusy ? '…' : '×'}
                  </button>
                </div>
              </article>
            );
          })}
        </section>
      )}

      {/* FOOTER */}
      {notifications.length > 0 && (
        <footer className="notifications-footer">
          <span className="notifications-footer-icon">✦</span>
          <p>
            You're viewing your QuestForge notifications.
            <span> Keep building, keep growing.</span>
          </p>
        </footer>
      )}
    </main>
  );
}

export default Notifications;