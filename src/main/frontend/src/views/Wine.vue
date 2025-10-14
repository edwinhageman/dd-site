<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import WineForm from '@/components/wine/WineForm.vue'
import { useRoute, useRouter } from 'vue-router'
import { useGetWineById, useUpdateWine } from '@/composables'
import { computed, watch } from 'vue'

const router = useRouter()
const route = useRoute()

const id = computed(() => {
  const val = Number(route.params.id)
  return Number.isNaN(val) ? undefined : val
})

const { data: wine, isError } = useGetWineById(id)
const formData = computed(() => ({
  ...wine.value,
  styles: wine.value?.styles.map((style) => style.id) ?? [],
  grapeComposition:
    wine.value?.grapeComposition.map((composition) => ({
      grapeId: composition.grape.id,
      percentage: composition.percentage,
    })) ?? [],
}))

// redirect if wine could not be loaded or if id is not a number
watch(
  [isError, id],
  () => {
    if (isError.value || id.value === undefined) {
      router.push('/wines')
    }
  },
  { immediate: true },
)

const { mutate: updateWine, isPending, error } = useUpdateWine()
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <div class="mx-auto w-full max-w-6xl">
      <Card>
        <CardHeader class="flex">
          <CardTitle>Wijn bewerken</CardTitle>
        </CardHeader>
        <CardContent>
          <WineForm
            v-if="wine"
            :is-pending="isPending"
            :errors="error?.payload?.fieldErrors"
            :data="formData"
            @submit="(payload) => updateWine({ wineId: id!, payload })"
          />
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<style scoped></style>
