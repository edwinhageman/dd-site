<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { computed, watch } from 'vue'
import { useGetEventById, useListCoursesByEvent, usePageRequest, useUpdateEvent } from '@/composables'
import EventForm from '@/components/event/EventForm.vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import CreateCourseDialog from '@/components/course/CreateCourseDialog.vue'
import { Button } from '@/components/ui/button'
import EventCourse from '@/components/event/EventCourse.vue'

const router = useRouter()
const route = useRoute()

const id = computed(() => {
  const val = Number(route.params.id)
  return Number.isNaN(val) ? undefined : val
})

const { data: event, isPending: isEventPending, isError } = useGetEventById(id)

// redirect if event could not be loaded or if id is not a number
watch(
  [isError, id],
  () => {
    if (isError.value || id.value === undefined) {
      router.push('/events')
    }
  },
  { immediate: true },
)

const { mutate: updateEvent, isPending: isUpdatePending, error: updateError } = useUpdateEvent()

const { pageRequest } = usePageRequest({ initialSize: Number.MAX_SAFE_INTEGER })
const { data: coursePage } = useListCoursesByEvent(id, pageRequest)

const courses = computed(() => coursePage.value?.content ?? [])
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <div class="grid auto-rows-min gap-4 md:grid-cols-2" v-if="event">
      <div>
        <Card>
          <CardHeader v-if="isEventPending">
            <CardTitle>Evenement wordt geladen</CardTitle>
          </CardHeader>
          <CardHeader class="flex justify-between" v-else-if="event">
            <CardTitle>Evenement {{ event.date }}</CardTitle>
          </CardHeader>
          <CardContent>
            <EventForm
              :is-pending="isUpdatePending"
              :data="event"
              :errors="updateError?.payload?.fieldErrors"
              @submit="(payload) => updateEvent({ eventId: id!, payload })"
            />
          </CardContent>
        </Card>
      </div>

      <div>
        <Card>
          <CardHeader class="flex justify-between">
            <div>
              <CardTitle>Gangen</CardTitle>
            </div>
            <CreateCourseDialog :event="event">
              <Button>Gang toevoegen</Button>
            </CreateCourseDialog>
          </CardHeader>
          <CardContent class="space-y-2">
            <EventCourse v-for="course in courses" :key="course.id" :course="course" />
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>
