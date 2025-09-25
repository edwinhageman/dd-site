<script lang="ts" setup>
import { type Pageable } from '@/generated/api'
import { wineApi } from '@/api'
import { columns } from './columns'
import { useQuery } from '@tanstack/vue-query'
import DataTable from '@/components/datatable/DataTable.vue'
import { ref } from 'vue'

const pageRequest = ref<Pageable>({})

const { data } = useQuery({
  queryKey: ['wines', pageRequest],
  queryFn: async () => {
    const response = await wineApi.listWines(pageRequest.value)
    return response.data
  },
})
const previousPage = () => {
  pageRequest.value = {
    page: (data.value?.page?.number ?? 0) - 1,
    size: data.value?.page?.size ?? 20,
  }
}
const nextPage = () => {
  pageRequest.value = {
    page: (data.value?.page?.number ?? 0) + 1,
    size: data.value?.page?.size ?? 20,
  }
}
</script>

<template>
  <div>
    <DataTable
      :columns="columns"
      :data="data?.content ?? []"
      :page-metadata="data?.page ?? {}"
      @previous-page="previousPage"
      @next-page="nextPage"
    />
  </div>
</template>
