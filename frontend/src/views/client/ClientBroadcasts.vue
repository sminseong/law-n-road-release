<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";

export default defineComponent({
  components: { ClientFrame: ClientFrame },
  setup() {
    const socket = ref(null);
    const nickname = "홍길동"; // 실제 닉네임은 사용자 로그인 값으로 대체 예정
    const message = ref("");
    const messages = ref([]);
    const messageContainer = ref(null); // 메시지 영역 DOM 참조

    const connectWebSocket = () => {
      socket.value = new WebSocket(`ws://localhost:8080/ws/chat?nickname=${encodeURIComponent(nickname)}`);

      socket.value.onmessage = (event) => {
        messages.value.push(event.data);
        scrollToBottom(); // 메시지 수신 시 스크롤 아래로
      };

      socket.value.onclose = () => {
        console.log("웹소켓 연결 종료");
      };

      socket.value.onerror = (error) => {
        console.error("웹소켓 에러:", error);
      };
    };

    const sendMessage = () => {
      const trimmed = message.value.trim(); // 앞뒤 공백 제거
      if (!trimmed) return; // 빈 문자열이면 리턴

      if (socket.value && socket.value.readyState === WebSocket.OPEN) {
        socket.value.send(message.value);
        message.value = "";
        scrollToBottom(); //  내가 보낼 때도 스크롤 아래로
      }
    };

    // 메시지 영역을 아래로 스크롤
    const scrollToBottom = () => {
      nextTick(() => {
        if (messageContainer.value) {
          messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
        }
      });
    };

    onMounted(() => {
      connectWebSocket();
    });

    onBeforeUnmount(() => {
      if (socket.value) socket.value.close();
    });

    return {
      message,
      messages,
      sendMessage,
      messageContainer, // 템플릿에 바인딩
    };
  },
});
</script>

<template>
  <ClientFrame>
    <!-- 전체 화면 기준으로 위치 설정 -->
    <div class="position-relative w-100 vh-100">
      <div
          class="position-absolute border rounded shadow p-4 d-flex flex-column"
          style="width: 400px; height: 700px; top: 2rem; right: 2rem;">
        <!-- 메시지 출력 영역 (스크롤 + 자동 아래로 이동) -->
        <div
            ref="messageContainer"
            class="flex-grow-1 overflow-auto mb-3 scroll-hidden"
            style="scroll-behavior: smooth;">
          <div v-for="(msg, index) in messages" :key="index" class="mb-3">
            {{ msg }}
          </div>
        </div>

        <!-- 입력창 영역 -->
        <div class="d-flex">
          <input
              v-model="message"
              type="text"
              class="form-control me-2"
              placeholder="메시지를 입력하세요..."
              @keyup.enter="sendMessage"/>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.scroll-hidden::-webkit-scrollbar {
  display: none;
}
.scroll-hidden {
  -ms-overflow-style: none;     /* IE, Edge */
}
</style>
