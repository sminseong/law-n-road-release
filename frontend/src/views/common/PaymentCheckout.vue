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

      <button class="button" @click="requestPayment">결제하기</button>
    </div>
  </div>
</template>

<script>
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";

const clientKey = "test_ck_d46qopOB89dkZvqg40zOrZmM75y0";
const customerKey = 6;
const amount = {
  currency: "KRW",
  value: 1,
};

export default {
  data() {
    return {
      payment: null,
      selectedPaymentMethod: null,
    };
  },
  methods: {
    async fetchPayment() {
      try {
        const tossPayments = await loadTossPayments(clientKey);
        this.payment = tossPayments.payment({ customerKey });
      } catch (error) {
        console.error("Error initializing TossPayments:", error);
      }
    },
    async requestPayment() {
      try {
        if (!this.selectedPaymentMethod) {
          alert("결제 수단을 선택해주세요.");
          return;
        }

        // [1] 주문 생성
        const createRes = await fetch("/api/orders", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            userNo: customerKey, // 실제 사용자 ID로 교체
            totalAmount: amount.value,
            status: "ORDERED",
            orderType: "RESERVATION",
          }),
        });

        const orderNo = await createRes.json();

        // [2] 주문 조회 → orderCode 확보
        const orderDetailRes = await fetch(`/api/orders/${orderNo}`);
        const order = await orderDetailRes.json();
        const orderCode = order.orderCode;

        // [3] Toss 결제 요청
        await this.payment.requestPayment({
          method: this.selectedPaymentMethod,
          amount,
          orderId: orderCode,
          orderName: "토스 티셔츠 외 2건",
          successUrl: window.location.origin + "/payment/success",
          failUrl: window.location.origin + "/fail",
          customerEmail: "customer123@gmail.com",
          customerName: "김토스",
          card: {
            useEscrow: false,
            flowMode: "DEFAULT",
            useCardPoint: false,
            useAppCardOnly: false,
          },
        });
      } catch (error) {
        console.error("결제 요청 실패:", error);
      }
    },
  },
  mounted() {
    this.fetchPayment();
  },
};
</script>
