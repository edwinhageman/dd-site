<script lang="ts" setup>
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useRoute, useRouter } from 'vue-router'
import { computed, watch } from 'vue'
import { useGetEventById } from '@/composables/useGetEventById.ts'
import EventUpdateSection from '@/components/event/EventUpdateSection.vue'

const router = useRouter()
const route = useRoute()

const id = computed(() => {
  const val = Number(route.params.id)
  return Number.isNaN(val) ? undefined : val
})

const { data, isPending, isError } = useGetEventById(id)

// redirect if event could not be loaded
watch(isError, (error) => {
  if (error) {
    router.push('/events')
  }
})
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <div class="grid auto-rows-min gap-4 md:grid-cols-2">
      <div>
        <Card>
          <CardHeader v-if="isPending">
            <CardTitle>Evenement wordt geladen</CardTitle>
          </CardHeader>
          <CardHeader v-else-if="data">
            <CardTitle>Evenement {{ data.date }}</CardTitle>
            <CardDescription>{{ data.host }} | {{ data.location }}</CardDescription>
          </CardHeader>
          <CardContent>
            <EventUpdateSection :event="data" v-if="data" />
          </CardContent>
        </Card>
      </div>
      <div></div>
    </div>
  </div>
</template>
