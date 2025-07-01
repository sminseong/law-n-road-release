<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/libs/HttpRequester'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";

const router = useRouter()

const position = ref('MAIN') // MAIN or SUB
const startDate = ref('')
const endDate = ref('')
const mainText = ref('')
const detailText = ref('')
const tipText = ref('')
const imageFile = ref(null)
const imagePreview = ref('')

const today = new Date()
const yyyy = today.getFullYear()
const mm = String(today.getMonth() + 1).padStart(2, '0')
const dd = String(today.getDate()).padStart(2, '0')
const todayString = `${yyyy}-${mm}-${dd}`
// const today = new Date().toISOString().split('T')[0]

function handleImageChange(e) {
  const file = e.target.files[0]
  if (!file || !file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.')
    return
  }

  const MAX_SIZE_MB = 10 * 1024 * 1024
  if (file.size > MAX_SIZE_MB) {
    alert('이미지는 10MB 이하로 업로드해 주세요.')
    return
  }

  imageFile.value = file
  imagePreview.value = URL.createObjectURL(file)
}

function handleStartDateChange() {
  if (startDate.value) {
    const d = new Date(startDate.value)
    d.setMonth(d.getMonth() + 1)
    endDate.value = d.toISOString().split('T')[0]
  }
}

async function handleSubmit() {
  if (!startDate.value) {
    alert('광고 시작일을 선택해 주세요.')
    return
  }
  if (!mainText.value || mainText.value.length > 30) {
    alert('광고 문구 1은 필수입니다. 20자 이하로 입력해 주세요.')
    return
  }
  if (!detailText.value || detailText.value.length > 50) {
    alert('광고 문구 2는 필수입니다. 20자 이하로 입력해 주세요.')
    return
  }
  if (!tipText.value || tipText.value.length > 20) {
    alert('광고 문구 3은 필수입니다. 10자 이하로 입력해 주세요.')
    return
  }
  if (!imageFile.value) {
    alert('이미지 파일을 입력해주세요. ')
    return
  }

  const formData = new FormData()
  formData.append('adType',    position.value)
  formData.append('startDate', startDate.value)
  formData.append('endDate',   endDate.value)
  formData.append('mainText',  mainText.value)
  formData.append('detailText',detailText.value)
  formData.append('tipText',   tipText.value || '')
  formData.append('file',      imageFile.value)

  const { data } = await http.post(
      '/api/lawyer/ads/register-with-order',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
  )
  const { orderCode, amount } = data

  // 2) 결제 팝업 띄우기
  const toss = TossPayments('test_ck_d46qopOB89dkZvqg40zOrZmM75y0')
  const { paymentKey } = await toss.requestPayment('카드', {
    amount,
    orderId:  orderCode,
    orderName: position.value === 'MAIN'
        ? '메인 배너 광고 결제'
        : '서브 배너 광고 결제'
  })

  // 3) 결제 승인 API 호출
  const token = localStorage.getItem('token')
  await http.post(
      '/api/confirm/payment',
      { paymentKey, orderId: orderCode, amount },
      { headers: { Authorization: `Bearer ${token}` } }
  )

  // 4) 완료 알림 및 리스트 이동
  alert('결제가 완료되었습니다.')
  router.push({ name: 'AdList' })
}
</script>

<template>
  <lawyer-frame>
    <div class="container py-4">
      <h3 class="fw-bold mb-3">광고 신청</h3>

      <!-- 설명 -->
      <p class="text-muted small mb-2">
        * 메인 베너는 홈페이지 상단에 크게 노출되며,<br class="d-sm-none" />
        서브 베너는 중간 또는 하단 영역에 작게 표시됩니다.
      </p>

      <!-- 버튼 -->
      <div class="d-flex w-100 gap-3 mb-3">
        <button class="btn flex-fill"
                :class="{ 'btn-primary': position === 'MAIN', 'btn-outline-secondary': position !== 'MAIN' }"
                @click="position = 'MAIN'">
          메인 배너 (400,000원)
        </button>
        <button class="btn flex-fill"
                :class="{ 'btn-primary': position === 'SUB', 'btn-outline-secondary': position !== 'SUB' }"
                @click="position = 'SUB'">
          서브 배너 (150,000원)
        </button>
      </div>

    <div class="mb-3">
      <label class="form-label">광고 시작일</label>
      <input type="date" v-model="startDate" class="form-control" :min="todayString" @change="handleStartDateChange" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 종료일</label>
      <input type="date" v-model="endDate" class="form-control" disabled />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 1 (메인 문구)</label>
      <input v-model="mainText" class="form-control" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 2 (상세 문구)</label>
      <input v-model="detailText" class="form-control" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 3 (CTA 문구)</label>
      <input v-model="tipText" class="form-control" />
    </div>

      <p class="my-0">
        배너 이미지 권장 사이즈 : 메인 배너: 1265 × 530(px), 서브 배너: 625 × 215(px)
      </p>
    <div class="preview-box mb-2 border d-flex justify-content-center align-items-center" style="height: 200px;">
      <img v-if="imagePreview" :src="imagePreview" class="img-fluid h-100" style="object-fit: contain" />
      <span v-else class="text-muted">이미지 미리보기</span>
    </div>
    <input type="file" accept="image/*" class="form-control mb-3" @change="handleImageChange" />

    <div class="text-end">
      <button class="btn btn-primary" @click="handleSubmit">결제하기</button>
    </div>
  </div>
  </lawyer-frame>
</template>

<style scoped>
.preview-box {
  background-color: #f9f9f9;
}
</style>
