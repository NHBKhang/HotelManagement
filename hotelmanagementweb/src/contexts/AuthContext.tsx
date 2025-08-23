"use client"

import React from "react"
import { createContext, useContext, useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { auth } from "../lib/better-auth"
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

  // Use better-auth session hook
  const { data: session, isPending: sessionLoading } = auth.useSession()

  // Sync with better-auth session
  useEffect(() => {
    if (!sessionLoading) {
      if (session?.user) {
        // Convert better-auth user to our User type
        const userData: User = {
          id: session.user.id,
          username: session.user.email, // Using email as username for now
          email: session.user.email,
          firstName: session.user.name?.split(' ')[0] || '',
          lastName: session.user.name?.split(' ')[1] || '',
        }
        setUser(userData)
        setToken(localStorage.getItem("auth_token"))
      } else {
        setUser(null)
        setToken(null)
      }
      setIsLoading(false)
    }
  }, [session, sessionLoading])

  const login = async (username: string, password: string) => {
    const result = await auth.signIn.email({
      email: username,
      password,
    })

    if (result.error) {
      throw new Error(result.error.message || "Login failed")
    }

    if (!result.data?.user) {
      throw new Error("Invalid response from server")
    }

    // Convert better-auth user to our User type
    const userData: User = {
      id: result.data.user.id,
      username: result.data.user.email,
      email: result.data.user.email,
      firstName: result.data.user.name?.split(' ')[0] || '',
      lastName: result.data.user.name?.split(' ')[1] || '',
    }

    setUser(userData)
    setToken(localStorage.getItem("auth_token"))
  }

  const logout = async () => {
    await auth.signOut()
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

  return React.createElement(AuthContext.Provider, { value }, children)
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
