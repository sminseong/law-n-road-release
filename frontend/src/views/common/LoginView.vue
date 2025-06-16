<script setup>
import { ref, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const token = localStorage.getItem('token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const router = useRouter()
const route = useRoute()

const tab = ref('client')

watchEffect(() => {
  const queryType = route.query.type
  if (queryType === 'lawyer' || queryType === 'client') {
    tab.value = queryType
  }
})

const clientId = ref('')
const password = ref('')
const remember = ref(false)

const submitLogin = async () => {
  try {
    console.log('📨 로그인 요청 데이터:', {
      clientId: clientId.value,
      password: password.value,
      type: tab.value
    })

    const res = await axios.post('/api/auth/login', {
      clientId: clientId.value,
      password: password.value,
      type: tab.value
    })

    console.log('✅ 로그인 성공 응답:', res.data)

    const { accessToken, refreshToken, name, nickname } = res.data

    localStorage.setItem('token', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('accountType', tab.value)
    localStorage.setItem('name', name)
    localStorage.setItem('nickname', nickname)
    console.log('🚨🚨🚨 localStorage 저장 완료! 🚨🚨🚨')
    console.log('TOKEN:', localStorage.getItem('token'))
    console.log('ACCOUNT TYPE:', localStorage.getItem('accountType'))

    // localStorage 저장 확인 로그
    console.log('💾 localStorage 저장된 데이터:', {
      token: localStorage.getItem('token'),
      refreshToken: localStorage.getItem('refreshToken'),
      accountType: localStorage.getItem('accountType'),
      name: localStorage.getItem('name'),
      nickname: localStorage.getItem('nickname')
    })

    axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`

    // ✅ 리다이렉트 처리 추가
    const redirect = route.query.redirect || (tab.value === 'lawyer' ? '/lawyer' : '/')
    router.push(redirect)
    // router.push(tab.value === 'lawyer' ? '/lawyer' : '/')

  } catch (err) {
    console.error('❌ 로그인 실패:', err)

    if (err.response) {
      console.error('📡 상태코드:', err.response.status)
      console.error('📩 에러 메시지:', err.response.data)
      alert(`로그인 실패: ${err.response.data}`)
    } else {
      alert('네트워크 오류 또는 서버 응답 없음')
    }
  }
}
</script>

<template>
  <AccountFrame>
    <section class="w-100" style="max-width: 420px;">
      <div class="btn-group w-100 mb-4">
        <button
            class="btn"
            :class="tab === 'client' ? 'btn-primary' : 'btn-outline-secondary'"
            @click="tab = 'client'"
        >
          의뢰인 로그인
        </button>
        <button
            class="btn"
            :class="tab === 'lawyer' ? 'btn-primary' : 'btn-outline-secondary'"
            @click="tab = 'lawyer'"
        >
          변호사 로그인
        </button>
      </div>

      <form @submit.prevent="submitLogin">
        <div class="mb-3">
          <input
              v-model="clientId"
              type="text"
              class="form-control"
              placeholder="아이디"
              required
          />
        </div>

        <div class="mb-3">
          <input
              v-model="password"
              type="password"
              class="form-control"
              placeholder="비밀번호"
              required
          />
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
          <div class="form-check">
            <input
                v-model="remember"
                type="checkbox"
                class="form-check-input"
                id="rememberMe"
            />
            <label class="form-check-label" for="rememberMe">자동 로그인</label>
          </div>
          <router-link to="/forgot-password" class="small">아이디/비밀번호 찾기</router-link>
        </div>
        <button type="submit" class="btn btn-primary w-100">로그인</button>
      </form>

      <div class="text-center mt-3">
        <span class="small text-muted">
          {{ tab === 'client' ? '아직 계정이 없으신가요?' : '변호사 계정이 없으신가요?' }}
        </span>
        <router-link
            :to="tab === 'client' ? '/client/signup' : '/lawyer/signup'"
            class="ms-1 small"
        >
          {{ tab === 'client' ? '회원가입' : '변호사 회원가입' }}
        </router-link>
      </div>
    </section>
  </AccountFrame>
</template>

<style scoped>
.btn-group .btn {
  flex: 1 1 0;
}
</style>