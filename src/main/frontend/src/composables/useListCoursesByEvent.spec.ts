import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useListCoursesByEvent } from './useListCoursesByEvent'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getCourseService } from '@/service'
import type { CourseResponse, Pageable, PagedModelCourseResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    listByEvent: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    courseService: mockService,
    getCourseService: mockGetService,
  }
})

describe('useListCoursesByEvent tests', () => {
  let queryClient: QueryClient
  const courseService = getCourseService()
  let testCourse1: CourseResponse = {
    id: 1,
    event: {
      id: 1,
      date: '2025-01-01',
      host: 'Host1',
      location: 'Location1',
    },
    courseNo: 1,
    cook: 'Cook1',
    dish: {
      id: 11,
      name: 'Dish1',
      mainIngredient: 'Ingredient1',
    },
  }
  let testCourse2: CourseResponse = {
    id: 2,
    event: {
      id: 2,
      date: '2025-02-01',
      host: 'Host2',
      location: 'Location2',
    },
    courseNo: 2,
    cook: 'Cook2',
    dish: {
      id: 22,
      name: 'Dish2',
      mainIngredient: 'Ingredient2',
    },
  }
  const page: PagedModelCourseResponse = {
    content: [testCourse1, testCourse2],
    page: {
      number: 1,
      size: 10,
      totalElements: 2,
      totalPages: 1,
    },
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

  it('calls courseService.listByEvent with eventId and page params and return course page', async () => {
    const eventId = 1
    const pageParams = { size: 10, page: 1 }

    vi.mocked(courseService.listByEvent).mockResolvedValueOnce(page)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [eventId, pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(courseService.listByEvent).toHaveBeenCalledTimes(1)
    expect(courseService.listByEvent).toHaveBeenCalledWith(eventId, pageParams)
    expect(result.data.value).toStrictEqual(page)
  })

  it('does not query when eventId is invalid/undefined', async () => {
    const invalidId = ref<number | null>(null)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [invalidId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(courseService.listByEvent).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()
  })

  it('re-queries when reactive eventId changes', async () => {
    const eventId = ref<number | null>(null)
    vi.mocked(courseService.listByEvent).mockResolvedValueOnce(page)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [eventId] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(courseService.listByEvent).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    eventId.value = 11
    await flushPromises()

    expect(courseService.listByEvent).toHaveBeenCalledTimes(1)
    expect(courseService.listByEvent).toHaveBeenCalledWith(11, {})
    expect(result.data.value).toStrictEqual(page)
  })

  it('re-queries when reactive page params change', async () => {
    const eventId = ref<number>(7)
    const pageParams = ref<Pageable | null>({ page: 2, size: 5 })
    const page1: PagedModelCourseResponse = {
      content: [testCourse1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelCourseResponse = {
      content: [testCourse2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(courseService.listByEvent).mockResolvedValueOnce(page1).mockResolvedValueOnce(page2)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [eventId, pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(courseService.listByEvent).toHaveBeenCalledTimes(1)
    expect(courseService.listByEvent).toHaveBeenCalledWith(7, { page: 2, size: 5 })
    expect(result.data.value).toStrictEqual(page1)

    pageParams.value = { page: 3, size: 5 }
    await flushPromises()

    expect(courseService.listByEvent).toHaveBeenCalledTimes(2)
    expect(courseService.listByEvent).toHaveBeenCalledWith(7, { page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('normalizes bad params', async () => {
    const badId = ref<any>('x')
    const badPageable = ref<any>(null)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [badId, badPageable] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(courseService.listByEvent).not.toHaveBeenCalled()
    expect(result.data.value).toBeUndefined()

    badId.value = 9
    vi.mocked(courseService.listByEvent).mockResolvedValueOnce(page)
    await flushPromises()

    expect(courseService.listByEvent).toHaveBeenCalledTimes(1)
    expect(courseService.listByEvent).toHaveBeenCalledWith(9, {})
    expect(result.data.value).toStrictEqual(page)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(courseService.listByEvent).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useListCoursesByEvent, args: [1] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
