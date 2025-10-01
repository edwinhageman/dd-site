import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { createApiControllers } from '@/api'

export type DeleteEventParams = {
  eventId: number
}

export function useDeleteEvent() {
  const qc = useQueryClient()
  const { eventApi } = createApiControllers()
  return useMutation({
    mutationFn: async ({ eventId }: DeleteEventParams) => {
      const response = await eventApi.deleteEvent(eventId)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.eventId] })
    },
  })
}
