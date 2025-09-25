import { h } from 'vue'
import type { ColumnDef } from '@tanstack/vue-table'
import type { EventResponse } from '@/generated/api'
import EventDataTableRowActions from '@/components/event/EventDataTableRowActions.vue'

export const columns: ColumnDef<EventResponse>[] = [
  {
    accessorKey: 'date',
    header: () => h('div', { class: 'text-right' }, 'Datum'),
    cell: ({ row }) => {
      let str = ''
      const value = row.original.date
      if (value) {
        const date = new Date(value)
        str = date.toLocaleDateString('nl-NL', { dateStyle: 'medium' })
      }
      return h('div', { class: 'text-right' }, str)
    },
  },
  {
    accessorKey: 'host',
    header: 'Host',
  },
  {
    accessorKey: 'location',
    header: 'Locatie',
  },
  {
    accessorKey: 'id',
    header: '',
    cell: ({ row }) => {
      return h(
        'div',
        { class: 'text-right' },
        h(EventDataTableRowActions, { id: row.original.id! }),
      )
    },
  },
]
