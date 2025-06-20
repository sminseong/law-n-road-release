<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import AccountFrame from '@/components/layout/account/AccountFrame.vue'

const router = useRouter()

// 기본 정보
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
const lawyerIntro = ref('')
const introDetail = ref('')

// 파일 업로드
const profileImage = ref(null)
const idCardFront = ref(null)
const idCardBack = ref(null)

function handleProfileUpload(e) {
  profileImage.value = e.target.files[0]
}
function handleIdFrontUpload(e) {
  idCardFront.value = e.target.files[0]
}
function handleIdBackUpload(e) {
  idCardBack.value = e.target.files[0]
}

// 주소 검색 API 로드
onMounted(() => {
  const script = document.createElement('script')
  script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
  script.async = true
  document.head.appendChild(script)
})

// 주소 검색 실행
function openPostcodePopup() {
  if (!window.daum || !window.daum.Postcode) {
    alert('주소 검색 API 로드 실패')
    return
  }

  new window.daum.Postcode({
    oncomplete(data) {
      zipcode.value = data.zonecode
      roadAddress.value = data.roadAddress
      landAddress.value = data.jibunAddress
    }
  }).open()
}

// 아이디 중복 확인
async function checkIdDuplicate() {
  if (!lawyerId.value.trim()) return alert('아이디를 입력하세요.')

  try {
    const res = await axios.get(`/api/auth/lawyer_check-id?lawyerId=${lawyerId.value}`)
    alert(res.data.available ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.')
  } catch (err) {
    console.error(err)
    alert('중복 확인 중 오류')
  }
}

// 이메일 인증 요청
async function requestEmailCode() {
  if (!email.value.trim()) return alert('이메일을 입력하세요.')

  try {
    const check = await axios.get(`/api/auth/check-email?email=${email.value}`)
    if (!check.data.available) {
      alert('이미 사용 중인 이메일입니다.')
      return
    }

    await axios.post('/mail/send', null, { params: { email: email.value } })
    alert('인증번호 발송 완료')
  } catch (err) {
    console.error(err)
    alert('이메일 인증 요청 실패')
  }
}

// 인증번호 확인
async function verifyEmailCode() {
  if (!email.value || !authCode.value) {
    alert('이메일과 인증번호를 모두 입력하세요.')
    return
  }

  try {
    const res = await axios.post('/mail/verify', null, {
      params: { email: email.value, code: authCode.value }
    })
    if (res.data === '인증 성공') {
      isEmailVerified.value = true
      alert('이메일 인증 성공')
    } else {
      alert('인증번호 불일치')
    }
  } catch (err) {
    console.error(err)
    alert('인증번호 확인 실패')
  }
}

// 회원가입 제출
async function onSubmit() {
  if (password.value !== confirmPassword.value) return alert('비밀번호가 일치하지 않습니다.')
  if (!agreeTerms.value) return alert('약관에 동의해야 합니다.')
  if (!isEmailVerified.value) return alert('이메일 인증이 필요합니다.')

  try {
    const formData = new FormData()
    formData.append('lawyerId', lawyerId.value)
    formData.append('fullName', fullName.value)
    formData.append('email', email.value)
    formData.append('password', password.value)
    formData.append('phone', phone.value)
    formData.append('officeName', officeName.value)
    formData.append('officeNumber', officeNumber.value)
    formData.append('zipcode', zipcode.value)
    formData.append('roadAddress', roadAddress.value)
    formData.append('landAddress', landAddress.value)
    formData.append('detailAddress', detailAddress.value)
    formData.append('consent', '1')
    formData.append('type', 'LAWYER')
    formData.append('lawyerIntro', lawyerIntro.value)
    formData.append('introDetail', introDetail.value)

    // ✅ 서버에서 지정한 이름으로 정확히 일치시켜야 함
    formData.append('profileImage', profileImage.value)
    formData.append('idCardFront', idCardFront.value)
    formData.append('idCardBack', idCardBack.value)

    await axios.post('/api/signuplawyer', formData)
    alert('회원가입 성공')
    router.push('/login?type=lawyer')
  } catch (err) {
    console.error(err)
    alert('회원가입 중 오류 발생')
  }
}
</script>

<template>
  <AccountFrame title="변호사 회원가입">
    <form @submit.prevent="onSubmit" style="max-width: 500px;" class="mx-auto">
      <input v-model="fullName" class="form-control mb-2" placeholder="이름" required />

      <div class="input-group mb-2">
        <input v-model="lawyerId" class="form-control" placeholder="아이디" required />
        <button class="btn btn-outline-secondary" type="button" @click="checkIdDuplicate">중복확인</button>
      </div>

      <div class="input-group mb-2">
        <input v-model="email" class="form-control" placeholder="이메일" required />
        <button class="btn btn-outline-secondary" type="button" @click="requestEmailCode">인증요청</button>
      </div>

      <div class="input-group mb-2">
        <input v-model="authCode" class="form-control" placeholder="인증번호" />
        <button class="btn btn-outline-secondary" type="button" @click="verifyEmailCode">인증확인</button>
      </div>

      <input v-model="password" type="password" class="form-control mb-2" placeholder="비밀번호" required />
      <input v-model="confirmPassword" type="password" class="form-control mb-2" placeholder="비밀번호 확인" required />
      <input v-model="phone" class="form-control mb-2" placeholder="전화번호" required />
      <input v-model="officeName" class="form-control mb-2" placeholder="사무실 이름" required />
      <input v-model="officeNumber" class="form-control mb-2" placeholder="사무실 번호" />

      <div class="input-group mb-2">
        <input v-model="zipcode" class="form-control" placeholder="우편번호" readonly />
        <button class="btn btn-outline-secondary" type="button" @click="openPostcodePopup">주소 검색</button>
      </div>
      <input v-model="roadAddress" class="form-control mb-2" placeholder="도로명 주소" readonly />
      <input v-model="landAddress" class="form-control mb-2" placeholder="지번 주소" readonly />
      <input v-model="detailAddress" class="form-control mb-2" placeholder="상세 주소" />

      <textarea v-model="lawyerIntro" class="form-control mb-2" placeholder="한 줄 소개" rows="2" />
      <textarea v-model="introDetail" class="form-control mb-2" placeholder="상세 소개" rows="4" />

      <div class="mb-2">
        <label class="form-label">프로필 사진</label>
        <input type="file" class="form-control" @change="handleProfileUpload" accept="image/*" required />
      </div>

      <div class="mb-2">
        <label class="form-label">신분증 앞면</label>
        <input type="file" class="form-control" @change="handleIdFrontUpload" accept="image/*" required />
      </div>

      <div class="mb-3">
        <label class="form-label">신분증 뒷면</label>
        <input type="file" class="form-control" @change="handleIdBackUpload" accept="image/*" required />
      </div>

      <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" v-model="agreeTerms" id="agree" />
        <label class="form-check-label" for="agree">약관에 동의합니다.</label>
      </div>

      <button type="submit" class="btn btn-primary w-100">회원가입</button>

      <div class="text-center mt-3">
        <router-link to="/login?type=lawyer">이미 계정이 있으신가요?</router-link>
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
