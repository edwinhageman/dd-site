import { computed, type Ref, unref } from 'vue'
import type { Pageable, PagedModelGrapeResponse } from '@/generated/api'
import { getGrapeService } from '@/service'
import { useQuery } from '@tanstack/vue-query'
import { ApiError } from '@/api'

export function useListGrapes(pageParams: Pageable | Ref<Pageable | null | undefined> = {}) {
  const normalizedPageParams = computed(() => {
    const raw = unref(pageParams)
    return typeof raw === 'object' && raw !== null ? raw : {}
  })

  const service = getGrapeService()

  return useQuery<PagedModelGrapeResponse, ApiError>({
    queryKey: ['grapes', normalizedPageParams],
    queryFn: async () => {
      return service.listAll(normalizedPageParams.value)
    },
  })
}
