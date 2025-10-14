import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { WineResponse, WineUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type CreateWineParams = {
  payload: WineUpsertRequest
}
export function useCreateWine() {
  const qc = useQueryClient()
  const service = getWineService()
  return useMutation<WineResponse, ApiError, CreateWineParams>({
    mutationFn: async ({ payload }: CreateWineParams) => {
      return service.create(payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['wines'] })
    },
  })
}
