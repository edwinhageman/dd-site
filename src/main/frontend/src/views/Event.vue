<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { computed, watch } from 'vue'
import { useGetEventById } from '@/composables/useGetEventById.ts'
import EventUpdateSection from '@/components/event/EventUpdateSection.vue'
import EventCourseSection from '@/components/event/EventCourseSection.vue'
import type { EventResponse } from '@/generated/api'

const router = useRouter()
const route = useRoute()

const id = computed(() => {
  const val = Number(route.params.id)
  return Number.isNaN(val) ? undefined : val
})

const { data: event, isError } = useGetEventById(id)

// redirect if event could not be loaded
watch(isError, (error) => {
  if (error) {
    router.push('/events')
  }
})
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <div class="grid auto-rows-min gap-4 md:grid-cols-2" v-if="event">
      <div>
        <EventUpdateSection :event="event as Required<EventResponse>" />
      </div>
      <div>
        <EventCourseSection :event="event as Required<EventResponse>" />
      </div>
    </div>
  </div>
</template>
