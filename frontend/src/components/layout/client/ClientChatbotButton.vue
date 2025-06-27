<!--<script setup>-->
<!--import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'-->
<!--import ClientChatbot from './ClientChatbot.vue'-->
<!--import { sendWelcomeMessage, sendChatToBot } from '@/service/chatbotService.js'-->

<!--const isChatbotOpen = ref(false)-->
<!--const buttonRef      = ref(null)-->
<!--const popupStyle     = ref({})-->

<!--function updatePopupPosition() {-->
<!--  const rect = buttonRef.value.getBoundingClientRect()-->
<!--  const popupWidth  = 420-->
<!--  const popupHeight = 560-->

<!--  // 버튼 가운데에 수평 정렬-->
<!--  let left = rect.left + rect.width / 2 - popupWidth / 2-->
<!--  // 버튼 위에 10px 띄우기-->
<!--  let top  = rect.top  - popupHeight     - 10-->

<!--  // 뷰포트 경계(20px 여유) 안으로 클램핑-->
<!--  left = Math.min(-->
<!--      Math.max(20, left),-->
<!--      window.innerWidth  - popupWidth  - 20-->
<!--  )-->
<!--  top = Math.max(20, top)-->

<!--  popupStyle.value = {-->
<!--    position: 'fixed',-->
<!--    left:     `${left}px`,-->
<!--    top:      `${top}px`,-->
<!--    zIndex:   10000,-->
<!--  }-->
<!--}-->

<!--async function openChatbot() {-->
<!--  isChatbotOpen.value = true-->
<!--  await nextTick()-->
<!--  updatePopupPosition()-->
<!--}-->

<!--function closeChatbot() {-->
<!--  isChatbotOpen.value = false-->
<!--}-->

<!--onMounted(() => {-->
<!--  window.addEventListener('scroll', () => {-->
<!--    if (isChatbotOpen.value) updatePopupPosition()-->
<!--  }, { passive: true })-->

<!--  window.addEventListener('resize', () => {-->
<!--    if (isChatbotOpen.value) updatePopupPosition()-->
<!--  })-->
<!--})-->

<!--onBeforeUnmount(() => {-->
<!--  window.removeEventListener('scroll', updatePopupPosition)-->
<!--  window.removeEventListener('resize', updatePopupPosition)-->
<!--})-->
<!--</script>-->

<!--<template>-->
<!--  <teleport to="body">-->
<!--    <div class="chatbot-fixed">-->
<!--      <button-->
<!--        v-if="!isChatbotOpen"-->
<!--        ref="buttonRef"-->
<!--        @click="openChatbot"-->
<!--        class="chatbot-open-btn"-->
<!--        aria-label="챗봇 열기"-->
<!--      >-->
<!--        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-robot" viewBox="0 0 16 16">-->
<!--          <path d="M8 0a.5.5 0 0 1 .5.5V1h1a.5.5 0 0 1 0 1h-.537l.244.488A5.5 5.5 0 0 1 14 7.5V10a2 2 0 0 1-2 2v1a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-1a2 2 0 0 1-2-2V7.5a5.5 5.5 0 0 1 5.793-5.012l.244-.488H6.5a.5.5 0 0 1 0-1h1V.5A.5.5 0 0 1 8 0Zm-3 8a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm6 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"/>-->
<!--        </svg>-->
<!--      </button>-->
<!--    </div>-->
<!--    <div v-if="isChatbotOpen" :style="popupStyle">-->
<!--      <ClientChatbot @close="closeChatbot" />-->
<!--    </div>-->
<!--  </teleport>-->
<!--</template>-->

<!--<style scoped>-->
<!--.chatbot-fixed {-->
<!--  position: fixed;-->
<!--  bottom: 20px;-->
<!--  left: 50%;-->
<!--  margin-left: 700px;-->
<!--  width: 140px;-->
<!--  z-index: 9999;-->
<!--}-->
<!--.chatbot-fixed button {-->
<!--  width: 80px;-->
<!--  height: 80px;-->
<!--  border-radius: 50%;-->
<!--  background: #fff;-->
<!--  border: 2px solid #eee;-->
<!--  box-shadow: 0 2px 8px rgba(0,0,0,0.12);-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  padding: 0;-->
<!--  cursor: pointer;-->
<!--  transition: box-shadow 0.2s;-->
<!--}-->
<!--.chatbot-fixed button:hover {-->
<!--  box-shadow: 0 4px 16px rgba(0,0,0,0.18);-->
<!--}-->
<!--.chatbot-open-btn {-->
<!--  position: fixed;-->
<!--  bottom: 20px;-->
<!--  left: 50%;-->
<!--  margin-left: 700px;-->
<!--  width: 140px;-->
<!--  z-index: 9999;-->
<!--  /* 나머지 스타일… */-->
<!--}-->
<!--</style>-->

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
  position: absolute;
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