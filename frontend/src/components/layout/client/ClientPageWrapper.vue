<script setup>
import { onMounted, onBeforeUnmount } from 'vue'

let observer

function updatePadding() {
  const header = document.querySelector('header')
  const page = document.querySelector('.client-page-wrapper')

  if (header && page) {
    const headerHeight = header.getBoundingClientRect().height
    page.style.paddingTop = `${headerHeight + 20}px`
  }
}

onMounted(() => {
  updatePadding()

  const header = document.querySelector('header')
  if (header && window.ResizeObserver) {
    observer = new ResizeObserver(updatePadding)
    observer.observe(header)
  }

  window.addEventListener('resize', updatePadding)
})

onBeforeUnmount(() => {
  if (observer) observer.disconnect()
  window.removeEventListener('resize', updatePadding) // 이제 됨 ✅
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