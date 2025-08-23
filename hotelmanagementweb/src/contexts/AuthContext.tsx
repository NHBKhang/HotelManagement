"use client"

import type React from "react"
import { createContext, useContext, useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { authApi } from "../services/api"
import type { User } from "../types/api"

interface AuthContextType {
  user: User | null
  token: string | null
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
  isLoading: boolean
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const navigate = useNavigate()

  // Check for existing token on mount
  useEffect(() => {
    const storedToken = localStorage.getItem("auth_token")
    const storedUser = localStorage.getItem("auth_user")

    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
      verifyToken()
    } else {
      setIsLoading(false)
    }
  }, [])

  const verifyToken = async () => {
    try {
      const userData = await authApi.verifyToken()
      setUser(userData)
    } catch (error) {
      // Token is invalid, clear storage
      localStorage.removeItem("auth_token")
      localStorage.removeItem("auth_user")
      setUser(null)
      setToken(null)
    } finally {
      setIsLoading(false)
    }
  }

  const login = async (username: string, password: string) => {
    const data = await authApi.login({ username, password })

    const userData: User = {
      id: data.id,
      username: data.username,
      email: data.email,
      firstName: data.firstName,
      lastName: data.lastName,
    }

    setUser(userData)
    setToken(data.accessToken)

    // Store in localStorage
    localStorage.setItem("auth_token", data.accessToken)
    localStorage.setItem("auth_user", JSON.stringify(userData))

    // Store refresh token if provided
    if (data.refreshToken) {
      localStorage.setItem("refresh_token", data.refreshToken)
    }
  }

  const logout = async () => {
    await authApi.logout()
    setUser(null)
    setToken(null)
    navigate("/login")
  }

  const value = {
    user,
    token,
    login,
    logout,
    isLoading,
    isAuthenticated: !!user && !!token,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
