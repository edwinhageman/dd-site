<script setup lang="ts">
import type { LucideIcon } from "lucide-vue-next"

import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import {useRoute} from "vue-router";

defineProps<{
  items: {
    title: string
    route: string
    icon: LucideIcon
  }[]
}>()

const route = useRoute()

</script>

<template>
  <SidebarMenu>
    <SidebarMenuItem v-for="item in items" :key="item.title">
      <SidebarMenuButton as-child :is-active="item.route === route.name">
        <router-link :to="{name: item.route}">
          <component :is="item.icon" />
          <span>{{ item.title }}</span>
        </router-link>
      </SidebarMenuButton>
    </SidebarMenuItem>
  </SidebarMenu>
</template>
