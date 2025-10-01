import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { createApiControllers } from '@/api'
import type { EventUpsertRequest } from '@/generated/api'

export type CreateEventParams = {
  request: EventUpsertRequest
}
export function useCreateEvent() {
  const qc = useQueryClient()
  const { eventApi } = createApiControllers()
  return useMutation({
    mutationFn: async ({ request }: CreateEventParams) => {
      const response = await eventApi.createEvent(request)
      return response.data
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}
