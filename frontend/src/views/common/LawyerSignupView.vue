<!-- src/views/account/LawyerSignupView.vue -->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const router = useRouter()

// 입력값을 담을 ref 선언
const fullName = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const lawFirm = ref('')        // 변호사 사무실 이름
const licenseNumber = ref('')  // 변호사 등록번호
const agreeTerms = ref(false)

async function onSubmit() {
  // 비밀번호 일치 여부 확인
  if (password.value !== confirmPassword.value) {
    alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.')
    return
  }
  // 약관 동의 체크
  if (!agreeTerms.value) {
    alert('이용약관에 동의해야 회원가입이 가능합니다.')
    return
  }

  // TODO: 실제 회원가입 API 호출 경로와 payload 확인 후 사용하세요.
  try {
    const payload = {
      fullName: fullName.value,
      email: email.value,
      password: password.value,
      lawFirm: lawFirm.value,
      licenseNumber: licenseNumber.value,
      type: 'lawyer'
    }
    // 예시: '/api/auth/signup'
    await axios.post('/api/auth/signup', payload)

    alert('변호사 회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.')
    router.push({ path: '/login', query: { type: 'lawyer' } })
  } catch (err) {
    console.error(err)
    alert('회원가입 중 오류가 발생했습니다. 다시 시도해주세요.')
  }
}
</script>

<template>
  <!-- ★ AccountFrame에 title 속성으로 '변호사 회원가입'을 전달 ★ -->
  <AccountFrame title="변호사 회원가입">
    <section class="w-100" style="max-width: 420px;">
      <form @submit.prevent="onSubmit">
        <!-- 이름 필드 -->
        <div class="mb-3">
          <input
              type="text"
              v-model="fullName"
              class="form-control"
              placeholder="이름(실명)"
              required
          />
        </div>

        <!-- 이메일 필드 -->
        <div class="mb-3">
          <input
              type="email"
              v-model="email"
              class="form-control"
              placeholder="이메일"
              required
          />
        </div>

        <!-- 비밀번호 필드 -->
        <div class="mb-3">
          <input
              type="password"
              v-model="password"
              class="form-control"
              placeholder="비밀번호 (영문·숫자 조합 8자 이상)"
              minlength="8"
              required
          />
        </div>

        <!-- 비밀번호 확인 필드 -->
        <div class="mb-3">
          <input
              type="password"
              v-model="confirmPassword"
              class="form-control"
              placeholder="비밀번호 확인"
              minlength="8"
              required
          />
        </div>

        <!-- 사무실 이름 필드 -->
        <div class="mb-3">
          <input
              type="text"
              v-model="lawFirm"
              class="form-control"
              placeholder="사무실 이름"
              required
          />
        </div>

        <!-- 변호사 등록번호 필드 -->
        <div class="mb-3">
          <input
              type="text"
              v-model="licenseNumber"
              class="form-control"
              placeholder="변호사 등록번호"
              required
          />
        </div>

        <!-- 이용약관 동의 -->
        <div class="form-check mb-4">
          <input
              class="form-check-input"
              type="checkbox"
              id="agreeTerms"
              v-model="agreeTerms"
          />
          <label class="form-check-label" for="agreeTerms">
            이용약관 및 개인정보처리방침에 동의합니다.
          </label>
        </div>

        <!-- 제출 버튼 -->
        <button type="submit" class="btn btn-primary w-100 mb-3">
          회원가입
        </button>

        <!-- 로그인 페이지로 이동 -->
        <div class="text-center small">
          이미 계정이 있으신가요?
          <a href="/login?type=lawyer" class="ms-1 text-decoration-none">
            로그인
          </a>
        </div>
      </form>
    </section>
  </AccountFrame>
</template>

<style scoped>
/* 회원가입 폼 영역 최대 너비 제한 */
section {
  margin: 0 auto;
}

/* 버튼 높이 통일 */
.btn {
  height: 44px;
  font-weight: 500;
}
</style>
