import { computed, type Ref, unref } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { eventApi } from '@/api'

export function useGetEventById(id: number | Ref<number | null | undefined>) {
  const normalizedId = computed(() => {
    const raw = unref(id)
    return typeof raw === 'number' && !Number.isNaN(raw) ? raw : undefined
  })

  const enabled = computed(() => normalizedId.value !== undefined)

  return useQuery({
    queryKey: ['event', normalizedId],
    queryFn: async () => {
      const response = await eventApi.getEventById(normalizedId.value!)
      return response.data
    },
    enabled,
  })
}
