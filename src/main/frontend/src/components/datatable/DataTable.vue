<script lang="ts" setup generic="TData, TValue">
import { type ColumnDef, FlexRender, getCoreRowModel, useVueTable } from '@tanstack/vue-table'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import type { PageMetadata } from '@/generated/api'

const props = defineProps<{
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
  pageMetadata: PageMetadata
}>()

const emits = defineEmits(['nextPage', 'previousPage'])

const table = useVueTable({
  get data() {
    return props.data
  },
  get columns() {
    return props.columns
  },
  manualPagination: true,
  rowCount: props.pageMetadata.totalElements,
  getCoreRowModel: getCoreRowModel(),
})
table.setPageSize(() => props.pageMetadata.size ?? 20)
</script>

<template>
  <div>
    <Table>
      <TableHeader>
        <TableRow v-for="headerGroup in table.getHeaderGroups()" :key="headerGroup.id">
          <TableHead v-for="header in headerGroup.headers" :key="header.id">
            <FlexRender
              v-if="!header.isPlaceholder"
              :render="header.column.columnDef.header"
              :props="header.getContext()"
            />
          </TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <template v-if="table.getRowModel().rows?.length">
          <TableRow
            v-for="row in table.getRowModel().rows"
            :key="row.id"
            :data-state="row.getIsSelected() ? 'selected' : undefined"
          >
            <TableCell v-for="cell in row.getVisibleCells()" :key="cell.id">
              <FlexRender :render="cell.column.columnDef.cell" :props="cell.getContext()" />
            </TableCell>
          </TableRow>
        </template>
        <template v-else>
          <TableRow>
            <TableCell :colspan="columns.length" class="h-24 text-center"> No results. </TableCell>
          </TableRow>
        </template>
      </TableBody>
    </Table>
  </div>
  <div class="flex items-center justify-center py-4 space-x-2">
    <Button
      variant="outline"
      size="sm"
      :disabled="pageMetadata.number === 0"
      @click="emits('previousPage')"
    >
      Vorige
    </Button>
    <div>Pagina {{ (pageMetadata?.number ?? 0) + 1 }} van {{ pageMetadata.totalPages }}</div>
    <Button
      variant="outline"
      size="sm"
      :disabled="pageMetadata.number === pageMetadata.totalPages! - 1"
      @click="emits('nextPage')"
    >
      Volgende
    </Button>
  </div>
</template>
