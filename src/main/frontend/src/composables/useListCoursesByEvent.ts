import { useQuery } from '@tanstack/vue-query'
import { computed, type Ref, unref } from 'vue'
import { getCourseService } from '@/service'
import type { Pageable } from '@/generated/api'

export function useListCoursesByEvent(
  eventId: number | Ref<number | null | undefined>,
  pageParams: Pageable | Ref<Pageable | null | undefined> = {},
) {
  const normalizedId = computed(() => {
    const raw = unref(eventId)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })
  const enabled = computed(() => normalizedId.value !== undefined)

  const normalizedPageParams = computed(() => {
    const raw = unref(pageParams)
    return typeof raw === 'object' && raw !== null ? raw : {}
  })

  const service = getCourseService()

  return useQuery({
    queryKey: ['coursesByEvent', normalizedId, normalizedPageParams],
    queryFn: async () => {
      return service.listByEvent(normalizedId.value!, normalizedPageParams.value)
    },
    enabled,
  })
}
