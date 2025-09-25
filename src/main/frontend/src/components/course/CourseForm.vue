<script lang="ts" setup>
import type { PartialString } from '@/lib/types.ts'
import { z } from 'zod'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { watch } from 'vue'
import { Input } from '@/components/ui/input'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Button } from '@/components/ui/button'

export type FormSchema = z.infer<typeof schema>
export type FieldErrors = PartialString<FormSchema>

const props = defineProps<{
  isPending: boolean
  errors?: FieldErrors
  data?: FormSchema
}>()

const emits = defineEmits<{
  (e: 'submit', values: FormSchema): void
}>()

const schema = z.object({
  courseNo: z.coerce.number().positive(),
  cook: z.string().trim().min(1),
  dishName: z.string().trim().min(1),
  dishMainIngredient: z.string().optional(),
})

const form = useForm({
  validationSchema: toTypedSchema(schema),
})

watch(
  () => props.data,
  (data) => {
    form.setValues({
      courseNo: data?.courseNo ?? '',
      cook: data?.cook ?? '',
      dishName: data?.dishName ?? '',
      dishMainIngredient: data?.dishMainIngredient ?? '',
    })
  },
  { immediate: true },
)

watch(
  () => props.errors,
  (errors) => {
    form.setErrors(errors ?? {})
  },
  { immediate: true },
)

const onSubmit = form.handleSubmit((values) => {
  emits('submit', values)
})
</script>

<template>
  <form @submit="onSubmit" class="space-y-2">
    <FormField v-slot="{ componentField }" name="courseNo">
      <FormItem>
        <FormLabel>Gangnummer</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="number" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="cook">
      <FormItem>
        <FormLabel>Kok</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="text" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="dishName">
      <FormItem>
        <FormLabel>Naam gerecht</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="text" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="dishMainIngredient">
      <FormItem>
        <FormLabel>Hoofdingredient</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="text" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <div class="text-right pt-2">
      <Button :disabled="isPending" type="submit">Verzenden</Button>
    </div>
  </form>
</template>
