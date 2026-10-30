import {
  Navigate,
  Outlet,
} from 'react-router-dom';

import {
  getToken,
  isAdmin,
  isLoggedIn,
} from '../utils/auth';

function AdminRoute() {
  const loggedIn = isLoggedIn();
  const token = getToken();
  const admin = isAdmin();

  if (!loggedIn || !token) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  if (!admin) {
    return (
      <Navigate
        to="/challenges"
        replace
      />
    );
  }

  return <Outlet />;
}

export default AdminRoute;