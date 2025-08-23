declare module "next-auth" {
  interface Session {
    user: {
      id: string
      username: string
      name?: string | null
      email?: string | null
      image?: string | null
    }
    accessToken: string
  }

  interface User {
    username: string
    accessToken: string
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    username: string
    accessToken: string
  }
}
