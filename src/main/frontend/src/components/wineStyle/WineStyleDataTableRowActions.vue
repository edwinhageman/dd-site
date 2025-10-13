<script setup lang="ts">
import { AlertCircle, MoreVertical } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { useDeleteWineStyle, useGetWineStyleById, useUpdateWineStyle } from '@/composables'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { ref, watch } from 'vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import WineStyleForm from '@/components/wineStyle/WineStyleForm.vue'

const props = defineProps<{
  wineStyleId: number
}>()

const updateDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const fetchId = ref<number | undefined>(undefined)

const {
  data: style,
  isPending: isFetchingStylePending,
  error: fetchStyleError,
} = useGetWineStyleById(fetchId)
watch(updateDialogOpen, (isOpen) => {
  if (isOpen && !style.value) {
    // set the fetchId when the update dialog is opened to delay fetching the data from the API
    // this delayed fetching is to prevent every row to fetch style data when being rendered
    fetchId.value = props.wineStyleId
  }
})

const { mutate: updateStyle, isPending: isUpdatePending, error: updateError } = useUpdateWineStyle()
watch(isUpdatePending, (isPending) => {
  if (!isPending && !updateError.value) {
    updateDialogOpen.value = false
  }
})

const { mutate: deleteStyle, isPending: isDeletePending, error: deleteError } = useDeleteWineStyle()
watch(isDeletePending, (isPending) => {
  if (!isPending && !deleteError.value) {
    deleteDialogOpen.value = false
  }
})
const onDeleteConfirm = () => {
  if (isDeletePending.value) {
    return
  }
  deleteStyle({ wineStyleId: props.wineStyleId })
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
        <DialogTitle>Stijl bewerken</DialogTitle>
        <DialogDescription> </DialogDescription>
      </DialogHeader>
      <Alert variant="destructive" v-if="updateError || fetchStyleError">
        <AlertCircle class="w-4 h-4" />
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>
          <div>
            {{ updateError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </div>
          <div>
            {{ fetchStyleError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </div>
        </AlertDescription>
      </Alert>
      <div class="grid gap-4 py-4">
        <WineStyleForm
          :is-pending="isUpdatePending || isFetchingStylePending"
          :data="style"
          :errors="updateError?.payload?.fieldErrors"
          @submit="(payload) => updateStyle({ wineStyleId, payload })"
        />
      </div>
    </DialogContent>
  </Dialog>

  <AlertDialog v-model:open="deleteDialogOpen">
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Bevestig verwijderen stijl</AlertDialogTitle>
        <Alert variant="destructive" v-if="deleteError">
          <AlertCircle class="w-4 h-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {{ deleteError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </AlertDescription>
        </Alert>
        <AlertDialogDescription>
          Weet je zeker dat je deze stijl wilt verwijderen?
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
