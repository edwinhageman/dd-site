<script lang="ts" setup>
import type { EventResponse } from '@/generated/api'
import { useUpdateEvent } from '@/composables/useUpdateEvent.ts'
import EventForm, { type SchemaType } from '@/components/event/EventForm.vue'

const props = defineProps<{
  event: Required<EventResponse> // todo check why the generated types have optional properties when they shouldn't
}>()

const { mutate, isPending } = useUpdateEvent()

const handleSubmit = (values: SchemaType) => {
  mutate({ id: props.event.id, request: values })
}
</script>

<template>
  <div>
    <EventForm :is-pending="isPending" :data="event" @submit="handleSubmit" />
  </div>
</template>
