import { Link } from 'react-router-dom';

function AdminNotificationLink() {
  return (
    <Link
      to="/admin/notifications"
      className="admin-notification-link"
    >
      📢 Notification Center
    </Link>
  );
}

export default AdminNotificationLink;