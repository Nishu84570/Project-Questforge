import React, { useEffect, useRef, useState } from 'react';
import '../styles/Navbar.css';
import {
  Link,
  NavLink,
  useLocation,
  useNavigate,
} from 'react-router-dom';

import {
  getUser,
  isLoggedIn,
  logout,
} from '../utils/auth';

import { apiRequest } from '../services/api';
import ThemeToggle from './ThemeToggle';

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const menuRef = useRef(null);

  const [loggedIn, setLoggedIn] = useState(isLoggedIn());
  const [user, setUser] = useState(getUser());
  const [notificationCount, setNotificationCount] = useState(0);
  const [menuOpen, setMenuOpen] = useState(false);

  const fetchNotificationCount = async () => {
    if (!isLoggedIn()) {
      setNotificationCount(0);
      return;
    }

    try {
      const { response, data } = await apiRequest(
        '/notifications/unread/count',
        { method: 'GET' }
      );

      if (!response.ok) return;

      setNotificationCount(Number(data?.count || 0));
    } catch (error) {
      console.error('Notification count error:', error);
    }
  };

  useEffect(() => {
    const handleAuthChange = () => {
      setLoggedIn(isLoggedIn());
      setUser(getUser());
      fetchNotificationCount();
    };

    window.addEventListener('questforge-auth-change', handleAuthChange);
    window.addEventListener('questforge-notification-read', fetchNotificationCount);

    fetchNotificationCount();

    return () => {
      window.removeEventListener('questforge-auth-change', handleAuthChange);
      window.removeEventListener('questforge-notification-read', fetchNotificationCount);
    };
  }, []);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logout();
    setLoggedIn(false);
    setUser(null);
    setNotificationCount(0);
    setMenuOpen(false);

    window.dispatchEvent(new Event('questforge-auth-change'));
    navigate('/login', { replace: true });
  };

  const navLinkClass = ({ isActive }) =>
    `navbar-menu-link ${isActive ? 'active' : ''}`;

  const userInitial = (user?.name || user?.email || 'U').charAt(0).toUpperCase();

  return (
    <nav className="navbar">
      {/* LEFT — LOGO */}
      <Link to="/" className="navbar-logo" aria-label="QuestForge Dashboard">
        <span className="navbar-logo-icon">⚡</span>
        <span className="navbar-logo-text">QuestForge</span>
      </Link>

      {/* CENTER — BRAND NAME */}
      <Link to="/" className="navbar-center-brand">
        QuestForge
      </Link>

      {/* RIGHT SIDE */}
      <div className="navbar-actions">
        {/* Theme Toggle */}
        <div className="navbar-theme">
          <ThemeToggle />
        </div>

        {/* USER MENU / LOGIN */}
        {loggedIn ? (
          <div className="navbar-user-menu" ref={menuRef}>
            <button
              type="button"
              className={`navbar-user-button ${menuOpen ? 'open' : ''}`}
              onClick={() => setMenuOpen((prev) => !prev)}
              aria-label="Open user menu"
              aria-expanded={menuOpen}
            >
              <span className="navbar-avatar">{userInitial}</span>
              <span className="navbar-user-chevron">{menuOpen ? '⌃' : '⌄'}</span>
            </button>

            {/* DROPDOWN MENU */}
            {menuOpen && (
              <div className="navbar-dropdown">
                {/* User Info Header */}
                <div className="navbar-dropdown-user">
                  <div className="navbar-dropdown-avatar">{userInitial}</div>
                  <div className="navbar-dropdown-user-info">
                    <strong>{user?.name || 'User'}</strong>
                    <span>{user?.email || ''}</span>
                  </div>
                </div>

                <div className="navbar-dropdown-divider" />

                {/* Navigation Links */}
                <div className="navbar-dropdown-links">
                  <NavLink to="/challenges" className={navLinkClass}>
                    <span className="navbar-menu-icon">⚔️</span>
                    <span>Challenges</span>
                  </NavLink>

                  {/* STREAK & CALENDAR OPTION */}
                  <NavLink to="/streak-calendar" className={navLinkClass}>
                    <span className="navbar-menu-icon">🔥</span>
                    <span>Streak & Calendar</span>
                  </NavLink>

                  <NavLink to="/submissions" className={navLinkClass}>
                    <span className="navbar-menu-icon">📄</span>
                    <span>My Submissions</span>
                  </NavLink>

                  <NavLink to="/leaderboard" className={navLinkClass}>
                    <span className="navbar-menu-icon">🏆</span>
                    <span>Leaderboard</span>
                  </NavLink>

                  <NavLink to="/notifications" className={navLinkClass}>
                    <span className="navbar-menu-icon">🔔</span>
                    <span className="navbar-notification-text">
                      Notifications
                      {notificationCount > 0 && (
                        <span className="navbar-notification-badge">
                          {notificationCount > 99 ? '99+' : notificationCount}
                        </span>
                      )}
                    </span>
                  </NavLink>

                  <NavLink to="/profile" className={navLinkClass}>
                    <span className="navbar-menu-icon">👤</span>
                    <span>Profile</span>
                  </NavLink>

                  {user?.role === 'ADMIN' && (
                    <NavLink to="/admin" className={navLinkClass}>
                      <span className="navbar-menu-icon">🛡️</span>
                      <span>Admin Dashboard</span>
                    </NavLink>
                  )}
                </div>

                <div className="navbar-dropdown-divider" />

                {/* Logout Button */}
                <button
                  type="button"
                  className="navbar-logout"
                  onClick={handleLogout}
                >
                  <span className="navbar-menu-icon">↪</span>
                  <span>Logout</span>
                </button>
              </div>
            )}
          </div>
        ) : (
          <Link to="/login" className="navbar-login-button">
            Login
          </Link>
        )}
      </div>
    </nav>
  );
}

export default Navbar;