<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const router = useRouter()

// 탭 상태
const tab = ref('id')

// 공통
const email = ref('')
const authCode = ref('')
const isEmailVerified = ref(false)

// 아이디 찾기용
const nameForId = ref('')
const foundId = ref(null)

// 비밀번호 재설정용
const clientId = ref('')
const nameForPw = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

// 인증번호 요청
const requestEmailCode = async () => {
  if (!email.value) return alert("이메일을 입력해주세요.")
  try {
    await axios.post('/mail/send', null, { params: { email: email.value } })
    alert("✅ 인증번호가 전송되었습니다.")
  } catch (err) {
    console.error(err)
    alert("인증번호 요청 실패")
  }
}

// 인증번호 확인
const verifyEmailCode = async () => {
  if (!authCode.value || !email.value) return alert("이메일과 인증번호를 입력해주세요.")
  try {
    const res = await axios.post('/mail/verify', null, {
      params: { email: email.value, code: authCode.value }
    })
    if (res.data === '인증 성공') {
      isEmailVerified.value = true
      alert('✅ 인증 완료')
    } else {
      alert('❌ 인증번호 불일치')
    }
  } catch (err) {
    console.error(err)
    alert("인증 실패")
  }
}

// 아이디 찾기
const findId = async () => {
  if (!nameForId.value || !email.value) return alert('이름과 이메일을 입력하세요.')
  if (!isEmailVerified.value) return alert('이메일 인증을 먼저 완료하세요.')
  try {
    const res = await axios.post('/api/auth/find-id', {
      fullName: nameForId.value,
      email: email.value
    })
    foundId.value = res.data.clientId
  } catch (err) {
    console.error(err)
    alert('일치하는 계정이 없습니다.')
    foundId.value = null
  }
}

// 비밀번호 재설정
const resetPassword = async () => {
  if (!clientId.value || !nameForPw.value || !email.value) {
    return alert('아이디, 이름, 이메일을 모두 입력해주세요.')
  }
  if (!isEmailVerified.value) return alert('이메일 인증을 먼저 완료해주세요.')
  if (newPassword.value !== confirmPassword.value) {
    return alert('비밀번호가 일치하지 않습니다.')
  }

  try {
    await axios.post('/api/auth/reset-password', {
      email: email.value,
      newPassword: newPassword.value
    })
    alert('✅ 비밀번호가 변경되었습니다. 로그인해주세요.')
    router.push('/login?type=client')
  } catch (err) {
    console.error(err)
    alert('비밀번호 재설정 실패')
  }
}
</script>

<template>
  <AccountFrame title="아이디 / 비밀번호 찾기">
    <section class="w-100" style="max-width: 420px;">
      <!-- 탭 전환 버튼 -->
      <div class="btn-group mb-4 w-100">
        <button :class="tab === 'id' ? 'btn btn-primary' : 'btn btn-outline-secondary'" @click="tab = 'id'">아이디 찾기</button>
        <button :class="tab === 'pw' ? 'btn btn-primary' : 'btn btn-outline-secondary'" @click="tab = 'pw'">비밀번호 찾기</button>
      </div>

      <!-- 아이디 찾기 폼 -->
      <form v-if="tab === 'id'" @submit.prevent="findId">
        <div class="mb-3">
          <input type="text" v-model="nameForId" class="form-control" placeholder="이름(실명)" required />
        </div>
        <div class="mb-3">
          <div class="input-group">
            <input type="email" v-model="email" class="form-control" placeholder="이메일" required />
            <button type="button" class="btn btn-outline-secondary" @click="requestEmailCode">인증 요청</button>
          </div>
        </div>
        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="authCode" class="form-control" placeholder="이메일 인증번호" required />
            <button type="button" class="btn btn-outline-secondary" @click="verifyEmailCode" :disabled="isEmailVerified">
              {{ isEmailVerified ? '✅ 인증 완료' : '인증 승인' }}
            </button>
          </div>
        </div>
        <button type="submit" class="btn btn-primary w-100">아이디 찾기</button>

        <div class="mt-4 text-center" v-if="foundId">
          <p class="fw-bold">🔎 찾은 아이디: {{ foundId }}</p>
          <router-link to="/login" class="btn btn-outline-primary mt-2">로그인 하러가기</router-link>
        </div>
      </form>

      <!-- 비밀번호 재설정 폼 -->
      <form v-if="tab === 'pw'" @submit.prevent="resetPassword">
        <div class="mb-3">
          <input type="text" v-model="clientId" class="form-control" placeholder="아이디" required />
        </div>
        <div class="mb-3">
          <input type="text" v-model="nameForPw" class="form-control" placeholder="이름(실명)" required />
        </div>
        <div class="mb-3">
          <div class="input-group">
            <input type="email" v-model="email" class="form-control" placeholder="이메일" required />
            <button type="button" class="btn btn-outline-secondary" @click="requestEmailCode">인증 요청</button>
          </div>
        </div>
        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="authCode" class="form-control" placeholder="이메일 인증번호" required />
            <button type="button" class="btn btn-outline-secondary" @click="verifyEmailCode" :disabled="isEmailVerified">
              {{ isEmailVerified ? '✅ 인증 완료' : '인증 승인' }}
            </button>
          </div>
        </div>
        <div class="mb-3">
          <input type="password" v-model="newPassword" class="form-control" placeholder="새 비밀번호" required />
        </div>
        <div class="mb-3">
          <input type="password" v-model="confirmPassword" class="form-control" placeholder="비밀번호 확인" required />
        </div>
        <button type="submit" class="btn btn-primary w-100">비밀번호 재설정</button>
      </form>
    </section>
  </AccountFrame>
</template>

<style scoped>
section {
  margin: 0 auto;
}
.btn-group .btn {
  flex: 1 1 0;
}
</style>
