import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useCreateGrape } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse, GrapeUpsertRequest } from '@/generated/api'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    create: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    grapeService: mockService,
    getGrapeService: mockGetService,
  }
})

describe('useCreateGrape tests', () => {
  let queryClient: QueryClient
  const grapeService = getGrapeService()
  const response: GrapeResponse = {
    id: 1,
    name: 'Grape1',
  }
  const payload: GrapeUpsertRequest = {
    name: 'Grape1',
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

  it('calls grapeService.create with payload and return created ', async () => {
    vi.mocked(grapeService.create).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useCreateGrape, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ payload })

    await flushPromises()

    expect(grapeService.create).toHaveBeenCalledTimes(1)
    expect(grapeService.create).toHaveBeenCalledWith(payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const { result } = withComponentLifecycle(useCreateGrape, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['grapes'] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(grapeService.create).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useCreateGrape, { plugins: [VueQueryPlugin] })
    result.mutate({ payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
