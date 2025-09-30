import { courseApi } from '@/api'
import type { CourseControllerApi, CourseUpsertRequest, Pageable } from '@/generated/api'

export class CourseService {
  private api: CourseControllerApi

  constructor() {
    this.api = courseApi
  }

  async findById(id: number) {
    const response = await this.api.getCourseById(id)
    return response.data
  }

  async listAll(pageParams: Pageable = {}) {
    const response = await this.api.listCourses(pageParams)
    return response.data
  }

  async listByEvent(eventId: number, pageParams: Pageable = {}) {
    const response = await this.api.listCoursesByEvent(eventId, pageParams)
    return response.data
  }

  async create(eventId: number, payload: CourseUpsertRequest) {
    const response = await this.api.createCourse(eventId, payload)
    return response.data
  }

  async update(id: number, payload: CourseUpsertRequest) {
    const response = await this.api.updateCourse(id, payload)
    return response.data
  }

  async delete(id: number) {
    await this.api.deleteCourse(id)
  }
}

let instance: CourseService | null = null
export function getCourseService() {
  if (!instance) {
    instance = new CourseService()
  }
  return instance
}
