<script lang="ts" setup>
import type { EventResponse } from '@/generated/api'
import { useUpdateEvent } from '@/composables'
import EventForm, { type FormSchema } from '@/components/event/EventForm.vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import DeleteEventDialog from '@/components/event/DeleteEventDialog.vue'
import { Button } from '@/components/ui/button'
import { useRouter } from 'vue-router'

const props = defineProps<{
  event: EventResponse
}>()

const router = useRouter()

const { mutate, isPending } = useUpdateEvent()

const handleSubmit = (payload: FormSchema) => {
  mutate({ eventId: props.event.id, payload })
}

const handleDelete = () => {
  router.push('/events')
}
</script>

<template>
  <Card>
    <CardHeader v-if="isPending">
      <CardTitle>Evenement wordt geladen</CardTitle>
    </CardHeader>
    <CardHeader class="flex justify-between" v-else-if="event">
      <div>
        <CardTitle>Evenement {{ event.date }}</CardTitle>
        <CardDescription>{{ event.host }} | {{ event.location }}</CardDescription>
      </div>
      <DeleteEventDialog :event="event" @deleted="handleDelete">
        <Button variant="destructive">Verwijderen</Button>
      </DeleteEventDialog>
    </CardHeader>
    <CardContent>
      <EventForm :is-pending="isPending" :data="event" @submit="handleSubmit" />
    </CardContent>
  </Card>
</template>
