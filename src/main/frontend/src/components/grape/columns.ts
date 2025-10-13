import type { ColumnDef } from '@tanstack/vue-table'
import type { GrapeResponse } from '@/generated/api'
import { h } from 'vue'
import GrapeDataTableRowActions from './GrapeDataTableRowActions.vue'

export const columns: ColumnDef<GrapeResponse>[] = [
  {
    accessorKey: 'name',
    header: 'Naam',
  },
  {
    accessorKey: 'id',
    header: '',
    cell: ({ row }) => {
      return h(
        'div',
        { class: 'text-right' },
        h(GrapeDataTableRowActions, { grapeId: row.original.id! }),
      )
    },
  },
]
