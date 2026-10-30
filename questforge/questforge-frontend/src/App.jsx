import { Routes, Route } from 'react-router-dom';

import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';

import Challenges from './pages/Challenges';
import SolveChallenge from './pages/SolveChallenge';

import Submissions from './pages/Submissions';
import SubmissionResult from './pages/SubmissionResult';

import Leaderboard from './pages/Leaderboard';
import Profile from './pages/Profile';

import Notifications from './pages/Notifications';

import AdminDashboard from './pages/AdminDashboard';
import AdminChallengeOverview from './pages/AdminChallengeOverview';
import AdminSubmissions from './pages/AdminSubmissions';

import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import AdminRoute from './components/AdminRoute';

function App() {
  return (
    <>
      <Navbar />

      <Routes>

        {/* =====================================
            PUBLIC ROUTES
        ===================================== */}

        <Route
          path="/"
          element={<Home />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />


        {/* =====================================
            PROTECTED USER ROUTES
        ===================================== */}

        <Route
          element={<ProtectedRoute />}
        >

          <Route
            path="/challenges"
            element={<Challenges />}
          />

          <Route
            path="/solve/:challengeId"
            element={<SolveChallenge />}
          />

          <Route
            path="/submissions"
            element={<Submissions />}
          />

          <Route
            path="/submission-result/:submissionId"
            element={<SubmissionResult />}
          />

          <Route
            path="/leaderboard"
            element={<Leaderboard />}
          />

          <Route
            path="/profile"
            element={<Profile />}
          />

          {/* =================================
              🔔 NOTIFICATIONS
              PRESERVED
          ================================= */}

          <Route
            path="/notifications"
            element={<Notifications />}
          />

        </Route>


        {/* =====================================
            ADMIN ROUTES
        ===================================== */}

        <Route
          element={<AdminRoute />}
        >

          {/* Main Admin Dashboard */}

          <Route
            path="/admin"
            element={<AdminDashboard />}
          />

          {/* Challenge Overview */}

          <Route
            path="/admin/challenge-overview"
            element={
              <AdminChallengeOverview />
            }
          />

          {/* Dedicated Admin Submissions */}

          <Route
            path="/admin/submissions"
            element={
              <AdminSubmissions />
            }
          />

        </Route>

      </Routes>
    </>
  );
}

export default App;