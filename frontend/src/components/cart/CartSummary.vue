<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  originalTotal: Number,
  totalPrice: Number,
})

// 할인액 계산
const discountAmount = computed(() =>
    Math.max(0, props.originalTotal - props.totalPrice)
)

// 버튼 클릭 시 호출
function goToPayment() {
  router.push('/client/template/payment')
}
</script>

<template>
  <div class="card p-4">
    <h5 class="mb-3">Summary</h5>
    <ul class="list-group list-group-flush mb-3">

      <!-- 원래 금액 -->
      <li class="list-group-item d-flex justify-content-between">
        <span>원래 금액</span>
        <del>{{ props.originalTotal.toLocaleString() }}원</del>
      </li>

      <!-- 할인 금액 -->
      <li class="list-group-item d-flex justify-content-between">
        <span>할인 금액</span>
        <strong class="text-danger">-{{ discountAmount.toLocaleString() }}원</strong>
      </li>

      <!-- 최종 결제 금액 -->
      <li class="list-group-item d-flex justify-content-between fs-4">
        <span>총 금액</span>
        <strong>{{ props.totalPrice.toLocaleString() }}원</strong>
      </li>
    </ul>
    <button class="btn btn-primary w-100" @click="goToPayment">
      결제하기
    </button>
  </div>
</template>
