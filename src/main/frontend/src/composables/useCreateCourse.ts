import { useMutation, useQueryClient } from '@tanstack/vue-query'
import type { CourseUpsertRequest } from '@/generated/api'
import { courseApi } from '@/api'

export type CreateCourseParams = {
  eventId: number
  request: CourseUpsertRequest
}

export function useCreateCourse() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async ({ eventId, request }: CreateCourseParams) => {
      const response = await courseApi.createCourse(eventId, request)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
      qc.invalidateQueries({ queryKey: ['course', variables.eventId] })
    },
  })
}
