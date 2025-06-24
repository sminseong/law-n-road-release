<template>
  <ClientFrame>
    <div
        class="container py-5 text-center"
        style="min-height: 100vh; padding: 20px;"
    >
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
          <div class="p-grid-col text--left"><b>주문코드</b></div>
          <div class="p-grid-col text--right" id="orderId">
            {{ orderId }}
          </div>
        </div>

        <div class="p-grid-col" style="margin-top: 30px; justify-content: center;">
          <router-link to="/">
            <button class="button p-grid-col5">메인화면으로 돌아가기</button>
          </router-link>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { useRoute } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()

// 쿼리 파라미터 추출
const orderId = route.query.orderCode || route.query.orderId || ''
const externalCode = route.query.code || 'UNKNOWN'
const externalMessage = route.query.message || '알 수 없는 오류가 발생했습니다.'
</script>

<style scoped>
.container { max-width: 600px; margin: auto; }
</style>
