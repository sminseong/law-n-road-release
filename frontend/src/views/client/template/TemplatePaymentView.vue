<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import http from '@/libs/HttpRequester'

// 카트 아이템 불러오기
const cartItems = ref([])
const isLoading = ref(true)
const router = useRouter()
const isProcessing = ref(false)

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
    cartItems.value.reduce((sum, item) => sum + (item.price * (item.quantity ?? 1)), 0)
)
const totalPrice = computed(() =>
    cartItems.value.reduce((sum, item) =>
        sum + (item.price * (1 - item.discountRate / 100) * (item.quantity ?? 1)), 0
    )
)
const discountAmount = computed(() =>
    Math.max(0, originalTotal.value - totalPrice.value)
)

// 결제 버튼 핸들러
async function goToCheckout() {
  if (isProcessing.value) return
  isProcessing.value = true

  try {
    // ✅ 1) 주문 생성 요청: cartIds는 안 보내도 됨 (백엔드에서 userNo=1L 하드코딩 중)
    const res = await http.post('/api/client/cart/aa', {})

    // ✅ 2) orderNo 응답받기
    const orderNo = res.data.orderNo
    console.log('✅ 주문번호:', orderNo)

    // ✅ 3) 성공 알림
    alert('결제가 완료되었습니다! 주문내역으로 이동합니다.')

    // ✅ 4) 주문내역 페이지로 이동
    router.push(`/client/template/orders`)

  } catch (err) {
    console.error('❌ 결제 실패:', err)
    alert('❌ 결제에 실패했습니다. 잠시 후 다시 시도해주세요.')
  } finally {
    isProcessing.value = false
  }
}
</script>

<template>
  <ClientFrame>
    <div class="container py-5">
      <h5 class="mb-4">주문 내역</h5>
      <div class="card shadow-sm">
        <ul class="list-group list-group-flush">
          <li
              v-for="item in cartItems"
              :key="item.no"
              class="list-group-item px-4 py-3"
          >
            <div class="row align-items-center">
              <div class="col-2">
                <img :src="item.thumbnailPath" alt="상품 이미지" class="img-fluid" />
              </div>
              <div class="col-5">
                <h6 class="mb-0">{{ item.title }}</h6>
                <small class="text-muted">
                  {{ item.type === 'EDITOR' ? 'AI 생성형 템플릿' : '문서 기반 템플릿' }} / {{ item.categoryName }}
                </small>
              </div>
              <div class="col-2">
                <small class="text-muted">
                  {{ item.lawyerName }} 변호사
                </small>
              </div>
              <div class="col-3 text-end">
                <div class="fw-bold">
                  {{ ((item.price * (1 - item.discountRate / 100)) * (item.quantity ?? 1)).toLocaleString() }}원
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
            <div class="alert alert-light-primary text-center" >
              <p>
                상품 결제 후, 마이페이지 > 주문 내역에서 상품을 다운로드 받으실 수 있습니다. <br>
                상품 다운로드 또는 사용 완료 이후에는 전액 환불이 불가하며, 부분 환불 또한 제공되지 않습니다.<br>
                구매 전 신중한 확인을 부탁드립니다.
              </p>
            </div>
          </blockquote>
          <button class="btn btn-primary w-100"
                  @click="goToCheckout"
                  :disabled="isProcessing">
            결제하기
          </button>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>

.alert-light-primary{
  /* 연한 하늘색 배경 */
  --fc-alert-bg: #ebf2fa;
  /* 연한 하늘색 테두리 */
  --fc-alert-border-color: #d6d6f5;
  /* 약간 어두운 청색 텍스트 */
  --fc-alert-color: #055160;
  /* 링크 색도 텍스트와 동일하게 */
  --fc-alert-link-color: #055160;
}
</style>
