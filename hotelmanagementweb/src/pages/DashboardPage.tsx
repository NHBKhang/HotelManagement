"use client"

import { useAuthBetter } from "../hooks/useAuthBetter"
import { Button } from "../components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card"

export default function DashboardPage() {
  const { user, logout } = useAuthBetter()

  return (
    <div className="min-h-screen bg-background p-4">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold">Dashboard</h1>
          <Button onClick={logout} variant="outline">
            Sign Out
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Welcome, {user?.firstName}!</CardTitle>
            <CardDescription>You are successfully authenticated</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <p>
                <strong>Username:</strong> {user?.username}
              </p>
              <p>
                <strong>Email:</strong> {user?.email}
              </p>
              <p>
                <strong>Full Name:</strong> {user?.firstName} {user?.lastName}
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
