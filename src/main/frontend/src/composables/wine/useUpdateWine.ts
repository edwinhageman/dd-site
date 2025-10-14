import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { WineResponse, WineUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type UpdateWineParams = {
  wineId: number
  payload: WineUpsertRequest
}

export function useUpdateWine() {
  const qc = useQueryClient()
  const service = getWineService()
  return useMutation<WineResponse, ApiError, UpdateWineParams>({
    mutationFn: async ({ wineId, payload }: UpdateWineParams) => {
      return service.update(wineId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['wines'] })
      qc.invalidateQueries({ queryKey: ['wine', variables.wineId] })
    },
  })
}
