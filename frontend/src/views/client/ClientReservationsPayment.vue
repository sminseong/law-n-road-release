<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-2xl font-bold mb-4">
        상담 결제
      </h2>

      <div class="bg-white shadow rounded-lg p-6">
        <p class="mb-2">
          <strong>예약번호:</strong> {{ reservationNo }}
        </p>
        <p class="mb-2">
          <strong>주문코드:</strong> {{ orderCode }}
        </p>
        <p class="mb-2">
          <strong>예약 시간:</strong> {{ formattedTime }}
        </p>
        <p class="mb-2">
          <strong>담당 변호사:</strong> {{ lawyerName }}
        </p>
        <p class="mb-4">
          <strong>결제 금액:</strong>
          <span class="text-lg text-blue-600">{{ amount.toLocaleString() }}원</span>
        </p>

        <button
            class="btn btn-primary w-full"
            @click="createOrderAndPay"
            :disabled="isProcessing"
        >
          결제 진행하기
        </button>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {getValidToken} from "@/libs/axios-auth.js";

const route = useRoute()

// ───────── 쿼리 파라미터 읽기 ─────────
const reservationNo = Number(route.query.reservationNo || 0)
const orderCode     = route.query.orderCode       || ''
const slotDate      = route.query.slotDate        || ''
const slotTime      = route.query.slotTime        || ''
const lawyerName    = route.query.lawyerName      || ''
const amount        = Number(route.query.amount   || 0)
// ───────────────────────────────────────

const isProcessing = ref(false)

const formattedTime = computed(() => {
  if (!slotDate || !slotTime) return '시간 정보 없음'
  const dt = new Date(`${slotDate}T${slotTime}`)
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

// SDK 로드 확인
onMounted(() => {
  if (!window.TossPayments) {
    console.error('TossPayments SDK가 로드되지 않았습니다!')
    alert('결제 기능을 사용할 수 없습니다.')
  }
})

async function createOrderAndPay() {
  const token = await getValidToken()
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  if (isProcessing.value) return
  isProcessing.value = true

  try {
    // public-client-key 로 SDK 인스턴스 생성
    const tossPayments = window.TossPayments('test_ck_d46qopOB89dkZvqg40zOrZmM75y0')

    // 카드 결제 요청
    await tossPayments.requestPayment('카드', {
      amount,
      orderId:   orderCode,
      orderName: `${lawyerName} 상담 예약`,
      successUrl: `${location.origin}/payment/success?reservationNo=${encodeURIComponent(reservationNo)}&orderCode=${encodeURIComponent(orderCode)}`,
      failUrl:    `${location.origin}/payment/fail?reservationNo=${encodeURIComponent(reservationNo)}&orderCode=${encodeURIComponent(orderCode)}`
    })

  } catch (err) {
    console.error('결제 준비 실패:', err)
    alert('결제 준비에 실패했습니다.')
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
