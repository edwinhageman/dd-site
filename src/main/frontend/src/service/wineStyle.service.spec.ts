import { beforeEach, describe, expect, it, vi } from 'vitest'
import { WineStyleService } from '@/service'
import { mockAxiosResponse } from '@/test/test-utils.ts'
import {
  type PagedModelWineStyleResponse,
  type WineStyleControllerApi,
  type WineStyleResponse,
  type WineStyleUpsertRequest,
} from '@/generated/api'

describe('WineStyleService tests', () => {
  let apiMock: WineStyleControllerApi
  let service: WineStyleService
  let testWineStyle1: WineStyleResponse = {
    id: 1,
    name: 'WineStyle1',
  }
  let testWineStyle2: WineStyleResponse = {
    id: 2,
    name: 'WineStyle2',
  }

  beforeEach(() => {
    apiMock = {
      getWineStyleById: vi.fn(),
      listWineStyles: vi.fn(),
      createWineStyle: vi.fn(),
      updateWineStyle: vi.fn(),
      deleteWineStyle: vi.fn(),
    } as unknown as WineStyleControllerApi

    service = new WineStyleService(apiMock)
  })

  it('findById should call api.getWineStyleById and return data', async () => {
    const wineStyleId = 1
    vi.mocked(apiMock.getWineStyleById).mockResolvedValue(mockAxiosResponse(testWineStyle1))

    const result = await service.findById(wineStyleId)

    expect(apiMock.getWineStyleById).toHaveBeenCalledWith(wineStyleId)
    expect(result).toBe(testWineStyle1)
  })

  it('listAll should call api.listWineStyles and pass page params and return paged data', async () => {
    const pageParams = { size: 10, page: 1 }
    const page: PagedModelWineStyleResponse = {
      content: [testWineStyle1, testWineStyle2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listWineStyles).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll(pageParams)

    expect(apiMock.listWineStyles).toHaveBeenCalledWith(pageParams)
    expect(result).toBe(page)
  })

  it('listAll should call api.listWineStyles with default page params', async () => {
    const page: PagedModelWineStyleResponse = {
      content: [testWineStyle1, testWineStyle2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listWineStyles).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll()

    expect(apiMock.listWineStyles).toHaveBeenCalledWith({})
    expect(result).toBe(page)
  })

  it('create should call api.createWineStyle and return created wineStyle', async () => {
    const payload: WineStyleUpsertRequest = {
      name: 'WineStyle11',
    }
    vi.mocked(apiMock.createWineStyle).mockResolvedValue(mockAxiosResponse(testWineStyle1))

    const result = await service.create(payload)

    expect(apiMock.createWineStyle).toHaveBeenCalledWith(payload)
    expect(result).toBe(testWineStyle1)
  })

  it('update should call api.updateWineStyle and return update wineStyle', async () => {
    const wineStyleId = 1
    const payload: WineStyleUpsertRequest = {
      name: 'WineStyle3',
    }
    vi.mocked(apiMock.updateWineStyle).mockResolvedValue(mockAxiosResponse(testWineStyle1))

    const result = await service.update(wineStyleId, payload)

    expect(apiMock.updateWineStyle).toHaveBeenCalledWith(wineStyleId, payload)
    expect(result).toBe(testWineStyle1)
  })

  it('delete should call api.deleteWineStyle', async () => {
    const wineStyleId = 1
    vi.mocked(apiMock.deleteWineStyle).mockResolvedValue(mockAxiosResponse(undefined))

    await service.delete(wineStyleId)

    expect(apiMock.deleteWineStyle).toHaveBeenCalledWith(wineStyleId)
  })

  it('propagates api error', async () => {
    vi.mocked(apiMock.getWineStyleById).mockRejectedValue(new Error('API error'))
    await expect(service.findById(1)).rejects.toThrow('API error')
  })

  it('factory method returns singleton', async () => {
    const { getWineStyleService } = await import('@/service/wineStyle.service')

    const first = getWineStyleService()
    const second = getWineStyleService()

    expect(second).toBe(first)
  })
})
