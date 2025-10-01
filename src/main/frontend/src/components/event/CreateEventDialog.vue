<script lang="ts" setup>
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { ref, watch } from 'vue'
import EventForm, { type FormSchema } from '@/components/event/EventForm.vue'
import { useCreateEvent } from '@/composables'
import { Button } from '@/components/ui/button'

const dialogOpen = ref(false)

const { mutate, isPending, isSuccess } = useCreateEvent()

watch(isSuccess, (isSuccess) => {
  if (isSuccess) {
    dialogOpen.value = false
  }
})

function handleSubmit(values: FormSchema) {
  mutate({ payload: values })
}
</script>

<template>
  <Dialog v-model:open="dialogOpen">
    <DialogTrigger as-child>
      <slot>
        <Button>Nieuw evenement</Button>
      </slot>
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Nieuw evenement</DialogTitle>
        <DialogDescription> </DialogDescription>
      </DialogHeader>
      <div class="grid gap-4 py-4">
        <EventForm :is-pending="isPending" @submit="handleSubmit" />
      </div>
    </DialogContent>
  </Dialog>
</template>
