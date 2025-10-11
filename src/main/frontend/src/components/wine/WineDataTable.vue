<script lang="ts" setup>
import { type Pageable } from '@/generated/api'
import { columns } from './columns'
import DataTable from '@/components/datatable/DataTable.vue'
import { ref } from 'vue'
import { useListWines } from '@/composables'

const pageRequest = ref<Pageable>({})
const { data } = useListWines(pageRequest)

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
