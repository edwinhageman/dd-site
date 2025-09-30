import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getCourseService } from '@/service'

export type DeleteCourseParams = {
  courseId: number
}

export function useDeleteCourse() {
  const qc = useQueryClient()
  const service = getCourseService()
  return useMutation({
    mutationFn: async ({ courseId }: DeleteCourseParams) => {
      await service.delete(courseId)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
      qc.invalidateQueries({ queryKey: ['course', variables.courseId] })
    },
  })
}
