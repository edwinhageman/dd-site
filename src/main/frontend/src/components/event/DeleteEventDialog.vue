<script lang="ts" setup>
import { ref, watch } from 'vue'
import type { EventResponse } from '@/generated/api'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { Button } from '@/components/ui/button'
import { useDeleteEvent } from '@/composables/useDeleteEvent.ts'

const dialogOpen = ref(false)

const props = defineProps<{
  event: EventResponse
}>()

const emits = defineEmits(['deleted'])

const { mutate, isPending, isSuccess } = useDeleteEvent()

watch(isSuccess, (isSuccess) => {
  if (isSuccess) {
    emits('deleted')
    dialogOpen.value = false
  }
})

const handleConfirm = () => {
  if (isPending.value) {
    return
  }
  mutate({ eventId: props.event.id })
}
</script>

<template>
  <AlertDialog>
    <AlertDialogTrigger as-child>
      <slot>
        <Button variant="destructive"> Verwijderen </Button>
      </slot>
    </AlertDialogTrigger>
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Bevestig verwijderen evenement</AlertDialogTitle>
        <AlertDialogDescription>
          Weet je zeker dat je dit evenement wilt verwijderen?
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Annuleren</AlertDialogCancel>
        <AlertDialogAction @click.prevent="handleConfirm">Bevestigen</AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>
