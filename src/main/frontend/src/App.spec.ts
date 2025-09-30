import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it } from 'vitest'
import App from '@/App.vue'
import { createMemoryHistory, createRouter } from 'vue-router'

const SlotStub = {
  template: '<div data-stub="slot-stub"><slot /></div>',
}

const BreadcrumbPageStub = {
  template: '<div data-testid="breadcrumb-page"><slot /></div>',
}

describe('App.vue', () => {
  let router: ReturnType<typeof createRouter>

  beforeEach(async () => {
    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        {
          path: '/events',
          name: 'events',
          component: { template: '<div data-testid="events-view">events view</div>' },
          meta: {
            title: 'Evenementen',
          },
        },
      ],
    })
    router.push('/events')
    await router.isReady()
  })

  function factory() {
    return mount(App, {
      global: {
        plugins: [router],
        stubs: {
          SidebarProvider: SlotStub,
          AppSidebar: SlotStub,
          SidebarInset: SlotStub,
          SidebarTrigger: SlotStub,
          Separator: SlotStub,
          Breadcrumb: SlotStub,
          BreadcrumbList: SlotStub,
          BreadcrumbItem: SlotStub,
          RouterView: false,

          BreadcrumbPage: BreadcrumbPageStub,
        },
      },
    })
  }

  it('renders the breadcrumb with route title', () => {
    const wrapper = factory()
    expect(wrapper.get('[data-testid="breadcrumb-page"]').text()).toBe('Evenementen')
  })

  it('renders the events view', () => {
    const wrapper = factory()
    const view = wrapper.find('[data-testid="events-view"]')
    expect(view.exists()).toBe(true)
  })
})
