import { createAuthClient } from "better-auth/react";
import api from "../services/api";

// Create better-auth client with backend API integration
export const auth = createAuthClient({
  baseURL: import.meta.env.VITE_SPRING_BOOT_API_URL || "http://localhost:8080",
  endpoints: {
    signIn: "/auth/login",
    signUp: "/auth/signup",
    signOut: "/auth/logout",
    getSession: "/auth/verify",
    refresh: "/auth/refresh",
  },
  session: {
    // Use localStorage for session persistence
    storage: "localStorage",
    // Session expires after 7 days
    expiresIn: 60 * 60 * 24 * 7, // 7 days in seconds
    // Refresh token configuration
    refreshToken: {
      enabled: true,
      expiresIn: 60 * 60 * 24 * 30, // 30 days in seconds
    },
  },
  callbacks: {
    // Customize the session data returned to the client
    session: async (session: any) => {
      // You can add additional session data here if needed
      return session;
    },
    // Handle API errors
    onError: (error: any) => {
      console.error("Auth error:", error);
      return error;
    },
  },
  // Integrate with existing axios instance for API calls
  apiClient: {
    request: async (config: any) => {
      try {
        const response = await api({
          method: config.method,
          url: config.url,
          data: config.body,
          headers: config.headers,
        });
        return {
          data: response.data,
          status: response.status,
          headers: response.headers,
        };
      } catch (error: any) {
        throw new Error(error.response?.data?.message || error.message);
      }
    },
  },
});

// Export auth hooks for easy usage
export const { useSession } = auth;

// Types for better-auth integration
export interface BetterAuthSession {
  user: {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    phone?: string;
  };
  accessToken: string;
  refreshToken?: string;
  expiresAt: number;
}

export interface BetterAuthSignInInput {
  username: string;
  password: string;
}

export interface BetterAuthSignUpInput {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  username: string;
  password: string;
}