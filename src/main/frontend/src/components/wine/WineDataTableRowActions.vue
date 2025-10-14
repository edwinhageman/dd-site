<script setup lang="ts">
import { AlertCircle, MoreVertical } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { useDeleteWine } from '@/composables'
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

const props = defineProps<{
  wineId: number
}>()

const deleteDialogOpen = ref(false)

const { mutate: deleteWine, isPending: isDeletePending, error: deleteError } = useDeleteWine()
watch(isDeletePending, (isPending) => {
  if (!isPending && !deleteError.value) {
    deleteDialogOpen.value = false
  }
})
const onDeleteConfirm = () => {
  if (isDeletePending.value) {
    return
  }
  deleteWine({ wineId: props.wineId })
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
        <RouterLink :to="{ name: 'wine', params: { id: wineId } }">Bewerken</RouterLink>
      </DropdownMenuItem>
      <DropdownMenuItem @click="deleteDialogOpen = true">Verwijderen</DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>

  <AlertDialog v-model:open="deleteDialogOpen">
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Bevestig verwijderen wijn</AlertDialogTitle>
        <Alert variant="destructive" v-if="deleteError">
          <AlertCircle class="w-4 h-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {{ deleteError?.payload?.detail ?? 'Er is iets misgegaan' }}
          </AlertDescription>
        </Alert>
        <AlertDialogDescription>
          Weet je zeker dat je deze wijn wilt verwijderen?
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
