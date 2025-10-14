import { type App, ref } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { useListWines } from '@/composables'
import { withComponentLifecycle } from '@/test/test-utils.ts'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { Pageable, PagedModelWineResponse, WineResponse } from '@/generated/api'

vi.mock('@/service', () => {
  const mockService = {
    listAll: vi.fn(),
  }
  const mockGetService = vi.fn(() => mockService)
  return {
    wineService: mockService,
    getWineService: mockGetService,
  }
})

describe('useListWines tests', () => {
  const wineService = getWineService()
  let testWine1: WineResponse = {
    id: 1,
    name: 'Wine1',
    styles: [],
    grapeComposition: [],
  }
  let testWine2: WineResponse = {
    id: 2,
    name: 'Wine2',
    styles: [],
    grapeComposition: [],
  }
  const page: PagedModelWineResponse = {
    content: [testWine1, testWine2],
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

  it('calls wineService.listAll with page params and returns page', async () => {
    const pageParams = { size: 10, page: 1 }

    vi.mocked(wineService.listAll).mockResolvedValueOnce(page)

    const { result } = withComponentLifecycle(
      { use: useListWines, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(wineService.listAll).toHaveBeenCalledTimes(1)
    expect(wineService.listAll).toHaveBeenCalledWith(pageParams)
    expect(result.data.value).toStrictEqual(page)
  })

  it('re-queries when reactive page params change', async () => {
    const pageParams = ref<Pageable | null>({ page: 2, size: 5 })
    const page1: PagedModelWineResponse = {
      content: [testWine1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelWineResponse = {
      content: [testWine2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(wineService.listAll).mockResolvedValueOnce(page1).mockResolvedValueOnce(page2)

    const { result } = withComponentLifecycle(
      { use: useListWines, args: [pageParams] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()
    expect(wineService.listAll).toHaveBeenCalledTimes(1)
    expect(wineService.listAll).toHaveBeenCalledWith({ page: 2, size: 5 })
    expect(result.data.value).toStrictEqual(page1)

    pageParams.value = { page: 3, size: 5 }
    await flushPromises()

    expect(wineService.listAll).toHaveBeenCalledTimes(2)
    expect(wineService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('normalizes bad params', async () => {
    const badPageable = ref<any>('invalid')

    const page1: PagedModelWineResponse = {
      content: [testWine1],
      page: { number: 2, size: 5, totalElements: 1, totalPages: 1 },
    }
    const page2: PagedModelWineResponse = {
      content: [testWine2],
      page: { number: 3, size: 5, totalElements: 1, totalPages: 1 },
    }

    vi.mocked(wineService.listAll).mockResolvedValueOnce(page1)

    const { result } = withComponentLifecycle(
      { use: useListWines, args: [badPageable] },
      {
        plugins: [vueQueryPluginFactory],
      },
    )

    await flushPromises()

    expect(wineService.listAll).toHaveBeenCalledTimes(1)
    expect(wineService.listAll).toHaveBeenCalledWith({})
    expect(result.data.value).toStrictEqual(page1)

    badPageable.value = { page: 3, size: 5 }
    vi.mocked(wineService.listAll).mockResolvedValueOnce(page2)

    await flushPromises()

    expect(wineService.listAll).toHaveBeenCalledTimes(2)
    expect(wineService.listAll).toHaveBeenCalledWith({ page: 3, size: 5 })
    expect(result.data.value).toStrictEqual(page2)
  })

  it('catches and exposes service errors', async () => {
    const error = new Error('service error')

    vi.mocked(wineService.listAll).mockRejectedValueOnce(error)

    const { result } = withComponentLifecycle(
      { use: useListWines, args: [] },
      { plugins: [vueQueryPluginFactory] },
    )

    await flushPromises()

    expect(result.isError.value).toBe(true)
    expect(result.error.value).toBe(error)
    expect(result.data.value).toBeUndefined()
  })
})
