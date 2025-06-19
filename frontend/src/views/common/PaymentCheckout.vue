<template>
  <div class="wrapper">
    <div class="box_section">
      <h1>일반 결제</h1>

      <div id="payment-method" style="display: flex">
        <button
            id="CARD"
            :class="{ active: selectedPaymentMethod === 'CARD' }"
            class="button2"
            @click="selectedPaymentMethod = 'CARD'"
        >
          카드
        </button>
      </div>

      <button
          class="button"
          @click="requestPayment"
          :disabled="!selectedPaymentMethod || isProcessing"
      >
        {{ isProcessing ? '처리 중...' : '결제하기' }}
      </button>
    </div>
  </div>
</template>

<script>
import { loadTossPayments } from '@tosspayments/tosspayments-sdk'
import axios from 'axios'

const clientKey = 'test_ck_d46qopOB89dkZvqg40zOrZmM75y0'
const amount = { currency: 'KRW', value: 1 }

export default {
  data() {
    return {
      payment: null,
      selectedPaymentMethod: null,
      isProcessing: false
    }
  },
  methods: {
    async fetchPayment() {
      try {
        this.payment = await loadTossPayments(clientKey)
      } catch (error) {
        console.error('Error initializing TossPayments:', error)
        alert('결제 기능을 초기화하는 데 실패했습니다.')
      }
    },
    async requestPayment() {
      if (!this.selectedPaymentMethod) {
        alert('결제 수단을 선택해주세요.')
        return
      }

      this.isProcessing = true
      try {
        // 1) 주문 생성
        const token = localStorage.getItem('accessToken')
        const createRes = await axios.post(
            '/api/orders',
            {
              userNo: /* 실제 로그인된 사용자 ID */, // 필요 시 교체
              totalAmount: amount.value,
              status: 'ORDERED',
              orderType: 'RESERVATION'
            },
            {
              headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`
              }
            }
        )
        const orderNo = createRes.data

        // 2) 주문 조회 → orderCode 확보
        const orderDetailRes = await axios.get(
            `/api/orders/${orderNo}`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
        const order = orderDetailRes.data
        const orderCode = order.orderCode

        // 3) Toss 결제 요청
        await this.payment.requestPayment({
          method: this.selectedPaymentMethod,
          amount,
          orderId: orderCode,
          orderName: '상담 예약',
          successUrl: window.location.origin +
              `/payment/success?orderCode=${encodeURIComponent(orderCode)}`,
          failUrl: window.location.origin +
              `/payment/fail?orderCode=${encodeURIComponent(orderCode)}`,
          customerEmail: order.customerEmail || '',
          customerName: order.customerName || '',
          card: {
            useEscrow: false,
            flowMode: 'DEFAULT',
            useCardPoint: false,
            useAppCardOnly: false
          }
        })
      } catch (error) {
        console.error('결제 요청 실패:', error)
        alert('결제 요청에 실패했습니다.')
      } finally {
        this.isProcessing = false
      }
    }
  },
  mounted() {
    this.fetchPayment()
  }
}
</script>

<style scoped>
.wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
.box_section {
  width: 600px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  text-align: center;
}
.button2 {
  margin-right: 8px;
}
.button2.active {
  background-color: #007aff;
  color: white;
}
.button {
  margin-top: 16px;
  width: 100%;
}
</style>
