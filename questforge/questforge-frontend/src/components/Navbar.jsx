import { useEffect, useRef, useState } from 'react';
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

  const [notificationCount, setNotificationCount] =
    useState(0);

  const [menuOpen, setMenuOpen] = useState(false);

  /*
  ============================================================
  NOTIFICATION COUNT
  ============================================================
  */

  const fetchNotificationCount = async () => {
    if (!isLoggedIn()) {
      setNotificationCount(0);
      return;
    }

    try {
      const {
        response,
        data,
      } = await apiRequest(
        '/notifications/unread/count',
        {
          method: 'GET',
        }
      );

      if (!response.ok) {
        return;
      }

      setNotificationCount(
        Number(data?.count || 0)
      );
    } catch (error) {
      console.error(
        'Notification count error:',
        error
      );
    }
  };

  /*
  ============================================================
  AUTH + NOTIFICATION EVENTS
  ============================================================
  */

  useEffect(() => {
    const handleAuthChange = () => {
      setLoggedIn(isLoggedIn());
      setUser(getUser());
      fetchNotificationCount();
    };

    window.addEventListener(
      'questforge-auth-change',
      handleAuthChange
    );

    window.addEventListener(
      'questforge-notification-read',
      fetchNotificationCount
    );

    fetchNotificationCount();

    return () => {
      window.removeEventListener(
        'questforge-auth-change',
        handleAuthChange
      );

      window.removeEventListener(
        'questforge-notification-read',
        fetchNotificationCount
      );
    };
  }, []);

  /*
  ============================================================
  CLOSE MENU WHEN CLICKING OUTSIDE
  ============================================================
  */

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        menuRef.current &&
        !menuRef.current.contains(event.target)
      ) {
        setMenuOpen(false);
      }
    };

    document.addEventListener(
      'mousedown',
      handleClickOutside
    );

    return () => {
      document.removeEventListener(
        'mousedown',
        handleClickOutside
      );
    };
  }, []);

  /*
  ============================================================
  CLOSE MENU AFTER ROUTE CHANGE
  ============================================================
  */

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  /*
  ============================================================
  LOGOUT
  ============================================================
  */

  const handleLogout = () => {
    logout();

    setLoggedIn(false);
    setUser(null);
    setNotificationCount(0);
    setMenuOpen(false);

    window.dispatchEvent(
      new Event('questforge-auth-change')
    );

    navigate('/login', {
      replace: true,
    });
  };

  /*
  ============================================================
  NAV LINK CLASS
  ============================================================
  */

  const navLinkClass = ({ isActive }) =>
    `navbar-menu-link ${
      isActive ? 'active' : ''
    }`;

  return (
    <nav className="navbar">

      {/* =====================================================
          LEFT — LOGO
      ===================================================== */}

      <Link
        to="/"
        className="navbar-logo"
        aria-label="QuestForge Dashboard"
      >
        <span className="navbar-logo-icon">
          ⚡
        </span>

        <span className="navbar-logo-text">
          QuestForge
        </span>
      </Link>


      {/* =====================================================
          CENTER — BRAND NAME
      ===================================================== */}

      <Link
        to="/"
        className="navbar-center-brand"
      >
        QuestForge
      </Link>


      {/* =====================================================
          RIGHT SIDE
      ===================================================== */}

      <div className="navbar-actions">

        {/* Theme */}
        <div className="navbar-theme">
          <ThemeToggle />
        </div>


        {/* =================================================
            USER MENU
        ================================================= */}

        {loggedIn ? (
          <div
            className="navbar-user-menu"
            ref={menuRef}
          >

            <button
              type="button"
              className={`navbar-user-button ${
                menuOpen ? 'open' : ''
              }`}
              onClick={() =>
                setMenuOpen(
                  (previous) => !previous
                )
              }
              aria-label="Open user menu"
              aria-expanded={menuOpen}
            >

              <span className="navbar-avatar">
                {(
                  user?.name ||
                  user?.email ||
                  'U'
                )
                  .charAt(0)
                  .toUpperCase()}
              </span>

              <span className="navbar-user-chevron">
                {menuOpen ? '⌃' : '⌄'}
              </span>

            </button>


            {/* =================================================
                DROPDOWN
            ================================================= */}

            {menuOpen && (
              <div className="navbar-dropdown">

                {/* User information */}

                <div className="navbar-dropdown-user">

                  <div className="navbar-dropdown-avatar">
                    {(
                      user?.name ||
                      user?.email ||
                      'U'
                    )
                      .charAt(0)
                      .toUpperCase()}
                  </div>

                  <div className="navbar-dropdown-user-info">

                    <strong>
                      {user?.name ||
                        'User'}
                    </strong>

                    <span>
                      {user?.email ||
                        ''}
                    </span>

                  </div>

                </div>


                <div className="navbar-dropdown-divider" />


                {/* Navigation */}

                <div className="navbar-dropdown-links">

                  <NavLink
                    to="/challenges"
                    className={navLinkClass}
                  >
                    <span className="navbar-menu-icon">
                      ⚔️
                    </span>

                    <span>
                      Challenges
                    </span>
                  </NavLink>


                  <NavLink
                    to="/submissions"
                    className={navLinkClass}
                  >
                    <span className="navbar-menu-icon">
                      📄
                    </span>

                    <span>
                      My Submissions
                    </span>
                  </NavLink>


                  <NavLink
                    to="/leaderboard"
                    className={navLinkClass}
                  >
                    <span className="navbar-menu-icon">
                      🏆
                    </span>

                    <span>
                      Leaderboard
                    </span>
                  </NavLink>


                  <NavLink
                    to="/notifications"
                    className={navLinkClass}
                  >
                    <span className="navbar-menu-icon">
                      🔔
                    </span>

                    <span className="navbar-notification-text">
                      Notifications

                      {notificationCount > 0 && (
                        <span className="navbar-notification-badge">
                          {notificationCount > 99
                            ? '99+'
                            : notificationCount}
                        </span>
                      )}
                    </span>
                  </NavLink>


                  <NavLink
                    to="/profile"
                    className={navLinkClass}
                  >
                    <span className="navbar-menu-icon">
                      👤
                    </span>

                    <span>
                      Profile
                    </span>
                  </NavLink>


                  {/* Admin */}

                  {user?.role === 'ADMIN' && (
                    <NavLink
                      to="/admin"
                      className={navLinkClass}
                    >
                      <span className="navbar-menu-icon">
                        🛡️
                      </span>

                      <span>
                        Admin Dashboard
                      </span>
                    </NavLink>
                  )}

                </div>


                <div className="navbar-dropdown-divider" />


                {/* Logout */}

                <button
                  type="button"
                  className="navbar-logout"
                  onClick={handleLogout}
                >
                  <span className="navbar-menu-icon">
                    ↪
                  </span>

                  <span>
                    Logout
                  </span>
                </button>

              </div>
            )}

          </div>
        ) : (

          /* =================================================
             NOT LOGGED IN
          ================================================= */

          <Link
            to="/login"
            className="navbar-login-button"
          >
            Login
          </Link>

        )}

      </div>

    </nav>
  );
}

export default Navbar;