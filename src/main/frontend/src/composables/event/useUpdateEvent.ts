import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse, EventUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type UpdateEventParams = {
  eventId: number
  payload: EventUpsertRequest
}

export function useUpdateEvent() {
  const qc = useQueryClient()
  const service = getEventService()
  return useMutation<EventResponse, ApiError, UpdateEventParams>({
    mutationFn: async ({ eventId, payload }: UpdateEventParams) => {
      return service.update(eventId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.eventId] })
    },
  })
}
