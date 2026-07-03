import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  server: {
    port: 9001,
    proxy: {
      '/api': { target: 'http://127.0.0.1:9000', changeOrigin: true },
      '/dolphinscheduler': { target: 'http://127.0.0.1:9000', changeOrigin: true },
      '/seatunnel-ui': { target: 'http://127.0.0.1:9000', changeOrigin: true }
    }
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true
  }
})
