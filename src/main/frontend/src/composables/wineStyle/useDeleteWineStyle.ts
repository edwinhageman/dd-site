import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import { ApiError } from '@/api'

export type DeleteWineStyleParams = {
  wineStyleId: number
}

export function useDeleteWineStyle() {
  const qc = useQueryClient()
  const service = getWineStyleService()
  return useMutation<void, ApiError, DeleteWineStyleParams>({
    mutationFn: async ({ wineStyleId }: DeleteWineStyleParams) => {
      return service.delete(wineStyleId)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['wineStyles'] })
      qc.invalidateQueries({ queryKey: ['wineStyle', variables.wineStyleId] })
    },
  })
}
