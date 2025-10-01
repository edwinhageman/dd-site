import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useCreateCourse } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getCourseService } from '@/service'
import type { CourseResponse, CourseUpsertRequest } from '@/generated/api'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    create: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    courseService: mockService,
    getCourseService: mockGetService,
  }
})

describe('useCreateCourse tests', () => {
  let queryClient: QueryClient
  const courseService = getCourseService()
  const response: CourseResponse = {
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
  const payload: CourseUpsertRequest = {
    courseNo: 1,
    cook: 'Cook1',
    dish: {
      name: 'Dish1',
      mainIngredient: 'Ingredient1',
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

  it('calls courseService.create with eventId and payload and return created course', async () => {
    const eventId = 1

    vi.mocked(courseService.create).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useCreateCourse, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ eventId, payload })

    await flushPromises()

    expect(courseService.create).toHaveBeenCalledTimes(1)
    expect(courseService.create).toHaveBeenCalledWith(eventId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const eventId = 1

    const { result } = withComponentLifecycle(useCreateCourse, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ eventId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['courses'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['coursesByEvent'] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(courseService.create).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useCreateCourse, { plugins: [VueQueryPlugin] })
    result.mutate({ eventId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
