<template>
  <teleport to="body">
    <!-- 열기 버튼 -->
    <button
        v-if="!isOpen"
        ref="buttonRef"
        @click="open"
        class="chatbot-open-btn"
        aria-label="챗봇 열기"
    >🤖</button>

    <!-- 팝업: 버튼 바로 위에 고정 -->
    <div v-if="isOpen" class="chatbot-popup">
      <ClientChatbot @close="close" />
    </div>
  </teleport>
</template>

<script setup>
import { ref } from 'vue'
import ClientChatbot from './ClientChatbot.vue'

const isOpen     = ref(false)
function open()  { isOpen.value = true  }
function close() { isOpen.value = false }
</script>

<style scoped>
.chatbot-open-btn {
  position: fixed;
  bottom: 20px;
  /* 레이아웃 최대 너비 1200px 기준, 안쪽으로 20px 떨어뜨림 */
  right: calc((100vw - 1200px) / 2 - 190px);
  width: 80px; height: 80px;
  border-radius: 50%;
  background: #fff; border: 2px solid #eee;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; z-index: 10000;
  font-size: 32px;
}
.chatbot-open-btn:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.18);
}

.chatbot-popup {
  position: fixed;
  bottom: 20px;
  right: calc((100vw - 1200px) / 2 - 100px);
  width: 420px; height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  z-index: 10000;
  overflow: hidden;
}
</style>