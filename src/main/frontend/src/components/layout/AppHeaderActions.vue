<script setup lang="ts">
import { Settings } from 'lucide-vue-next'

import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'

const data = [
  [
    {
      label: 'Instellingen',
      icon: Settings,
      route: 'settings',
    },
  ],
]

const isOpen = ref(false)
</script>

<template>
  <div class="flex items-center gap-2 text-sm">
    <Popover v-model:open="isOpen">
      <PopoverTrigger as-child>
        <Button variant="ghost" size="icon" class="h-7 w-7 data-[state=open]:bg-accent">
          <Settings />
        </Button>
      </PopoverTrigger>
      <PopoverContent class="w-56 overflow-hidden rounded-lg p-0" align="end">
        <Sidebar collapsible="none" class="bg-transparent">
          <SidebarContent>
            <SidebarGroup
              v-for="(group, index) in data"
              :key="index"
              class="border-b last:border-none"
            >
              <SidebarGroupContent class="gap-0">
                <SidebarMenu>
                  <SidebarMenuItem v-for="(item, index) in group" :key="index">
                    <SidebarMenuButton as-child>
                      <router-link :to="{ name: 'settings' }">
                        <component :is="item.icon" /> <span>{{ item.label }}</span>
                      </router-link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </SidebarContent>
        </Sidebar>
      </PopoverContent>
    </Popover>
  </div>
</template>
