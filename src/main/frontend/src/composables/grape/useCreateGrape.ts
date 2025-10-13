import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse, GrapeUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type CreateGrapeParams = {
  payload: GrapeUpsertRequest
}
export function useCreateGrape() {
  const qc = useQueryClient()
  const service = getGrapeService()
  return useMutation<GrapeResponse, ApiError, CreateGrapeParams>({
    mutationFn: async ({ payload }: CreateGrapeParams) => {
      return service.create(payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['grapes'] })
    },
  })
}
