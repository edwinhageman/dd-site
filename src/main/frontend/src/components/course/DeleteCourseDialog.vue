<script lang="ts" setup>
import { ref, watch } from 'vue'
import type { CourseResponse } from '@/generated/api'
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
import { useDeleteCourse } from '@/composables/useDeleteCourse.ts'

const dialogOpen = ref(false)

const props = defineProps<{
  course: CourseResponse
}>()

const emits = defineEmits(['deleted'])

const { mutate, isPending, isSuccess } = useDeleteCourse()

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
  mutate({ courseId: props.course.id })
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
        <AlertDialogTitle>Bevestig verwijderen gang</AlertDialogTitle>
        <AlertDialogDescription>
          Weet je zeker dat je deze gang wilt verwijderen?
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Annuleren</AlertDialogCancel>
        <AlertDialogAction @click.prevent="handleConfirm">Bevestigen</AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>
