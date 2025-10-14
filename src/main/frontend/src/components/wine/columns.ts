import type { ColumnDef } from '@tanstack/vue-table'
import type { WineResponse } from '@/generated/api'
import { h } from 'vue'
import WineDataTableRowActions from '@/components/wine/WineDataTableRowActions.vue'

export const columns: ColumnDef<WineResponse>[] = [
  {
    accessorKey: 'name',
    header: 'Naam',
  },
  {
    accessorKey: 'winery',
    header: 'Wijnhuis',
  },
  {
    accessorKey: 'country',
    header: 'Land',
  },
  {
    accessorKey: 'region',
    header: 'Regio',
  },
  {
    accessorKey: 'appellation',
    header: 'Appellatie',
  },
  {
    accessorKey: 'vintage',
    header: 'Wijnjaar',
  },
  {
    accessorKey: 'id',
    header: '',
    cell: ({ row }) => {
      return h(
        'div',
        { class: 'text-right' },
        h(WineDataTableRowActions, { wineId: row.original.id! }),
      )
    },
  },
]
