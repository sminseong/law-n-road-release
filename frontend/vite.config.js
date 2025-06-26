import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    // 프론트이므로 모든 외부 접속에 대해 허용
    //   host: '0.0.0.0', // 외부 접속 허용
    //   port: 5173,      // 원하는 포트

    // API 요청을 백엔드(8080 포트)로 프록시할 수 있게 설정
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

  // Vue 파일을 해석할 수 있게 하는 Vite의 Vue 플러그인을 활성화
  // .vue 파일을 컴포넌트로 사용할 수 있게 함
  plugins: [
    vue(),
  ],

  // @을 src 디렉토리로 매핑함
  // 예: @/components/Foo.vue → src/components/Foo.vue
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
