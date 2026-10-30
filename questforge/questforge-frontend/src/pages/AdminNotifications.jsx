import { useState } from 'react';
import { Link } from 'react-router-dom';

import { apiRequest } from '../services/api';

function AdminNotifications() {
  const [form, setForm] = useState({
    title: '',
    message: '',
    type: 'INFO',
  });

  const [sending, setSending] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  function handleChange(event) {
    const {
      name,
      value,
    } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setSuccess('');
    setError('');
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setSuccess('');
    setError('');

    if (!form.title.trim()) {
      setError(
        'Please enter a notification title.'
      );
      return;
    }

    if (!form.message.trim()) {
      setError(
        'Please enter a notification message.'
      );
      return;
    }

    setSending(true);

    try {
      const {
        response,
        data,
      } = await apiRequest(
        '/notifications/broadcast',
        {
          method: 'POST',
          body: JSON.stringify({
            title: form.title.trim(),
            message: form.message.trim(),
            type: form.type,
          }),
        }
      );

      if (!response.ok) {
        throw new Error(
          data?.message ||
          'Failed to broadcast notification.'
        );
      }

      setSuccess(
        'Notification broadcast successfully! 📢'
      );

      setForm({
        title: '',
        message: '',
        type: 'INFO',
      });

      window.dispatchEvent(
        new Event(
          'questforge-notification-change'
        )
      );

    } catch (err) {
      console.error(
        'Notification broadcast error:',
        err
      );

      setError(
        err.message ||
        'Unable to broadcast notification.'
      );
    } finally {
      setSending(false);
    }
  }

  function handleClear() {
    setForm({
      title: '',
      message: '',
      type: 'INFO',
    });

    setSuccess('');
    setError('');
  }

  return (
    <div className="page admin-notifications-page">

      {/* =========================
          HEADER
      ========================= */}

      <div className="page-header">

        <div>

          <span className="page-kicker">
            ADMIN • COMMUNICATION
          </span>

          <h1>
            Notification Center
          </h1>

          <p>
            Send an announcement to QuestForge users.
          </p>

        </div>

        <div className="page-header-actions">

          <Link
            to="/admin"
            className="secondary-button"
          >
            ← Back to Admin
          </Link>

          <Link
            to="/notifications"
            className="secondary-button"
          >
            View Notifications
          </Link>

        </div>

      </div>


      {/* =========================
          MAIN CARD
      ========================= */}

      <section className="admin-notification-panel">

        <div className="admin-notification-panel-header">

          <div>

            <span className="admin-panel-icon">
              📢
            </span>

            <div>

              <h2>
                Broadcast Notification
              </h2>

              <p>
                Create a notification that will be
                delivered to users.
              </p>

            </div>

          </div>

        </div>


        {/* =========================
            SUCCESS
        ========================= */}

        {success && (
          <div className="success-message">
            {success}
          </div>
        )}


        {/* =========================
            ERROR
        ========================= */}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}


        {/* =========================
            FORM
        ========================= */}

        <form
          className="admin-notification-form"
          onSubmit={handleSubmit}
        >

          <div className="form-group">

            <label htmlFor="notification-title">
              Notification Title
            </label>

            <input
              id="notification-title"
              name="title"
              type="text"
              value={form.title}
              onChange={handleChange}
              placeholder="Example: New Challenge Available!"
              maxLength={200}
              disabled={sending}
            />

            <small>
              {form.title.length}/200 characters
            </small>

          </div>


          <div className="form-group">

            <label htmlFor="notification-message">
              Message
            </label>

            <textarea
              id="notification-message"
              name="message"
              value={form.message}
              onChange={handleChange}
              placeholder="Write your announcement here..."
              rows={7}
              maxLength={2000}
              disabled={sending}
            />

            <small>
              {form.message.length}/2000 characters
            </small>

          </div>


          <div className="form-group">

            <label htmlFor="notification-type">
              Notification Type
            </label>

            <select
              id="notification-type"
              name="type"
              value={form.type}
              onChange={handleChange}
              disabled={sending}
            >

              <option value="INFO">
                INFO
              </option>

              <option value="SUCCESS">
                SUCCESS
              </option>

              <option value="WARNING">
                WARNING
              </option>

              <option value="ERROR">
                ERROR
              </option>

            </select>

          </div>


          {/* =========================
              PREVIEW
          ========================= */}

          <div className="notification-preview">

            <div className="notification-preview-header">

              <span>
                Preview
              </span>

              <span
                className={`notification-type-preview notification-type-${form.type.toLowerCase()}`}
              >
                {form.type}
              </span>

            </div>

            <div className="notification-preview-body">

              <h3>
                {form.title.trim() ||
                  'Notification title'}
              </h3>

              <p>
                {form.message.trim() ||
                  'Your notification message will appear here.'}
              </p>

            </div>

          </div>


          {/* =========================
              ACTIONS
          ========================= */}

          <div className="form-actions">

            <button
              type="button"
              className="secondary-button"
              onClick={handleClear}
              disabled={sending}
            >
              Clear
            </button>

            <button
              type="submit"
              className="primary-button"
              disabled={sending}
            >
              {sending
                ? 'Sending...'
                : '📢 Broadcast Notification'}
            </button>

          </div>

        </form>

      </section>

    </div>
  );
}

export default AdminNotifications;