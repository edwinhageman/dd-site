<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import DataTable from '@/components/datatable/DataTable.vue'
import { columns } from './columns.ts'
import { useCreateGrape, useListGrapes } from '@/composables'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { computed, ref, watchEffect } from 'vue'
import GrapeForm from './GrapeForm.vue'
import { usePageRequest } from '@/composables/datatable/usePageRequest.ts'

const dialogOpen = ref(false)

const { pageRequest, nextPage, previousPage } = usePageRequest({ initialSize: 20 })
const { data: grapePage, isPending: isFetchListPending } = useListGrapes(pageRequest)

const pageMeta = computed(() => grapePage.value?.page ?? {})
const dataTableContent = computed(() => grapePage.value?.content ?? [])

const { mutate: createGrape, isPending: isCreatePending, error: createError } = useCreateGrape()
watchEffect(() => {
  if (!isCreatePending.value && !createError.value) {
    dialogOpen.value = false
  }
})
</script>

<template>
  <div>
    <Card>
      <CardHeader class="flex">
        <CardTitle>Druiven</CardTitle>
        <div class="ml-auto">
          <Dialog v-model:open="dialogOpen">
            <DialogTrigger as-child>
              <Button>Druif toevoegen</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nieuwe druif</DialogTitle>
                <DialogDescription> </DialogDescription>
              </DialogHeader>
              <div class="grid gap-4 py-4">
                <GrapeForm
                  :is-pending="isCreatePending"
                  :errors="createError?.payload?.fieldErrors"
                  @submit="(payload) => createGrape({ payload })"
                />
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </CardHeader>
      <CardContent class="p-0">
        <DataTable
          :data="dataTableContent"
          :page-metadata="pageMeta"
          :columns="columns"
          :is-loading="isFetchListPending"
          @previous-page="previousPage"
          @next-page="nextPage"
        />
      </CardContent>
    </Card>
  </div>
</template>

<style scoped></style>
