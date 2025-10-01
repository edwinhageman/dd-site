import { EventControllerApi, type EventUpsertRequest, type Pageable } from '@/generated/api'
import { createApiControllers, unwrapRequest } from '@/api'

export class EventService {
  constructor(private readonly api: EventControllerApi) {}

  async findById(id: number) {
    return unwrapRequest(() => this.api.getEventById(id))
  }

  async listAll(pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listEvents(pageParams))
  }

  async create(payload: EventUpsertRequest) {
    return unwrapRequest(() => this.api.createEvent(payload))
  }

  async update(id: number, payload: EventUpsertRequest) {
    return unwrapRequest(() => this.api.updateEvent(id, payload))
  }

  async delete(id: number) {
    unwrapRequest(() => this.api.deleteEvent(id))
  }
}

let instance: EventService | null = null
export function getEventService() {
  if (!instance) {
    const { eventApi } = createApiControllers()
    instance = new EventService(eventApi)
  }
  return instance
}
