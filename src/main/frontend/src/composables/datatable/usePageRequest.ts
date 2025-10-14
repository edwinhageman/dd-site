import { computed, ref } from 'vue'
import type { Pageable } from '@/generated/api'

export function usePageRequest(
  options: {
    initialSize?: number
    initialSort?: string[]
    initialPage?: number
  } = {},
) {
  const pageRequest = ref<Pageable>({
    size: options?.initialSize ?? 25,
    sort: options?.initialSort ?? undefined,
    page: options?.initialPage ?? 0,
  })
  const setSort = (sort: string[]) => {
    pageRequest.value = { ...pageRequest.value, sort }
  }
  const setSize = (size: number) => {
    pageRequest.value = { ...pageRequest.value, size }
  }
  const setPage = (page: number) => {
    pageRequest.value = { ...pageRequest.value, page }
  }
  const nextPage = () => {
    setPage((pageRequest.value.page ?? 0) + 1)
  }
  const previousPage = () => {
    setPage((pageRequest.value.page ?? 0) - 1)
  }
  return {
    pageRequest: computed(() => pageRequest.value),
    setSort,
    setSize,
    setPage,
    nextPage,
    previousPage,
  }
}
