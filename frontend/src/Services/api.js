const API_BASE_URL = "https://localhost:8443";

/**
 * Helper to handle fetch responses
 */
const handleResponse = async (res) => {
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || `Request failed with status ${res.status}`);
  }
  const contentType = res.headers.get("Content-Type");
  return contentType && contentType.includes("application/json")
    ? res.json()
    : res.text();
};

/**
 * Register a new user
 * @param {Object} userData
 */
export const register = async (userData) => {
  const res = await fetch(`${API_BASE_URL}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData),
  });
  return handleResponse(res);
};

/**
 * Login with credentials
 * @param {Object} userData
 */
// export const login = async (userData) => {
//   const res = await fetch(`${API_BASE_URL}/login`, {
//     method: "POST",
//     headers: { "Content-Type": "application/json" },
//     body: JSON.stringify(userData),
//   });
//   return handleResponse(res);
// };
export const login = async (userData) => {
  const res = await fetch(`${API_BASE_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData),
  });
  return handleResponse(res);
};

/**
 * Ping a protected route
 * @param {string} token
 */
export const ping = async (token) => {
  const res = await fetch(`${API_BASE_URL}/ping`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return handleResponse(res);
};
  