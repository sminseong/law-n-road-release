import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    proxy: {
        "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api') // 필요하면 수정
        },
        "/mail": {
            target: "http://localhost:8080",
            changeOrigin: true
        },
        '/uploads': { // 템플릿에서 필요합니다 지우지 마세요
            target: 'http://localhost:8080',
            changeOrigin: true,
            rewrite: path => path.replace(/^\/uploads/, '/uploads')
        }


    }
  },
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
