<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { makeApiRequest } from '@/libs/axios-auth.js'  // 전역 유틸 import 재발급을 하기 위한

const router = useRouter()

// 로컬스토리지 초기화: token 및 사용자 no 확인
const token = localStorage.getItem('token')
const userNo = localStorage.getItem('no')

// 입력 필드
const officeNumber = ref('')
const phone = ref('')
const detailAddress = ref('')

// 디버깅용: 페이지 로드 시 콘솔 출력
onMounted(() => {
  console.log('=== Lawyer Profile Edit Debug ===')
  console.log('Token:', token)
  console.log('User No:', userNo)
  console.log('================================')
})

// 정보 수정 요청 함수
const updateLawyerInfo = async () => {
  if (!token || !userNo) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  // 필수 입력 확인
  if (!officeNumber.value.trim() || !phone.value.trim() || !detailAddress.value.trim()) {
    alert('모든 필드를 입력해주세요.')
    return
  }

  try {
    const response = await makeApiRequest({
      method: 'put',
      url: '/api/lawyer/info',
      data: {
        officeNumber: officeNumber.value,
        phone: phone.value,
        detailAddress: detailAddress.value
      }
    })

    if (response && response.status === 200) {
      alert('✅ 정보가 성공적으로 수정되었습니다.')
      router.push('/lawyer')
    } else {
      alert('❌ 수정 요청에 실패했습니다. 다시 시도해주세요.')
    }
  } catch (error) {
    console.error('❌ 정보 수정 실패:', error)
    alert('❌ 정보 수정 중 오류가 발생했습니다.')
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h2>계정 설정</h2>
      <p>변호사 계정 정보 수정 및 설정</p>

      <!-- 정보 수정 폼 -->
      <div class="card p-4 mt-4">
        <div class="mb-3">
          <label for="officeNumber" class="form-label">사무실 번호</label>
          <input
              id="officeNumber"
              type="text"
              class="form-control"
              v-model="officeNumber"
              placeholder="사무실 번호를 입력하세요"
          />
        </div>

        <div class="mb-3">
          <label for="phone" class="form-label">전화번호</label>
          <input
              id="phone"
              type="text"
              class="form-control"
              v-model="phone"
              placeholder="전화번호를 입력하세요"
          />
        </div>

        <div class="mb-3">
          <label for="detailAddress" class="form-label">상세 주소</label>
          <input
              id="detailAddress"
              type="text"
              class="form-control"
              v-model="detailAddress"
              placeholder="상세 주소를 입력하세요"
          />
        </div>

        <button class="btn btn-primary w-100" @click="updateLawyerInfo">정보 수정</button>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.container {
  max-width: 700px;
  margin: 0 auto;
}

.card {
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 12px;
}
</style>
