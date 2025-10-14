<script setup lang="ts">
import { computed } from 'vue'
import type { GrapeResponse } from '@/generated/api'
import { Field, FieldContent, FieldDescription, FieldLabel } from '@/components/ui/field'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Slider } from '@/components/ui/slider'
import { Button } from '@/components/ui/button'
import { Trash2 } from 'lucide-vue-next'
import type { AcceptableValue } from 'reka-ui'

const props = defineProps<{
  modelValue: { grapeId: number; percentage?: number }
  grapes: GrapeResponse[]
  removable?: boolean
}>()

const emits = defineEmits<{
  (e: 'update:modelValue', value: { grapeId: number; percentage?: number }): void
  (e: 'remove'): void
}>()

const percentDisplay = computed({
  get: () => [Math.round((props.modelValue.percentage ?? 0) * 100)],
  set: ([val]: [number]) =>
    emits('update:modelValue', { ...props.modelValue, percentage: val / 100 }),
})

function onGrapeChange(id: AcceptableValue) {
  emits('update:modelValue', { ...props.modelValue, grapeId: Number(id) })
}
</script>

<template>
  <Field orientation="horizontal">
    <FieldContent>
      <FieldLabel class="sr-only">Druif</FieldLabel>
      <Select :model-value="String(props.modelValue.grapeId)" @update:model-value="onGrapeChange">
        <SelectTrigger>
          <SelectValue placeholder="Selecteer druif" />
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectItem v-for="grape in grapes" :key="grape.id" :value="String(grape.id)">{{
              grape.name
            }}</SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>

      <Slider v-model="percentDisplay" :min="0" :max="100" :step="1" class="pt-4" />

      <FieldDescription class="text-right"> {{ percentDisplay[0] }}% </FieldDescription>
    </FieldContent>

    <Button v-if="removable" variant="ghost" size="icon" @click.prevent="$emit('remove')">
      <Trash2 class="text-destructive-foreground" />
    </Button>
  </Field>
</template>

<style scoped></style>
