import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { eventApi } from '@/api'
import type { EventUpsertRequest } from '@/generated/api'

export function useCreateEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async (request: EventUpsertRequest) => {
      const response = await eventApi.createEvent(request)
      return response.data
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}
