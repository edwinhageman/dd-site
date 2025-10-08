import {
  Configuration,
  CourseControllerApi,
  EventControllerApi,
  WineControllerApi,
} from '@/generated/api'
import { envBrowser } from '../env.browser.ts'
import axios from 'axios'

export interface ApiControllers {
  eventApi: EventControllerApi
  courseApi: CourseControllerApi
  wineApi: WineControllerApi
}

let instance: ApiControllers | null = null

export function createApiControllers(): ApiControllers {
  if (instance) {
    return instance
  }

  const axiosInstance = axios.create({
    timeout: envBrowser.VITE_API_TIMEOUT_MS,
  })

  const config = new Configuration({
    basePath: envBrowser.VITE_API_BASE_URL,
  })

  const eventApi = new EventControllerApi(config, undefined, axiosInstance)
  const courseApi = new CourseControllerApi(config, undefined, axiosInstance)
  const wineApi = new WineControllerApi(config, undefined, axiosInstance)

  instance = { eventApi, courseApi, wineApi }
  return instance
}
