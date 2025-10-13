import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useGetGrapeById } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    findById: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    grapeService: mockService,
    getGrapeService: mockGetService,
  }
})

describe('useGetGrapeById tests', () => {
  const grapeService = getGrapeService()
  const response: GrapeResponse = {
    id: 1,
    name: 'Grape1',
  }

  const vueQueryPluginFactory = (app: App) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false }, //disable retries so we can reliably test errors
      },
    })
    app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls grapeService.findById with grapeId and return grape', async () => {
    const grapeId = 1

    vi.mocked(grapeService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetGrapeById, args: [grapeId] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(grapeService.findById).toHaveBeenCalledTimes(1)
    expect(grapeService.findById).toHaveBeenCalledWith(grapeId)
    expect(result.data.value).toStrictEqual(response)
  })

  it('does not query when grapeId is invalid/undefined', async () => {
    const invalidId = ref<number | null>(null)

    const { result } = withComponentLifecycle(
      { use: useGetGrapeById, args: [invalidId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(grapeService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()
  })

  it('re-queries when reactive grapeId changes', async () => {
    const grapeId = ref<number | null>(null)
    vi.mocked(grapeService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetGrapeById, args: [grapeId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(grapeService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    grapeId.value = 11
    await flushPromises()

    expect(grapeService.findById).toHaveBeenCalledTimes(1)
    expect(grapeService.findById).toHaveBeenCalledWith(11)
    expect(result.data.value).toStrictEqual(response)
  })

  it('normalizes bad params', async () => {
    const badId = ref<any>('x')

    const { result } = withComponentLifecycle(
      { use: useGetGrapeById, args: [badId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(grapeService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    badId.value = 9
    vi.mocked(grapeService.findById).mockResolvedValueOnce(response)
    await flushPromises()

    expect(grapeService.findById).toHaveBeenCalledTimes(1)
    expect(grapeService.findById).toHaveBeenCalledWith(9)
    expect(result.data.value).toStrictEqual(response)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(grapeService.findById).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useGetGrapeById, args: [1] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
