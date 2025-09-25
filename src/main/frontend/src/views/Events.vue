<script setup lang="ts">
import EventDataTable from '@/components/event/EventDataTable.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import EventForm, { type SchemaType } from '@/components/event/EventForm.vue'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger
} from '@/components/ui/dialog'
import { useCreateEvent } from '@/composables/useCreateEvent.ts'
import { ref, watch } from 'vue'

const dialogOpen = ref(false)

// todo handle validation errors from backend
const { mutate, isPending, isSuccess } = useCreateEvent()

watch(isSuccess, (isSuccess) => {
  if (isSuccess) {
    dialogOpen.value = false
  }
})

const customError = ref({
  host: 'test',
})

function handleSubmit(values: SchemaType) {
  mutate(values)
}
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <Card>
      <CardHeader class="flex">
        <CardTitle>Evenementen</CardTitle>
        <div class="ml-auto">
          <Dialog v-model:open="dialogOpen">
            <DialogTrigger as-child>
              <Button>Nieuw evenement</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nieuw evenement</DialogTitle>
                <DialogDescription> </DialogDescription>
              </DialogHeader>
              <div class="grid gap-4 py-4">
                <EventForm :is-pending="isPending" :errors="customError" @submit="handleSubmit" />
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <EventDataTable />
      </CardContent>
    </Card>
  </div>
</template>
