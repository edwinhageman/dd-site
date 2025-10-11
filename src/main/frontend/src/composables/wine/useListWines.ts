import { useQuery } from '@tanstack/vue-query'
import { computed, type Ref, unref } from 'vue'
import { getWineService } from '@/service'
import type { Pageable } from '@/generated/api'

export function useListWines(pageParams: Pageable | Ref<Pageable | null | undefined> = {}) {
  const normalizedPageParams = computed(() => {
    const raw = unref(pageParams)
    return typeof raw === 'object' && raw !== null ? raw : {}
  })

  const service = getWineService()

  return useQuery({
    queryKey: ['wines', normalizedPageParams],
    queryFn: async () => {
      return service.listAll(normalizedPageParams.value)
    },
  })
}
