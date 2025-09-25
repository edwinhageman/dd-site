import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { courseApi } from '@/api'

export type DeleteCourseParams = {
  courseId: number
}

export function useDeleteCourse() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async ({ courseId }: DeleteCourseParams) => {
      const response = await courseApi.deleteCourse(courseId)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
      qc.invalidateQueries({ queryKey: ['course', variables.courseId] })
    },
  })
}
