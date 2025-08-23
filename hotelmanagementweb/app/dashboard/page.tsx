"use client"

import { useSession, signOut } from "next-auth/react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Loader2 } from "lucide-react"

export default function DashboardPage() {
  const { data: session, status } = useSession()

  if (status === "loading") {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="flex items-center gap-2">
          <Loader2 className="h-4 w-4 animate-spin" />
          <span>Loading...</span>
        </div>
      </div>
    )
  }

  if (!session) {
    return null // Middleware will handle redirect
  }

  return (
    <div className="min-h-screen bg-background p-4">
      <div className="max-w-4xl mx-auto">
        <Card>
          <CardHeader>
            <CardTitle>Welcome to your Dashboard</CardTitle>
            <CardDescription>You are successfully logged in as {session.user.username}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <h3 className="font-semibold">User Information</h3>
                <p className="text-sm text-muted-foreground">Name: {session.user.name}</p>
                <p className="text-sm text-muted-foreground">Username: {session.user.username}</p>
                <p className="text-sm text-muted-foreground">Email: {session.user.email}</p>
              </div>
              <div>
                <h3 className="font-semibold">Account Status</h3>
                <p className="text-sm text-green-600">✓ Account Active</p>
                <p className="text-sm text-green-600">✓ Email Verified</p>
                <p className="text-sm text-muted-foreground">Member since: {new Date().toLocaleDateString()}</p>
              </div>
            </div>
            <div className="flex gap-2">
              <Button onClick={() => signOut({ callbackUrl: "/login" })} variant="outline">
                Sign Out
              </Button>
              <Button variant="secondary">Edit Profile</Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
