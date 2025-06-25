<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import LawyerHeader from './LawyerHeader.vue'
import LawyerSidebar from './LawyerSidebar.vue'
import LawyerFooter from './LawyerFooter.vue'
import LawyerPageWrapper from './LawyerPageWrapper.vue'

const route = useRoute()
const currentTitle = ref('')

function resolveTitle(path) {
  if (path === '/lawyer') return '홈 대시보드'
  if (/^\/lawyer\/\d+\/reservation/.test(path)) return '1:1 상담예약'
  if (path.startsWith('/lawyer/broadcasts/schedule')) return '방송 스케줄'
  if (path.startsWith('/lawyer/templates')) return '문서 템플릿'
  if (path.startsWith('/lawyer/qna')) return 'Q&A 관리'
  if (path.startsWith('/lawyer/ads')) return '광고 관리'
  if (path.startsWith('/lawyer/profile')) return '계정 설정'
  return ''
}

watch(() => route.path, (newPath) => {
  currentTitle.value = resolveTitle(newPath)
}, { immediate: true })
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
