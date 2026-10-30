import { useState } from 'react';
import {
  Link,
  useNavigate,
} from 'react-router-dom';

import { apiRequest } from '../services/api';

function Register() {
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] =
    useState('');

  const [error, setError] = useState('');
  const [success, setSuccess] =
    useState('');
  const [loading, setLoading] =
    useState(false);

  const handleRegister = async (event) => {
    event.preventDefault();

    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const {
        response,
        data,
      } = await apiRequest('/users', {
        method: 'POST',
        body: JSON.stringify({
          name: name.trim(),
          email: email.trim(),
          password,
        }),
      });

      if (!response.ok) {
        setError(
          data?.message ||
            data?.error ||
            'Registration failed.'
        );

        return;
      }

      setSuccess(
        'Account created successfully! Redirecting to login...'
      );

      setName('');
      setEmail('');
      setPassword('');

      setTimeout(() => {
        navigate('/login', {
          replace: true,
        });
      }, 1200);

    } catch (err) {
      console.error(
        'Registration error:',
        err
      );

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
          Create Account 🚀
        </h1>

        <p className="auth-subtitle">
          Join QuestForge and start your journey.
        </p>


        {/* REGISTER FORM */}

        <form
          onSubmit={handleRegister}
          className="auth-form"
        >

          {/* NAME */}

          <label htmlFor="register-name">
            Name
          </label>

          <input
            id="register-name"
            type="text"
            placeholder="Enter your name"
            value={name}
            onChange={(event) => {
              setName(event.target.value);
              setError('');
            }}
            autoComplete="name"
            required
            disabled={loading}
          />


          {/* EMAIL */}

          <label htmlFor="register-email">
            Email
          </label>

          <input
            id="register-email"
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

          <label htmlFor="register-password">
            Password
          </label>

          <input
            id="register-password"
            type="password"
            placeholder="Create a password"
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
              setError('');
            }}
            autoComplete="new-password"
            required
            minLength={6}
            disabled={loading}
          />


          {/* ERROR */}

          {error && (
            <div className="auth-error">
              <p>{error}</p>
            </div>
          )}


          {/* SUCCESS */}

          {success && (
            <div className="auth-success">
              <p>{success}</p>
            </div>
          )}


          {/* REGISTER BUTTON */}

          <button
            type="submit"
            className="primary-button auth-button"
            disabled={loading}
          >
            {loading
              ? 'Creating account...'
              : 'Create Account'}
          </button>

        </form>


        {/* LOGIN LINK */}

        <p className="auth-footer">
          Already have an account?{' '}

          <Link to="/login">
            Login
          </Link>
        </p>

      </div>

    </div>
  );
}

export default Register;