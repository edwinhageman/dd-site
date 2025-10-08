import type { ColumnDef } from '@tanstack/vue-table'
import type { WineResponse } from '@/generated/api'

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
]
