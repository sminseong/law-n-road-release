// 전역 CSS
import './assets/styles/theme.min.css'  // 부트스트랩 테마 (선택)
import './assets/styles/main.css'       // 커스텀 CSS
import 'bootstrap'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// ✅ axios import 먼저 해줘야 함
import axios from 'axios'

// ✅ 전역 axios Authorization 헤더 설정
const token = localStorage.getItem('token')
if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

// ✅ Vue 앱 생성 및 마운트
const app = createApp(App)

app.use(createPinia())
app.use(router)









app.mount('#app')
