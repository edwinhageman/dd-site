<script lang="ts" setup>
import type { EventResponse } from '@/generated/api'
import EventCourseList from '@/components/event/EventCourseList.vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import CreateCourseDialog from '@/components/course/CreateCourseDialog.vue'
import { Button } from '@/components/ui/button'

defineProps<{
  event: Required<EventResponse>
}>()
</script>

<template>
  <Card>
    <CardHeader class="flex justify-between">
      <div>
        <CardTitle>Gangen</CardTitle>
        <CardDescription>Beheer hier de gangen</CardDescription>
      </div>
      <CreateCourseDialog :event="event">
        <Button>Gang toevoegen</Button>
      </CreateCourseDialog>
    </CardHeader>
    <CardContent>
      <EventCourseList :event="event" />
    </CardContent>
  </Card>
</template>
