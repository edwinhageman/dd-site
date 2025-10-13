import { GrapeControllerApi, type GrapeUpsertRequest, type Pageable } from '@/generated/api'
import { createApiControllers, unwrapRequest } from '@/api'

export class GrapeService {
  constructor(private readonly api: GrapeControllerApi) {}

  async findById(id: number) {
    return unwrapRequest(() => this.api.getGrapeById(id))
  }

  async listAll(pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listGrapes(pageParams))
  }
  async create(payload: GrapeUpsertRequest) {
    return unwrapRequest(() => this.api.createGrape(payload))
  }

  async update(id: number, payload: GrapeUpsertRequest) {
    return unwrapRequest(() => this.api.updateGrape(id, payload))
  }

  async delete(id: number) {
    return unwrapRequest(() => this.api.deleteGrape(id))
  }
}

let instance: GrapeService | null = null
export function getGrapeService() {
  if (!instance) {
    const { grapeApi } = createApiControllers()
    instance = new GrapeService(grapeApi)
  }
  return instance
}
