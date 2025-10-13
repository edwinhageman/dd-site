import type { ColumnDef } from '@tanstack/vue-table'
import type { WineStyleResponse } from '@/generated/api'
import { h } from 'vue'
import WineStyleDataTableRowActions from './WineStyleDataTableRowActions.vue'

export const columns: ColumnDef<WineStyleResponse>[] = [
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
        h(WineStyleDataTableRowActions, { wineStyleId: row.original.id! }),
      )
    },
  },
]
