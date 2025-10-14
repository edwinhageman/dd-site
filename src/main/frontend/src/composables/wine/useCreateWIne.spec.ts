import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useCreateWine } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { WineResponse, WineUpsertRequest } from '@/generated/api'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    create: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineService: mockService,
    getWineService: mockGetService,
  }
})

describe('useCreateWine tests', () => {
  let queryClient: QueryClient
  const wineService = getWineService()
  const response: WineResponse = {
    id: 1,
    name: 'Wine1',
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
    return app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls wineService.create with payload and return created ', async () => {
    vi.mocked(wineService.create).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useCreateWine, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ payload })

    await flushPromises()

    expect(wineService.create).toHaveBeenCalledTimes(1)
    expect(wineService.create).toHaveBeenCalledWith(payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const { result } = withComponentLifecycle(useCreateWine, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wines'] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineService.create).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useCreateWine, { plugins: [VueQueryPlugin] })
    result.mutate({ payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
