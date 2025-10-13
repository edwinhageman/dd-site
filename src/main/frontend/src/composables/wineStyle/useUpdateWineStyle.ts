import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse, WineStyleUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type UpdateWineStyleParams = {
  wineStyleId: number
  payload: WineStyleUpsertRequest
}

export function useUpdateWineStyle() {
  const qc = useQueryClient()
  const service = getWineStyleService()
  return useMutation<WineStyleResponse, ApiError, UpdateWineStyleParams>({
    mutationFn: async ({ wineStyleId, payload }: UpdateWineStyleParams) => {
      return service.update(wineStyleId, payload)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['wineStyles'] })
      qc.invalidateQueries({ queryKey: ['wineStyle', variables.wineStyleId] })
    },
  })
}
