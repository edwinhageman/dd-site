import { computed, type Ref, unref } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { getGrapeService } from '@/service'
import type { GrapeResponse } from '@/generated/api'
import { ApiError } from '@/api'

export function useGetGrapeById(id: number | Ref<number | null | undefined>) {
  const normalizedId = computed(() => {
    const raw = unref(id)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })

  const enabled = computed(() => normalizedId.value !== undefined)
  const service = getGrapeService()

  return useQuery<GrapeResponse, ApiError>({
    queryKey: ['grape', normalizedId],
    queryFn: async () => {
      return service.findById(normalizedId.value!)
    },
    enabled,
  })
}
