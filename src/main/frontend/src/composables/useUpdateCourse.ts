import { useMutation, useQueryClient } from '@tanstack/vue-query'
import type { CourseUpsertRequest } from '@/generated/api'
import { courseApi } from '@/api'

export type UpdateCourseParams = {
  courseId: number
  request: CourseUpsertRequest
}

export function useUpdateCourse() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async ({ courseId, request }: UpdateCourseParams) => {
      const response = await courseApi.updateCourse(courseId, request)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
      qc.invalidateQueries({ queryKey: ['course', variables.courseId] })
    },
  })
}
