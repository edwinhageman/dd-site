import { beforeEach, describe, expect, it, vi } from 'vitest'
import { CourseService } from '@/service'
import { mockAxiosResponse } from '@/test/test-utils.ts'
import type { CourseResponse, CourseUpsertRequest, PagedModelCourseResponse } from '@/generated/api'
import { courseApi } from '@/api'

vi.mock('@/api', () => {
  const mockApi = {
    getCourseById: vi.fn(),
    listCourses: vi.fn(),
    listCoursesByEvent: vi.fn(),
    createCourse: vi.fn(),
    updateCourse: vi.fn(),
    deleteCourse: vi.fn(),
  }
  return { courseApi: mockApi }
})

describe('CourseService tests', () => {
  let service: CourseService
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

  beforeEach(() => {
    vi.clearAllMocks()
    service = new CourseService()
  })

  it('findById should call api.getCourseById and return data', async () => {
    const courseId = 1
    vi.mocked(courseApi.getCourseById).mockResolvedValue(mockAxiosResponse(testCourse1))

    const result = await service.findById(courseId)

    expect(courseApi.getCourseById).toHaveBeenCalledWith(courseId)
    expect(result).toBe(testCourse1)
  })

  it('listAll should call api.listCourses and pass page params and return paged data', async () => {
    const pageParams = { size: 10, page: 1 }
    const page: PagedModelCourseResponse = {
      content: [testCourse1, testCourse2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(courseApi.listCourses).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll(pageParams)

    expect(courseApi.listCourses).toHaveBeenCalledWith(pageParams)
    expect(result).toBe(page)
  })

  it('listAll should call api.listCourses with default page params', async () => {
    const page: PagedModelCourseResponse = {
      content: [testCourse1, testCourse2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(courseApi.listCourses).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listAll()

    expect(courseApi.listCourses).toHaveBeenCalledWith({})
    expect(result).toBe(page)
  })

  it('listByEvent should call api.listCoursesByEvent and pass page params and return paged data', async () => {
    const eventId = 1
    const pageParams = { size: 10, page: 1 }
    const page: PagedModelCourseResponse = {
      content: [testCourse1, testCourse2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(courseApi.listCoursesByEvent).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listByEvent(eventId, pageParams)

    expect(courseApi.listCoursesByEvent).toHaveBeenCalledWith(eventId, pageParams)
    expect(result).toBe(page)
  })

  it('listByEvent should call api.listCoursesByEvent with default page params', async () => {
    const eventId = 1
    const page: PagedModelCourseResponse = {
      content: [testCourse1, testCourse2],
      page: {
        number: 1,
        size: 10,
        totalElements: 2,
        totalPages: 1,
      },
    }
    vi.mocked(courseApi.listCoursesByEvent).mockResolvedValue(mockAxiosResponse(page))

    const result = await service.listByEvent(eventId)

    expect(courseApi.listCoursesByEvent).toHaveBeenCalledWith(eventId, {})
    expect(result).toBe(page)
  })

  it('create should call api.createCourse and return created course', async () => {
    const eventId = 1
    const payload: CourseUpsertRequest = {
      courseNo: 1,
      cook: 'Cook1',
      dish: {
        name: 'Dish1',
        mainIngredient: 'Ingredient1',
      },
    }
    vi.mocked(courseApi.createCourse).mockResolvedValue(mockAxiosResponse(testCourse1))

    const result = await service.create(eventId, payload)

    expect(courseApi.createCourse).toHaveBeenCalledWith(eventId, payload)
    expect(result).toBe(testCourse1)
  })

  it('update should call api.createCourse and return created course', async () => {
    const courseId = 1
    const payload: CourseUpsertRequest = {
      courseNo: 1,
      cook: 'Cook1',
      dish: {
        name: 'Dish1',
        mainIngredient: 'Ingredient1',
      },
    }
    vi.mocked(courseApi.updateCourse).mockResolvedValue(mockAxiosResponse(testCourse1))

    const result = await service.update(courseId, payload)

    expect(courseApi.updateCourse).toHaveBeenCalledWith(courseId, payload)
    expect(result).toBe(testCourse1)
  })

  it('delete should call api.deleteCourse', async () => {
    const courseId = 1
    vi.mocked(courseApi.deleteCourse).mockResolvedValue(mockAxiosResponse(undefined))

    await service.delete(courseId)

    expect(courseApi.deleteCourse).toHaveBeenCalledWith(courseId)
  })

  it('propagates api error', async () => {
    vi.mocked(courseApi.getCourseById).mockRejectedValue(new Error('API error'))
    await expect(service.findById(1)).rejects.toThrow('API error')
  })
})
