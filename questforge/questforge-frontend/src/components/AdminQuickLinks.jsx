import { Link } from 'react-router-dom';

function AdminQuickLinks() {
  return (
    <div className="admin-section">

      <div className="admin-section-header">

        <div>

          <p className="eyebrow">
            QUESTFORGE ADMIN TOOLS
          </p>

          <h2>
            Administration Center 🛠️
          </h2>

          <p>
            Manage users, submissions,
            challenges, and analytics.
          </p>

        </div>

      </div>

      <div
        style={{
          display: 'flex',
          gap: '12px',
          flexWrap: 'wrap',
        }}
      >

        <Link
          className="primary-button"
          to="/admin/dashboard-overview"
        >
          📊 Dashboard Overview
        </Link>

        <Link
          className="primary-button"
          to="/admin/users"
        >
          👥 User Management
        </Link>

        <Link
          className="primary-button"
          to="/admin/submissions"
        >
          📝 Submission Review
        </Link>

        <Link
          className="primary-button"
          to="/admin/challenge-overview"
        >
          ⚔️ Challenge Overview
        </Link>

        <Link
          className="primary-button"
          to="/admin/challenge-analytics"
        >
          📈 Challenge Analytics
        </Link>

        <Link
          className="secondary-button"
          to="/admin/notifications"
        >
          📢 Notification Center
        </Link>

      </div>

    </div>
  );
}

export default AdminQuickLinks;