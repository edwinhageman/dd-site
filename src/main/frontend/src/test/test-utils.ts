import { flushPromises } from '@vue/test-utils'
import { onTestFinished } from 'vitest'
import type { App, Plugin } from 'vue'
import { createApp, defineComponent, h, provide } from 'vue'
import { AxiosHeaders, type AxiosResponse } from 'axios'

export type PluginFactory = (app: App) => void

export interface Options {
  plugins?: Array<Plugin | PluginFactory>
  autoCleanup?: boolean
  provided?: Record<string, unknown>
}

export interface ComposableFactory<TArgs extends any[], T> {
  use: (...args: TArgs) => T
  args: TArgs
}

export function withComponentLifecycle<T>(
  composableOrFactory: (() => T) | ComposableFactory<any[], T>,
  options: Options = {},
): { result: T; app: App; cleanup: () => Promise<void> } {
  const { plugins = [], autoCleanup = true, provided = {} } = options
  let result: T

  const component = defineComponent(() => {
    if (typeof composableOrFactory === 'function') {
      result = composableOrFactory()
    } else {
      result = composableOrFactory.use(...composableOrFactory.args)
    }
    return () => null
  })

  const app = createApp({
    setup() {
      Object.entries(provided).forEach(([key, value]) => {
        provide(key, value)
      })
      return () => h(component)
    },
  })

  plugins.forEach((plugin) => {
    if (typeof plugin === 'function') {
      plugin(app)
    } else {
      app.use(plugin)
    }
  })
  app.mount(document.createElement('div'))

  const cleanup = async () => {
    app.unmount()
    await flushPromises()
  }

  if (autoCleanup) {
    onTestFinished(cleanup)
  }

  return {
    // @ts-expect-error We know that result will be assigned
    result,
    app,
    cleanup: autoCleanup
      ? () => {
          throw new Error('Auto cleanup is enabled, no manual cleanup is possible')
        }
      : cleanup,
  }
}

export function mockAxiosResponse<T>(data: T): AxiosResponse<T> {
  return {
    data,
    status: 200,
    statusText: 'OK',
    headers: {},
    config: {
      headers: new AxiosHeaders(),
    },
  }
}
