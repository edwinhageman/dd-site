import {
  Configuration,
  CourseControllerApi,
  EventControllerApi,
  WineControllerApi,
} from '@/generated/api'

// make sure the api calls are passed through the vite development server proxy to prevent CORS issues
const apiConfig = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL ?? '',
})

export const eventApi = new EventControllerApi(apiConfig)
export const wineApi = new WineControllerApi(apiConfig)
export const courseApi = new CourseControllerApi(apiConfig)
