<script setup>
import { ref } from 'vue'
import LawyerHeader from './LawyerHeader.vue'
import LawyerSidebar from './LawyerSidebar.vue'
import LawyerFooter from './LawyerFooter.vue'
import LawyerPageWrapper from './LawyerPageWrapper.vue'

const currentTitle = ref('홈 대시보드')
</script>

<template>
  <div class="layout-wrapper d-flex min-vh-100">
    <!-- 사이드바에서 title 변경을 emit -->
    <LawyerSidebar @update:title="currentTitle = $event" />

    <!-- 오른쪽 전체 영역 -->
    <div class="content-wrapper d-flex flex-column flex-grow-1">
      <LawyerHeader :title="currentTitle" />
      <main class="flex-grow-1">
        <LawyerPageWrapper>
          <slot></slot>
        </LawyerPageWrapper>
      </main>
      <LawyerFooter />
    </div>
  </div>
</template>

<style scoped>
.layout-wrapper {
  min-height: 100vh;
}
</style>
