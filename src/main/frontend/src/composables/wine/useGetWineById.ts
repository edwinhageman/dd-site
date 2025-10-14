import { computed, type Ref, unref } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { getWineService } from '@/service'
import type { WineResponse } from '@/generated/api'
import { ApiError } from '@/api'

export function useGetWineById(id: number | Ref<number | null | undefined>) {
  const normalizedId = computed(() => {
    const raw = unref(id)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })

  const enabled = computed(() => normalizedId.value !== undefined)
  const service = getWineService()

  return useQuery<WineResponse, ApiError>({
    queryKey: ['wine', normalizedId],
    queryFn: async () => {
      return service.findById(normalizedId.value!)
    },
    enabled,
  })
}
