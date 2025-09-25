<script lang="ts" setup>
import type { CourseResponse } from '@/generated/api'
import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import UpdateCourseDialog from '@/components/course/UpdateCourseDialog.vue'
import DeleteCourseDialog from '@/components/course/DeleteCourseDialog.vue'

defineProps<{
  course: Required<CourseResponse>
}>()
</script>

<template>
  <Card>
    <CardHeader class="flex justify-between">
      <div>
        <CardTitle>Gang {{ course.courseNo }}</CardTitle>
        <CardDescription>{{ course.cook }}</CardDescription>
      </div>
      <div>
        <CardTitle>{{ course.dish.name }}</CardTitle>
        <CardDescription>{{ course.dish.mainIngredient }}</CardDescription>
      </div>
      <div>
        <CardTitle>Wijn | Producent</CardTitle>
        <CardDescription>Type | Land | Regio | Jaar</CardDescription>
      </div>
    </CardHeader>
    <CardFooter>
      <div class="ml-auto space-x-2">
        <UpdateCourseDialog :course="course">
          <Button>Bewerken</Button>
        </UpdateCourseDialog>
        <DeleteCourseDialog :course="course">
          <Button variant="destructive">Verwijderen</Button>
        </DeleteCourseDialog>
      </div>
    </CardFooter>
  </Card>
</template>
