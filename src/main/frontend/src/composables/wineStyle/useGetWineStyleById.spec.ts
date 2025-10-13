import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useGetWineStyleById } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    findById: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineStyleService: mockService,
    getWineStyleService: mockGetService,
  }
})

describe('useGetWineStyleById tests', () => {
  const wineStyleService = getWineStyleService()
  const response: WineStyleResponse = {
    id: 1,
    name: 'WineStyle1',
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

  it('calls wineStyleService.findById with wineStyleId and return wineStyle', async () => {
    const wineStyleId = 1

    vi.mocked(wineStyleService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetWineStyleById, args: [wineStyleId] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(wineStyleService.findById).toHaveBeenCalledTimes(1)
    expect(wineStyleService.findById).toHaveBeenCalledWith(wineStyleId)
    expect(result.data.value).toStrictEqual(response)
  })

  it('does not query when wineStyleId is invalid/undefined', async () => {
    const invalidId = ref<number | null>(null)

    const { result } = withComponentLifecycle(
      { use: useGetWineStyleById, args: [invalidId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(wineStyleService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()
  })

  it('re-queries when reactive wineStyleId changes', async () => {
    const wineStyleId = ref<number | null>(null)
    vi.mocked(wineStyleService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetWineStyleById, args: [wineStyleId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(wineStyleService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    wineStyleId.value = 11
    await flushPromises()

    expect(wineStyleService.findById).toHaveBeenCalledTimes(1)
    expect(wineStyleService.findById).toHaveBeenCalledWith(11)
    expect(result.data.value).toStrictEqual(response)
  })

  it('normalizes bad params', async () => {
    const badId = ref<any>('x')

    const { result } = withComponentLifecycle(
      { use: useGetWineStyleById, args: [badId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(wineStyleService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    badId.value = 9
    vi.mocked(wineStyleService.findById).mockResolvedValueOnce(response)
    await flushPromises()

    expect(wineStyleService.findById).toHaveBeenCalledTimes(1)
    expect(wineStyleService.findById).toHaveBeenCalledWith(9)
    expect(result.data.value).toStrictEqual(response)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineStyleService.findById).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useGetWineStyleById, args: [1] },
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
