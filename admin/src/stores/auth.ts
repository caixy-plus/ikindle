import { create } from 'zustand';
import type { User } from '../api/auth';

interface AuthState {
  token: string | null;
  user: User | null;
  setAuth: (token: string, user: User) => void;
  clearAuth: () => void;
  isAdmin: () => boolean;
}

const storedToken = localStorage.getItem('token');

export const useAuthStore = create<AuthState>((set, get) => ({
  token: storedToken,
  user: null,
  setAuth: (token, user) => {
    localStorage.setItem('token', token);
    set({ token, user });
  },
  clearAuth: () => {
    localStorage.removeItem('token');
    set({ token: null, user: null });
  },
  isAdmin: () => {
    const user = get().user;
    return user?.roles?.includes('ADMIN') ?? false;
  },
}));
