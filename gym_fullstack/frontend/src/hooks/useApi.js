import { useCallback } from 'react';

import { useAuth } from '../context/AuthContext.jsx';

export function useApi() {
  const { token, refresh, logout, apiBase } = useAuth();

  const request = useCallback(
    async (endpoint, { method = 'GET', body, headers = {}, secure = true } = {}) => {
      let accessToken = token;
      let retried = false;

      const makeRequest = async (authToken) => {
        const response = await fetch(`${apiBase}${endpoint}`, {
          method,
          headers: {
            'Content-Type': 'application/json',
            ...(secure && authToken ? { Authorization: `Bearer ${authToken}` } : {}),
            ...headers
          },
          body: body ? JSON.stringify(body) : undefined
        });

        if (response.status === 401 && secure && refresh) {
          return { needsRefresh: true };
        }

        if (response.status === 204) {
          return { data: null };
        }

        if (!response.ok) {
          const errorBody = await response.json().catch(() => ({}));
          const error = new Error(errorBody.error || errorBody.detail || 'Error en la petición');
          error.status = response.status;
          throw error;
        }

        const data = await response.json();
        return { data };
      };

      while (true) {
        const result = await makeRequest(accessToken);
        if (result.needsRefresh) {
          if (retried) {
            logout();
            throw new Error('Sesión expirada. Inicia sesión nuevamente.');
          }
          retried = true;
          accessToken = await refresh();
          continue;
        }
        return result.data;
      }
    },
    [apiBase, token, refresh, logout]
  );

  return { request };
}
