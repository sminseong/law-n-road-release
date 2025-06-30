<template>
  <ClientFrame>
    <div class="container py-5 text-center" style="min-height: 100vh; padding: 20px;">
      <div class="box_section" style="width: 600px; margin: auto; text-align: center;">
        <img
            width="100px"
            src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png"
            alt="성공 이미지"
        />

        <h2 class="mt-4">결제를 완료했어요</h2>

        <div class="p-grid typography--p mt-10">
          <div class="p-grid-col text--left"><b>결제금액</b></div>
          <div class="p-grid-col text--right" id="amount">
            {{ formattedAmount }}
          </div>
        </div>

        <div class="p-grid typography--p mt-4">
          <div class="p-grid-col text--left"><b>주문번호</b></div>
          <div class="p-grid-col text--right" id="orderId">
            {{ orderId }}
          </div>
        </div>

        <div class="p-grid typography--p mt-4">
          <div class="p-grid-col text--left"><b>Payment Key</b></div>
          <div class="p-grid-col text--right" id="paymentKey">
            {{ paymentKey }}
          </div>
        </div>

        <div class="p-grid-col mt-8 justify-center">
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
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {
  sendClientReservationCreatedAlimtalk,
  sendLawyerReservationCreatedAlimtalk
} from '@/service/notification.js'

const route = useRoute()
const router = useRouter()

// Query parameters
const orderId        = route.query.orderId || ''
const rawAmount      = Number(route.query.amount || 0)
const paymentKey     = route.query.paymentKey || ''
const reservationNo  = Number(route.query.reservationNo || 0)

// Detail data
const detail = ref(null)
const error  = ref(null)

// Format amount
const formattedAmount = computed(() => rawAmount.toLocaleString() + '원')

// 결제 승인 요청
async function confirmPayment() {
  try {
    const token = localStorage.getItem('token')
    const payload = { orderId, amount: rawAmount, paymentKey }
    await axios.post(
        '/api/confirm/payment',
        payload,
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        }
    )
  } catch (err) {
    const code    = err.response?.data?.code    || 'UNKNOWN'
    const message = err.response?.data?.message || '결제 승인 중 오류가 발생했습니다.'
    router.replace(
        `/payment/fail?code=${encodeURIComponent(code)}&message=${encodeURIComponent(message)}`
    )
  }
}

// 예약 상세 조회 및 알림 발송
/**
 * @typedef {Object} ReservationDetail
 * @property {string} userName
 * @property {string} userPhone
 * @property {string} lawyerName
 * @property {string} lawyerPhone
 * @property {string} slotDate
 * @property {string} slotTime
 */
/** @type {ReservationDetail} */
async function fetchDetailAndNotify() {
  try {
    const token = localStorage.getItem('token')
    const { data } = await axios.get(
        `/api/client/reservations/${reservationNo}`,
        { headers: { Authorization: `Bearer ${token}` } }
    )
    detail.value = data

    const datetime = `${data.slotDate} ${data.slotTime}`

    // 회원 알림 발송
    await sendClientReservationCreatedAlimtalk({
      to:       data.userPhone,
      client:   data.userName,
      lawyer:   data.lawyerName,
      datetime,
    })

    // 변호사 알림 발송
    await sendLawyerReservationCreatedAlimtalk({
      to:       data.lawyerPhone,
      lawyer:   data.lawyerName,
      client:   data.userName,
      datetime,
    })
    console.log('알림 발송 완료')
  } catch (e) {
    console.error('상세 조회 또는 알림 발송 오류', e)
    error.value = '알림 발송 중 오류가 발생했습니다.'
  }
}

// 컴포넌트 마운트 시 처리
onMounted(async () => {
  await confirmPayment()
  if (reservationNo) {
    await fetchDetailAndNotify()
  }
})
</script>

<style scoped>
.container { max-width: 600px; margin: auto; }
</style>
