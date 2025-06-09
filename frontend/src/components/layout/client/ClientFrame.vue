<script setup>
import ClientHeader from './ClientHeader.vue'
import ClientFooter from './ClientFooter.vue'
import ClientSidebar from './ClientSidebar.vue'
import ClientPageWrapper from './ClientPageWrapper.vue'
</script>

<template>
  <div class="layout-wrapper d-flex min-vh-100">
    <!-- 사이드바 -->
    <ClientSidebar />

    <!-- 우측 본문 영역 -->
    <div class="content-wrapper d-flex flex-column flex-grow-1">
      <ClientHeader />
      <main class="flex-grow-1">
        <ClientPageWrapper>
          <slot></slot>
        </ClientPageWrapper>
      </main>
      <ClientFooter />
    </div>
  </div>
</template>

<style scoped>
.layout-wrapper {
  min-height: 100vh;
}
</style>
