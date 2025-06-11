 src/views/account/LoginView.vue
<script setup>
import { ref, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

// 🔐 JWT 토큰이 있으면 모든 axios 요청에 자동 추가
const token = localStorage.getItem('token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const router = useRouter()
const route = useRoute()

const tab = ref('client') // 기본값: 의뢰인

watchEffect(() => {
  const queryType = route.query.type
  if (queryType === 'lawyer' || queryType === 'client') {
    tab.value = queryType
  }
})

const email = ref('')
const password = ref('')
const remember = ref(false)

// const submitLogin = async () => {
//   // try {
//   //   const res = await axios.post('/api/auth/login', {
//   //     email: email.value,
//   //     password: password.value,
//   //     type: tab.value
//   //   })
//   //
//   //   const { token } = res.data
//   //   localStorage.setItem('token', token)
//   //   localStorage.setItem('accountType', tab.value)
//   //
//   //   //router.push(tab.value === 'lawyer' ? '/lawyer' : '/client')
//   //   router.push(tab.value === 'lawyer' ? '/lawyer' : '/client/mypage')
//   //
//   // } catch (err) {
//   //   alert('로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다.')
//   // }
//
//
//
//
//   try {
//     console.log('📨 로그인 요청 데이터:', {
//       email: email.value,
//       password: password.value,
//       type: tab.value
//     })
//
//
//     const res = await axios.post('/api/auth/login', {
//       email: email.value,
//       password: password.value,
//       type: tab.value
//     })
//
//     console.log('✅ 로그인 성공 응답:', res.data)
//
//     const { token, name } = res.data
//     localStorage.setItem('token', token)
//     axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
//     localStorage.setItem('accountType', tab.value)
//     localStorage.setItem('name', name)
//     // ✅ 콘솔에 저장된 값 출력
//     console.log('✅ 저장된 localStorage 값:')
//     console.log('🔐 token:', localStorage.getItem('token'))
//     console.log('🙍 name:', localStorage.getItem('name'))
//     console.log('🧭 accountType:', localStorage.getItem('accountType'))
//
//
//
//     router.push(tab.value === 'lawyer' ? '/lawyer' : '/client/mypage')
//
//   } catch (err) {
//     console.error('❌ 로그인 실패:', err)
//
//     if (err.response) {
//       console.error('📡 상태코드:', err.response.status)
//       console.error('📩 에러 메시지:', err.response.data)
//
//       alert(`로그인 실패: ${err.response.data}`)
//     } else {
//       alert('네트워크 오류 또는 서버 응답 없음')
//     }
//   }
//
//
// }

// 수정된 submitLogin 함수 부분
const submitLogin = async () => {
  try {
    console.log('📨 로그인 요청 데이터:', {
      email: email.value,
      password: password.value,
      type: tab.value
    })

    const res = await axios.post('/api/auth/login', {
      email: email.value,
      password: password.value,
      type: tab.value
    })

    console.log('✅ 로그인 성공 응답:', res.data)

    // ✅ 수정: accessToken으로 변경
    const { accessToken, refreshToken, name } = res.data

    // ✅ accessToken을 token으로 저장
    localStorage.setItem('token', accessToken)
    // ✅ refreshToken도 함께 저장 (토큰 갱신용)
    localStorage.setItem('refreshToken', refreshToken)

    axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem('accountType', tab.value)
    localStorage.setItem('name', name)

    // ✅ 콘솔에 저장된 값 출력
    console.log('✅ 저장된 localStorage 값:')
    console.log('🔐 token:', localStorage.getItem('token'))
    console.log('🔄 refreshToken:', localStorage.getItem('refreshToken'))
    console.log('🙍 name:', localStorage.getItem('name'))
    console.log('🧭 accountType:', localStorage.getItem('accountType'))

    router.push(tab.value === 'lawyer' ? '/lawyer' : '/client/mypage')

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
      <!-- 탭 전환 버튼 -->
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

      <!-- 로그인 폼 -->
      <form @submit.prevent="submitLogin">
        <div class="mb-3">
          <input
              v-model="email"
              type="email"
              class="form-control"
              placeholder="이메일"
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
        <!-- 탭에 따라 서로 다른 라우트로 이동 -->
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
