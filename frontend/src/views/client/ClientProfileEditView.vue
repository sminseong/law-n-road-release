<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { makeApiRequest } from '@/libs/axios-auth.js'

const router = useRouter()

const originalNickname = localStorage.getItem('nickname') || ''
const originalEmail = localStorage.getItem('email') || ''
const originalPhone = localStorage.getItem('phone') || ''

const nickname = ref(originalNickname)
const phone = ref(originalPhone)

// 이메일 입력 관련 상태
const emailId = ref(originalEmail.split('@')[0] || '')
const domainList = ['gmail.com', 'naver.com', 'daum.net', 'custom']
const savedDomain = originalEmail.split('@')[1] || 'gmail.com'
const emailDomainSelect = ref(domainList.includes(savedDomain) ? savedDomain : 'custom')
const emailDomainCustom = ref(emailDomainSelect.value === 'custom' ? savedDomain : '')

const email = computed(() => {
  return (
      emailId.value + '@' +
      (emailDomainSelect.value === 'custom' ? emailDomainCustom.value : emailDomainSelect.value)
  )
})

onMounted(async () => {
  try {
    const res = await makeApiRequest({ method: 'get', url: '/api/client/profile' })
    if (res?.data) {
      nickname.value = res.data.nickname
      phone.value = res.data.phone
      const [id, domain] = res.data.email.split('@')
      emailId.value = id
      if (domainList.includes(domain)) {
        emailDomainSelect.value = domain
        emailDomainCustom.value = ''
      } else {
        emailDomainSelect.value = 'custom'
        emailDomainCustom.value = domain
      }
    }
  } catch (err) {
    console.error('프로필 조회 실패:', err)
  }
})

const updateProfile = async () => {
  if (!nickname.value.trim() || !email.value.trim() || !phone.value.trim()) {
    alert('모든 정보를 입력해주세요.')
    return
  }

  try {
    const response = await makeApiRequest({
      method: 'put',
      url: '/api/client/profile',
      data: {
        nickname: nickname.value,
        email: email.value,
        phone: phone.value
      }
    })

    if (response) {
      localStorage.setItem('nickname', nickname.value)
      localStorage.setItem('email', email.value)
      localStorage.setItem('phone', phone.value)
      alert('✅ 프로필이 성공적으로 수정되었습니다.')
      router.push('/client/mypage')
    }
  } catch (error) {
    console.error('❌ 프로필 수정 실패:', error)
    const status = error.response?.status
    if (status === 400) {
      alert('입력 정보가 올바르지 않습니다.')
    } else if (status === 403) {
      alert('권한이 없습니다. 다시 로그인해주세요.')
      localStorage.clear()
      router.push('/login')
    } else {
      alert('서버 오류가 발생했습니다.')
    }
  }
}

const withdrawAccount = async () => {
  if (!confirm('정말로 회원 탈퇴하시겠습니까?')) return
  try {
    const res = await makeApiRequest({ method: 'delete', url: '/api/client/withdraw' })
    if (res) {
      localStorage.clear()
      alert('회원 탈퇴가 완료되었습니다.')
      router.push('/')
    }
  } catch (err) {
    console.error('❌ 회원 탈퇴 실패:', err)
    alert('탈퇴 중 오류가 발생했습니다.')
  }
}
</script>

<template>
  <div class="mypage-wrapper">
    <h2 class="page-title">프로필 관리</h2>
    <p class="welcome-msg">안녕하세요, {{ nickname }}님! 회원정보를 수정하실 수 있습니다.</p>

    <div class="info-card">
      <h5 class="section-title">기본 정보 수정</h5>

      <div class="form-group">
        <label for="nickname">닉네임</label>
        <input id="nickname" v-model="nickname" class="form-control" type="text" placeholder="새 닉네임 입력" />
      </div>

      <div class="form-group">
        <label>이메일</label>
        <div class="email-row">
          <input v-model="emailId" class="form-control small" placeholder="아이디 입력" />
          <span class="at">@</span>
          <select v-model="emailDomainSelect" class="form-control small">
            <option value="gmail.com">gmail.com</option>
            <option value="naver.com">naver.com</option>
            <option value="daum.net">daum.net</option>
            <option value="custom">직접입력</option>
          </select>
          <input
              v-if="emailDomainSelect === 'custom'"
              v-model="emailDomainCustom"
              class="form-control small"
              placeholder="도메인 입력"
          />
        </div>
        <p class="text-muted">현재 이메일: {{ email }}</p>
      </div>

      <div class="form-group">
        <label for="phone">전화번호</label>
        <input id="phone" v-model="phone" class="form-control" type="tel" placeholder="전화번호 입력" />
      </div>

      <div class="btn-group">
        <button class="btn btn-primary" @click="updateProfile">수정 완료</button>
        <button class="btn btn-primary" @click="withdrawAccount">회원 탈퇴</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.mypage-wrapper {
  max-width: 720px;
  margin: 0 auto;
  padding: 40px 16px;
}
.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2E4065;
  margin-bottom: 8px;
}
.welcome-msg {
  font-size: 16px;
  margin-bottom: 32px;
}
.info-card {
  background: #f9f9f9;
  border: 1px solid #dcdcdc;
  border-radius: 12px;
  padding: 24px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  border-bottom: 1px solid #ccc;
  padding-bottom: 8px;
}
.form-group {
  margin-bottom: 20px;
}
label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}
.form-control {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
}
.form-control.small {
  width: auto;
  min-width: 100px;
  flex: 1;
}
.email-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}
.at {
  font-weight: bold;
  color: #444;
}
.btn-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 24px;
}
.btn-primary {
  background-color: #2E4065;
  color: white;
  border: none;
  font-weight: 600;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
}
.btn-primary:hover {
  background-color: #1f2d4c;
}
.text-muted {
  font-size: 12px;
  color: #888;
}
</style>
