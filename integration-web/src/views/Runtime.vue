<template>
  <n-card title="Runtime Versions">
    <n-space vertical size="large">
      <n-alert type="info">
        Manage official DolphinScheduler / SeaTunnel releases. Install downloads from Apache; switch changes active version and restarts services.
      </n-alert>
      <n-data-table :columns="columns" :data="rows" :loading="loading" />
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { h, onMounted, ref } from 'vue'
import {
  NAlert,
  NButton,
  NCard,
  NDataTable,
  NSelect,
  NSpace,
  useMessage,
  type DataTableColumns
} from 'naive-ui'
import {
  getRuntimeComponents,
  installRuntimeVersion,
  switchRuntimeVersion
} from '@/service/api'

type Row = {
  componentId: string
  displayName: string
  activeVersion: string
  installed: boolean
  bundledAvailable: boolean
  availableVersions: string[]
  selectedVersion: string
}

const loading = ref(false)
const rows = ref<Row[]>([])
const message = useMessage()

const columns: DataTableColumns<Row> = [
  { title: 'Component', key: 'displayName' },
  { title: 'Active', key: 'activeVersion' },
  {
    title: 'Status',
    key: 'installed',
    render: (row) => (row.installed ? 'installed' : 'missing')
  },
  {
    title: 'Version',
    key: 'selectedVersion',
    render: (row) =>
      h(NSelect, {
        value: row.selectedVersion,
        options: row.availableVersions.map((v) => ({ label: v, value: v })),
        style: 'width: 180px',
        onUpdateValue: (v: string) => {
          row.selectedVersion = v
        }
      })
  },
  {
    title: 'Actions',
    key: 'actions',
    render: (row) =>
      h(NSpace, null, {
        default: () => [
          h(
            NButton,
            {
              size: 'small',
              type: 'primary',
              onClick: () => onInstall(row)
            },
            { default: () => 'Download & Install' }
          ),
          h(
            NButton,
            {
              size: 'small',
              disabled: row.selectedVersion === row.activeVersion,
              onClick: () => onSwitch(row)
            },
            { default: () => 'Switch' }
          )
        ]
      })
  }
]

async function load() {
  loading.value = true
  try {
    const list: any[] = await getRuntimeComponents()
    rows.value = (list || []).map((item) => ({
      componentId: item.componentId,
      displayName: item.displayName,
      activeVersion: item.activeVersion,
      installed: item.installed,
      bundledAvailable: item.bundledAvailable,
      availableVersions: item.availableVersions || [item.activeVersion],
      selectedVersion: item.activeVersion
    }))
  } catch (e: any) {
    message.error(e?.message || 'Failed to load runtime info')
  } finally {
    loading.value = false
  }
}

async function onInstall(row: Row) {
  try {
    await installRuntimeVersion(row.componentId, row.selectedVersion)
    message.success(`${row.displayName} ${row.selectedVersion} installed`)
    await load()
  } catch (e: any) {
    message.error(e?.message || 'Install failed')
  }
}

async function onSwitch(row: Row) {
  try {
    await switchRuntimeVersion(row.componentId, row.selectedVersion)
    message.success(`Switched ${row.displayName} to ${row.selectedVersion}`)
    await load()
  } catch (e: any) {
    message.error(e?.message || 'Switch failed')
  }
}

onMounted(load)
</script>
