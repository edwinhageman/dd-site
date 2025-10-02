import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventResponse, EventUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type CreateEventParams = {
  payload: EventUpsertRequest
}
export function useCreateEvent() {
  const qc = useQueryClient()
  const service = getEventService()
  return useMutation<EventResponse, ApiError, CreateEventParams>({
    mutationFn: async ({ payload }: CreateEventParams) => {
      return service.create(payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}
