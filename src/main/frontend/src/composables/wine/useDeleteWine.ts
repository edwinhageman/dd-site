import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import { ApiError } from '@/api'

export type DeleteWineParams = {
  wineId: number
}

export function useDeleteWine() {
  const qc = useQueryClient()
  const service = getWineService()
  return useMutation<void, ApiError, DeleteWineParams>({
    mutationFn: async ({ wineId }: DeleteWineParams) => {
      return service.delete(wineId)
    },
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['wines'] })
      qc.invalidateQueries({ queryKey: ['wine', variables.wineId] })
    },
  })
}
