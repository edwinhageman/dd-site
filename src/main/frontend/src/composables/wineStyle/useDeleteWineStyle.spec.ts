import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useDeleteWineStyle } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    delete: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineStyleService: mockService,
    getWineStyleService: mockGetService,
  }
})

describe('useDeleteWineStyle tests', () => {
  let queryClient: QueryClient
  const wineStyleService = getWineStyleService()

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

  it('calls wineStyleService.delete with wineStyleId', async () => {
    const wineStyleId = 42

    const { result } = withComponentLifecycle(useDeleteWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    await result.mutateAsync({ wineStyleId })

    await flushPromises()

    expect(wineStyleService.delete).toHaveBeenCalledTimes(1)
    expect(wineStyleService.delete).toHaveBeenCalledWith(wineStyleId)
  })

  it('invalidates expected queries on success', async () => {
    const wineStyleId = 7

    const { result } = withComponentLifecycle(useDeleteWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ wineStyleId })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wineStyles'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['wineStyle', wineStyleId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineStyleService.delete).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useDeleteWineStyle, { plugins: [VueQueryPlugin] })
    result.mutate({ wineStyleId: 1 })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
