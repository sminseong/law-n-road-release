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
const userType = ref('client') // 👈 사용자 유형 추가

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
    const res = await axios.post('/api/find-id', {
      fullName: nameForId.value,
      email: email.value
    })

    const { clientId, lawyerId } = res.data
    if (clientId) {
      foundId.value = `의뢰인 아이디: ${clientId}`
    } else if (lawyerId) {
      foundId.value = `변호사 아이디: ${lawyerId}`
    } else {
      foundId.value = null
      console.log(foundId.value)
      alert('❌ 일치하는 계정을 찾을 수 없습니다.')
    }
  } catch (err) {
    console.error(err)
    alert('일치하는 계정을 찾을 수 없습니다.')
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
    await axios.post('/api/reset-password', {
      userId: clientId.value,
      fullName: nameForPw.value,
      email: email.value,
      newPassword: newPassword.value,
      userType: userType.value // 👈 사용자 유형 전송
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
      <div class="btn-group mb-4 w-100">
        <button :class="tab === 'id' ? 'btn btn-primary' : 'btn btn-outline-secondary'" @click="tab = 'id'">아이디 찾기</button>
        <button :class="tab === 'pw' ? 'btn btn-primary' : 'btn btn-outline-secondary'" @click="tab = 'pw'">비밀번호 찾기</button>
      </div>

      <!-- 아이디 찾기 -->
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

      <!-- 비밀번호 재설정 -->
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
        <!-- 👇 사용자 유형 선택 -->
        <div class="mb-3">
          <label class="form-label">사용자 유형</label>
          <div class="form-check">
            <input class="form-check-input" type="radio" id="client" value="client" v-model="userType">
            <label class="form-check-label" for="client">의뢰인</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" id="lawyer" value="lawyer" v-model="userType">
            <label class="form-check-label" for="lawyer">변호사</label>
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
