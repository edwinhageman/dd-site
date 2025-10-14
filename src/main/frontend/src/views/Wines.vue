<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { useListWines, usePageRequest } from '@/composables'
import { columns } from '@/components/wine/columns.ts'
import DataTable from '@/components/datatable/DataTable.vue'
import { computed } from 'vue'

const { pageRequest, nextPage, previousPage } = usePageRequest({ initialSize: 25 })
const { data, isPending } = useListWines(pageRequest)

const pageMeta = computed(() => data.value?.page ?? {})
const content = computed(() => data.value?.content ?? [])
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <Card>
      <CardHeader class="flex">
        <CardTitle>Wijnen</CardTitle>
        <div class="ml-auto">
          <Button as-child>
            <RouterLink :to="{ name: 'new-wine' }">Wijn toevoegen</RouterLink>
          </Button>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <DataTable
          :columns="columns"
          :data="content"
          :page-metadata="pageMeta"
          :is-loading="isPending"
          @previous-page="previousPage"
          @next-page="nextPage"
        />
      </CardContent>
    </Card>
  </div>
</template>
