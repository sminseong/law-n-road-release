<!-- src/views/client/TemplatePaymentView.vue -->
<template>
  <ClientFrame>
    <div class="container py-5">
      <h5 class="mb-4">주문 내역</h5>

      <div v-if="isLoading" class="text-center py-10">로딩 중…</div>
      <div v-else class="card shadow-sm">
        <ul class="list-group list-group-flush">
          <li
              v-for="item in cartItems"
              :key="item.no"
              class="list-group-item px-4 py-3"
          >
            <div class="row align-items-center">
              <div class="col-2">
                <img :src="item.thumbnailPath" alt="상품 이미지" class="img-fluid"/>
              </div>
              <div class="col-5">
                <h6 class="mb-0">{{ item.name }}</h6>
                <small class="text-muted">
                  {{ item.type === 'EDITOR' ? 'AI 생성형 템플릿' : '문서 기반 템플릿' }}
                  / {{ item.categoryName }}
                </small>
              </div>
              <div class="col-2 text-muted text-truncate">
                {{ item.lawyerName }} 변호사
              </div>
              <div class="col-3 text-end">
                <div class="fw-bold">
                  {{
                    (
                        item.price * (1 - item.discountRate / 100) * (item.quantity ?? 1)
                    ).toLocaleString()
                  }}원
                </div>
                <div class="text-decoration-line-through text-muted small">
                  {{ (item.price * (item.quantity ?? 1)).toLocaleString() }}원
                </div>
              </div>
            </div>
          </li>

          <li class="list-group-item px-4 py-3">
            <div class="d-flex justify-content-between mb-2">
              <span>원래 금액</span>
              <span class="fw-bold">{{ originalTotal.toLocaleString() }}원</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span>할인 금액</span>
              <span class="fw-bold text-danger">-{{ discountAmount.toLocaleString() }}원</span>
            </div>
            <div class="d-flex justify-content-between fs-4">
              <span>최종 결제 금액</span>
              <span class="fw-bold">{{ totalPrice.toLocaleString() }}원</span>
            </div>
          </li>
        </ul>

        <div class="p-4">
          <blockquote class="blockquote mb-3">
            <div class="alert alert-light-primary text-center">
              <p>
                상품 결제 후, 마이페이지 > 주문 내역에서 상품을 다운로드 받으실 수 있습니다.<br/>
                구매 완료 이후에는 환불이 불가하니, 신중히 확인해주세요.
              </p>
            </div>
          </blockquote>
          <button
              class="btn btn-primary w-100"
              @click="goToCheckout"
              :disabled="isProcessing"
          >
            결제하기
          </button>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import http from '@/libs/HttpRequester'
import {getValidToken} from '@/libs/axios-auth.js'

const cartItems = ref([])
const isLoading = ref(true)
const isProcessing = ref(false)
const router = useRouter()

// 1) 장바구니 아이템 불러오기
onMounted(async () => {
  try {
    const res = await http.get('/api/client/cart')
    cartItems.value = res.data
  } catch (err) {
    console.error(err)
  } finally {
    isLoading.value = false
  }
})

// 금액 계산
const originalTotal = computed(() =>
    cartItems.value.reduce(
        (sum, item) => sum + item.price * (item.quantity ?? 1),
        0
    )
)
const totalPrice = computed(() =>
    cartItems.value.reduce(
        (sum, item) =>
            sum +
            item.price * (1 - item.discountRate / 100) * (item.quantity ?? 1),
        0
    )
)
const discountAmount = computed(() =>
    Math.max(0, originalTotal.value - totalPrice.value)
)

// 2) 결제 버튼 핸들러: 주문 생성 → Toss 팝업
async function goToCheckout() {
  if (isProcessing.value) return
  isProcessing.value = true

  try {
    // (1) 주문 생성 요청
    const token = await getValidToken()
    if (!token) throw new Error('로그인이 필요합니다.')
    const res = await http.post('/api/client/cart/checkout', {
      userNo: 2
    })
    const {orderCode, amount} = res.data

    // (2) Toss SDK 호출
    const toss = window.TossPayments('test_ck_d46qopOB89dkZvqg40zOrZmM75y0')
    await toss.requestPayment('카드', {
      amount,
      orderId:   orderCode,
      orderName: '템플릿 구매',
      successUrl: `${location.origin}/client/template/payment/success` +
          `?orderCode=${encodeURIComponent(orderCode)}` +
          `&orderId=${encodeURIComponent(orderCode)}` +
          `&amount=${amount}`,
      failUrl:    `${location.origin}/client/template/payment/fail` +
          `?orderCode=${encodeURIComponent(orderCode)}` +
          `&error=${encodeURIComponent('결제 중 오류가 발생했습니다.')}`,
    })

    // — 팝업에서 리디렉션되므로 여기선 더 이상의 라우팅 불필요 —

  } catch (err) {
    console.error('❌ 결제 실패:', err)
    alert(err.message || '결제에 실패했습니다. 다시 시도해주세요.')
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
.alert-light-primary {
  --fc-alert-bg: #ebf2fa;
  --fc-alert-border-color: #d6d6f5;
  --fc-alert-color: #055160;
  --fc-alert-link-color: #055160;
}
</style>
