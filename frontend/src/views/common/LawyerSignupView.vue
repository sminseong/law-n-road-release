<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const router = useRouter()

const lawyerId = ref('')
const fullName = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const phone = ref('')
const officeName = ref('')
const officeNumber = ref('')
const zipcode = ref('')
const roadAddress = ref('')
const landAddress = ref('')
const detailAddress = ref('')
const agreeTerms = ref(false)
const authCode = ref('')
const isEmailVerified = ref(false)

// ✅ 주소 검색 팝업 함수
function openPostcodePopup() {
  if (!window.daum || !window.daum.Postcode) {
    alert("다음 주소 검색 API가 로드되지 않았습니다.")
    return
  }

  new window.daum.Postcode({
    oncomplete: function (data) {
      zipcode.value = data.zonecode
      roadAddress.value = data.roadAddress
      landAddress.value = data.jibunAddress
    }
  }).open()
}

// ✅ 아이디 중복 확인
async function checkIdDuplicate() {
  if (!lawyerId.value.trim()) {
    alert('아이디를 입력하세요.')
    return
  }

  try {
    const res = await axios.get(`/api/auth/lawyer_check-id?lawyerId=${lawyerId.value}`)
    alert(res.data.available ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.')
  } catch (err) {
    console.error(err)
    alert('아이디 중복 확인 중 오류 발생')
  }
}

// ✅ 이메일 인증 요청
async function requestEmailCode() {
  if (!email.value.trim()) {
    alert("이메일을 입력하세요.")
    return
  }

  try {
    const check = await axios.get(`/api/auth/check-email?email=${email.value}`)
    if (!check.data.available) {
      alert('이미 사용 중인 이메일입니다.')
      return
    }

    await axios.post('/mail/send', null, { params: { email: email.value } })
    alert('인증번호가 이메일로 전송되었습니다.')
  } catch (err) {
    console.error(err)
    alert('이메일 인증 요청 중 오류 발생')
  }
}

// ✅ 인증번호 확인
async function verifyEmailCode() {
  if (!email.value || !authCode.value) {
    alert("이메일과 인증번호를 모두 입력하세요.")
    return
  }

  try {
    const res = await axios.post('/mail/verify', null, {
      params: { email: email.value, code: authCode.value }
    })
    if (res.data === '인증 성공') {
      isEmailVerified.value = true
      alert('이메일 인증 성공!')
    } else {
      alert('인증번호가 일치하지 않습니다.')
    }
  } catch (err) {
    console.error(err)
    alert('이메일 인증 확인 중 오류 발생')
  }
}

// ✅ 회원가입 제출
async function onSubmit() {
  if (password.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  if (!agreeTerms.value) {
    alert('약관에 동의해야 가입할 수 있습니다.')
    return
  }

  if (!isEmailVerified.value) {
    alert('이메일 인증을 완료해주세요.')
    return
  }

  try {
    const payload = {
      lawyerId: lawyerId.value,
      fullName: fullName.value,
      email: email.value,
      password: password.value,
      phone: phone.value,
      officeName: officeName.value,
      officeNumber: officeNumber.value,
      zipcode: zipcode.value,
      roadAddress: roadAddress.value,
      landAddress: landAddress.value,
      detailAddress: detailAddress.value,
      consent: 1,
      type: 'LAWYER'
    }

    await axios.post('/api/auth/lawyer_signup', payload)
    alert('변호사 회원가입이 완료되었습니다.')
    router.push('/login?type=lawyer')
  } catch (err) {
    console.error(err)
    alert('회원가입 중 오류가 발생했습니다.')
  }
}
</script>

<template>
  <AccountFrame title="변호사 회원가입">
    <form @submit.prevent="onSubmit" style="max-width: 500px;" class="mx-auto">
      <input v-model="fullName" class="form-control mb-2" placeholder="이름" required />

      <!-- 아이디 + 중복확인 -->
      <div class="input-group mb-2">
        <input v-model="lawyerId" class="form-control" placeholder="아이디" required />
        <button class="btn btn-outline-secondary" type="button" @click="checkIdDuplicate">중복확인</button>
      </div>

      <!-- 이메일 + 인증 -->
      <div class="input-group mb-2">
        <input v-model="email" type="email" class="form-control" placeholder="이메일" required />
        <button class="btn btn-outline-secondary" type="button" @click="requestEmailCode">인증요청</button>
      </div>

      <!-- 인증번호 -->
      <div class="input-group mb-2">
        <input v-model="authCode" type="text" class="form-control" placeholder="인증번호" />
        <button class="btn btn-outline-secondary" type="button" @click="verifyEmailCode">인증확인</button>
      </div>

      <input v-model="password" type="password" class="form-control mb-2" placeholder="비밀번호" required />
      <input v-model="confirmPassword" type="password" class="form-control mb-2" placeholder="비밀번호 확인" required />
      <input v-model="phone" class="form-control mb-2" placeholder="전화번호" required />
      <input v-model="officeName" class="form-control mb-2" placeholder="사무실 이름" required />
      <input v-model="officeNumber" class="form-control mb-2" placeholder="사무실 전화번호" />

      <!-- 주소 검색 -->
      <div class="input-group mb-2">
        <input v-model="zipcode" class="form-control" placeholder="우편번호" readonly />
        <button class="btn btn-outline-secondary" type="button" @click="openPostcodePopup">주소 검색</button>
      </div>
      <input v-model="roadAddress" class="form-control mb-2" placeholder="도로명 주소" readonly />
      <input v-model="landAddress" class="form-control mb-2" placeholder="지번 주소" readonly />
      <input v-model="detailAddress" class="form-control mb-2" placeholder="상세 주소" />

      <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" v-model="agreeTerms" id="agree" />
        <label class="form-check-label" for="agree">약관에 동의합니다.</label>
      </div>

      <button type="submit" class="btn btn-primary w-100">회원가입</button>

      <div class="text-center mt-3">
        <router-link to="/login?type=lawyer" class="text-decoration-none">이미 계정이 있으신가요?</router-link>
      </div>
    </form>
  </AccountFrame>
</template>

<style scoped>
.btn {
  height: 44px;
  font-weight: 500;
}
</style>
