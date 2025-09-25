<script lang="ts" setup>
defineProps<{
  id: number
}>()
</script>

<template>
  <div><RouterLink :to="{ name: 'event', params: { id } }">bekijk</RouterLink></div>
</template>
