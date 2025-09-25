import { createRouter, createWebHistory } from 'vue-router'
import Events from '../views/Events.vue'
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
        title: 'Events',
      },
    },
    {
      path: '/wines',
      name: 'wines',
      component: Wines,
      meta: {
        title: 'Wines',
      },
    },
  ],
})

export default router
