const TOKEN_KEY = 'token';
const USER_KEY = 'user';

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const getUser = () => {
  const user = localStorage.getItem(USER_KEY);

  if (!user) {
    return null;
  }

  try {
    return JSON.parse(user);
  } catch (error) {
    console.error(
      'Failed to parse stored user:',
      error
    );

    localStorage.removeItem(USER_KEY);

    return null;
  }
};

export const setAuth = (data) => {
  if (!data?.token) {
    throw new Error(
      'Authentication token is missing.'
    );
  }

  localStorage.setItem(
    TOKEN_KEY,
    data.token
  );

  const user = {
    id: data.id,
    name: data.name,
    email: data.email,
    role: data.role,
  };

  localStorage.setItem(
    USER_KEY,
    JSON.stringify(user)
  );
};

export const isLoggedIn = () => {
  return Boolean(getToken());
};

export const isAdmin = () => {
  const user = getUser();

  return user?.role === 'ADMIN';
};

export const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};