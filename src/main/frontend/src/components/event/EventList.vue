<script lang="ts" setup>
import { Configuration, EventControllerApi } from '@/generated/api'
import { useQuery } from '@tanstack/vue-query'
import { Button } from '@/components/ui/button'

const apiConfig = new Configuration({
  basePath: 'http://localhost:3000'
})
const apiClient = new EventControllerApi(apiConfig)

const { isPending, isError, data, error } = useQuery({
  queryKey: ['events'],
  queryFn: async () => {
    const response = await apiClient.listEvents({})
    return response.data
  },
})
</script>

<template>
  <div>
    <div v-if="isPending">Loading...</div>
    <div v-else-if="isError">{{ error }}</div>
    <div v-else>
      <div v-if="!data?.page?.totalElements">No data</div>
      <div v-else>
        <div><Button>Test</Button></div>
        <div v-for="event in data?.content" :key="event.id">
          <div>{{ event.host }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
