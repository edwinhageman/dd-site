import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import Wines from '@/views/Wines.vue'

const SlotStub = {
  template: '<div data-stub="slot-stub"><slot /></div>',
}

const CardTitleStub = {
  template: '<h1 data-testid="card-title"><slot /></h1>',
}

const WineDataTableStub = {
  template: '<div data-testid="wine-data-table">WineDataTable</div>',
}

describe('Wines.vue', () => {
  function factory() {
    return mount(Wines, {
      global: {
        stubs: {
          Card: SlotStub,
          CardHeader: SlotStub,
          CardContent: SlotStub,
          CardTitle: CardTitleStub,

          WineDataTable: WineDataTableStub,
        },
      },
    })
  }

  it('renders the page title', () => {
    const wrapper = factory()
    expect(wrapper.get('[data-testid="card-title"]').text()).toBe('Wijnen')
  })

  it('renders the wine data table', () => {
    const wrapper = factory()
    const table = wrapper.find('[data-testid="wine-data-table"]')
    expect(table.exists()).toBe(true)
  })
})
