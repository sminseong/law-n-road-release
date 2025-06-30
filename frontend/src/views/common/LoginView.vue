<script setup>
import { ref, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'
import { useLawyerStore } from '@/stores/lawyer'

const token = localStorage.getItem('token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const router = useRouter()
const route = useRoute()
const lawyerStore = useLawyerStore()

const tab = ref('client')
watchEffect(() => {
  const queryType = route.query.type
  if (queryType === 'lawyer' || queryType === 'client') {
    tab.value = queryType
  }
})

const clientId = ref('')
const password = ref('')

const naverLogin = () => {
  const redirectUri = encodeURIComponent(window.location.origin + '/login')
  window.location.href = `${__API_BASE__}/oauth2/authorization/naver?redirect_uri=${redirectUri}`
}

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

    const { accessToken, refreshToken, name, nickname, no, phone } = res.data

    localStorage.setItem('token', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('accountType', tab.value)
    localStorage.setItem('name', name)
    localStorage.setItem('nickname', nickname)
    localStorage.setItem('no', no)
    localStorage.setItem('phone', phone)

    axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`

    if (tab.value === 'lawyer') {
      await lawyerStore.fetchLawyerInfo(no)
    }

    const redirect = route.query.redirect || (tab.value === 'lawyer' ? '/lawyer' : '/')
    router.push(redirect)
  } catch (err) {
    console.error('❌ 로그인 실패:', err)
    const msg = err.response?.data || '로그인 정보가 일치하지 않습니다.'
    alert(`로그인 실패: ${msg}`)
  }
}

function parseJwt(token) {
  try {
    let base64 = token.split('.')[1]
    base64 = base64.replace(/-/g, '+').replace(/_/g, '/')
    while (base64.length % 4 !== 0) base64 += '='
    return JSON.parse(atob(base64))
  } catch (e) {
    console.error('❌ JWT 파싱 실패:', e)
    return null
  }
}

watchEffect(async () => {
  const queryToken = route.query.token
  if (queryToken) {
    try {
      const decoded = parseJwt(queryToken)
      const { no, nickname, role } = decoded

      localStorage.setItem('token', queryToken)
      localStorage.setItem('nickname', nickname)
      localStorage.setItem('no', no)
      localStorage.setItem('accountType', role.toLowerCase())

      axios.defaults.headers.common['Authorization'] = `Bearer ${queryToken}`

      if (role === 'LAWYER') {
        await lawyerStore.fetchLawyerInfo(no)
        router.replace('/lawyer')
      } else {
        router.replace('/')
      }
    } catch (e) {
      console.error('소셜 로그인 JWT 처리 실패:', e)
      alert('소셜 로그인 실패')
    }
  }
})
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
          <router-link to="/forgot-password" class="small">
            아이디/비밀번호 찾기
          </router-link>
        </div>
        <button type="submit" class="btn btn-primary w-100">로그인</button>
      </form>

      <div class="text-center mt-3">
        <span class="small text-muted">
          {{ tab === 'client'
            ? '아직 계정이 없으신가요?'
            : '변호사 계정이 없으신가요?' }}
        </span>
        <router-link
            :to="tab === 'client' ? '/signup/client' : '/signup/lawyer'"
            class="ms-1 small"
        >
          {{ tab === 'client' ? '회원가입' : '변호사 회원가입' }}
        </router-link>
      </div>

      <!-- 네이버 소셜 로그인 버튼: 의뢰인 탭일 때만 -->
      <div v-if="tab === 'client'" class="text-center mt-4">
        <button
            class="btn btn-outline-success no-border w-100 d-flex align-items-center justify-content-center"
            @click="naverLogin"
        >
          <span class="naver-logo">N</span>
          네이버로 로그인
        </button>
      </div>
    </section>
  </AccountFrame>
</template>

<style scoped>
.btn-group .btn {
  flex: 1 1 0;
}

/* N 로고 박스 스타일 */
.naver-logo {
  display: inline-block;
  width: 20px;
  height: 20px;
  background-color: #03c75a;
  color: white;
  font-weight: bold;
  font-size: 14px;
  line-height: 20px;
  text-align: center;
  margin-right: 8px;
  border-radius: 2px;
}

/* 네이버 버튼 테두리 제거 */
.no-border {
  border: none !important;
}
</style>
