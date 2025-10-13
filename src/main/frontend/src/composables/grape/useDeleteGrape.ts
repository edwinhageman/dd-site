import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import { ApiError } from '@/api'

export type DeleteGrapeParams = {
  grapeId: number
}

export function useDeleteGrape() {
  const qc = useQueryClient()
  const service = getGrapeService()
  return useMutation<void, ApiError, DeleteGrapeParams>({
    mutationFn: async ({ grapeId }: DeleteGrapeParams) => {
      return service.delete(grapeId)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['grapes'] })
      qc.invalidateQueries({ queryKey: ['grape', variables.grapeId] })
    },
  })
}
