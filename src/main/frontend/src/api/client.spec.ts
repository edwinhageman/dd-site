import { describe, expect, it } from 'vitest'

describe('createApiController tests', () => {
  it('returns singleton', async () => {
    const { createApiControllers } = await import('@/api/client')

    const first = createApiControllers()
    const second = createApiControllers()

    expect(second).toBe(first)
  })
})
