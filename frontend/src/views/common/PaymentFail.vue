<template>
  <ClientFrame>
    <!-- 전체 화면을 채우는 Flex 컨테이너 -->
    <div
        class="flex justify-center items-center"
        style="min-height: 100vh; padding: 20px;"
    >
      <!-- 실패 메시지 박스 -->
      <div id="info" class="box_section" style="width: 600px; text-align: center;">
        <img
            width="100px"
            src="https://static.toss.im/lotties/error-spot-no-loop-space-apng.png"
            alt="에러 이미지"
        />
        <h2>결제를 실패했어요</h2>

        <div class="p-grid typography--p" style="margin-top: 50px">
          <div class="p-grid-col text--left"><b>에러메시지</b></div>
          <div class="p-grid-col text--right" id="message">
            {{ externalMessage }}
          </div>
        </div>

        <div class="p-grid typography--p" style="margin-top: 10px">
          <div class="p-grid-col text--left"><b>에러코드</b></div>
          <div class="p-grid-col text--right" id="code">
            {{ externalCode }}
          </div>
        </div>

        <div class="p-grid typography--p" style="margin-top: 10px">
          <div class="p-grid-col text--left"><b>주문번호</b></div>
          <div class="p-grid-col text--right" id="orderId">
            {{ orderId }}
          </div>
        </div>

        <div class="p-grid-col" style="margin-top: 30px; justify-content: center;">
          <a href="/">
            <button class="button p-grid-col5">메인화면으로 돌아가기</button>
          </a>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import ClientFrame from "@/components/layout/client/ClientFrame.vue"

const route = useRoute()
const router = useRouter()

// 쿼리 파라미터 추출
const orderId         = route.query.orderCode || route.query.orderId || ''
const externalCode    = route.query.code    || 'UNKNOWN'
const externalMessage = route.query.message || '알 수 없는 오류가 발생했습니다.'

// 내부 에러 상태
const cancelError = ref(null)

async function cancelPayment() {
  if (!orderId) return

  const token = localStorage.getItem('token')
  try {
    await axios.post(
        '/api/confirm/cancel',
        { orderId },
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        }
    )
  } catch (err) {
    // 백엔드 호출 실패 시 에러 저장 후 콘솔 출력
    cancelError.value = err.response?.data?.message || err.message
    console.error('환불 처리 호출 에러', err)
  }
}

onMounted(() => {
  // 토스에서 넘어온 에러 메시지/코드는 외부 값으로 표시하고,
  // 동시에 백엔드에 cancel 요청을 보내서 환불 기록을 남깁니다.
  cancelPayment()
})
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
