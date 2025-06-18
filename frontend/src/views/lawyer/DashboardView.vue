<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a>
    <br>
    <a href="/lawyer/broadcasts/live?scheduleNo=1">라이브 방송하기</a>
    <br>
    <br>
    <h2 class="mb-3">📊 변호사 대시보드</h2>
    <ul>
      <li>오늘 상담 신청: 3건</li>
      <li>예정된 방송: 2건</li>
      <li>광고 클릭수: 128회</li>
    </ul>
    </LawyerFrame>
</template>
