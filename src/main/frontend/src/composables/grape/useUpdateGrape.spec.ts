import type { App } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useUpdateGrape } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse, GrapeUpsertRequest } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    update: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    grapeService: mockService,
    getGrapeService: mockGetService,
  }
})

describe('useUpdateGrape tests', () => {
  let queryClient: QueryClient
  const grapeService = getGrapeService()
  const response: GrapeResponse = {
    id: 1,
    name: 'grape1',
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
    app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls grapeService.update with grapeId and payload and return updated grape', async () => {
    const grapeId = 1

    vi.mocked(grapeService.update).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useUpdateGrape, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ grapeId, payload })

    await flushPromises()

    expect(grapeService.update).toHaveBeenCalledTimes(1)
    expect(grapeService.update).toHaveBeenCalledWith(grapeId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const grapeId = 1

    const { result } = withComponentLifecycle(useUpdateGrape, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ grapeId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['grapes'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['grape', grapeId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(grapeService.update).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useUpdateGrape, { plugins: [VueQueryPlugin] })
    result.mutate({ grapeId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
