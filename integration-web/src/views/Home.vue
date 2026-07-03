<template>
  <n-card title="Integration Platform">
    <p>统一入口：SeaTunnel + DolphinScheduler</p>
    <n-space vertical>
      <n-text depth="3">后端 API: /api/v1/platform/info</n-text>
      <pre v-if="info">{{ JSON.stringify(info, null, 2) }}</pre>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { NCard, NSpace, NText } from 'naive-ui'
import { getPlatformInfo } from '@/service/api'

const info = ref(null)
onMounted(async () => {
  try {
    info.value = await getPlatformInfo()
  } catch {
    info.value = { error: 'integration-server not reachable' } as any
  }
})
</script>
