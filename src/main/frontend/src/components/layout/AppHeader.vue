<script lang="ts" setup>
import { SidebarMenu, SidebarMenuButton, SidebarMenuItem } from '@/components/ui/sidebar'
import { Grape } from 'lucide-vue-next'
</script>

<template>
  <SidebarMenu>
    <SidebarMenuItem>
      <SidebarMenuButton size="lg" as-child>
        <RouterLink to="/">
          <div
            class="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground"
          >
            <Grape class="size-4" />
          </div>
          <div class="flex flex-col gap-0.5 leading-none">
            <span class="font-semibold">DD Site</span>
          </div>
        </RouterLink>
      </SidebarMenuButton>
    </SidebarMenuItem>
  </SidebarMenu>
</template>
