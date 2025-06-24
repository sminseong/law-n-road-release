<template>
  <LawyerFrame>
    <div class="container py-5 text-center">
      <h2 class="text-2xl font-bold mb-4 text-red-600">
        결제에 실패했습니다
      </h2>
      <p class="mb-6">
        <!-- Toss가 붙여준 실패 정보가 있으면 그대로, 없으면 기본 메시지 -->
        {{ message }}
      </p>
      <button class="btn btn-outline-secondary" @click="goBack">
        다시 시도하기
      </button>
    </div>
  </LawyerFrame>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const route  = useRoute()
const router = useRouter()

// 쿼리로 넘어온 에러 메시지나, 기본 문구
const message = route.query.message || '알 수 없는 오류가 발생했습니다.'

function goBack() {
  // 등록 화면으로 돌아가기
  router.push({ name: 'AdRegister' })
}
</script>
