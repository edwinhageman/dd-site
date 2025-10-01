import type { App } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useUpdateEvent } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse, EventUpsertRequest } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    update: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    eventService: mockService,
    getEventService: mockGetService,
  }
})

describe('useUpdateEvent tests', () => {
  let queryClient: QueryClient
  const eventService = getEventService()
  const response: EventResponse = {
    id: 1,
    date: '2025-01-01',
    host: 'Host1',
    location: 'Location1',
  }
  const payload: EventUpsertRequest = {
    date: '2025-01-01',
    host: 'Host1',
    location: 'Location1',
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

  it('calls eventService.update with eventId and payload and return updated event', async () => {
    const eventId = 1

    vi.mocked(eventService.update).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useUpdateEvent, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ eventId, payload })

    await flushPromises()

    expect(eventService.update).toHaveBeenCalledTimes(1)
    expect(eventService.update).toHaveBeenCalledWith(eventId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const eventId = 1

    const { result } = withComponentLifecycle(useUpdateEvent, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ eventId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['events'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['event', eventId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(eventService.update).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useUpdateEvent, { plugins: [VueQueryPlugin] })
    result.mutate({ eventId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
