import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useDeleteCourse } from './useDeleteCourse'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getCourseService } from '@/service'
import type { App } from 'vue'

vi.mock('@/service', () => {
  const mockService = {
    delete: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    courseService: mockService,
    getCourseService: mockGetService,
  }
})

describe('useDeleteCourse tests', () => {
  let queryClient: QueryClient
  const courseService = getCourseService()

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

  it('calls courseService.delete with courseId', async () => {
    const courseId = 42

    const { result } = withComponentLifecycle(useDeleteCourse, { plugins: [vueQueryPluginFactory] })

    await result.mutateAsync({ courseId })

    await flushPromises()

    expect(courseService.delete).toHaveBeenCalledTimes(1)
    expect(courseService.delete).toHaveBeenCalledWith(courseId)
  })

  it('invalidates expected queries on success', async () => {
    const courseId = 7

    const { result } = withComponentLifecycle(useDeleteCourse, { plugins: [vueQueryPluginFactory] })

    const spy = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ courseId })
    await flushPromises()

    expect(spy).toHaveBeenCalledWith({ queryKey: ['courses'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['coursesByEvent'] })
    expect(spy).toHaveBeenCalledWith({ queryKey: ['course', courseId] })
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')
    vi.mocked(courseService.delete).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(useDeleteCourse, { plugins: [VueQueryPlugin] })
    result.mutate({ courseId: 1 })

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
