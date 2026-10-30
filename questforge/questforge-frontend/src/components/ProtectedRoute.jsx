import { Navigate, Outlet } from 'react-router-dom';

import { getToken, isLoggedIn } from '../utils/auth';

function ProtectedRoute() {
  const loggedIn = isLoggedIn();
  const token = getToken();

  if (!loggedIn || !token) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return <Outlet />;
}

export default ProtectedRoute;