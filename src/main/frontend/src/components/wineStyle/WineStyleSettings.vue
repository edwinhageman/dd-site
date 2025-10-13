<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import DataTable from '@/components/datatable/DataTable.vue'
import { columns } from './columns.ts'
import { useCreateWineStyle, useListWineStyles } from '@/composables'
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
import WineStyleForm from './WineStyleForm.vue'
import { usePageRequest } from '@/composables/datatable/usePageRequest.ts'

const dialogOpen = ref(false)

const { pageRequest, nextPage, previousPage } = usePageRequest({ initialSize: 20 })
const { data: wineStylePage, isPending: isFetchListPending } = useListWineStyles(pageRequest)

const pageMeta = computed(() => wineStylePage.value?.page ?? {})
const dataTableContent = computed(() => wineStylePage.value?.content ?? [])

const {
  mutate: createWineStyle,
  isPending: isCreatePending,
  error: createError,
} = useCreateWineStyle()
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
        <CardTitle>Wijn-stijlen</CardTitle>
        <div class="ml-auto">
          <Dialog v-model:open="dialogOpen">
            <DialogTrigger as-child>
              <Button>Stijl toevoegen</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nieuwe stijl</DialogTitle>
                <DialogDescription> </DialogDescription>
              </DialogHeader>
              <div class="grid gap-4 py-4">
                <WineStyleForm
                  :is-pending="isCreatePending"
                  :errors="createError?.payload?.fieldErrors"
                  @submit="(payload) => createWineStyle({ payload })"
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
