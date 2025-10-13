<script lang="ts" setup>
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { watch } from 'vue'
import type { FormFieldErrors } from '@/lib/types.ts'

export type FormSchema = z.infer<typeof schema>

const props = defineProps<{
  isPending?: boolean
  errors?: FormFieldErrors<FormSchema>
  data?: FormSchema
}>()

const emits = defineEmits<{
  (e: 'submit', values: FormSchema): void
}>()

const schema = z.object({
  name: z.string().trim().min(1),
})

const form = useForm({
  validationSchema: toTypedSchema(schema),
})

watch(
  () => props.data,
  (data) => {
    form.setValues({
      name: data?.name ?? '',
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
    <FormField v-slot="{ componentField }" name="name">
      <FormItem>
        <FormLabel>Naam</FormLabel>
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
