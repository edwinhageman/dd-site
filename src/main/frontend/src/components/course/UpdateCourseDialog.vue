<script lang="ts" setup>
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { computed, ref, watch } from 'vue'
import type { CourseResponse } from '@/generated/api'
import CourseForm, { type FormSchema } from '@/components/course/CourseForm.vue'
import { useUpdateCourse } from '@/composables/useUpdateCourse.ts'

const dialogOpen = ref(false)

const props = defineProps<{
  course: CourseResponse
}>()

const { mutate, isPending, isSuccess } = useUpdateCourse()

watch(isSuccess, (isSuccess) => {
  if (isSuccess) {
    dialogOpen.value = false
  }
})

const formData = computed(() => {
  return {
    courseNo: props.course.courseNo,
    cook: props.course.cook,
    dishName: props.course.dish.name,
    dishMainIngredient: props.course.dish.mainIngredient,
  }
})

const handleSubmit = (values: FormSchema) => {
  mutate({
    courseId: props.course.id,
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
        <DialogDescription
          >Evenement {{ course.event.date }} - {{ course.event.host }}</DialogDescription
        >
      </DialogHeader>
      <div class="grid gap-4 py-4">
        <CourseForm :is-pending="isPending" :data="formData" @submit="handleSubmit" />
      </div>
    </DialogContent>
  </Dialog>
</template>
