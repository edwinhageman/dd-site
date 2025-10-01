import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useGetEventById } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    findById: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    eventService: mockService,
    getEventService: mockGetService,
  }
})

describe('useGetEventById tests', () => {
  const eventService = getEventService()
  const response: EventResponse = {
    id: 1,
    date: '2025-01-01',
    host: 'Host1',
    location: 'Location1',
  }

  const vueQueryPluginFactory = (app: App) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false }, //disable retries so we can reliably test errors
      },
    })
    app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls eventService.findById with eventId and return event', async () => {
    const eventId = 1

    vi.mocked(eventService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetEventById, args: [eventId] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(eventService.findById).toHaveBeenCalledTimes(1)
    expect(eventService.findById).toHaveBeenCalledWith(eventId)
    expect(result.data.value).toStrictEqual(response)
  })

  it('does not query when eventId is invalid/undefined', async () => {
    const invalidId = ref<number | null>(null)

    const { result } = withComponentLifecycle(
      { use: useGetEventById, args: [invalidId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(eventService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()
  })

  it('re-queries when reactive eventId changes', async () => {
    const eventId = ref<number | null>(null)
    vi.mocked(eventService.findById).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(
      { use: useGetEventById, args: [eventId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(eventService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    eventId.value = 11
    await flushPromises()

    expect(eventService.findById).toHaveBeenCalledTimes(1)
    expect(eventService.findById).toHaveBeenCalledWith(11)
    expect(result.data.value).toStrictEqual(response)
  })

  it('normalizes bad params', async () => {
    const badId = ref<any>('x')

    const { result } = withComponentLifecycle(
      { use: useGetEventById, args: [badId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(eventService.findById).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    badId.value = 9
    vi.mocked(eventService.findById).mockResolvedValueOnce(response)
    await flushPromises()

    expect(eventService.findById).toHaveBeenCalledTimes(1)
    expect(eventService.findById).toHaveBeenCalledWith(9)
    expect(result.data.value).toStrictEqual(response)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(eventService.findById).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useGetEventById, args: [1] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
