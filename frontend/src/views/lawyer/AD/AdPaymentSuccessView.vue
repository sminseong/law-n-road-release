<template>
  <LawyerFrame>
    <div class="container py-5 text-center">
      <h2 class="text-2xl font-bold mb-4">결제가 완료되었습니다</h2>
      <p class="mb-6">
        주문번호: {{ orderCode }} / 금액: {{ amount.toLocaleString() }}원
      </p>

      <!-- 로딩 중 표시 -->
      <p v-if="loading" class="mb-4 text-blue-600">승인 처리 중…</p>
      <!-- 에러 표시 -->
      <p v-if="error" class="mb-4 text-red-600">{{ error }}</p>
    </div>
  </LawyerFrame>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/libs/HttpRequester'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const route      = useRoute()
const router     = useRouter()
const loading    = ref(true)   // 마운트 되면 바로 승인 시도, 기본 true
const error      = ref('')
const orderCode  = route.query.orderCode || ''
const amount     = Number(route.query.amount) || 0
const paymentKey = route.query.paymentKey || ''

async function confirmPayment() {
  if (!paymentKey) {
    error.value = '결제 키가 없습니다.'
    loading.value = false
    return
  }

  try {
    const token = localStorage.getItem('token')
    await http.post(
        '/api/confirm/payment',
        { paymentKey, orderId: orderCode, amount },
        { headers: { Authorization: `Bearer ${token}` } }
    )
    // 승인 완료되면 광고 목록으로 이동
    router.push({ name: 'AdList' })
  } catch (e) {
    error.value = e.response?.data?.message || '결제 확정에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 컴포넌트 마운트 시 자동 승인
onMounted(() => {
  confirmPayment()
})
</script>
