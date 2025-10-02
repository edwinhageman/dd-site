<script lang="ts" setup>
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { ref, watch } from 'vue'
import type { EventResponse } from '@/generated/api'
import { useCreateCourse } from '@/composables'
import CourseForm, { type FormSchema } from '@/components/course/CourseForm.vue'

const dialogOpen = ref(false)

const props = defineProps<{
  event: EventResponse
}>()

const { mutate, isPending, isSuccess, error } = useCreateCourse()

watch(isSuccess, (isSuccess) => {
  if (isSuccess) {
    dialogOpen.value = false
  }
})

const handleSubmit = (values: FormSchema) => {
  mutate({
    eventId: props.event.id,
    payload: {
      courseNo: values.courseNo,
      cook: values.cook,
      dish: {
        name: values.dishName,
        mainIngredient: values.dishMainIngredient,
      },
    },
  })
}
</script>

<template>
  <Dialog v-model:open="dialogOpen">
    <DialogTrigger as-child>
      <slot />
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Nieuwe gang</DialogTitle>
        <DialogDescription>Evenement {{ event.date }} - {{ event.host }}</DialogDescription>
      </DialogHeader>
      <div class="grid gap-4 py-4">
        <CourseForm
          :is-pending="isPending"
          :errors="error?.payload?.fieldErrors"
          @submit="handleSubmit"
        />
      </div>
    </DialogContent>
  </Dialog>
</template>
