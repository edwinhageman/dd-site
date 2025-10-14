<script lang="ts" setup>
import type { CourseResponse } from '@/generated/api'
import { Button } from '@/components/ui/button'
import UpdateCourseDialog from '@/components/course/UpdateCourseDialog.vue'
import DeleteCourseDialog from '@/components/course/DeleteCourseDialog.vue'
import { Item, ItemActions, ItemContent, ItemDescription, ItemTitle } from '@/components/ui/item'
import { Trash2 } from 'lucide-vue-next'

defineProps<{
  course: CourseResponse
}>()
</script>

<template>
  <Item variant="outline">
    <ItemContent>
      <ItemTitle>Gang {{ course.courseNo }} - {{ course.cook }}</ItemTitle>
      <ItemDescription> {{ course.dish.name }} - {{ course.dish.mainIngredient }}</ItemDescription>
    </ItemContent>
    <ItemActions>
      <UpdateCourseDialog :course="course">
        <Button variant="outline" size="sm">Bewerken</Button>
      </UpdateCourseDialog>
      <DeleteCourseDialog :course="course">
        <Button variant="ghost" size="icon">
          <Trash2 class="text-destructive-foreground" />
        </Button>
      </DeleteCourseDialog>
    </ItemActions>
  </Item>
</template>
