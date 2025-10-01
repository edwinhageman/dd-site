import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useDeleteEvent } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    delete: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    eventService: mockService,
    getEventService: mockGetService,
  }
})

describe('useDeleteEvent tests', () => {
  let queryClient: QueryClient
  const eventService = getEventService()

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

  it('calls eventService.delete with eventId', async () => {
    const eventId = 42

    const { result } = withComponentLifecycle(useDeleteEvent, { plugins: [vueQueryPluginFactory] })

    await result.mutateAsync({ eventId })

    await flushPromises()

    expect(eventService.delete).toHaveBeenCalledTimes(1)
    expect(eventService.delete).toHaveBeenCalledWith(eventId)
  })

  it('invalidates expected queries on success', async () => {
    const eventId = 7

    const { result } = withComponentLifecycle(useDeleteEvent, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ eventId })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['events'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['event', eventId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(eventService.delete).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useDeleteEvent, { plugins: [VueQueryPlugin] })
    result.mutate({ eventId: 1 })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
