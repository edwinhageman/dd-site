import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useDeleteGrape } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    delete: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    grapeService: mockService,
    getGrapeService: mockGetService,
  }
})

describe('useDeleteGrape tests', () => {
  let queryClient: QueryClient
  const grapeService = getGrapeService()

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

  it('calls grapeService.delete with grapeId', async () => {
    const grapeId = 42

    const { result } = withComponentLifecycle(useDeleteGrape, { plugins: [vueQueryPluginFactory] })

    await result.mutateAsync({ grapeId })

    await flushPromises()

    expect(grapeService.delete).toHaveBeenCalledTimes(1)
    expect(grapeService.delete).toHaveBeenCalledWith(grapeId)
  })

  it('invalidates expected queries on success', async () => {
    const grapeId = 7

    const { result } = withComponentLifecycle(useDeleteGrape, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ grapeId })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['grapes'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['grape', grapeId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(grapeService.delete).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useDeleteGrape, { plugins: [VueQueryPlugin] })
    result.mutate({ grapeId: 1 })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
