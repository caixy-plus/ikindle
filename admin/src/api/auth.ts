import client from './client';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface User {
  id: number;
  username: string;
  nickname?: string;
  email?: string;
  enabled: boolean;
  roles?: string[];
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export const login = (req: LoginRequest) =>
  client.post<ApiResponse<LoginResponse>>('/users/login', req);

export const getCurrentUser = () =>
  client.get<ApiResponse<User>>('/users/me');
