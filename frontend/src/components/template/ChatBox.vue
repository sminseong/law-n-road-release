<script setup>
import { ref, watch, nextTick } from 'vue'
import http from '@/libs/HttpRequester'
import html2pdf from 'html2pdf.js'

const props = defineProps({
  content: String,
  description: String,
  variables: {
    type: Array,
    required: true
  }
})

// 채팅 상태
const messages = ref([
  { role: 'assistant', content: '안녕하세요! 문서 작성을 도와드릴게요. 필요한 정보를 하나씩 물어볼게요.' }
])

const input = ref('')
const finalHtml = ref('') // 완성된 문서 HTML

// 메시지 전송 → Gemini 호출
async function send() {
  const text = input.value.trim()
  if (!text) return

  messages.value.push({ role: 'user', content: text })
  input.value = ''

  try {
    const res = await http.post('/api/gemini/interview', {
      description: props.description,
      content: props.content,
      variables: props.variables,
      history: messages.value
    })

    messages.value.push({ role: 'assistant', content: res.data.reply })

    if (res.data.allVariablesFilled) {
      finalHtml.value = res.data.finalHtml
    }

  } catch (err) {
    console.error(err)
    messages.value.push({
      role: 'assistant',
      content: 'AI 서버와의 통신 중 오류가 발생했습니다.'
    })
  }
}

// PDF 저장
async function downloadPdf() {
  await nextTick()
  const element = document.querySelector('.preview-content')
  console.log("11111111111111111 : " + element)
  if (!element) {
    alert("PDF로 저장할 내용이 없습니다.")
    return
  }
  html2pdf().set({
    margin: 10,
    filename: 'document.pdf',
    image: {type: 'jpeg', quality: 0.98},
    html2canvas: {scale: 2},
    jsPDF: {unit: 'mm', format: 'a4', orientation: 'portrait'},
    pagebreak: {mode: ['avoid-all', 'css', 'legacy']}
  }).from(element).save()
}

const chatContainer = ref(null)

// 메시지가 추가될 때마다 스크롤 내리기
watch(messages, async () => {
  await nextTick()
  const el = chatContainer.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
})

function formatMessage(text) {
  return text
      .replace(/```html/g, '')           // 코드 블럭 시작 제거
      .replace(/```/g, '')               // 코드 블럭 끝 제거
      .replace(/<br\s*\/?>/gi, '\n')     // <br> → 줄바꿈
      .replace(/<\/?p>/gi, '\n')         // <p> → 줄바꿈
      .replace(/\n{2,}/g, '\n')          // 연속 줄바꿈 하나로 정리
      .trim()                            // 앞뒤 공백 제거
}

</script>

<template>
  <div class="card p-3 mt-4 chat-box">
    <h5 class="mb-3">AI 채팅</h5>

    <!-- 채팅 영역 -->
    <div class="chat-messages mb-3" ref="chatContainer">
      <div
          v-for="(msg, i) in messages"
          :key="i"
          :class="['message', msg.role]"
      >
        <strong>{{ msg.role === 'user' ? '나' : 'AI' }}:</strong>
        <pre class="message-text">{{ formatMessage(msg.content) }}</pre>
      </div>
    </div>

    <!-- 입력창 -->
    <div class="input-group mb-3">
      <input
          v-model="input"
          @keyup.enter="send"
          type="text"
          class="form-control"
          placeholder="메시지를 입력하세요..."
      />
      <button class="btn btn-primary" @click="send">전송</button>
    </div>

    <!-- 문서 미리보기 -->
    <div v-if="finalHtml" class="preview-box">
      <h6 class="mb-2">문서 미리보기</h6>
      <div class="preview-content border p-3 mb-2" v-html="finalHtml" />
      <button class="btn btn-danger w-100" @click="downloadPdf">PDF로 저장</button>
    </div>
  </div>
</template>

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
.preview-box {
  background: #fff;
  border-top: 1px solid #ddd;
  padding-top: 1rem;
}
.preview-content {
  background: #fcfcfc;
  white-space: pre-wrap;
  min-height: 100px;
}
</style>