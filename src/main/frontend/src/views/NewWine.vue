<script setup lang="ts">
import WineForm from '@/components/wine/WineForm.vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useCreateWine } from '@/composables'
import { watchEffect } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const { data: createdWine, mutate: createWine, isPending, error } = useCreateWine()
watchEffect(() => {
  if (!isPending.value && !error.value && createdWine.value) {
    router.push({ name: 'wine', params: { id: createdWine.value.id } })
  }
})
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <div class="mx-auto w-full max-w-6xl">
      <Card>
        <CardHeader class="flex">
          <CardTitle>Nieuwe wijn</CardTitle>
        </CardHeader>
        <CardContent>
          <WineForm
            :is-pending="isPending"
            :errors="error?.payload?.fieldErrors"
            @submit="(payload) => createWine({ payload })"
          />
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<style scoped></style>
