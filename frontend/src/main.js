// 전역 CSS
import './assets/styles/theme.min.css'  // 스타일 직접 import (optional: 이미 index.html에서 로드했으면 생략 가능)
import './assets/styles/main.css'             // 네가 만든 override CSS

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

