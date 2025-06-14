<!-- src/views/account/LawyerSignupView.vue -->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const router = useRouter()

// 입력값을 담을 ref 선언
const lawyerId = ref('') // 내가 추가 한거
const fullName = ref('')
const email = ref('')
const password = ref('')
const phone = ref('')
const confirmPassword = ref('')
const office_name = ref('')        // 변호사 사무실 이름
const licenseNumber = ref('')  // 변호사 등록번호
const agreeTerms = ref(false)
const zipcode = ref('')
const roadcode = ref('')
const landcode = ref('')
const detailcode = ref('')
const authCode = ref('')
//const agreeTerms = ref(false)
const isEmailVerified = ref(false) // ✅ 이메일 인증 완료 여부



//내가 추가 한거
async function checkIdLawyerIdDuplicate() {
  if (!lawyerId.value.trim()) {
    alert('아이디를 입력하세요.')
    return
  }

  try {
    const res = await axios.get(`/api/auth/lawyer_check-id?lawyerId=${lawyerId.value}`)
    if (res.data.available) {
      alert('사용 가능한 아이디입니다.')
    } else {
      alert('이미 사용 중인 아이디입니다.')
    }
  } catch (err) {
    console.error(err)
    alert('아이디 중복 확인 중 오류가 발생했습니다.')
  }
}

// 이메일 인증 요청
async function requestEmailCode() {
  if (!email.value) {
    alert("이메일을 입력해주세요.");
    return;
  }

  try {
    const res = await axios.get(`/api/auth/check-email?email=${email.value}`)
    if (!res.data.available) {
      alert("❌ 이미 가입된 이메일입니다.");
      return;
    }

    const verifyRes = await axios.post('/mail/send', null, {
      params: { email: email.value }
    });

    alert("✅ 인증번호가 전송되었습니다.");
  } catch (err) {
    console.error("이메일 인증 요청 중 오류:", err);
    alert("이메일 인증 요청 실패");
  }
}

// 이메일 인증번호 확인
async function verifyEmailCode() {
  if (!email.value || !authCode.value) {
    alert("이메일과 인증번호를 모두 입력해주세요.");
    return;
  }

  try {
    const res = await axios.post('/mail/verify', null, {
      params: {
        email: email.value,
        code: authCode.value
      }
    });

    if (res.data === "인증 성공") {
      isEmailVerified.value = true  // ✅ 여기 중요!
      alert("✅ 이메일 인증에 성공했습니다!");
    } else {
      alert("❌ 인증번호가 일치하지 않습니다.");
    }
  } catch (err) {
    console.error("인증 확인 실패:", err);
    alert("인증 확인 중 문제가 발생했습니다.");
  }
}

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
      lawyerId: lawyerId.value,
      fullName: fullName.value,
      email: email.value,
      password: password.value,
      office_name: office_name.value,
      zipcode : zipcode.value,
      roadcode :roadcode.value,
      landcode : landcode.value,
      detailcode : detailcode.value,
      type: 'lawyer'
    }
    // 예시: '/api/auth/signup'
    await axios.post('/api/auth/lawyer_signup', payload)

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

        <!-- 아이디 -->
        <div class="mb-3">
          <div class="input-group">
            <input type="text"
                   v-model="lawyerId"
                   class="form-control"
                   placeholder="아이디" required />
            <button type="button" class="btn btn-outline-secondary"
                    @click="checkIdLawyerIdDuplicate">중복 확인</button>
          </div>
        </div>

        <!-- 이메일 + 인증 요청 -->
        <div class="mb-3">
          <div class="input-group">
            <input type="email"
                   v-model="email"
                   class="form-control"
                   placeholder="이메일" required />
            <button type="button" class="btn btn-outline-secondary" @click="requestEmailCode">인증 요청</button>
          </div>
        </div>

        <!-- 인증번호 + 인증 승인 -->
        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="authCode" class="form-control" placeholder="이메일 인증번호" required />
            <button type="button" class="btn btn-outline-secondary" @click="verifyEmailCode">인증 승인</button>
          </div>
        </div>

        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="phone" class="form-control" placeholder="전화번호" required />

          </div>
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
              v-model="office_name"
              class="form-control"
              placeholder="사무실 이름"
              required
          />
        </div>

        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="zipcode" class="form-control" placeholder="우편번호" required />
          </div>
        </div>

        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="roadcode" class="form-control" placeholder="도로명 주소" required />
          </div>
        </div>

        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="landcode" class="form-control" placeholder="지번 주소" required />
          </div>
        </div>

        <div class="mb-3">
          <div class="input-group">
            <input type="text" v-model="detailcode" class="form-control" placeholder="상세 주소" required />
          </div>
        </div>



        <!-- 변호사 등록번호 필드 -->


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
