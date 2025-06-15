<template>
  <div class="card p-3 mt-4 chat-box">
    <h5 class="mb-3">AI 채팅</h5>
    <div class="chat-messages mb-3">
      <div
          v-for="(msg, i) in messages"
          :key="i"
          :class="['message', msg.role]"
      >
        <strong>{{ msg.role === 'user' ? '나' : 'AI' }}:</strong>
        <span>{{ msg.content }}</span>
      </div>
    </div>
    <div class="input-group">
      <input
          v-model="input"
          @keyup.enter="send"
          type="text"
          class="form-control"
          placeholder="메시지를 입력하세요..."
      />
      <button class="btn btn-primary" @click="send">전송</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import http from '@/libs/HttpRequester'

// 부모로부터 주문번호, tmplNo를 props로 받도록 해도 되고
// const props = defineProps({ orderNo: Number, tmplNo: Number })

const messages = ref([])
const input = ref('')

async function send() {
  const text = input.value.trim()
  if (!text) return
  // 1) 사용자가 입력한 메시지 추가
  messages.value.push({ role: 'user', content: text })
  input.value = ''
  try {
    // 2) AI 서버 호출 (예: /api/ai/chat 에 orderNo, tmplNo, message 전송)
    const res = await http.post('/api/ai/chat', {
      // orderNo: props.orderNo,
      // tmplNo:  props.tmplNo,
      message: text
    })
    // 3) AI 응답 추가
    messages.value.push({ role: 'assistant', content: res.data.reply })
  } catch (err) {
    console.error(err)
    messages.value.push({
      role: 'assistant',
      content: '죄송합니다. 응답을 가져오는 중 오류가 발생했습니다.'
    })
  }
}
</script>

<style scoped>
.chat-box {
  background: #f9f9f9;
}
.chat-messages {
  max-height: 300px;
  overflow-y: auto;
}
.message {
  margin-bottom: .5rem;
}
.message.user {
  text-align: right;
}
.message.assistant {
  text-align: left;
}
</style>
