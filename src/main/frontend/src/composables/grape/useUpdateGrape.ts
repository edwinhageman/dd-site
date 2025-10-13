import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse, GrapeUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type UpdateGrapeParams = {
  grapeId: number
  payload: GrapeUpsertRequest
}

export function useUpdateGrape() {
  const qc = useQueryClient()
  const service = getGrapeService()
  return useMutation<GrapeResponse, ApiError, UpdateGrapeParams>({
    mutationFn: async ({ grapeId, payload }: UpdateGrapeParams) => {
      return service.update(grapeId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['grapes'] })
      qc.invalidateQueries({ queryKey: ['grape', variables.grapeId] })
    },
  })
}
