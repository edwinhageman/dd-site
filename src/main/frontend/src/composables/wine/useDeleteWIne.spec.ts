import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useDeleteWine } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    delete: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineService: mockService,
    getWineService: mockGetService,
  }
})

describe('useDeleteWine tests', () => {
  let queryClient: QueryClient
  const wineService = getWineService()

  const vueQueryPluginFactory = (app: App) => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false }, //disable retries so we can reliably test errors
      },
    })
    return app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls wineService.delete with wineId', async () => {
    const wineId = 42

    const { result } = withComponentLifecycle(useDeleteWine, { plugins: [vueQueryPluginFactory] })

    await result.mutateAsync({ wineId })

    await flushPromises()

    expect(wineService.delete).toHaveBeenCalledTimes(1)
    expect(wineService.delete).toHaveBeenCalledWith(wineId)
  })

  it('invalidates expected queries on success', async () => {
    const wineId = 7

    const { result } = withComponentLifecycle(useDeleteWine, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ wineId })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wines'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['wine', wineId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineService.delete).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useDeleteWine, { plugins: [VueQueryPlugin] })
    result.mutate({ wineId: 1 })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
