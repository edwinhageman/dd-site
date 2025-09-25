import type { ColumnDef } from '@tanstack/vue-table'
import type { WineResponse } from '@/generated/api'

export const columns: ColumnDef<WineResponse>[] = [
  {
    accessorKey: 'name',
    header: 'Naam',
  },
  {
    accessorKey: 'type',
    header: 'Soort',
    cell: ({ row }) => {
      let value = ''
      switch (row.original.type) {
        case 'RED':
          value = 'Rood'
          break
        case 'WHITE':
          value = 'Wit'
          break
        case 'ROSE':
          value = 'Roze'
          break
        case 'SPARKLING':
          value = 'Mousserend'
          break
        case 'DESSERT':
          value = 'Dessert'
          break
        case 'FORTIFIED':
          value = 'Versterkt'
          break
        case 'UNKNOWN':
        default:
          value = 'Onbekend'
      }
      return value
    },
  },
  {
    accessorKey: 'grape',
    header: 'Druif',
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
    accessorKey: 'year',
    header: 'Jaar',
  },
]
