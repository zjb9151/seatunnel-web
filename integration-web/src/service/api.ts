import axios from 'axios'

const request = axios.create({ baseURL: '/api/v1', timeout: 30000 })

request.interceptors.response.use((res) => {
  const body = res.data
  if (body && body.code === 0) {
    return body.data
  }
  return Promise.reject(new Error(body?.msg || 'Request failed'))
})

export default request

export function getPlatformInfo() {
  return request.get('/platform/info')
}

export function getSeatunnelUiInfo() {
  return request.get('/ds/seatunnel-ui/info')
}

export function getDsUiInfo() {
  return request.get('/ds/info')
}
