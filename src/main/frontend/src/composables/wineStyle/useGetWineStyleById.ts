import { computed, type Ref, unref } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { getWineStyleService } from '@/service'
import type { WineStyleResponse } from '@/generated/api'
import { ApiError } from '@/api'

export function useGetWineStyleById(id: number | Ref<number | null | undefined>) {
  const normalizedId = computed(() => {
    const raw = unref(id)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })

  const enabled = computed(() => normalizedId.value !== undefined)
  const service = getWineStyleService()

  return useQuery<WineStyleResponse, ApiError>({
    queryKey: ['wineStyle', normalizedId],
    queryFn: async () => {
      return service.findById(normalizedId.value!)
    },
    enabled,
  })
}
