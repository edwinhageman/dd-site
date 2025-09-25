<script lang="ts" setup>
import type { CourseResponse, EventResponse } from '@/generated/api'
import { useListCoursesByEvent } from '@/composables/useListCoursesByEvent.ts'
import EventCourse from '@/components/event/EventCourse.vue'

const props = defineProps<{
  event: Required<EventResponse>
}>()

const { data } = useListCoursesByEvent(props.event.id)
</script>

<template>
  <div class="space-y-4">
    <EventCourse
      v-for="course in data?.content"
      :key="course.id"
      :course="course as Required<CourseResponse>"
    />
  </div>
</template>
