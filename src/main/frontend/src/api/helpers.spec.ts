import { describe, expect, it, vi } from 'vitest'
import { ApiError } from '@/api/error.ts'
import { toErrorResponse, unwrapRequest } from '@/api/helpers.ts'

describe('unwrapRequest tests', async () => {
  const fn = vi.fn()

  it('should call the wrapped function and return the data property', async () => {
    fn.mockResolvedValue({ data: 'value' })

    const result = await unwrapRequest(fn)

    expect(fn).toHaveBeenCalledTimes(1)
    expect(result).toBe('value')
  })

  it('when the wrapped function errors should throw and instance of ApiError with an error response object', async () => {
    const error = new Error('Error')
    fn.mockRejectedValue(error)

    await expect(unwrapRequest(fn)).rejects.toBeInstanceOf(ApiError)
    await expect(unwrapRequest(fn)).rejects.toMatchObject(new ApiError(toErrorResponse(error)))
  })
})
