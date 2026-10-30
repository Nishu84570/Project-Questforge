import { useEffect, useState } from 'react';

const THEME_KEY = 'questforge-theme';

function getSystemTheme() {
  if (
    typeof window === 'undefined' ||
    !window.matchMedia
  ) {
    return 'light';
  }

  return window.matchMedia(
    '(prefers-color-scheme: dark)'
  ).matches
    ? 'dark'
    : 'light';
}

function applyTheme(theme) {
  const actualTheme =
    theme === 'auto'
      ? getSystemTheme()
      : theme;

  document.documentElement.setAttribute(
    'data-theme',
    actualTheme
  );
}

function ThemeToggle() {
  const [theme, setTheme] = useState(
    () =>
      localStorage.getItem(THEME_KEY) ||
      'auto'
  );

  useEffect(() => {
    applyTheme(theme);

    if (!window.matchMedia) {
      return undefined;
    }

    const mediaQuery = window.matchMedia(
      '(prefers-color-scheme: dark)'
    );

    const handleSystemThemeChange = () => {
      if (theme === 'auto') {
        applyTheme('auto');
      }
    };

    if (
      mediaQuery.addEventListener
    ) {
      mediaQuery.addEventListener(
        'change',
        handleSystemThemeChange
      );
    } else {
      mediaQuery.addListener(
        handleSystemThemeChange
      );
    }

    return () => {
      if (
        mediaQuery.removeEventListener
      ) {
        mediaQuery.removeEventListener(
          'change',
          handleSystemThemeChange
        );
      } else {
        mediaQuery.removeListener(
          handleSystemThemeChange
        );
      }
    };
  }, [theme]);

  const handleThemeChange = (event) => {
    const newTheme =
      event.target.value;

    setTheme(newTheme);

    localStorage.setItem(
      THEME_KEY,
      newTheme
    );

    applyTheme(newTheme);
  };

  return (
    <select
      value={theme}
      onChange={handleThemeChange}
      className="theme-select"
      aria-label="Choose theme"
    >
      <option value="light">
        ☀ Light
      </option>

      <option value="dark">
        🌙 Dark
      </option>

      <option value="auto">
        🖥 Auto
      </option>
    </select>
  );
}

export default ThemeToggle;