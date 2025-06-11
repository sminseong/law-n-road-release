<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a><br>
    <router-link
        :to="{ name: 'TimeSlotUpdate', params: { lawyerNo: 1 } }">
      예약 스케줄 설정
    </router-link>
    <br>
    <br>
  <div class="container py-4">
    <h2>1:1 상담 예약</h2>
    <p>상담 예약 내역 또는 스케줄 관리 영역입니다.</p>
  </div>
  </LawyerFrame>
</template>
