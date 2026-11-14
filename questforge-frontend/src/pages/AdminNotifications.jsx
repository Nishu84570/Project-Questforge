
import React, { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import '../styles/AdminNotifications.css';

const INITIAL_FORM = {
  title: '',
  message: '',
  type: 'INFO',
};

const NOTIFICATION_TYPES = [
  {
    value: 'INFO',
    label: 'Information',
    icon: 'ℹ️',
    description: 'General announcements and platform updates.',
  },
  {
    value: 'SUCCESS',
    label: 'Success',
    icon: '✅',
    description: 'Achievements, milestones and positive updates.',
  },
  {
    value: 'WARNING',
    label: 'Warning',
    icon: '⚠️',
    description: 'Important reminders and upcoming changes.',
  },
  {
    value: 'ERROR',
    label: 'Important Alert',
    icon: '🚨',
    description: 'Critical platform notices and urgent information.',
  },
];

function AdminNotifications() {
  const [form, setForm] = useState(INITIAL_FORM);
  const [sending, setSending] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const selectedType = useMemo(
    () =>
      NOTIFICATION_TYPES.find((item) => item.value === form.type) ||
      NOTIFICATION_TYPES[0],
    [form.type]
  );

  const titleLength = form.title.length;
  const messageLength = form.message.length;

  const isFormValid = Boolean(
    form.title.trim() && form.message.trim()
  );

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setSuccess('');
    setError('');
  }

  function handleClear() {
    if (sending) return;

    setForm(INITIAL_FORM);
    setSuccess('');
    setError('');
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (sending) return;

    setSuccess('');
    setError('');

    const title = form.title.trim();
    const message = form.message.trim();

    if (!title) {
      setError('Please enter a notification title.');
      return;
    }

    if (!message) {
      setError('Please enter a notification message.');
      return;
    }

    if (title.length > 200 || message.length > 2000) {
      setError('Your notification exceeds the allowed character limit.');
      return;
    }

    setSending(true);

    try {
      const { response, data } = await apiRequest(
        '/notifications/broadcast',
        {
          method: 'POST',
          body: JSON.stringify({
            title,
            message,
            type: form.type,
          }),
        }
      );

      if (!response.ok) {
        throw new Error(
          data?.message || 'Failed to broadcast notification.'
        );
      }

      setSuccess('Notification broadcast successfully!');

      setForm(INITIAL_FORM);

      window.dispatchEvent(
        new Event('questforge-notification-change')
      );
    } catch (err) {
      console.error('Notification broadcast error:', err);

      setError(
        err.message || 'Unable to broadcast notification.'
      );
    } finally {
      setSending(false);
    }
  }

  return (
    <main className="page qnc-page">
      <div className="qnc-shell">
        {/* PAGE HEADER */}
        <header className="qnc-header">
          <div className="qnc-header-main">
            <div className="qnc-breadcrumb">
              <span />
              QUESTFORGE / ADMINISTRATION / COMMUNICATION
            </div>

            <div className="qnc-title-row">
              <div>
                <h1>Notification Center</h1>
                <p>
                  Keep your community informed with platform-wide
                  announcements and important updates.
                </p>
              </div>

              <div className="qnc-header-icon" aria-hidden="true">
                📢
              </div>
            </div>

            <div className="qnc-header-status">
              <span className="qnc-status-dot" />
              Broadcast management
            </div>
          </div>

          <div className="qnc-header-actions">
            <Link to="/notifications" className="qnc-secondary-button">
              <span>🔔</span>
              View Notifications
            </Link>

            <Link to="/admin" className="qnc-secondary-button">
              <span>←</span>
              Admin Dashboard
            </Link>
          </div>
        </header>

        {/* MAIN CONTENT */}
        <div className="qnc-layout">
          {/* COMPOSE PANEL */}
          <section className="qnc-compose-panel">
            <div className="qnc-panel-heading">
              <div className="qnc-panel-heading-icon">
                ✍️
              </div>

              <div>
                <span className="qnc-section-kicker">
                  CREATE ANNOUNCEMENT
                </span>
                <h2>Broadcast a Message</h2>
                <p>
                  Compose a notification for QuestForge users.
                </p>
              </div>
            </div>

            {/* FEEDBACK */}
            {success && (
              <div
                className="qnc-feedback qnc-success"
                role="status"
                aria-live="polite"
              >
                <span className="qnc-feedback-icon">✓</span>
                <div>
                  <strong>Broadcast successful</strong>
                  <p>{success}</p>
                </div>
              </div>
            )}

            {error && (
              <div
                className="qnc-feedback qnc-error"
                role="alert"
                aria-live="assertive"
              >
                <span className="qnc-feedback-icon">!</span>
                <div>
                  <strong>Unable to send notification</strong>
                  <p>{error}</p>
                </div>
              </div>
            )}

            <form
              className="qnc-form"
              onSubmit={handleSubmit}
              noValidate
            >
              {/* TITLE */}
              <div className="qnc-field">
                <div className="qnc-label-row">
                  <label htmlFor="notification-title">
                    Notification Title
                    <span className="qnc-required">*</span>
                  </label>

                  <span
                    className={`qnc-counter ${
                      titleLength >= 180 ? 'qnc-counter-warning' : ''
                    }`}
                  >
                    {titleLength}/200
                  </span>
                </div>

                <input
                  id="notification-title"
                  name="title"
                  type="text"
                  value={form.title}
                  onChange={handleChange}
                  placeholder="Example: New Challenge Available!"
                  maxLength={200}
                  disabled={sending}
                  autoComplete="off"
                  aria-required="true"
                  aria-invalid={Boolean(error && !form.title.trim())}
                />

                <span className="qnc-field-hint">
                  Keep your title short, clear and engaging.
                </span>
              </div>

              {/* MESSAGE */}
              <div className="qnc-field">
                <div className="qnc-label-row">
                  <label htmlFor="notification-message">
                    Notification Message
                    <span className="qnc-required">*</span>
                  </label>

                  <span
                    className={`qnc-counter ${
                      messageLength >= 1800 ? 'qnc-counter-warning' : ''
                    }`}
                  >
                    {messageLength}/2000
                  </span>
                </div>

                <textarea
                  id="notification-message"
                  name="message"
                  value={form.message}
                  onChange={handleChange}
                  placeholder="Write your announcement here. Tell users what's happening..."
                  rows={7}
                  maxLength={2000}
                  disabled={sending}
                  aria-required="true"
                  aria-invalid={Boolean(error && !form.message.trim())}
                />

                <span className="qnc-field-hint">
                  Provide enough information so users understand
                  your announcement.
                </span>
              </div>

              {/* NOTIFICATION TYPE */}
              <div className="qnc-field">
                <label className="qnc-field-label">
                  Notification Category
                  <span className="qnc-required">*</span>
                </label>

                <div className="qnc-type-grid">
                  {NOTIFICATION_TYPES.map((type) => (
                    <button
                      key={type.value}
                      type="button"
                      className={`qnc-type-option qnc-type-${type.value.toLowerCase()} ${
                        form.type === type.value ? 'qnc-type-selected' : ''
                      }`}
                      onClick={() =>
                        handleChange({
                          target: {
                            name: 'type',
                            value: type.value,
                          },
                        })
                      }
                      disabled={sending}
                      aria-pressed={form.type === type.value}
                    >
                      <span className="qnc-type-icon">
                        {type.icon}
                      </span>

                      <span className="qnc-type-label">
                        {type.label}
                      </span>

                      <span className="qnc-type-check">
                        {form.type === type.value ? '✓' : ''}
                      </span>
                    </button>
                  ))}
                </div>

                <span className="qnc-field-hint">
                  {selectedType.description}
                </span>
              </div>

              {/* LIVE PREVIEW */}
              <div className="qnc-preview-section">
                <div className="qnc-preview-heading">
                  <div>
                    <span className="qnc-section-kicker">
                      LIVE PREVIEW
                    </span>
                    <h3>How users will see it</h3>
                  </div>

                  <span className="qnc-preview-live">
                    <span />
                    LIVE
                  </span>
                </div>

                <article
                  className={`qnc-notification-preview qnc-preview-${form.type.toLowerCase()}`}
                >
                  <div className="qnc-preview-accent" />

                  <div className="qnc-preview-content">
                    <div className="qnc-preview-top">
                      <div className="qnc-preview-brand">
                        <span className="qnc-preview-brand-icon">
                          Q
                        </span>
                        <span>QUESTFORGE</span>
                      </div>

                      <span className="qnc-preview-type">
                        {selectedType.icon} {form.type}
                      </span>
                    </div>

                    <h4>
                      {form.title.trim() || 'Your notification title'}
                    </h4>

                    <p>
                      {form.message.trim() ||
                        'Your notification message will appear here as you type.'}
                    </p>

                    <div className="qnc-preview-footer">
                      <span>Just now</span>
                      <span>QuestForge Notifications</span>
                    </div>
                  </div>
                </article>

                <p className="qnc-preview-disclaimer">
                  Preview is illustrative. Final appearance may depend
                  on the user's notification interface.
                </p>
              </div>

              {/* ACTIONS */}
              <div className="qnc-form-actions">
                <button
                  type="button"
                  className="qnc-clear-button"
                  onClick={handleClear}
                  disabled={sending}
                >
                  <span>↺</span>
                  Clear Form
                </button>

                <button
                  type="submit"
                  className="qnc-submit-button"
                  disabled={sending || !isFormValid}
                >
                  {sending ? (
                    <>
                      <span className="qnc-button-spinner" />
                      Broadcasting...
                    </>
                  ) : (
                    <>
                      <span>📢</span>
                      Broadcast Notification
                      <span className="qnc-submit-arrow">→</span>
                    </>
                  )}
                </button>
              </div>

              <p className="qnc-submit-note">
                Review your message before broadcasting. This action
                sends the announcement through the platform's broadcast
                endpoint.
              </p>
            </form>
          </section>

          {/* SIDEBAR */}
          <aside className="qnc-sidebar">
            <section className="qnc-info-card">
              <div className="qnc-info-icon">💡</div>
              <span className="qnc-section-kicker">
                COMMUNICATION TIPS
              </span>
              <h3>Make every announcement count.</h3>

              <ul className="qnc-tips-list">
                <li>
                  <span>01</span>
                  <div>
                    <strong>Write a clear title</strong>
                    <p>Tell users what the announcement is about.</p>
                  </div>
                </li>

                <li>
                  <span>02</span>
                  <div>
                    <strong>Keep it useful</strong>
                    <p>Share relevant information and clear next steps.</p>
                  </div>
                </li>

                <li>
                  <span>03</span>
                  <div>
                    <strong>Choose the right type</strong>
                    <p>Use categories to communicate the message's purpose.</p>
                  </div>
                </li>
              </ul>
            </section>

            <section className="qnc-info-card qnc-type-guide">
              <span className="qnc-section-kicker">
                CATEGORY GUIDE
              </span>
              <h3>Notification Types</h3>

              {NOTIFICATION_TYPES.map((type) => (
                <div
                  className="qnc-guide-item"
                  key={type.value}
                >
                  <span className={`qnc-guide-icon qnc-guide-${type.value.toLowerCase()}`}>
                    {type.icon}
                  </span>

                  <div>
                    <strong>{type.value}</strong>
                    <p>{type.description}</p>
                  </div>
                </div>
              ))}
            </section>

            <div className="qnc-sidebar-note">
              <span>🔐</span>
              <p>
                Broadcast access should be restricted to authorized
                administrators by the backend.
              </p>
            </div>
          </aside>
        </div>

        {/* FOOTER */}
        <footer className="qnc-footer">
          <span>
            <span className="qnc-footer-dot" />
            QuestForge Administration
          </span>

          <span>Notification Management Center</span>
        </footer>
      </div>
    </main>
  );
}

export default AdminNotifications;
