import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse, WineStyleUpsertRequest } from '@/generated/api'
import { ApiError } from '@/api'

export type CreateWineStyleParams = {
  payload: WineStyleUpsertRequest
}
export function useCreateWineStyle() {
  const qc = useQueryClient()
  const service = getWineStyleService()
  return useMutation<WineStyleResponse, ApiError, CreateWineStyleParams>({
    mutationFn: async ({ payload }: CreateWineStyleParams) => {
      return service.create(payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['wineStyles'] })
    },
  })
}
