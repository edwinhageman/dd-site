import * as z from 'zod'

export const ModesSchema = z.enum(['development', 'production', 'test']).default('development')

export type Modes = z.infer<typeof ModesSchema>
