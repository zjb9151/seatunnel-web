<template>
  <n-layout has-sider style="height: 100vh">
    <n-layout-sider bordered collapse-mode="width" :collapsed-width="64" :width="220" show-trigger>
      <div class="logo">Integration</div>
      <n-menu :options="menuOptions" :value="activeKey" @update:value="onMenuSelect" />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered class="header">
        <span>{{ headerTitle }}</span>
        <n-tag type="success" size="small">:9000</n-tag>
      </n-layout-header>
      <n-layout-content content-style="padding: 12px;">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NTag } from 'naive-ui'
import type { MenuOption } from 'naive-ui'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const menuOptions = computed<MenuOption[]>(() => [
  { label: t('menu.home'), key: '/home' },
  { label: 'Runtime', key: '/runtime' },
  { label: t('menu.ds_projects'), key: '/ds/projects' },
  { label: t('menu.ds_workflows'), key: '/ds/workflows' },
  { type: 'divider', key: 'd1' },
  { label: t('menu.st_tasks'), key: '/seatunnel/tasks' },
  { label: t('menu.st_instances'), key: '/seatunnel/instances' },
  { label: t('menu.st_virtual_tables'), key: '/seatunnel/virtual-tables' },
  { label: t('menu.st_datasource'), key: '/seatunnel/datasource' }
])

const activeKey = computed(() => route.path)
const headerTitle = computed(() => {
  const item = menuOptions.value.find((m) => m.key === route.path)
  return (item?.label as string) || 'Integration Platform'
})

function onMenuSelect(key: string) {
  router.push(key)
}
</script>

<style scoped>
.logo {
  padding: 16px;
  font-weight: 700;
  font-size: 16px;
}
.header {
  padding: 0 16px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
