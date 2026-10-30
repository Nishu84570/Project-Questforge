import { getToken } from '../utils/auth';

const API_BASE_URL = 'http://localhost:8080';

export const apiRequest = async (
  endpoint,
  options = {}
) => {
  const token = getToken();

  const headers = {
    ...(options.headers || {}),
  };

  /*
   * IMPORTANT:
   * Do not manually set Content-Type when body is FormData.
   * The browser automatically sets the correct multipart boundary.
   */
  if (
    options.body &&
    !(options.body instanceof FormData)
  ) {
    headers['Content-Type'] = 'application/json';
  }

  /*
   * Attach JWT token to every authenticated request.
   */
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      ...options,
      headers,
    }
  );

  let data = null;

  const contentType =
    response.headers.get('content-type');

  if (
    contentType &&
    contentType.includes('application/json')
  ) {
    data = await response.json();
  } else {
    const text = await response.text();
    data = text || null;
  }

  return {
    response,
    data,
  };
};

export default API_BASE_URL;