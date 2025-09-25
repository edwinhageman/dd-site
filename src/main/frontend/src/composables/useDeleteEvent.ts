import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { courseApi } from '@/api'

export type DeleteEventParams = {
  eventId: number
}

export function useDeleteEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async ({ eventId }: DeleteEventParams) => {
      const response = await courseApi.deleteCourse(eventId)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.eventId] })
    },
  })
}
