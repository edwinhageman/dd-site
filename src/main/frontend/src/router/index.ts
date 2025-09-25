import { createRouter, createWebHistory } from 'vue-router'
import Events from '../views/Events.vue'
import Event from '../views/Event.vue'
import Wines from '../views/Wines.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'events',
      component: Events,
      alias: '/events',
      meta: {
        title: 'Evenementen',
      },
    },
    {
      path: '/event/:id',
      name: 'event',
      component: Event,
      meta: {
        title: 'Evenement',
      },
    },
    {
      path: '/wines',
      name: 'wines',
      component: Wines,
      meta: {
        title: 'Wijnen',
      },
    },
  ],
})

export default router
