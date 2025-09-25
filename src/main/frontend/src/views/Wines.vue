<script setup lang="ts">
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import WineDataTable from '@/components/wine/WineDataTable.vue'
</script>

<template>
  <div class="flex flex-1 flex-col gap-4 px-4 py-10 bg-white">
    <Card>
      <CardHeader class="flex">
        <CardTitle>Wijnen</CardTitle>
      </CardHeader>
      <CardContent class="p-0">
        <WineDataTable />
      </CardContent>
    </Card>
  </div>
</template>
