import { createApiControllers, unwrapRequest } from '@/api'
import type { CourseControllerApi, CourseUpsertRequest, Pageable } from '@/generated/api'

export class CourseService {
  constructor(private readonly api: CourseControllerApi) {}

  async findById(id: number) {
    return unwrapRequest(() => this.api.getCourseById(id))
  }

  async listAll(pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listCourses(pageParams))
  }

  async listByEvent(eventId: number, pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listCoursesByEvent(eventId, pageParams))
  }

  async create(eventId: number, payload: CourseUpsertRequest) {
    return unwrapRequest(() => this.api.createCourse(eventId, payload))
  }

  async update(id: number, payload: CourseUpsertRequest) {
    return unwrapRequest(() => this.api.updateCourse(id, payload))
  }

  async delete(id: number) {
    unwrapRequest(() => this.api.deleteCourse(id))
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
