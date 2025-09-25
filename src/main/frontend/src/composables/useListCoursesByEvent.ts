import { useQuery } from '@tanstack/vue-query'
import { courseApi } from '@/api'
import { computed, type Ref, unref } from 'vue'

export function useListCoursesByEvent(eventId: number | Ref<number | null | undefined>) {
  const normalizedId = computed(() => {
    const raw = unref(eventId)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })
  const enabled = computed(() => normalizedId.value !== undefined)
  const pageRequest = { size: Number.MAX_SAFE_INTEGER }
  return useQuery({
    queryKey: ['coursesByEvent', normalizedId, pageRequest],
    queryFn: async () => {
      const response = await courseApi.listCoursesByEvent(normalizedId.value!, pageRequest)
      return response.data
    },
    enabled,
  })
}
