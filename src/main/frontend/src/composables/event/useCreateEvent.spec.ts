import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useCreateEvent } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse, EventUpsertRequest } from '@/generated/api'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    create: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    eventService: mockService,
    getEventService: mockGetService,
  }
})

describe('useCreateEvent tests', () => {
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
    return app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls eventService.create with payload and return created ', async () => {
    vi.mocked(eventService.create).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useCreateEvent, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ payload })

    await flushPromises()

    expect(eventService.create).toHaveBeenCalledTimes(1)
    expect(eventService.create).toHaveBeenCalledWith(payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const eventId = 1

    const { result } = withComponentLifecycle(useCreateEvent, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['events'] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(eventService.create).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useCreateEvent, { plugins: [VueQueryPlugin] })
    result.mutate({ payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
