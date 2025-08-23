import axios, { type AxiosError, type AxiosResponse } from "axios"
import type {
  ApiError,
  LoginRequest,
  LoginResponse,
  SignupRequest,
  SignupResponse,
  User,
  UpdateUserRequest,
} from "../types/api"

// Create axios instance
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000, // 10 seconds timeout
})

// Request interceptor - Add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("auth_token")
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // Log requests in development
    if (import.meta.env.DEV) {
      console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
        data: config.data,
        params: config.params,
      })
    }

    return config
  },
  (error) => {
    console.error("[API Request Error]", error)
    return Promise.reject(error)
  },
)

// Response interceptor - Handle responses and errors
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // Log responses in development
    if (import.meta.env.DEV) {
      console.log(`[API Response] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        data: response.data,
      })
    }
    return response
  },
  (error: AxiosError) => {
    // Handle 401 - Unauthorized
    if (error.response?.status === 401) {
      localStorage.removeItem("auth_token")
      localStorage.removeItem("auth_user")
      window.location.href = "/login"
      return Promise.reject(new Error("Session expired. Please login again."))
    }

    // Handle network errors
    if (!error.response) {
      return Promise.reject(new Error("Network error. Please check your connection."))
    }

    // Handle other HTTP errors
    const apiError: ApiError = {
      message: error.response.data?.message || error.message || "An unexpected error occurred",
      code: error.response.data?.code || error.code,
      details: error.response.data,
    }

    console.error("[API Response Error]", {
      status: error.response.status,
      url: error.config?.url,
      error: apiError,
    })

    return Promise.reject(apiError)
  },
)

// Auth API Functions
export const authApi = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>("/auth/login", credentials)
    return response.data
  },

  signup: async (userData: SignupRequest): Promise<SignupResponse> => {
    const response = await api.post<SignupResponse>("/auth/signup", userData)
    return response.data
  },

  logout: async (): Promise<void> => {
    try {
      await api.post("/auth/logout")
    } catch (error) {
      // Continue with logout even if API call fails
      console.warn("Logout API call failed:", error)
    } finally {
      localStorage.removeItem("auth_token")
      localStorage.removeItem("auth_user")
    }
  },

  refreshToken: async (): Promise<LoginResponse> => {
    const refreshToken = localStorage.getItem("refresh_token")
    const response = await api.post<LoginResponse>("/auth/refresh", {
      refreshToken,
    })
    return response.data
  },

  verifyToken: async (): Promise<User> => {
    const response = await api.get<User>("/auth/verify")
    return response.data
  },
}

// User API Functions
export const userApi = {
  getProfile: async (): Promise<User> => {
    const response = await api.get<User>("/user/profile")
    return response.data
  },

  updateProfile: async (userData: UpdateUserRequest): Promise<User> => {
    const response = await api.put<User>("/user/profile", userData)
    return response.data
  },

  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    await api.post("/user/change-password", {
      currentPassword,
      newPassword,
    })
  },

  deleteAccount: async (): Promise<void> => {
    await api.delete("/user/account")
  },
}

// Generic API helper functions
export const apiHelpers = {
  // Generic GET request
  get: async (url: string, params?: any): Promise<any> => {
    const response = await api.get(url, { params })
    return response.data
  },

  // Generic POST request
  post: async (url: string, data?: any): Promise<any> => {
    const response = await api.post(url, data)
    return response.data
  },

  // Generic PUT request
  put: async (url: string, data?: any): Promise<any> => {
    const response = await api.put(url, data)
    return response.data
  },

  // Generic DELETE request
  delete: async (url: string): Promise<any> => {
    const response = await api.delete(url)
    return response.data
  },

  // Upload file
  uploadFile: async (url: string, file: File, onProgress?: (progress: number) => void): Promise<any> => {
    const formData = new FormData()
    formData.append("file", file)

    const response = await api.post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      },
    })

    return response.data
  },
}

export default api
