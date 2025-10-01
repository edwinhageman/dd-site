import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useListEvents } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse, Pageable, PagedModelEventResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    listAll: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    eventService: mockService,
    getEventService: mockGetService,
  }
})

describe('useListEvents tests', () => {
  const eventService = getEventService()
  let testEvent1: EventResponse = {
    id: 1,
    date: '2025-01-01',
    host: 'Host1',
    location: 'Location1',
  }
  let testEvent2: EventResponse = {
    id: 2,
    date: '2025-02-01',
    host: 'Host2',
    location: 'Location2',
  }
  const page: PagedModelEventResponse = {
    content: [testEvent1, testEvent2],
    page: {
      number: 1,
      size: 10,
      totalElements: 2,
      totalPages: 1,
    },
  }

  const vueQueryPluginFactory = (app: App) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false }, //disable retries so we can reliably test errors
      },
    })
    return app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls eventService.listAll with page params and return event page', async () => {
    const pageParams = { size: 10, page: 1 }

    vi.mocked(eventService.listAll).mockResolvedValueOnce(page)

    const { result } = withComponentLifecycle(
      { use: useListEvents, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(eventService.listAll).toHaveBeenCalledTimes(1)
    expect(eventService.listAll).toHaveBeenCalledWith(pageParams)
    expect(result.data.value).toStrictEqual(page)
  })

  it('re-queries when reactive page params change', async () => {
    const pageParams = ref<Pageable | null>({ page: 2, size: 5 })
    const page1: PagedModelEventResponse = {
      content: [testEvent1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelEventResponse = {
      content: [testEvent2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(eventService.listAll).mockResolvedValueOnce(page1).mockResolvedValueOnce(page2)

    const { result } = withComponentLifecycle(
      { use: useListEvents, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(eventService.listAll).toHaveBeenCalledTimes(1)
    expect(eventService.listAll).toHaveBeenCalledWith({ page: 2, size: 5 })
    expect(result.data.value).toStrictEqual(page1)

    pageParams.value = { page: 3, size: 5 }
    await flushPromises()

    expect(eventService.listAll).toHaveBeenCalledTimes(2)
    expect(eventService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('normalizes bad params', async () => {
    const badPageable = ref<any>('invalid')

    const page1: PagedModelEventResponse = {
      content: [testEvent1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelEventResponse = {
      content: [testEvent2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(eventService.listAll).mockResolvedValueOnce(page1)

    const { result } = withComponentLifecycle(
      { use: useListEvents, args: [badPageable] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(eventService.listAll).toHaveBeenCalledTimes(1)
    expect(eventService.listAll).toHaveBeenCalledWith({})
    expect(result.data.value).toStrictEqual(page1)

    badPageable.value = { page: 3, size: 5 }
    vi.mocked(eventService.listAll).mockResolvedValueOnce(page2)

    await flushPromises()

    expect(eventService.listAll).toHaveBeenCalledTimes(2)
    expect(eventService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')

    vi.mocked(eventService.listAll).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useListEvents, args: [] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
