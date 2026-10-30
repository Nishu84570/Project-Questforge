import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function NotificationBell() {
  const [unreadCount, setUnreadCount] = useState(0);

  const loadUnreadCount = useCallback(async () => {
    try {
      const { response, data } = await apiRequest(
        '/notifications/unread/count',
        {
          method: 'GET',
        }
      );

      if (!response.ok) {
        return;
      }

      const count =
        typeof data === 'number'
          ? data
          : Number(
              data?.count ??
              data?.unreadCount ??
              0
            );

      setUnreadCount(
        Number.isFinite(count) && count > 0
          ? count
          : 0
      );
    } catch (error) {
      console.error(
        'Notification count error:',
        error
      );
    }
  }, []);

  useEffect(() => {
    loadUnreadCount();

    const handleNotificationChange = () => {
      loadUnreadCount();
    };

    window.addEventListener(
      'questforge-notification-change',
      handleNotificationChange
    );

    const interval = setInterval(
      loadUnreadCount,
      30000
    );

    return () => {
      window.removeEventListener(
        'questforge-notification-change',
        handleNotificationChange
      );

      clearInterval(interval);
    };
  }, [loadUnreadCount]);

  return (
    <Link
      to="/notifications"
      className="notification-bell"
      aria-label={
        unreadCount > 0
          ? `${unreadCount} unread notifications`
          : 'Notifications'
      }
      title="Notifications"
    >
      <span aria-hidden="true">
        🔔
      </span>

      {unreadCount > 0 && (
        <span className="notification-badge">
          {unreadCount > 99
            ? '99+'
            : unreadCount}
        </span>
      )}
    </Link>
  );
}

export default NotificationBell;