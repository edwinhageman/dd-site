<script setup lang="ts">
import EventDataTable from '@/components/event/EventDataTable.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import CreateEventDialog from '@/components/event/CreateEventDialog.vue'
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <Card>
      <CardHeader class="flex">
        <CardTitle>Evenementen</CardTitle>
        <div class="ml-auto">
          <CreateEventDialog>
            <Button>Nieuw evenement</Button>
          </CreateEventDialog>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <EventDataTable />
      </CardContent>
    </Card>
  </div>
</template>
