<template>
  <ClientFrame>
    <!-- 화면 전체를 채우는 flex 컨테이너 -->
    <div
        class="flex justify-center items-center"
        style="min-height: 100vh; padding: 20px;"
    >
      <!-- 성공 메시지 박스 -->
      <div class="box_section" style="width: 600px; text-align: center;">
        <img
            width="100px"
            src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png"
            alt="성공 이미지"
        />

        <h2>결제를 완료했어요</h2>

        <div class="p-grid typography--p" style="margin-top: 50px">
          <div class="p-grid-col text--left"><b>결제금액</b></div>
          <div class="p-grid-col text--right" id="amount">
            {{ formattedAmount }}
          </div>
        </div>

        <div class="p-grid typography--p" style="margin-top: 10px">
          <div class="p-grid-col text--left"><b>주문번호</b></div>
          <div class="p-grid-col text--right" id="orderId">
            {{ orderId }}
          </div>
        </div>

        <div class="p-grid typography--p" style="margin-top: 10px">
          <div class="p-grid-col text--left"><b>paymentKey</b></div>
          <div class="p-grid-col text--right" id="paymentKey">
            {{ paymentKey }}
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import {
  sendClientReservationCreatedAlimtalk, sendLawyerReservationCreatedAlimtalk,
} from "@/service/notification.js"

const route     = useRoute()
const router    = useRouter()

// 쿼리 파라미터 추출 + 기본값
const orderId     = route.query.orderId    || ''
const rawAmount   = Number(route.query.amount   || 0)
const paymentKey  = route.query.paymentKey || ''

// 통화 포맷
const formattedAmount = computed(() =>
    rawAmount.toLocaleString() + '원'
)

// 서버 응답 저장 (디버그용)
const responseData = ref(null)

onMounted(async () => {
  try {
    await sendClientReservationCreatedAlimtalk({
      to: "01096471213",
      client: "홍길동",
      lawyer: "박건희",
      datetime: "2025-06-05 15:00",
      summary: "음주운전 벌금 문의"
    });
    await sendLawyerReservationCreatedAlimtalk({
      to: "01081272572",
      lawyer: "박건희",
      client: "홍길동",
      datetime: "2025-06-05 15:00",
      summary: "음주운전 벌금 문의"
    });
  } catch (e) {
    console.log(e)
  }
})

async function confirmPayment() {
  const payload = { orderId, amount: rawAmount, paymentKey }
  try {
    const token = localStorage.getItem('token')
    const res = await axios.post(
        '/api/confirm/payment',
        payload,
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        }
    )
    responseData.value = res.data
  } catch (err) {
    // 실패 시 /payment/fail 로 이동, 에러 메시지 쿼리로 전달
    const code    = err.response?.data?.code    || 'UNKNOWN'
    const message = err.response?.data?.message || '결제 승인 중 오류가 발생했습니다.'
    router.replace(
        `/payment/fail?code=${encodeURIComponent(code)}&message=${encodeURIComponent(message)}`
    )
  }
}

// 컴포넌트가 마운트되면 바로 승인 요청
onMounted(confirmPayment)
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
