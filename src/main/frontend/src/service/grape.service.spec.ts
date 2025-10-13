import { beforeEach, describe, expect, it, vi } from 'vitest'
import { GrapeService } from '@/service'
import { mockAxiosResponse } from '@/test/test-utils.ts'
import {
  type GrapeControllerApi,
  type GrapeResponse,
  type GrapeUpsertRequest,
  type PagedModelGrapeResponse,
} from '@/generated/api'

describe('GrapeService tests', () => {
  let apiMock: GrapeControllerApi
  let service: GrapeService
  let testGrape1: GrapeResponse = {
    id: 1,
    name: 'Grape1',
  }
  let testGrape2: GrapeResponse = {
    id: 2,
    name: 'Grape2',
  }

  beforeEach(() => {
    apiMock = {
      getGrapeById: vi.fn(),
      listGrapes: vi.fn(),
      createGrape: vi.fn(),
      updateGrape: vi.fn(),
      deleteGrape: vi.fn(),
    } as unknown as GrapeControllerApi

    service = new GrapeService(apiMock)
  })

  it('findById should call api.getGrapeById and return data', async () => {
    const grapeId = 1
    vi.mocked(apiMock.getGrapeById).mockResolvedValue(mockAxiosResponse(testGrape1))

    const result = await service.findById(grapeId)

    expect(apiMock.getGrapeById).toHaveBeenCalledWith(grapeId)
    expect(result).toBe(testGrape1)
  })

  it('listAll should call api.listGrapes and pass page params and return paged data', async () => {
    const pageParams = { size: 10, page: 1 }
    const page: PagedModelGrapeResponse = {
      content: [testGrape1, testGrape2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listGrapes).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll(pageParams)

    expect(apiMock.listGrapes).toHaveBeenCalledWith(pageParams)
    expect(result).toBe(page)
  })

  it('listAll should call api.listGrapes with default page params', async () => {
    const page: PagedModelGrapeResponse = {
      content: [testGrape1, testGrape2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listGrapes).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll()

    expect(apiMock.listGrapes).toHaveBeenCalledWith({})
    expect(result).toBe(page)
  })

  it('create should call api.createGrape and return created grape', async () => {
    const payload: GrapeUpsertRequest = {
      name: 'Grape11',
    }
    vi.mocked(apiMock.createGrape).mockResolvedValue(mockAxiosResponse(testGrape1))

    const result = await service.create(payload)

    expect(apiMock.createGrape).toHaveBeenCalledWith(payload)
    expect(result).toBe(testGrape1)
  })

  it('update should call api.updateGrape and return update grape', async () => {
    const grapeId = 1
    const payload: GrapeUpsertRequest = {
      name: 'Grape3',
    }
    vi.mocked(apiMock.updateGrape).mockResolvedValue(mockAxiosResponse(testGrape1))

    const result = await service.update(grapeId, payload)

    expect(apiMock.updateGrape).toHaveBeenCalledWith(grapeId, payload)
    expect(result).toBe(testGrape1)
  })

  it('delete should call api.deleteGrape', async () => {
    const grapeId = 1
    vi.mocked(apiMock.deleteGrape).mockResolvedValue(mockAxiosResponse(undefined))

    await service.delete(grapeId)

    expect(apiMock.deleteGrape).toHaveBeenCalledWith(grapeId)
  })

  it('propagates api error', async () => {
    vi.mocked(apiMock.getGrapeById).mockRejectedValue(new Error('API error'))
    await expect(service.findById(1)).rejects.toThrow('API error')
  })

  it('factory method returns singleton', async () => {
    const { getGrapeService } = await import('@/service/grape.service')

    const first = getGrapeService()
    const second = getGrapeService()

    expect(second).toBe(first)
  })
})
