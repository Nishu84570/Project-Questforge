import { Link } from 'react-router-dom';
import { getUser, isLoggedIn } from '../utils/auth';

function Home() {
  const loggedIn = isLoggedIn();
  const user = getUser();

  return (
    <div className="page home-page">

      <section className="hero home-hero">

        <div className="hero-content">

          <div className="hero-badge">
            ⚡ QUESTFORGE
          </div>

          {loggedIn ? (
            <>
              <p className="eyebrow">
                WELCOME BACK, {user?.name?.toUpperCase() || 'PLAYER'}
              </p>

              <h1>
                Ready to forge
                <span> your next skill?</span>
              </h1>

              <p className="hero-description">
                Your coding journey continues here.
                Solve challenges, improve your problem-solving skills,
                earn XP, and climb the leaderboard.
              </p>

              <div className="hero-actions">

                <Link
                  className="primary-button"
                  to="/challenges"
                >
                  ⚔️ Start a Challenge
                </Link>

                <Link
                  className="secondary-button"
                  to="/profile"
                >
                  👤 View My Profile
                </Link>

              </div>
            </>
          ) : (
            <>
              <p className="eyebrow">
                LEVEL UP YOUR CODING JOURNEY
              </p>

              <h1>
                Forge Your Skills.
                <span> Become Unstoppable.</span>
              </h1>

              <p className="hero-description">
                QuestForge is your interactive coding arena.
                Solve challenges, submit solutions, earn XP,
                track your progress, and compete with other developers.
              </p>

              <div className="hero-actions">

                <Link
                  className="primary-button"
                  to="/register"
                >
                  🚀 Create Your Account
                </Link>

                <Link
                  className="secondary-button"
                  to="/login"
                >
                  🔐 Login
                </Link>

              </div>
            </>
          )}

        </div>

        <div className="hero-visual">

          <div className="hero-orbit orbit-one"></div>
          <div className="hero-orbit orbit-two"></div>

          <div className="hero-core">
            <div className="hero-core-icon">
              ⚔️
            </div>

            <strong>
              QUEST
            </strong>

            <span>
              FORGE
            </span>
          </div>

          <div className="floating-card floating-card-one">
            <span>🔥</span>
            <div>
              <strong>Keep Learning</strong>
              <small>Every challenge counts</small>
            </div>
          </div>

          <div className="floating-card floating-card-two">
            <span>🏆</span>
            <div>
              <strong>Compete</strong>
              <small>Climb the leaderboard</small>
            </div>
          </div>

          <div className="floating-card floating-card-three">
            <span>⚡</span>
            <div>
              <strong>Earn XP</strong>
              <small>Level up your profile</small>
            </div>
          </div>

        </div>

      </section>

      <section className="home-features">

        <div className="section-heading">

          <p className="eyebrow">
            THE QUESTFORGE EXPERIENCE
          </p>

          <h2>
            Everything you need to grow.
          </h2>

          <p>
            Practice, compete, track your progress,
            and build stronger problem-solving skills.
          </p>

        </div>

        <div className="feature-grid">

          <div className="feature-card">

            <div className="feature-icon">
              ⚔️
            </div>

            <h3>
              Coding Challenges
            </h3>

            <p>
              Solve carefully designed challenges
              and sharpen your programming skills
              one quest at a time.
            </p>

            <Link to="/challenges">
              Explore Challenges →
            </Link>

          </div>

          <div className="feature-card">

            <div className="feature-icon">
              🏆
            </div>

            <h3>
              Global Leaderboard
            </h3>

            <p>
              See how you rank against other players
              and push yourself higher with every
              successful challenge.
            </p>

            <Link to="/leaderboard">
              View Leaderboard →
            </Link>

          </div>

          <div className="feature-card">

            <div className="feature-icon">
              📊
            </div>

            <h3>
              Track Your Progress
            </h3>

            <p>
              Monitor your attempts, scores, XP,
              achievements, and overall growth
              from your profile.
            </p>

            <Link to="/profile">
              Open Profile →
            </Link>

          </div>

          <div className="feature-card">

            <div className="feature-icon">
              🔥
            </div>

            <h3>
              Build Your Streak
            </h3>

            <p>
              Stay consistent, maintain your learning
              streak, and turn daily practice into
              long-term progress.
            </p>

            <Link to="/profile">
              Check Progress →
            </Link>

          </div>

        </div>

      </section>

      <section className="home-cta">

        <div>

          <p className="eyebrow">
            YOUR NEXT QUEST AWAITS
          </p>

          <h2>
            Don't just learn.
            <span> Forge yourself.</span>
          </h2>

          <p>
            Every problem you solve is another step
            toward becoming a better developer.
          </p>

        </div>

        <Link
          className="primary-button"
          to={loggedIn ? '/challenges' : '/register'}
        >
          {loggedIn
            ? '⚔️ Enter the Arena'
            : '🚀 Start Your Journey'}
        </Link>

      </section>

    </div>
  );
}

export default Home;