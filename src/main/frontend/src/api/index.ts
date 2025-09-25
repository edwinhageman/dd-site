import { Configuration, EventControllerApi, WineControllerApi } from '@/generated/api'

// make sure the api calls are passed through the vite development server proxy to prevent CORS issues
let apiConfig = new Configuration()
if (import.meta.env.MODE === 'development') {
  apiConfig = new Configuration({
    basePath: 'http://localhost:3000',
  })
}

export const eventApi = new EventControllerApi(apiConfig)
export const wineApi = new WineControllerApi(apiConfig)
