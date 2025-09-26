<script lang="ts" setup>
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { watch } from 'vue'
import type { PartialString } from '@/lib/types.ts'

export type FormSchema = z.infer<typeof schema>
export type FieldErrors = PartialString<FormSchema>

const props = defineProps<{
  isPending?: boolean
  errors?: FieldErrors
  data?: FormSchema
}>()

const emits = defineEmits<{
  (e: 'submit', values: FormSchema): void
}>()

const schema = z.object({
  date: z.iso.date(),
  host: z.string().trim().min(1),
  location: z.string().optional(),
})

const form = useForm({
  validationSchema: toTypedSchema(schema),
})

watch(
  () => props.data,
  (data) => {
    form.setValues({
      date: data?.date ?? '',
      host: data?.host ?? '',
      location: data?.location ?? '',
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
    <FormField v-slot="{ componentField }" name="date">
      <FormItem>
        <FormLabel>Datum</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="date" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="host">
      <FormItem>
        <FormLabel>Host</FormLabel>
        <FormControl>
          <Input v-bind="componentField" type="text" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="location">
      <FormItem>
        <FormLabel>Locatie</FormLabel>
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
