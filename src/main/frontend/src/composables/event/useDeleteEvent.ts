import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import { ApiError } from '@/api'

export type DeleteEventParams = {
  eventId: number
}

export function useDeleteEvent() {
  const qc = useQueryClient()
  const service = getEventService()
  return useMutation<void, ApiError, DeleteEventParams>({
    mutationFn: async ({ eventId }: DeleteEventParams) => {
      return service.delete(eventId)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.eventId] })
    },
  })
}
