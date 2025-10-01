import { isRef, ref, unref } from 'vue'
import { flushPromises, mount, VueWrapper } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import Event from '@/views/Event.vue'
import { useGetEventById } from '@/composables'
import { createMemoryHistory, createRouter } from 'vue-router'

vi.mock('@/composables', () => ({
  useGetEventById: vi.fn(),
}))

const EventUpdateSectionStub = {
  props: ['event'],
  template: '<div data-testid="event-update-section"></div>',
}
const EventCourseSectionStub = {
  props: ['event'],
  template: '<div data-testid="event-course-section"></div>',
}

const routes = [{ path: '/events', name: 'events', component: { template: '<div>events</div>' } }]

describe('Event.vue tests', () => {
  let wrapper: VueWrapper
  let router: ReturnType<typeof createRouter>

  const eventId = 123
  const mockComposableData = {
    data: ref({ id: 123, host: 'Alice', date: '2025-01-01', location: 'Rotterdam' }),
    isError: ref(false),
  } as ReturnType<typeof useGetEventById>

  beforeEach(async () => {
    vi.resetAllMocks()
    vi.mocked(useGetEventById).mockReturnValue(mockComposableData)

    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        ...routes,
        { path: '/event/:id', name: 'event', component: { template: '<div></div>' } },
      ],
    })

    router.push(`/event/${eventId}`)
    await router.isReady()

    wrapper = mountComponent()
  })

  function mountComponent() {
    return mount(Event, {
      global: {
        plugins: [router],
        stubs: {
          EventUpdateSection: EventUpdateSectionStub,
          EventCourseSection: EventCourseSectionStub,
        },
      },
    })
  }

  const getUpdateSection = () => wrapper.find('[data-testid="event-update-section"]')
  const getCourseSection = () => wrapper.find('[data-testid="event-course-section"]')

  it('useGetEventById should be called with the correct id as a ref', async () => {
    await flushPromises()

    const [arg] = vi.mocked(useGetEventById).mock.calls[0]

    expect(isRef(arg)).toBe(true)
    expect(unref(arg)).toBe(eventId)
  })

  it('invalid id route param should be normalized', async () => {
    router.push(`/event/invalid`)

    await flushPromises()

    const [arg] = vi.mocked(useGetEventById).mock.calls[0]

    expect(isRef(arg)).toBe(true)
    expect(unref(arg)).toBe(undefined)
  })

  it('should redirect to /events when id is invalid', async () => {
    router.push(`/event/invalid`)

    await flushPromises()

    expect(router.currentRoute.value.fullPath).toBe('/events')
  })

  it('renders sections when event data is available', async () => {
    await flushPromises()

    expect(getUpdateSection().exists()).toBe(true)
    expect(getCourseSection().exists()).toBe(true)
  })

  it('does not render sections when event data is missing', async () => {
    vi.mocked(useGetEventById).mockReturnValue({
      data: ref(undefined),
      isError: ref(false),
    } as ReturnType<typeof useGetEventById>)

    wrapper = mountComponent()

    await flushPromises()

    expect(getUpdateSection().exists()).toBe(false)
    expect(getCourseSection().exists()).toBe(false)
  })
})
