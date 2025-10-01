import { beforeEach, describe, expect, it, vi } from 'vitest'
import { EventService } from '@/service'
import { mockAxiosResponse } from '@/test/test-utils.ts'
import type {
  EventControllerApi,
  EventResponse,
  EventUpsertRequest,
  PagedModelEventResponse,
} from '@/generated/api'

describe('EventService tests', () => {
  let apiMock: EventControllerApi
  let service: EventService
  let testEvent1: EventResponse = {
    id: 1,
    date: '2025-01-01',
    host: 'Host1',
    location: 'Location1',
  }
  let testEvent2: EventResponse = {
    id: 2,
    date: '2025-02-02',
    host: 'Host2',
    location: 'Location2',
  }

  beforeEach(() => {
    apiMock = {
      getEventById: vi.fn(),
      listEvents: vi.fn(),
      createEvent: vi.fn(),
      updateEvent: vi.fn(),
      deleteEvent: vi.fn(),
    } as unknown as EventControllerApi

    service = new EventService(apiMock)
  })

  it('findById should call api.getEventById and return data', async () => {
    const eventId = 1
    vi.mocked(apiMock.getEventById).mockResolvedValue(mockAxiosResponse(testEvent1))

    const result = await service.findById(eventId)

    expect(apiMock.getEventById).toHaveBeenCalledWith(eventId)
    expect(result).toBe(testEvent1)
  })

  it('listAll should call api.listEvents and pass page params and return paged data', async () => {
    const pageParams = { size: 10, page: 1 }
    const page: PagedModelEventResponse = {
      content: [testEvent1, testEvent2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listEvents).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll(pageParams)

    expect(apiMock.listEvents).toHaveBeenCalledWith(pageParams)
    expect(result).toBe(page)
  })

  it('listAll should call api.listEvents with default page params', async () => {
    const page: PagedModelEventResponse = {
      content: [testEvent1, testEvent2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(apiMock.listEvents).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll()

    expect(apiMock.listEvents).toHaveBeenCalledWith({})
    expect(result).toBe(page)
  })

  it('create should call api.createEvent and return created event', async () => {
    const payload: EventUpsertRequest = {
      date: '2025-01-01',
      host: 'Host1',
      location: 'Location1',
    }
    vi.mocked(apiMock.createEvent).mockResolvedValue(mockAxiosResponse(testEvent1))

    const result = await service.create(payload)

    expect(apiMock.createEvent).toHaveBeenCalledWith(payload)
    expect(result).toBe(testEvent1)
  })

  it('update should call api.updateEvent and return update event', async () => {
    const eventId = 1
    const payload: EventUpsertRequest = {
      date: '2025-01-01',
      host: 'Host1',
      location: 'Location1',
    }
    vi.mocked(apiMock.updateEvent).mockResolvedValue(mockAxiosResponse(testEvent1))

    const result = await service.update(eventId, payload)

    expect(apiMock.updateEvent).toHaveBeenCalledWith(eventId, payload)
    expect(result).toBe(testEvent1)
  })

  it('delete should call api.deleteEvent', async () => {
    const eventId = 1
    vi.mocked(apiMock.deleteEvent).mockResolvedValue(mockAxiosResponse(undefined))

    await service.delete(eventId)

    expect(apiMock.deleteEvent).toHaveBeenCalledWith(eventId)
  })

  it('propagates api error', async () => {
    vi.mocked(apiMock.getEventById).mockRejectedValue(new Error('API error'))
    await expect(service.findById(1)).rejects.toThrow('API error')
  })

  it('factory method returns singleton', async () => {
    const { getEventService } = await import('@/service/event.service')

    const first = getEventService()
    const second = getEventService()

    expect(second).toBe(first)
  })
})
