<script setup lang="ts">
import { columns } from '@/components/event/columns'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { computed, ref, watchEffect } from 'vue'
import { useCreateEvent, useListEvents, usePageRequest } from '@/composables'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import EventForm from '@/components/event/EventForm.vue'
import DataTable from '@/components/datatable/DataTable.vue'

const dialogOpen = ref(false)

const { pageRequest, nextPage, previousPage } = usePageRequest({ initialSize: 25 })
const { data: eventPage, isPending: listIsPending } = useListEvents(pageRequest)

const pageMeta = computed(() => eventPage.value?.page ?? {})
const content = computed(() => eventPage.value?.content ?? [])

const { mutate: createEvent, isPending: createIsPending, isSuccess, error } = useCreateEvent()

watchEffect(() => {
  if (isSuccess.value) {
    dialogOpen.value = false
  }
})
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
                <EventForm
                  :is-pending="createIsPending"
                  :errors="error?.payload?.fieldErrors"
                  @submit="(payload) => createEvent({ payload })"
                />
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <DataTable
          :columns="columns"
          :data="content"
          :page-metadata="pageMeta"
          :is-loading="listIsPending"
          @previous-page="previousPage"
          @next-page="nextPage"
        />
      </CardContent>
    </Card>
  </div>
</template>
