<template>
  <ClientFrame>
    <!-- 화면 전체를 채우는 flex 컨테이너 -->
    <div
        class="flex justify-center items-center"
        style="min-height: 100vh; padding: 20px;"
    >
      <!-- 기존 성공 메시지 박스 -->
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
          <div
              class="p-grid-col text--right"
              id="paymentKey">
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

    <!-- 응답 데이터 디버그용 -->
    <!--    <div-->
    <!--        class="box_section"-->
    <!--        style="width: 600px; text-align: left; margin: 40px auto 0;"-->
    <!--        v-if="responseData"-->
    <!--    >-->
    <!--      <b>Response Data :</b>-->
    <!--      <pre style="white-space: pre-wrap">{{ JSON.stringify(responseData, null, 2) }}</pre>-->
    <!--    </div>-->
  </ClientFrame>
</template>


<script setup>
import {ref, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";

const route = useRoute()
const router = useRouter()

// 쿼리 파라미터 추출 + 기본값
const orderId = route.query.orderId || ''
const rawAmount = Number(route.query.amount || 0)
const paymentKey = route.query.paymentKey || ''

// 통화 포맷
const formattedAmount = computed(() =>
    rawAmount.toLocaleString() + '원'
)

const responseData = ref(null)

async function confirmPayment() {
  const payload = {
    orderId,
    amount: rawAmount,
    paymentKey
  }

  try {
    const res = await fetch('/api/confirm/payment', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(payload)
    })

    const json = await res.json()
    if (!res.ok) {
      // 서버가 { message, code } 형태로 에러를 던진다고 가정
      const code = json.code || 'UNKNOWN'
      const message = json.message || '결제 승인 중 오류가 발생했습니다.'
      throw {code, message}
    }

    responseData.value = json
  } catch (err) {
    // 실패 시 /payment/fail 로 이동
    router.replace(
        `/payment/fail?code=${encodeURIComponent(err.code)}&message=${encodeURIComponent(err.message)}`
    )
  }
}

// 마운트되면 바로 승인 요청
confirmPayment()
</script>
