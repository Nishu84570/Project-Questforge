
import React, { useEffect, useState } from 'react';
import '../styles/ThemeToggle.css';

const THEME_KEY = 'questforge-theme';

const THEMES = [
  {
    value: 'light',
    label: 'Light',
    icon: 'sun',
    description: 'Light appearance'
  },
  {
    value: 'dark',
    label: 'Dark',
    icon: 'moon',
    description: 'Dark appearance'
  },
  {
    value: 'auto',
    label: 'System',
    icon: 'monitor',
    description: 'Follow system preference'
  }
];

function getSystemTheme() {
  if (
    typeof window === 'undefined' ||
    typeof window.matchMedia !== 'function'
  ) {
    return 'light';
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? 'dark'
    : 'light';
}

function getSavedTheme() {
  try {
    if (typeof window === 'undefined') return 'auto';

    const savedTheme = window.localStorage.getItem(THEME_KEY);

    return THEMES.some((theme) => theme.value === savedTheme)
      ? savedTheme
      : 'auto';
  } catch {
    return 'auto';
  }
}

function applyTheme(theme) {
  if (typeof document === 'undefined') return;

  const actualTheme = theme === 'auto'
    ? getSystemTheme()
    : theme;

  document.documentElement.setAttribute('data-theme', actualTheme);
  document.documentElement.style.colorScheme = actualTheme;
}

function ThemeIcon({ type }) {
  const commonProps = {
    width: 17,
    height: 17,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    'aria-hidden': true
  };

  switch (type) {
    case 'sun':
      return (
        <svg {...commonProps}>
          <circle cx="12" cy="12" r="4" />
          <path d="M12 2v2" />
          <path d="M12 20v2" />
          <path d="m4.93 4.93 1.42 1.42" />
          <path d="m17.65 17.65 1.42 1.42" />
          <path d="M2 12h2" />
          <path d="M20 12h2" />
          <path d="m6.35 17.65-1.42 1.42" />
          <path d="m19.07 4.93-1.42 1.42" />
        </svg>
      );

    case 'moon':
      return (
        <svg {...commonProps}>
          <path d="M20.9 13A9 9 0 0 1 11 3.1 9 9 0 1 0 20.9 13Z" />
        </svg>
      );

    case 'monitor':
      return (
        <svg {...commonProps}>
          <rect x="3" y="4" width="18" height="13" rx="2" />
          <path d="M8 21h8" />
          <path d="M12 17v4" />
        </svg>
      );

    default:
      return null;
  }
}

function ThemeToggle() {
  const [theme, setTheme] = useState(getSavedTheme);

  useEffect(() => {
    applyTheme(theme);

    try {
      window.localStorage.setItem(THEME_KEY, theme);
    } catch {
      // Theme switching still works if storage is unavailable.
    }

    const mediaQuery = window.matchMedia?.(
      '(prefers-color-scheme: dark)'
    );

    const handleSystemThemeChange = () => {
      if (theme === 'auto') {
        applyTheme('auto');
      }
    };

    const handleStorageChange = (event) => {
      if (event.key !== THEME_KEY) return;

      const nextTheme = THEMES.some(
        (item) => item.value === event.newValue
      )
        ? event.newValue
        : 'auto';

      setTheme(nextTheme);
    };

    if (mediaQuery?.addEventListener) {
      mediaQuery.addEventListener('change', handleSystemThemeChange);
    } else {
      mediaQuery?.addListener?.(handleSystemThemeChange);
    }

    window.addEventListener('storage', handleStorageChange);

    return () => {
      if (mediaQuery?.removeEventListener) {
        mediaQuery.removeEventListener(
          'change',
          handleSystemThemeChange
        );
      } else {
        mediaQuery?.removeListener?.(handleSystemThemeChange);
      }

      window.removeEventListener('storage', handleStorageChange);
    };
  }, [theme]);

  const handleThemeChange = (newTheme) => {
    if (newTheme === theme) return;
    setTheme(newTheme);
  };

  return (
    <div className="theme-toggle">
      <div
        className="theme-toggle-options"
        role="group"
        aria-label="Choose appearance"
      >
        {THEMES.map((item) => {
          const isActive = theme === item.value;

          return (
            <button
              key={item.value}
              type="button"
              className={`theme-toggle-btn ${
                isActive ? 'active' : ''
              }`}
              onClick={() => handleThemeChange(item.value)}
              aria-pressed={isActive}
              aria-label={item.description}
              title={item.description}
            >
              <ThemeIcon type={item.icon} />

              <span>{item.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}

export default ThemeToggle;
