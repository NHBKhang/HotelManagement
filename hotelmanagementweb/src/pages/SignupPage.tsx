import { SignupForm } from "../components/auth/SignupForm"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card"

export default function SignupPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-bold">Create Account</CardTitle>
          <CardDescription>Sign up to get started with your new account</CardDescription>
        </CardHeader>
        <CardContent>
          <SignupForm />
        </CardContent>
      </Card>
    </div>
  )
}
