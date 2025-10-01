import { useQuery } from '@tanstack/vue-query'
import { computed, type Ref, unref } from 'vue'
import { getEventService } from '@/service'
import type { Pageable } from '@/generated/api'

export function useListEvents(pageParams: Pageable | Ref<Pageable | null | undefined> = {}) {
  const normalizedPageParams = computed(() => {
    const raw = unref(pageParams)
    return typeof raw === 'object' && raw !== null ? raw : {}
  })

  const service = getEventService()

  return useQuery({
    queryKey: ['events', normalizedPageParams],
    queryFn: async () => {
      return service.listAll(normalizedPageParams.value)
    },
  })
}
