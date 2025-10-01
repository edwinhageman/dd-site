import axios, { type AxiosResponse } from 'axios'
import { ApiError, type ErrorResponse, ErrorResponseSchema } from '@/api/error.ts'

export function toErrorResponse(error: unknown): ErrorResponse {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data
    const result = ErrorResponseSchema.safeParse(data)
    if (result.success) {
      return result.data
    }
    return {
      type: 'about:blank',
      title: error.message,
      status: error.response?.status ?? 0,
      detail: String(error.response?.statusText ?? 'Request failed'),
      instance: '',
    }
  }
  return {
    type: 'about:blank',
    title: `Unexpected error`,
    status: 0,
    detail: error instanceof Error ? error.message : String(error),
    instance: '',
  }
}

export async function unwrapRequest<T>(fn: () => Promise<AxiosResponse<T>>): Promise<T> {
  try {
    const { data } = await fn()
    return data
  } catch (e) {
    throw new ApiError(toErrorResponse(e))
  }
}
