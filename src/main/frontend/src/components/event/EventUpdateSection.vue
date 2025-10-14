<script lang="ts" setup>
import type { EventResponse } from '@/generated/api'
import { useUpdateEvent } from '@/composables'
import EventForm, { type FormSchema } from '@/components/event/EventForm.vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

const props = defineProps<{
  event: EventResponse
}>()

const { mutate, isPending, error } = useUpdateEvent()

const handleSubmit = (payload: FormSchema) => {
  mutate({ eventId: props.event.id, payload })
}
</script>

<template>
  <Card>
    <CardHeader v-if="isPending">
      <CardTitle>Evenement wordt geladen</CardTitle>
    </CardHeader>
    <CardHeader class="flex justify-between" v-else-if="event">
      <CardTitle>Evenement {{ event.date }}</CardTitle>
    </CardHeader>
    <CardContent>
      <EventForm
        :is-pending="isPending"
        :data="event"
        :errors="error?.payload?.fieldErrors"
        @submit="handleSubmit"
      />
    </CardContent>
  </Card>
</template>
