import { createRouter, createWebHistory } from 'vue-router'
import Events from '../views/Events.vue'
import Event from '../views/Event.vue'
import Wines from '../views/Wines.vue'
import NewWine from '../views/NewWine.vue'
import Wine from '../views/Wine.vue'
import Settings from '../views/Settings.vue'

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
    {
      path: '/wines/new',
      name: 'new-wine',
      component: NewWine,
      meta: {
        title: 'Nieuwe wijn',
      },
    },
    {
      path: '/wines/:id',
      name: 'wine',
      component: Wine,
      meta: {
        title: 'Wijn',
      },
    },
    {
      path: '/settings',
      name: 'settings',
      component: Settings,
      meta: {
        title: 'Instellingen',
      },
    },
  ],
})

export default router
