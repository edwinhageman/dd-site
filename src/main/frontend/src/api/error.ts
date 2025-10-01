import * as z from 'zod'

export class ApiError extends Error {
  constructor(public readonly payload: ErrorResponse) {
    super(payload.detail)
    this.name = 'ApiError'
  }
}

export const ErrorResponseSchema = z.object({
  type: z.string(),
  title: z.string(),
  status: z.number(),
  detail: z.string(),
  instance: z.string(),
  fieldErrors: z.record(z.string(), z.array(z.string())).optional(),
})

export type ErrorResponse = z.infer<typeof ErrorResponseSchema>
