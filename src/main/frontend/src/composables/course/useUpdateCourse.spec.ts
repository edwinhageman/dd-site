import type { App } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useUpdateCourse } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getCourseService } from '@/service'
import type { CourseResponse, CourseUpsertRequest } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    update: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    courseService: mockService,
    getCourseService: mockGetService,
  }
})

describe('useUpdateCourse tests', () => {
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
    app.use(VueQueryPlugin, { queryClient })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('calls courseService.update with courseId and payload and return updated course', async () => {
    const courseId = 1

    vi.mocked(courseService.update).mockResolvedValueOnce(response)

    const { result } = withComponentLifecycle(useUpdateCourse, { plugins: [vueQueryPluginFactory] })

    const mutateResult = await result.mutateAsync({ courseId, payload })

    await flushPromises()

    expect(courseService.update).toHaveBeenCalledTimes(1)
    expect(courseService.update).toHaveBeenCalledWith(courseId, payload)
    expect(mutateResult).toBe(response)
  })

  it('invalidates expected queries on success', async () => {
    const courseId = 1

    const { result } = withComponentLifecycle(useUpdateCourse, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ courseId, payload })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['courses'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['coursesByEvent'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['course', courseId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(courseService.update).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useUpdateCourse, { plugins: [VueQueryPlugin] })
    result.mutate({ courseId: 1, payload })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
