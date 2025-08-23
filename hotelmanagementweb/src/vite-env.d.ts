/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_SPRING_BOOT_API_URL: string
  readonly VITE_BETTER_AUTH_SECRET: string
  readonly VITE_BETTER_AUTH_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}