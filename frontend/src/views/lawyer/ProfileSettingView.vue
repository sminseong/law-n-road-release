<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const router = useRouter()
const token = localStorage.getItem('token')

// 입력 필드
const officeNumber = ref('')
const phone = ref('')
const detailAddress = ref('')

// 수정 요청 함수
const updateLawyerInfo = async () => {
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  try {
    await axios.put('/api/lawyer/info', {
      officeNumber: officeNumber.value,
      phone: phone.value,
      detailAddress: detailAddress.value
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    alert('✅ 정보가 성공적으로 수정되었습니다.')
    router.push('/lawyer')
  } catch (error) {
    console.error('변경 실패:', error)
    alert('❌ 정보 수정에 실패했습니다.')
  }
}
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a>
    <br><br>

    <div class="container py-4">
      <h2>계정 설정</h2>
      <p>변호사 계정 정보 수정 및 설정</p>

      <!-- ✅ 정보 수정 폼 -->
      <div class="card p-4 mt-4">
        <div class="mb-3">
          <label for="officeNumber" class="form-label">사무실 번호</label>
          <input id="officeNumber" type="text" class="form-control" v-model="officeNumber" />
        </div>

        <div class="mb-3">
          <label for="phone" class="form-label">전화번호</label>
          <input id="phone" type="text" class="form-control" v-model="phone" />
        </div>

        <div class="mb-3">
          <label for="detailAddress" class="form-label">상세 주소</label>
          <input id="detailAddress" type="text" class="form-control" v-model="detailAddress" />
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
