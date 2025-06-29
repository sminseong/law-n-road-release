<script setup>
import { ref, onMounted } from 'vue'
import ClientChatbot from '@/components/layout/client/ClientChatbot.vue'

const isChatOpen = ref(false)
const toggleChatbot = () => {
  isChatOpen.value = !isChatOpen.value
}
onMounted(() => {
  window.addEventListener('open-chatbot', () => {
    isChatOpen.value = true
  })
})
</script>

<template>
  <router-view />
  <ClientChatbot v-if="isChatOpen" @close="toggleChatbot" class="floating-chatbot" />
</template>

<style scoped>
.floating-chatbot {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 9999;
}
</style>
