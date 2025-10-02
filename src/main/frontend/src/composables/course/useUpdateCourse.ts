import { useMutation, useQueryClient } from '@tanstack/vue-query'
import type { CourseResponse, CourseUpsertRequest } from '@/generated/api'
import { getCourseService } from '@/service'
import { ApiError } from '@/api'

export type UpdateCourseParams = {
  courseId: number
  payload: CourseUpsertRequest
}

export function useUpdateCourse() {
  const qc = useQueryClient()
  const service = getCourseService()
  return useMutation<CourseResponse, ApiError, UpdateCourseParams>({
    mutationFn: async ({ courseId, payload }: UpdateCourseParams) => {
      return service.update(courseId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
      qc.invalidateQueries({ queryKey: ['course', variables.courseId] })
    },
  })
}
