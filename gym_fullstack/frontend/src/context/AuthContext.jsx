import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8000/api';

const AuthContext = createContext({});

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [refreshToken, setRefreshToken] = useState(() => localStorage.getItem('refresh'));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  useEffect(() => {
    if (refreshToken) {
      localStorage.setItem('refresh', refreshToken);
    } else {
      localStorage.removeItem('refresh');
    }
  }, [refreshToken]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }, [user]);

  const login = async (email, password) => {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || error.error || 'Credenciales inválidas');
    }

    const data = await response.json();
    setToken(data.jwt);
    setRefreshToken(data.refresh);
    setUser(data.usuario);
    return data.usuario;
  };

  const logout = () => {
    setToken(null);
    setRefreshToken(null);
    setUser(null);
  };

  const refresh = async () => {
    if (!refreshToken) {
      throw new Error('No hay refresh token disponible');
    }

    const response = await fetch(`${API_BASE}/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ refresh: refreshToken })
    });

    if (!response.ok) {
      logout();
      throw new Error('No se pudo refrescar el token');
    }

    const data = await response.json();
    setToken(data.access);
    return data.access;
  };

  const value = useMemo(
    () => ({ token, refreshToken, user, login, logout, refresh, apiBase: API_BASE, setUser }),
    [token, refreshToken, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
