import * as z from 'zod'
import { ModesSchema } from '@/env.ts'

// strongly type env variables and by parsing them with zod we fail fast if vars are missing or have the wrong type/value
const NodeEnvSchema = z.object({
  NODE_ENV: ModesSchema,
})

export type NodeEnv = z.infer<typeof NodeEnvSchema>

export const envNode = NodeEnvSchema.parse(process.env)
