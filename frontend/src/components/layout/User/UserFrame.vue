<script setup>
import UserHeader from './UserHeader.vue'
import UserFooter from './UserFooter.vue'
import UserSidebar from './UserSidebar.vue'
import UserPageWrapper from './UserPageWrapper.vue'
</script>

<template>
  <div class="layout-wrapper d-flex min-vh-100">
    <!-- 사이드바 -->
    <UserSidebar />

    <!-- 우측 본문 영역 -->
    <div class="content-wrapper d-flex flex-column flex-grow-1">
      <UserHeader />
      <main class="flex-grow-1">
        <UserPageWrapper>
          <slot></slot>
        </UserPageWrapper>
      </main>
      <UserFooter />
    </div>
  </div>
</template>

<style scoped>
.layout-wrapper {
  min-height: 100vh;
}
</style>
