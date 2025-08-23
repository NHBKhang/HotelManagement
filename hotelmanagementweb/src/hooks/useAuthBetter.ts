import { useCallback, useEffect, useState } from "react"
import { auth } from "../lib/better-auth"
import type { User } from "../types/api"

export function useAuthBetter() {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const { data: session, isPending: sessionLoading } = auth.useSession()

  useEffect(() => {
    if (!sessionLoading) {
      if (session?.user) {
        // Convert better-auth user to our User type
        const userData: User = {
          id: session.user.id,
          username: session.user.email || "",
          email: session.user.email || "",
          firstName: session.user.name?.split(' ')[0] || '',
          lastName: session.user.name?.split(' ')[1] || '',
        }
        setUser(userData)
      } else {
        setUser(null)
      }
      setIsLoading(false)
    }
  }, [session, sessionLoading])

  const login = useCallback(async (username: string, password: string) => {
    try {
      setError(null)
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
        username: result.data.user.email || "",
        email: result.data.user.email || "",
        firstName: result.data.user.name?.split(' ')[0] || '',
        lastName: result.data.user.name?.split(' ')[1] || '',
      }

      setUser(userData)
      return userData
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Login failed"
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const logout = useCallback(async () => {
    try {
      await auth.signOut()
      setUser(null)
      setError(null)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Logout failed"
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const signup = useCallback(async (userData: {
    firstName: string
    lastName: string
    email: string
    phone: string
    username: string
    password: string
  }) => {
    try {
      setError(null)
      const result = await auth.signUp.email({
        email: userData.email,
        password: userData.password,
        name: `${userData.firstName} ${userData.lastName}`,
      })

      if (result.error) {
        throw new Error(result.error.message || "Signup failed")
      }

      if (!result.data?.user) {
        throw new Error("Invalid response from server")
      }

      // Convert better-auth user to our User type
      const newUser: User = {
        id: result.data.user.id,
        username: result.data.user.email || "",
        email: result.data.user.email || "",
        firstName: userData.firstName,
        lastName: userData.lastName,
        phone: userData.phone,
      }

      setUser(newUser)
      return newUser
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Signup failed"
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  return {
    user,
    isLoading,
    error,
    isAuthenticated: !!user,
    login,
    logout,
    signup,
    clearError: () => setError(null),
  }
}