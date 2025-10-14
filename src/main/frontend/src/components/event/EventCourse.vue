<script lang="ts" setup>
import type { CourseResponse } from '@/generated/api'
import { Button } from '@/components/ui/button'
import UpdateCourseDialog from '@/components/course/UpdateCourseDialog.vue'
import DeleteCourseDialog from '@/components/course/DeleteCourseDialog.vue'
import { Item, ItemActions, ItemContent, ItemDescription, ItemTitle } from '@/components/ui/item'
import { Dot, Hamburger, Trash2, Wine } from 'lucide-vue-next'

defineProps<{
  course: CourseResponse
}>()
</script>

<template>
  <Item variant="outline">
    <ItemContent>
      <ItemTitle>Gang {{ course.courseNo }} - {{ course.cook }}</ItemTitle>
      <ItemDescription>
        <span
          ><Hamburger class="inline" :size="16" /> {{ course.dish.name }} -
          {{ course.dish.mainIngredient }} <Dot class="inline mx-1" :size="16" />
          <Wine class="inline" :size="16" /> Naam wijn - wijnstijl1, wijnstijl2
        </span>
      </ItemDescription>
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
