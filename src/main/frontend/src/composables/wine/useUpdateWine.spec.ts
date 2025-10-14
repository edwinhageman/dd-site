import type { App } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useUpdateWine } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { WineResponse, WineUpsertRequest } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    update: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineService: mockService,
    getWineService: mockGetService,
  }
})

describe('useUpdateWine tests', () => {
  let queryClient: QueryClient
  const wineService = getWineService()
  const response: WineResponse = {
    id: 1,
    name: 'wine1',
    styles: [],
    grapeComposition: [],
  }
  const payload: WineUpsertRequest = {
    name: 'Wine1',
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

  it('calls wineService.update with wineId and payload and return updated wine', async () => {
    const wineId = 1

    vi.mocked(wineService.update).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useUpdateWine, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ wineId, payload })

    await flushPromises()

    expect(wineService.update).toHaveBeenCalledTimes(1)
    expect(wineService.update).toHaveBeenCalledWith(wineId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const wineId = 1

    const { result } = withComponentLifecycle(useUpdateWine, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ wineId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wines'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['wine', wineId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineService.update).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useUpdateWine, { plugins: [VueQueryPlugin] })
    result.mutate({ wineId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
