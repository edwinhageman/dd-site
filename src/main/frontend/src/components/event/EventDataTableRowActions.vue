<script lang="ts" setup>
import { ref, watch } from 'vue'
import { useDeleteEvent } from '@/composables'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
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
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { AlertCircle, MoreVertical } from 'lucide-vue-next'

const props = defineProps<{
  eventId: number
}>()

const deleteDialogOpen = ref(false)

const { mutate: deleteEvent, isPending: isDeletePending, error: deleteError } = useDeleteEvent()
watch(isDeletePending, (isPending) => {
  if (!isPending && !deleteError.value) {
    deleteDialogOpen.value = false
  }
})
const onDeleteConfirm = () => {
  if (isDeletePending.value) {
    return
  }
  deleteEvent({ eventId: props.eventId })
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
      <DropdownMenuItem as-child>
        <RouterLink :to="{ name: 'event', params: { id: eventId } }">Bewerken</RouterLink>
      </DropdownMenuItem>
      <DropdownMenuItem @click="deleteDialogOpen = true">Verwijderen</DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>

  <AlertDialog v-model:open="deleteDialogOpen">
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Bevestig verwijderen evenement</AlertDialogTitle>
        <Alert variant="destructive" v-if="deleteError">
          <AlertCircle class="w-4 h-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {{ deleteError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </AlertDescription>
        </Alert>
        <AlertDialogDescription>
          Weet je zeker dat je dit evenement wilt verwijderen?
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Annuleren</AlertDialogCancel>
        <AlertDialogAction @click.prevent="onDeleteConfirm">Bevestigen</AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>
