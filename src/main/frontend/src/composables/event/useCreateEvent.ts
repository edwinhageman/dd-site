import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getEventService } from '@/service'
import type { EventUpsertRequest } from '@/generated/api'

export type CreateEventParams = {
  payload: EventUpsertRequest
}
export function useCreateEvent() {
  const qc = useQueryClient()
  const service = getEventService()
  return useMutation({
    mutationFn: async ({ payload }: CreateEventParams) => {
      return service.create(payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}
