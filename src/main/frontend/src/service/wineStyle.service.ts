import { type GrapeUpsertRequest, type Pageable, WineStyleControllerApi } from '@/generated/api'
import { createApiControllers, unwrapRequest } from '@/api'

export class WineStyleService {
  constructor(private readonly api: WineStyleControllerApi) {}

  async findById(id: number) {
    return unwrapRequest(() => this.api.getWineStyleById(id))
  }

  async listAll(pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listWineStyles(pageParams))
  }
  async create(payload: GrapeUpsertRequest) {
    return unwrapRequest(() => this.api.createWineStyle(payload))
  }

  async update(id: number, payload: GrapeUpsertRequest) {
    return unwrapRequest(() => this.api.updateWineStyle(id, payload))
  }

  async delete(id: number) {
    return unwrapRequest(() => this.api.deleteWineStyle(id))
  }
}

let instance: WineStyleService | null = null
export function getWineStyleService() {
  if (!instance) {
    const { wineStyleApi } = createApiControllers()
    instance = new WineStyleService(wineStyleApi)
  }
  return instance
}
