<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  const header = document.querySelector('header')
  const page = document.querySelector('.client-page-wrapper')

  if (header && page) {
    const headerHeight = header.getBoundingClientRect().height
    page.style.paddingTop = `${headerHeight + 20}px`
  }
})
</script>

<template>
  <div class="container client-page-wrapper px-3">
    <slot></slot>
  </div>
</template>

<style scoped>
.client-page-wrapper {
  /* 여기에 패딩 넣으면 안 됨. JS로 넣을 것 */
  transition: padding-top 0.3s ease;
}
</style>