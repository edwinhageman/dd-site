<script lang="ts" setup>
import type { EventResponse } from '@/generated/api'
import { useListCoursesByEvent } from '@/composables/useListCoursesByEvent.ts'
import EventCourse from '@/components/event/EventCourse.vue'

const props = defineProps<{
  event: EventResponse
}>()

const { data } = useListCoursesByEvent(props.event.id, { size: Number.MAX_SAFE_INTEGER })
</script>

<template>
  <div class="space-y-4">
    <EventCourse v-for="course in data?.content" :key="course.id" :course="course" />
  </div>
</template>
