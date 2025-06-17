<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

// 로컬스토리지 초기값
const originalNickname = localStorage.getItem('nickname') || ''
const originalEmail = localStorage.getItem('email') || ''
const originalPhone = localStorage.getItem('phone') || ''
const token = localStorage.getItem('token')

// 닉네임, 전화번호
const nickname = ref(originalNickname)
const phone = ref(originalPhone)

// 이메일 파싱 (이전에 저장된 값이 있다면 분리)
const emailId = ref(originalEmail.split('@')[0] || '')
const savedDomain = originalEmail.split('@')[1] || 'gmail.com'
const emailDomainSelect = ref(['gmail.com', 'naver.com', 'daum.net'].includes(savedDomain) ? savedDomain : 'custom')
const emailDomainCustom = ref(emailDomainSelect.value === 'custom' ? savedDomain : '')

// 이메일 최종 조합
const email = computed(() => {
  return emailId.value + '@' + (emailDomainSelect.value === 'custom' ? emailDomainCustom.value : emailDomainSelect.value)
})

const updateProfile = async () => {
  if (!nickname.value.trim() || !email.value.trim() || !phone.value.trim()) {
    alert('모든 정보를 입력해주세요.')
    return
  }

  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  try {
    await axios.put(
        '/api/client/profile',
        {
          nickname: nickname.value,
          email: email.value,
          phone: phone.value
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    // 로컬스토리지 업데이트
    localStorage.setItem('nickname', nickname.value)
    localStorage.setItem('email', email.value)
    localStorage.setItem('phone', phone.value)

    alert('✅ 프로필이 성공적으로 수정되었습니다.')
    router.push('/client/mypage')
  } catch (error) {
    console.error('프로필 수정 실패:', error)
    if (error.response?.status === 403) {
      alert('권한이 없습니다. 다시 로그인해주세요.')
      router.push('/login')
    } else {
      alert('❌ 수정에 실패했습니다.')
    }
  }
}


const withdrawAccount = async () => {
  if (!confirm('정말로 회원 탈퇴하시겠습니까?')) return

  try {
    await axios.put('/api/client/withdraw', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    // 로그아웃 처리
    localStorage.clear()
    alert('회원 탈퇴가 완료되었습니다.')
    router.push('/login')
  } catch (err) {
    console.error('회원 탈퇴 실패:', err)
    alert('탈퇴 중 오류가 발생했습니다.')
  }
}






</script>

<template>
  <div class="container mt-5">
    <h3 class="mb-4">프로필 수정</h3>
    <div class="card p-4">
      <div class="mb-3">
        <label for="nickname" class="form-label">닉네임</label>
        <input
            id="nickname"
            type="text"
            class="form-control"
            v-model="nickname"
            placeholder="새 닉네임을 입력하세요"
        />
      </div>

      <div class="mb-3">
        <label class="form-label">이메일</label>
        <div class="d-flex gap-2 flex-wrap">
          <input
              type="text"
              class="form-control"
              style="flex: 1"
              v-model="emailId"
              placeholder="이메일 아이디 입력"
          />
          <span class="align-self-center">@</span>
          <div v-if="emailDomainSelect !== 'custom'" style="flex: 1">
            <select class="form-select" v-model="emailDomainSelect">
              <option value="gmail.com">gmail.com</option>
              <option value="naver.com">naver.com</option>
              <option value="daum.net">daum.net</option>
              <option value="custom">직접입력</option>
            </select>
          </div>
          <div v-else style="flex: 1">
            <input
                type="text"
                class="form-control"
                v-model="emailDomainCustom"
                placeholder="도메인 직접 입력"
            />
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label for="phone" class="form-label">전화번호</label>
        <input
            id="phone"
            type="tel"
            class="form-control"
            v-model="phone"
            placeholder="전화번호를 입력하세요"
        />
      </div>

      <button class="btn btn-primary" @click="updateProfile">프로필 수정</button>
      <button class="btn btn-danger mt-2" @click="withdrawAccount">회원 탈퇴</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 600px;
  margin: 0 auto;
}

.card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
}

.btn {
  width: 100%;
}
</style>
