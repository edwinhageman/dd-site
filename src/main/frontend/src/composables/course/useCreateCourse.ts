import { useMutation, useQueryClient } from '@tanstack/vue-query'
import type { CourseUpsertRequest } from '@/generated/api'
import { getCourseService } from '@/service'

export type CreateCourseParams = {
  eventId: number
  payload: CourseUpsertRequest
}

export function useCreateCourse() {
  const qc = useQueryClient()
  const service = getCourseService()
  return useMutation({
    mutationFn: async ({ eventId, payload }: CreateCourseParams) => {
      return service.create(eventId, payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['courses'] })
      qc.invalidateQueries({ queryKey: ['coursesByEvent'] })
    },
  })
}
