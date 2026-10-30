import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { apiRequest } from '../services/api';
import { setAuth } from '../utils/auth';

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (event) => {
    event.preventDefault();

    setError('');
    setLoading(true);

    try {
      const { response, data } = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify({
          email: email.trim(),
          password,
        }),
      });

      if (!response.ok) {
        setError(
          data?.message ||
            data?.error ||
            'Invalid email or password.'
        );

        return;
      }

      if (!data?.token) {
        setError(
          'Login succeeded, but no authentication token was returned.'
        );

        return;
      }

      setAuth(data);

      window.dispatchEvent(
        new Event('questforge-auth-change')
      );

      if (data.role === 'ADMIN') {
        navigate('/admin', {
          replace: true,
        });
      } else {
        navigate('/challenges', {
          replace: true,
        });
      }
    } catch (err) {
      console.error('Login error:', err);

      setError(
        'Unable to connect to QuestForge backend.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">

      <div className="auth-card">

        {/* BRAND */}

        <p className="auth-brand">
          QUESTFORGE
        </p>


        {/* HEADING */}

        <h1>
          Welcome Back 👋
        </h1>

        <p className="auth-subtitle">
          Login to continue your quest.
        </p>


        {/* LOGIN FORM */}

        <form
          onSubmit={handleLogin}
          className="auth-form"
        >

          {/* EMAIL */}

          <label htmlFor="login-email">
            Email
          </label>

          <input
            id="login-email"
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(event) => {
              setEmail(event.target.value);
              setError('');
            }}
            autoComplete="email"
            required
            disabled={loading}
          />


          {/* PASSWORD */}

          <label htmlFor="login-password">
            Password
          </label>

          <input
            id="login-password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
              setError('');
            }}
            autoComplete="current-password"
            required
            disabled={loading}
          />


          {/* ERROR */}

          {error && (
            <div className="auth-error">
              <p>{error}</p>
            </div>
          )}


          {/* LOGIN BUTTON */}

          <button
            type="submit"
            className="primary-button auth-button"
            disabled={loading}
          >
            {loading
              ? 'Logging in...'
              : 'Login'}
          </button>

        </form>


        {/* REGISTER LINK */}

        <p className="auth-footer">
          Don't have an account?{' '}

          <Link to="/register">
            Create one
          </Link>
        </p>

      </div>

    </div>
  );
}

export default Login;