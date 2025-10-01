import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventUpsertRequest } from '@/generated/api'

export type UpdateEventParams = {
  eventId: number
  payload: EventUpsertRequest
}

export function useUpdateEvent() {
  const qc = useQueryClient()
  const service = getEventService()
  return useMutation({
    mutationFn: async ({ eventId, payload }: UpdateEventParams) => {
      return service.update(eventId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.eventId] })
    },
  })
}
