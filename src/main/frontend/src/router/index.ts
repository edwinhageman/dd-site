import { createRouter, createWebHistory } from 'vue-router'
import Events from '../views/Events.vue'
import Dishes from '../views/Dishes.vue'
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
      path: '/dishes',
      name: 'dishes',
      component: Dishes,
      meta: {
        title: 'Dishes',
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
