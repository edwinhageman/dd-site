import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { eventApi } from '@/api'
import type { EventUpsertRequest } from '@/generated/api'

export type UpdateEventParams = {
  id: number
  request: EventUpsertRequest
}

export function useUpdateEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: async ({ id, request }: UpdateEventParams) => {
      const response = await eventApi.updateEvent(id, request)
      return response.data
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['events'] })
      qc.invalidateQueries({ queryKey: ['event', variables.id] })
    },
  })
}
