<template>
  <div class="embed-page">
    <n-spin v-if="loading" size="large" />
    <n-alert v-else-if="errorMessage" type="warning" title="DolphinScheduler">{{ errorMessage }}</n-alert>
    <iframe v-else class="embed-frame" :src="embedUrl" frameborder="0" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { NAlert, NSpin } from 'naive-ui'
import { getDsUiInfo } from '@/service/api'

const props = defineProps<{ dsPath?: string }>()
const loading = ref(true)
const embedUrl = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const info: any = await getDsUiInfo()
    if (!info?.enabled || !info?.embedUrl) {
      errorMessage.value = info?.message || 'DolphinScheduler is not configured'
      return
    }
    let url = info.embedUrl
    if (!url.startsWith('http')) {
      url = `${window.location.origin}${url.startsWith('/') ? url : '/' + url}`
    }
    if (props.dsPath) {
      url = url.replace(/\/$/, '') + props.dsPath
    }
    embedUrl.value = url
  } catch (e: any) {
    errorMessage.value = e?.message || 'Failed to load DS UI'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.embed-page {
  height: calc(100vh - 64px);
}
.embed-frame {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
