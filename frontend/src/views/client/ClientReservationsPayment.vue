<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-2xl font-bold mb-4">
        상담 결제
      </h2>

      <div class="bg-white shadow rounded-lg p-6">
        <p class="mb-2">
          <strong>예약번호:</strong> {{ reservationNo || '정보 없음' }}
        </p>
        <p class="mb-2">
          <strong>예약 시간:</strong> {{ formattedTime }}
        </p>
        <p class="mb-2">
          <strong>담당 변호사:</strong> {{ lawyerName || '정보 없음' }}
        </p>
        <p class="mb-4">
          <strong>결제 금액:</strong>
          <span class="text-lg text-blue-600">{{ amount.toLocaleString() }}원</span>
        </p>

        <button
            class="btn btn-primary w-full"
            @click="createOrderAndRedirect"
            :disabled="isProcessing"
        >
          결제 진행하기
        </button>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, computed } from 'vue'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const router = useRouter()

// 쿼리 파라미터
const reservationNo = Number(route.query.reservationNo || 0)
const slotDate      = route.query.slotDate
const slotTime      = route.query.slotTime
const lawyerName    = route.query.lawyerName || ''
const amount        = Number(route.query.amount || 0)

const isProcessing = ref(false)

const formattedTime = computed(() => {
  if (!slotDate || !slotTime) return '시간 정보 없음'
  const iso = `${slotDate}T${slotTime}`
  const dt = new Date(iso)
  return isNaN(dt)
      ? '유효하지 않은 시간'
      : dt.toLocaleString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
})

// ✅ 공용 결제 처리 함수
async function createOrderAndRedirect() {
  if (isProcessing.value) return
  isProcessing.value = true

  try {
    // const user = JSON.parse(localStorage.getItem('user'))
    // if (!user || !user.no) {
    //   alert('로그인 정보가 없습니다.')
    //   return
    // }

    const userNo = 6

    const res = await axios.post('/api/orders', {
      reservationNo,
      amount,
      // userNo: user.no
      userNo
    })

    const { checkoutUrl } = res.data
    if (!checkoutUrl) throw new Error('checkoutUrl 없음')

    window.location.href = checkoutUrl
  } catch (err) {
    console.error('결제 생성 실패:', err)
    alert('결제 생성에 실패했습니다.')
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
