// API Response Types
export interface ApiResponse<T = any> {
  data: T
  message?: string
  success: boolean
}

export interface ApiError {
  message: string
  code?: string
  details?: any
}

// Auth Types
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  accessToken: string
  refreshToken?: string
}

export interface SignupRequest {
  firstName: string
  lastName: string
  email: string
  phone: string
  username: string
  password: string
}

export interface SignupResponse {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  message: string
}

// User Types
export interface User {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  phone?: string
  createdAt?: string
  updatedAt?: string
}

export interface UpdateUserRequest {
  firstName?: string
  lastName?: string
  email?: string
  phone?: string
}
