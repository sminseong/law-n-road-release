<script setup>
import {ref, onMounted, nextTick} from 'vue'
import {sendWelcomeMessage, sendChatToBot} from '@/service/chatbotService.js'

const emit = defineEmits(['close'])

const userInput = ref('')
const messages = ref([]) // 전체 대화 메시지 저장

const chatArea = ref(null) // chat-area를 참조할 DOM 변수

//스크롤 함수
const scrollToBottom = () => {
  if (chatArea.value) {
    chatArea.value.scrollTop = chatArea.value.scrollHeight
  }
}

// 메시지 전송
const sendMessage = async (manualText = null) => {
  const text = manualText || userInput.value
  if (!text.trim()) return
  userInput.value = ''

  messages.value.push({ type: 'user', text })

  try {
    const res = await sendChatToBot(text)

    const content = res.data?.content?.[0]
    if (!content) {
      messages.value.push({ type: 'bot', text: '응답을 가져올 수 없습니다.' })
      return
    }

    const type = content.type || 'text'
    const data = content.data || {}

    if (type === 'text') {
      messages.value.push({ type: 'bot', text: data.details })
      await nextTick()
      scrollToBottom()
    } else if (type === 'button') {
      messages.value.push({
        type: 'bot',
        buttons: data.content // [{ title, type, data }]
      })
      await nextTick()
      scrollToBottom()
    }else if (type === 'image') {
      messages.value.push({
        type: 'bot',
        text: data.description || '',         // 이미지 설명 텍스트
        imageUrl: data.url,                   // 이미지 URL
        title: content.title || ''            // 제목 (선택사항)
      })
      await nextTick()
      scrollToBottom()
    } else if (type === 'group') {
      const groupData = data.content || []
      const title = content.title || ''

      if (groupData.length > 0) {
        messages.value.push({
          type: 'bot',
          text: title, //말풍선 상단 텍스트
          buttons: groupData //버튼 리스트
        })
        await nextTick()
        scrollToBottom()
      }
    }

  } catch (err) {
    console.error('챗봇 응답 실패:', err)
    messages.value.push({ type: 'bot', text: '⚠️ 오류가 발생했습니다.' })
  }
}


// 버튼 클릭 처리
const handleOptionClick = (text) => {
  sendMessage(text)
}


onMounted(async () => {
  try {
    const welcome = await sendWelcomeMessage()

    // .content 기준으로 파싱
    const items = welcome?.content
    if (!items?.length) {
      return
    }

    items.forEach((item, index) => {

      if (item.type === 'text') {
        messages.value.push({
          type: 'system',
          text: item.data.details
        })
      } else if (item.type === 'template') {
        const buttons = item.data?.buttons || []
        const desc = item.data.cover?.description || ''
        messages.value.push({
          type: 'system',
          text: desc,
          options: buttons.map(btn => btn.label)
        })
      }
    })

  } catch (err) {
    console.error('웰컴 메시지 오류:', err)
    messages.value.push({type: 'system', text: '초기 메시지를 불러오지 못했어요.'})
  }
})


</script>

<template>
  <div class="chatbot-wrapper">
    <!-- 헤더 -->
    <div class="chatbot-header">
      <span>로앤로드 챗봇</span>
      <button class="close-btn" @click="emit('close')">×</button>
    </div>

    <!-- 메시지 영역 -->
    <div class="chat-area" ref="chatArea">
      <div
          v-for="(msg, index) in messages"
          :key="index"
          class="msg-block"
          :class="msg.type === 'user' ? 'right' : 'left'"
      >
        <!-- 유저 메시지 -->
        <div v-if="msg.type === 'user'" class="user-msg">{{ msg.text }}</div>

        <!-- 챗봇 메시지 (하나의 블록에 title + text + buttons + image 전부 출력) -->
        <div v-else class="bot-msg">
          <div class="bot-avatar">🤖</div>
          <div class="msg-text">
            <!-- 🔹 title이 있으면 출력 -->
            <div v-if="msg.title" class="msg-title">{{ msg.title }}</div>

            <!-- 🔸 text 출력 -->
            <div v-if="msg.text">{{ msg.text }}</div>

            <!-- 🔹 버튼 출력 -->
            <div v-if="msg.buttons" class="btn-list">
              <div
                  v-for="(btn, i) in msg.buttons"
                  :key="i"
                  class="btn-list-item"
                  @click="handleOptionClick(btn.data?.code || btn.title)"
              >
                {{ btn.title }}
              </div>
            </div>

            <!-- 🔸 이미지 출력 -->
            <div v-if="msg.imageUrl" class="img-wrapper">
              <img :src="msg.imageUrl" alt="챗봇 이미지" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 입력 -->
    <div class="input-area">
      <input v-model="userInput" @keyup.enter="sendMessage()" placeholder="질문을 입력하세요."/>
      <button class="send-btn" @click="sendMessage()">➤</button>
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

.msg-block {
  display: flex;
  margin-bottom: 10px;
}

.msg-block.left {
  flex-direction: row;
}

.msg-block.right {
  flex-direction: row-reverse;
}

.user-msg {
  background: #e0f2fe;
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 70%;
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
  line-height: 1.4;

  max-width: 80%;
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

/* 리스트형 버튼 스타일 */
.btn-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.btn-list-item {
  padding: 10px;
  background: #e6f0ff;
  border-radius: 8px;
  cursor: pointer;
  color: #2563eb;
  font-weight: 500;
  border: 1px solid #cbd5e1;
  transition: background 0.2s;
}

.btn-list-item:hover {
  background: #dbeafe;
}

/* 이미지 스타일 */
.img-wrapper img {
  margin-top: 10px;
  max-width: 100%;
  border-radius: 8px;
}
.msg-title {
  font-weight: bold;
  margin-bottom: 4px;
}
</style>