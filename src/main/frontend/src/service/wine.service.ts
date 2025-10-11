import { type Pageable, WineControllerApi, type WineUpsertRequest } from '@/generated/api'
import { createApiControllers, unwrapRequest } from '@/api'

export class WineService {
  constructor(private readonly api: WineControllerApi) {}

  async findById(id: number) {
    return unwrapRequest(() => this.api.getWineById(id))
  }

  async listAll(pageParams: Pageable = {}) {
    return unwrapRequest(() => this.api.listWines(pageParams))
  }
  async create(payload: WineUpsertRequest) {
    return unwrapRequest(() => this.api.createWine(payload))
  }

  async update(id: number, payload: WineUpsertRequest) {
    return unwrapRequest(() => this.api.updateWine(id, payload))
  }

  async delete(id: number) {
    unwrapRequest(() => this.api.deleteWine(id))
  }
}

let instance: WineService | null = null
export function getWineService() {
  if (!instance) {
    const { wineApi } = createApiControllers()
    instance = new WineService(wineApi)
  }
  return instance
}
