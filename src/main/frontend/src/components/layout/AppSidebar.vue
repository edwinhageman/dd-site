<script setup lang="ts">
import { Sidebar, SidebarContent, SidebarHeader, type SidebarProps, SidebarRail } from '@/components/ui/sidebar'

import { Calendar, Hamburger, Wine } from 'lucide-vue-next'
import NavMain from '@/components/layout/NavMain.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

const props = defineProps<SidebarProps>()

const data = {
  navMain: [
    {
      title: 'Events',
      route: 'events',
      icon: Calendar,
    },
    {
      title: 'Dishes',
      route: 'dishes',
      icon: Hamburger,
    },
    {
      title: 'Wines',
      route: 'wines',
      icon: Wine,
    },
  ],
}
</script>

<template>
  <Sidebar class="border-r-0" v-bind="props">
    <SidebarHeader>
      <AppHeader />
      <NavMain :items="data.navMain" />
    </SidebarHeader>
    <SidebarContent></SidebarContent>
    <SidebarRail />
  </Sidebar>
</template>
