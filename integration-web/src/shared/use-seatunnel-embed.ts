import { onMounted, ref } from 'vue'
import { getSeatunnelUiInfo } from '@/service/api'

export function useSeatunnelEmbed(seatunnelPath: string) {
  const loading = ref(true)
  const embedUrl = ref('')
  const errorMessage = ref('')

  const resolveIframeSrc = (baseUrl: string, path: string) => {
    let normalizedBase = baseUrl
    if (!normalizedBase.startsWith('http')) {
      const prefix = normalizedBase.startsWith('/') ? normalizedBase : `/${normalizedBase}`
      normalizedBase = `${window.location.origin}${prefix}`
    }
    normalizedBase = normalizedBase.endsWith('/') ? normalizedBase.slice(0, -1) : normalizedBase
    const normalizedPath = path.startsWith('/') ? path : `/${path}`
    const query = new URLSearchParams({ embed: '1', datasourceSource: 'dolphinscheduler' })
    return `${normalizedBase}/#${normalizedPath}?${query.toString()}`
  }

  onMounted(async () => {
    try {
      const info: any = await getSeatunnelUiInfo()
      if (!info?.enabled || !info?.embedBaseUrl) {
        errorMessage.value = info?.message || 'SeaTunnel Web is not configured'
        return
      }
      embedUrl.value = resolveIframeSrc(info.embedBaseUrl, seatunnelPath)
    } catch (err: any) {
      errorMessage.value = err?.message || 'Failed to load SeaTunnel UI'
    } finally {
      loading.value = false
    }
  })

  return { loading, embedUrl, errorMessage }
}
