<script setup lang="ts">
import { AlertCircle, MoreVertical } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { useDeleteGrape, useGetGrapeById, useUpdateGrape } from '@/composables'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle
} from '@/components/ui/alert-dialog'
import { ref, watch } from 'vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import GrapeForm from '@/components/grape/GrapeForm.vue'

const props = defineProps<{
  grapeId: number
}>()

const updateDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const fetchId = ref<number | undefined>(undefined)

const {
  data: grape,
  isPending: isFetchingGrapePending,
  error: fetchGrapeError,
} = useGetGrapeById(fetchId)
watch(updateDialogOpen, (isOpen) => {
  if (isOpen && !grape.value) {
    // set the fetchId when the update dialog is opened to delay fetching the data from the API
    // this delayed fetching is to prevent every row to fetch grape data when being rendered
    fetchId.value = props.grapeId
  }
})

const { mutate: updateGrape, isPending: isUpdatePending, error: updateError } = useUpdateGrape()
watch(isUpdatePending, (isPending) => {
  if (!isPending && !updateError.value) {
    updateDialogOpen.value = false
  }
})

const { mutate: deleteGrape, isPending: isDeletePending, error: deleteError } = useDeleteGrape()
watch(isDeletePending, (isPending) => {
  if (!isPending && !deleteError.value) {
    deleteDialogOpen.value = false
  }
})
const onDeleteConfirm = () => {
  if (isDeletePending.value) {
    return
  }
  deleteGrape({ grapeId: props.grapeId })
}
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="ghost" class="w-8 h-8 p-0">
        <span class="sr-only">Open menu</span>
        <MoreVertical class="w-4 h-4" />
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent align="end">
      <DropdownMenuLabel>Acties</DropdownMenuLabel>
      <DropdownMenuItem @click="updateDialogOpen = true">Bewerken</DropdownMenuItem>
      <DropdownMenuItem @click="deleteDialogOpen = true">Verwijderen</DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>

  <Dialog v-model:open="updateDialogOpen">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Druif bewerken</DialogTitle>
        <DialogDescription> </DialogDescription>
      </DialogHeader>
      <Alert variant="destructive" v-if="updateError || fetchGrapeError">
        <AlertCircle class="w-4 h-4" />
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>
          <div>
            {{ updateError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </div>
          <div>
            {{ fetchGrapeError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </div>
        </AlertDescription>
      </Alert>
      <div class="grid gap-4 py-4">
        <GrapeForm
          :is-pending="isUpdatePending || isFetchingGrapePending"
          :data="grape"
          :errors="updateError?.payload?.fieldErrors"
          @submit="(payload) => updateGrape({ grapeId, payload })"
        />
      </div>
    </DialogContent>
  </Dialog>

  <AlertDialog v-model:open="deleteDialogOpen">
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Bevestig verwijderen druif</AlertDialogTitle>
        <Alert variant="destructive" v-if="deleteError">
          <AlertCircle class="w-4 h-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {{ deleteError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </AlertDescription>
        </Alert>
        <AlertDialogDescription>
          Weet je zeker dat je deze druif wilt verwijderen?
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Annuleren</AlertDialogCancel>
        <AlertDialogAction @click.prevent="onDeleteConfirm">Bevestigen</AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>

<style scoped></style>
