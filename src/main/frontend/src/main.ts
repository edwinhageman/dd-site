import './assets/index.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { VueQueryPlugin } from '@tanstack/vue-query'
import * as z from 'zod'
import { nl } from 'zod/locales'

const app = createApp(App)

app.use(router)
app.use(VueQueryPlugin)

z.config(nl())

app.mount('#app')
