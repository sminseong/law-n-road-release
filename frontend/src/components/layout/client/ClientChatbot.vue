<script setup>
import {ref} from 'vue'

const emit = defineEmits(['close'])
const userInput = ref('')
const messages = ref([]) // 전체 메시지 기록


// 사용자 메시지 전송
const sendMessage = () => {
  if (!userInput.value.trim()) return

  messages.value.push({ type: 'user', text: userInput.value })
  userInput.value = ''
}
</script>

<template>
  <div class="chatbot-wrapper">
    <!-- 헤더 -->
    <div class="chatbot-header">
      <span>로앤로드 챗봇</span>
      <button class="close-btn" @click="emit('close')">×</button>
    </div>

    <!-- 메시지 영역 -->
    <div class="chat-area">
      <div v-for="(msg, index) in messages" :key="index" class="msg-block">
        <div v-if="msg.type === 'user'" class="msg user-msg">{{ msg.text }}</div>
        <!-- 버튼 포함 응답 처리 -->
        <div v-else-if="msg.type === 'system'" class="msg bot-msg">
          <div class="bot-avatar">🤖</div>
          <div class="msg-text">
            {{ msg.text }}
            <div v-if="msg.buttons && msg.buttons.length" class="msg-buttons mt-2">
              <button
                  v-for="(btn, idx) in msg.buttons"
                  :key="idx"
                  class="chat-btn"
                  @click="handleButtonClick(btn)"
              >
                {{ btn.label }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 입력 영역 -->
    <div class="input-area">
      <input v-model="userInput" @keyup.enter="sendMessage" type="text" placeholder="질문을 입력하세요."/>
      <button class="send-btn" @click="sendMessage">➤</button>
    </div>
  </div>
</template>

<style scoped>
.chatbot-wrapper {
  width: 420px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  z-index: 9999;
}

.chatbot-header {
  background: #353f8e;
  color: white;
  padding: 16px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
}

.close-btn {
  background: transparent;
  color: white;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.chat-area {
  flex: 1;
  padding: 14px;
  overflow-y: auto;
  background: #f1f5f9;
  font-size: 14px;
}

.input-area {
  display: flex;
  padding: 10px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

.input-area input {
  flex: 1;
  border: none;
  padding: 10px;
  border-radius: 8px;
  background: #f9fafb;
  font-size: 14px;
  outline: none;
}

.send-btn {
  background: #353f8e;
  color: white;
  border: none;
  padding: 0 14px;
  margin-left: 8px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.msg-block {
  margin-bottom: 10px;
}

.user-msg {
  text-align: right;
  background: #e0f2fe;
  display: inline-block;
  padding: 8px 12px;
  border-radius: 12px;
  margin-left: auto;
  max-width: 80%;
}

.bot-msg {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.bot-avatar {
  font-size: 20px;
}

.msg-text {
  background: white;
  padding: 10px 14px;
  border-radius: 12px;
  max-width: 80%;
  line-height: 1.4;
}

.msg-buttons {
  display: flex;
  flex-direction: column;
  margin-top: 8px;
  gap: 6px;
}

.chat-btn {
  background: #ffffff;
  border: 1px solid #ccc;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-btn:hover {
  background: #f0f0f0;
}
</style>