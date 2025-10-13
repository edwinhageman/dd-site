import { computed, type Ref, unref } from 'vue'
import type { Pageable, PagedModelWineStyleResponse } from '@/generated/api'
import { getWineStyleService } from '@/service'
import { useQuery } from '@tanstack/vue-query'
import { ApiError } from '@/api'

export function useListWineStyles(pageParams: Pageable | Ref<Pageable | null | undefined> = {}) {
  const normalizedPageParams = computed(() => {
    const raw = unref(pageParams)
    return typeof raw === 'object' && raw !== null ? raw : {}
  })

  const service = getWineStyleService()

  return useQuery<PagedModelWineStyleResponse, ApiError>({
    queryKey: ['wineStyles', normalizedPageParams],
    queryFn: async () => {
      return service.listAll(normalizedPageParams.value)
    },
  })
}
