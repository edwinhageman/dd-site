import { createApiControllers } from '@/api'
import type { CourseControllerApi, CourseUpsertRequest, Pageable } from '@/generated/api'

export class CourseService {
  constructor(private readonly api: CourseControllerApi) {}

  async findById(id: number) {
    const { data } = await this.api.getCourseById(id)
    return data
  }

  async listAll(pageParams: Pageable = {}) {
    const { data } = await this.api.listCourses(pageParams)
    return data
  }

  async listByEvent(eventId: number, pageParams: Pageable = {}) {
    const { data } = await this.api.listCoursesByEvent(eventId, pageParams)
    return data
  }

  async create(eventId: number, payload: CourseUpsertRequest) {
    const { data } = await this.api.createCourse(eventId, payload)
    return data
  }

  async update(id: number, payload: CourseUpsertRequest) {
    const { data } = await this.api.updateCourse(id, payload)
    return data
  }

  async delete(id: number) {
    await this.api.deleteCourse(id)
  }
}

let instance: CourseService | null = null
export function getCourseService() {
  if (!instance) {
    const { courseApi } = createApiControllers()
    instance = new CourseService(courseApi)
  }
  return instance
}
