<script lang="ts" setup>
import type { FormFieldErrors } from '@/lib/types.ts'
import * as z from 'zod'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { watch } from 'vue'
import { Input } from '@/components/ui/input'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Button } from '@/components/ui/button'

export type FormSchema = z.infer<typeof schema>

const props = defineProps<{
  isPending: boolean
  errors?: FormFieldErrors<FormSchema>
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
        <FormLabel>Gang</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="number" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="cook">
      <FormItem>
        <FormLabel>Persoon</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="text" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="dishName">
      <FormItem>
        <FormLabel>Gerecht</FormLabel>
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
