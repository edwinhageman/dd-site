import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import Events from '@/views/Events.vue'

const SlotStub = {
  template: '<div data-stub="slot-stub"><slot /></div>',
}

const CardTitleStub = {
  template: '<h1 data-testid="card-title"><slot /></h1>',
}

const CreateEventDialogStub = {
  template: '<div data-testid="create-event-dialog"><slot /></div>',
}

const EventDataTableStub = {
  template: '<div data-testid="event-data-table">EventDataTable</div>',
}

describe('Events.vue', () => {
  function factory() {
    return mount(Events, {
      global: {
        stubs: {
          Button: SlotStub,
          Card: SlotStub,
          CardHeader: SlotStub,
          CardContent: SlotStub,
          CardTitle: CardTitleStub,

          CreateEventDialog: CreateEventDialogStub,
          EventDataTable: EventDataTableStub,
        },
      },
    })
  }

  it('renders the page title', () => {
    const wrapper = factory()
    expect(wrapper.get('[data-testid="card-title"]').text()).toBe('Evenementen')
  })

  it('renders the create event dialog trigger button', () => {
    const wrapper = factory()
    const dialog = wrapper.get('[data-testid="create-event-dialog"]')
    expect(dialog.text()).toContain('Nieuw evenement')
  })

  it('renders the event data table', () => {
    const wrapper = factory()
    const table = wrapper.find('[data-testid="event-data-table"]')
    expect(table.exists()).toBe(true)
  })
})
