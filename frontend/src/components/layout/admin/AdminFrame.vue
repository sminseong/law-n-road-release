<script setup>
import { ref } from 'vue'
import AdminHeader from './AdminHeader.vue'
import AdminSidebar from './AdminSidebar.vue'
import AdminFooter from './AdminFooter.vue'
import AdminPageWrapper from './AdminPageWrapper.vue'

const currentTitle = ref('홈 대시보드')
</script>

<template>
  <div class="layout-wrapper d-flex min-vh-100">
    <!-- 사이드바에서 title 변경을 emit -->
    <AdminSidebar @update:title="currentTitle = $event" />

    <!-- 오른쪽 전체 영역 -->
    <div class="content-wrapper d-flex flex-column flex-grow-1">
      <AdminHeader :title="currentTitle" />
      <main class="flex-grow-1">
        <AdminPageWrapper>
          <slot></slot>
        </AdminPageWrapper>
      </main>
      <AdminFooter />
    </div>
  </div>
</template>

<style scoped>
.layout-wrapper {
  min-height: 100vh;
}
</style>
