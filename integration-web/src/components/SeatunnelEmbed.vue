<template>
  <div class="embed-page">
    <n-spin v-if="loading" size="large" />
    <n-alert v-else-if="errorMessage" type="warning" :title="title">{{ errorMessage }}</n-alert>
    <iframe v-else class="embed-frame" :src="embedUrl" frameborder="0" />
  </div>
</template>

<script setup lang="ts">
import { NAlert, NSpin } from 'naive-ui'
import { useSeatunnelEmbed } from '@/shared/use-seatunnel-embed'

const props = defineProps<{ seatunnelPath: string; title: string }>()
const { loading, embedUrl, errorMessage } = useSeatunnelEmbed(props.seatunnelPath)
</script>

<style scoped>
.embed-page {
  height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
}
.embed-frame {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
