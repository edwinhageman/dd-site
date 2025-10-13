import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useListGrapes } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse, Pageable, PagedModelGrapeResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    listAll: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    grapeService: mockService,
    getGrapeService: mockGetService,
  }
})

describe('useListGrapes tests', () => {
  const grapeService = getGrapeService()
  let testGrape1: GrapeResponse = {
    id: 1,
    name: 'Grape1',
  }
  let testGrape2: GrapeResponse = {
    id: 2,
    name: 'Grape2',
  }
  const page: PagedModelGrapeResponse = {
    content: [testGrape1, testGrape2],
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

  it('calls grapeService.listAll with page params and return grape page', async () => {
    const pageParams = { size: 10, page: 1 }

    vi.mocked(grapeService.listAll).mockResolvedValueOnce(page)

    const { result } = withComponentLifecycle(
      { use: useListGrapes, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(grapeService.listAll).toHaveBeenCalledTimes(1)
    expect(grapeService.listAll).toHaveBeenCalledWith(pageParams)
    expect(result.data.value).toStrictEqual(page)
  })

  it('re-queries when reactive page params change', async () => {
    const pageParams = ref<Pageable | null>({ page: 2, size: 5 })
    const page1: PagedModelGrapeResponse = {
      content: [testGrape1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelGrapeResponse = {
      content: [testGrape2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(grapeService.listAll).mockResolvedValueOnce(page1).mockResolvedValueOnce(page2)

    const { result } = withComponentLifecycle(
      { use: useListGrapes, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(grapeService.listAll).toHaveBeenCalledTimes(1)
    expect(grapeService.listAll).toHaveBeenCalledWith({ page: 2, size: 5 })
    expect(result.data.value).toStrictEqual(page1)

    pageParams.value = { page: 3, size: 5 }
    await flushPromises()

    expect(grapeService.listAll).toHaveBeenCalledTimes(2)
    expect(grapeService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('normalizes bad params', async () => {
    const badPageable = ref<any>('invalid')

    const page1: PagedModelGrapeResponse = {
      content: [testGrape1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelGrapeResponse = {
      content: [testGrape2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(grapeService.listAll).mockResolvedValueOnce(page1)

    const { result } = withComponentLifecycle(
      { use: useListGrapes, args: [badPageable] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(grapeService.listAll).toHaveBeenCalledTimes(1)
    expect(grapeService.listAll).toHaveBeenCalledWith({})
    expect(result.data.value).toStrictEqual(page1)

    badPageable.value = { page: 3, size: 5 }
    vi.mocked(grapeService.listAll).mockResolvedValueOnce(page2)

    await flushPromises()

    expect(grapeService.listAll).toHaveBeenCalledTimes(2)
    expect(grapeService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')

    vi.mocked(grapeService.listAll).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useListGrapes, args: [] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
