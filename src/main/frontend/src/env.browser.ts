import * as z from 'zod'
import { ModesSchema } from './env.ts'

// strongly type env variables and by parsing them with zod we fail fast if vars are missing or have the wrong type/value
const BrowserEnvSchema = z.object({
  MODE: ModesSchema,
  BASE_URL: z.string(),
  PROD: z.boolean(),
  DEV: z.boolean(),
  SSR: z.boolean(),
  VITE_PORT: z.coerce.number().int().default(3000),
  VITE_API_BASE_URL: z.string().default(''),
  VITE_API_TIMEOUT_MS: z.coerce.number().int().default(10000),
})

export type BrowserEnv = z.infer<typeof BrowserEnvSchema>

export const envBrowser = BrowserEnvSchema.parse(import.meta.env)
