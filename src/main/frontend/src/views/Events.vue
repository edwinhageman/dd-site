<script setup lang="ts">
import EventDataTable from '@/components/event/EventDataTable.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <Card>
      <CardHeader class="flex">
        <CardTitle>Evenementen</CardTitle>
        <div class="ml-auto">
          <Button>Nieuw evenement</Button>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <EventDataTable />
      </CardContent>
    </Card>
  </div>
</template>
