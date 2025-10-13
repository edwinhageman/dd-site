import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useCreateWineStyle } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse, WineStyleUpsertRequest } from '@/generated/api'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    create: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineStyleService: mockService,
    getWineStyleService: mockGetService,
  }
})

describe('useCreateWineStyle tests', () => {
  let queryClient: QueryClient
  const wineStyleService = getWineStyleService()
  const response: WineStyleResponse = {
    id: 1,
    name: 'WineStyle1',
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
    return app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls wineStyleService.create with payload and return created ', async () => {
    vi.mocked(wineStyleService.create).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useCreateWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    const mutateResult = await result.mutateAsync({ payload })

    await flushPromises()

    expect(wineStyleService.create).toHaveBeenCalledTimes(1)
    expect(wineStyleService.create).toHaveBeenCalledWith(payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const { result } = withComponentLifecycle(useCreateWineStyle, {
      plugins: [vueQueryPluginFactory],
    })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['wineStyles'] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(wineStyleService.create).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useCreateWineStyle, { plugins: [VueQueryPlugin] })
    result.mutate({ payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
