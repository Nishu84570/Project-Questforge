import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import '../styles/AdminNotificationLink.css';

function AdminNotificationLink() {
  const location = useLocation();
  const isActive = location.pathname === '/admin/notifications';

  return (
    <Link
      to="/admin/notifications"
      className={`admin-notification-link ${isActive ? 'active' : ''}`}
    >
      <span className="link-icon-wrapper">
        <span className="notification-icon">📢</span>
        <span className="pulse-glow"></span>
      </span>
      <span className="link-text">Notification Center</span>
    </Link>
  );
}

export default AdminNotificationLink;