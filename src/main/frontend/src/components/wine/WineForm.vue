<script lang="ts" setup>
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { computed, watch } from 'vue'
import type { FormFieldErrors } from '@/lib/types.ts'
import { useListGrapes, useListWineStyles, usePageRequest } from '@/composables'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { FieldGroup, FieldLegend, FieldSet } from '@/components/ui/field'
import GrapeCompositionForm from '@/components/wine/GrapeCompositionForm.vue'

export type FormSchema = z.infer<typeof schema>

const props = defineProps<{
  isPending?: boolean
  errors?: FormFieldErrors<FormSchema>
  data?: FormSchema
}>()

const emits = defineEmits<{
  (e: 'submit', values: FormSchema): void
}>()

const vintageSchema = z
  .string()
  .trim()
  .regex(/^[0-9]{4}$/, 'Wijnjaar moet uit 4 cijfers bestaan')
  .refine((val) => {
    const vintage = Number(val)
    return vintage >= 1800 && vintage <= new Date().getFullYear()
  }, `Wijnjaar moet tussen 1800 en ${new Date().getFullYear()} liggen`)

const schema = z.object({
  name: z.string().trim().min(1),
  winery: z.string().trim().optional(),
  country: z.string().trim().optional(),
  region: z.string().trim().optional(),
  appellation: z.string().trim().optional(),
  vintage: vintageSchema.optional(),
  vivinoUrl: z.url({ hostname: /^vivino\.com$/, normalize: true }).optional(),
  styles: z.array(z.number()),
  grapeComposition: z.array(
    z.object({
      grapeId: z.number(),
      percentage: z.number().min(0).max(1),
    }),
  ),
})

const form = useForm({
  validationSchema: toTypedSchema(schema),
})

watch(
  () => props.data,
  (data) => {
    form.setValues({
      name: data?.name ?? '',
      winery: data?.winery ?? undefined,
      country: data?.country ?? undefined,
      region: data?.region ?? undefined,
      appellation: data?.appellation ?? undefined,
      vintage: data?.vintage ?? undefined,
      vivinoUrl: data?.vivinoUrl ?? undefined,
      styles: data?.styles ?? [],
      grapeComposition: data?.grapeComposition ?? [],
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

const { pageRequest } = usePageRequest({ initialSize: Number.MAX_SAFE_INTEGER })

const { data: stylePage } = useListWineStyles(pageRequest)
const styles = computed(() => stylePage.value?.content ?? [])

const { data: grapePage } = useListGrapes(pageRequest)
const grapes = computed(() => grapePage.value?.content ?? [])

const onSubmit = form.handleSubmit((values) => {
  // filter duplicate grapes
  values.grapeComposition = [
    ...new Map(values.grapeComposition.map((v) => [v.grapeId, v])).values(),
  ]
  emits('submit', values)
})

const totalPercent = computed(() =>
  Math.round(
    (form.values.grapeComposition?.reduce((s, i) => s + (i.percentage ?? 0), 0) ?? 0) * 100,
  ),
)

function addGrape() {
  if (!grapes.value?.length) {
    return
  }
  form.setFieldValue('grapeComposition', [
    ...(form.values.grapeComposition ?? []),
    { grapeId: grapes.value[0].id, percentage: 0 },
  ])
}

function removeGrapeAt(idx: number) {
  const copy = [...(form.values.grapeComposition ?? [])]
  copy.splice(idx, 1)
  form.setFieldValue('grapeComposition', copy)
}

function updateGrapeAt(idx: number, composition: { grapeId: number; percentage: number }) {
  const copy = [...(form.values.grapeComposition ?? [])]
  copy[idx] = composition
  form.setFieldValue('grapeComposition', copy)
}
</script>

<template>
  <form @submit="onSubmit" class="space-y-2">
    <div class="grid auto-rows-min gap-4 md:grid-cols-2">
      <div>
        <FieldGroup>
          <FieldSet>
            <FieldLegend>Wijn informatie</FieldLegend>
            <FormField v-slot="{ componentField }" name="name">
              <FormItem>
                <FormLabel>Naam</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="winery">
              <FormItem>
                <FormLabel>Wijnhuis</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="country">
              <FormItem>
                <FormLabel>Land</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="region">
              <FormItem>
                <FormLabel>Regio</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="appellation">
              <FormItem>
                <FormLabel>Appellatie</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="vintage">
              <FormItem>
                <FormLabel>Wijnjaar</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="vivinoUrl">
              <FormItem>
                <FormLabel>Vivino url</FormLabel>
                <FormControl>
                  <Input v-bind="componentField" type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <FormField v-slot="{ componentField }" name="styles">
              <FormItem>
                <FormLabel>Wijnstijlen</FormLabel>

                <Select v-bind="componentField" multiple>
                  <FormControl>
                    <SelectTrigger class="w-full">
                      <SelectValue placeholder="Selecteer één of meerdere wijnstijlen" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectGroup>
                      <SelectItem v-for="style in styles" :key="style.id" :value="style.id">
                        {{ style.name }}
                      </SelectItem>
                    </SelectGroup>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            </FormField>
          </FieldSet>
        </FieldGroup>
      </div>

      <div>
        <FieldGroup>
          <FieldSet>
            <FieldLegend>Druiven</FieldLegend>

            <div class="space-y-2">
              <GrapeCompositionForm
                v-for="(item, idx) in form.values.grapeComposition"
                :key="idx"
                :model-value="item"
                :grapes="grapes"
                :removable="true"
                @update:modelValue="(v) => updateGrapeAt(idx, v)"
                @remove="removeGrapeAt(idx)"
              />
            </div>
            <div class="flex items-center justify-between pt-2">
              <div
                :class="['text-sm', totalPercent > 100 ? 'text-red-600' : 'text-muted-foreground']"
              >
                Totaal: {{ totalPercent }}%
              </div>
              <Button type="button" variant="secondary" @click="addGrape" :disabled="!grapes.length"
                >Druif toevoegen</Button
              >
            </div>
          </FieldSet>
        </FieldGroup>
      </div>
    </div>

    <div class="text-right pt-2">
      <Button :disabled="isPending" type="submit">Verzenden</Button>
    </div>
  </form>
</template>
