<template>
  <ClientFrame>
    <div class="container py-5 text-center">
      <h2 class="mb-4 text-danger">결제에 실패했습니다</h2>
      <p>주문 코드: <strong>{{ orderCode }}</strong></p>
      <p>오류 메시지: <strong>{{ error }}</strong></p>
      <button class="btn btn-primary" @click="goBack">
        다시 결제하기
      </button>
    </div>
  </ClientFrame>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const orderCode = route.query.orderCode || ''
const error     = route.query.error     || '알 수 없는 오류'

const router = useRouter()
function goBack() {
  router.back()
}
</script>
