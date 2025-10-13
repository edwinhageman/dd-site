import type { App } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useUpdateWineStyle } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse, WineStyleUpsertRequest } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    update: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineStyleService: mockService,
    getWineStyleService: mockGetService,
  }
})

describe('useUpdateWineStyle tests', () => {
  let queryClient: QueryClient
  const wineStyleService = getWineStyleService()
  const response: WineStyleResponse = {
    id: 1,
    name: 'wineStyle1',
  }
  const payload: WineStyleUpsertRequest = {
    name: 'WineStyle1',
  }

  const vueQueryPluginFactory = (app: App) => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false }, //disable retries so we can reliably test errors
      },
    })
    app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls wineStyleService.update with wineStyleId and payload and return updated wineStyle', async () => {
    const wineStyleId = 1

    vi.mocked(wineStyleService.update).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useUpdateWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    const mutateResult = await result.mutateAsync({ wineStyleId, payload })

    await flushPromises()

    expect(wineStyleService.update).toHaveBeenCalledTimes(1)
    expect(wineStyleService.update).toHaveBeenCalledWith(wineStyleId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const wineStyleId = 1

    const { result } = withComponentLifecycle(useUpdateWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ wineStyleId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wineStyles'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['wineStyle', wineStyleId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineStyleService.update).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useUpdateWineStyle, { plugins: [VueQueryPlugin] })
    result.mutate({ wineStyleId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
