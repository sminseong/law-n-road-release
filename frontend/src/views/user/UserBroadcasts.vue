<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import UserFrame from "@/components/layout/User/UserFrame.vue";
import { OpenVidu } from "openvidu-browser";
import axios from "axios";

export default defineComponent({
  components: { UserFrame },
  setup() {
    // --- 방송 시청 OpenVidu 관련 ---
    const videoContainer = ref(null);
    const sessionId = "ses_FBdN3DN4gu";

    const connectOpenVidu = async () => {
      try {
        const { data: token } = await axios.post(
            "/api/broadcast/token",
            {
              sessionId,
              role: "SUBSCRIBER"
            },
            {
              headers: {
                "Content-Type": "application/json"
              }
            }
        );

        const OV = new OpenVidu();
        const session = OV.initSession();

        session.on("streamCreated", ({ stream }) => {
          const subscriber = session.subscribe(stream, undefined);

          subscriber.on("videoElementCreated", (event) => {
            const video = event.element;
            video.style.width = "100%";
            video.style.height = "100%";
            video.style.objectFit = "cover"; // 중요: 비율 유지
            video.style.borderRadius = "1rem"; // 추가: UI 통일

            // 여기가 중요!!!
            nextTick(() => {
              if (videoContainer.value) {
                videoContainer.value.innerHTML = "";
                videoContainer.value.appendChild(video);
              } else {
                console.warn("❌ videoContainer is null!");
              }
            });
          });
        });

        await session.connect(token);
        console.log("✅ [시청자] 방송 연결 완료");
      } catch (err) {
        console.error("❌ [시청자] 오류 발생:", err);
      }
    };

    // --- 채팅 WebSocket 관련 ---
    const stompClient = ref(null);
    const nickname = "홍길동";           // 실제 로그인 닉네임으로 대체
    const broadcastNo = 3;             // 실제 방송 ID로 대체
    const message = ref("");
    const messages = ref([]);
    const messageContainer = ref(null);

    // 스크롤 하단 이동
    const scrollToBottom = () => {
      nextTick(() => {
        if (messageContainer.value) {
          messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
        }
      });
    };

    onMounted(() => {
      connect();
      connectOpenVidu();
    });
    // STOMP 연결
    const connect = () => {
      stompClient.value = new Client({
        // SockJS 팩토리로 연결
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
        reconnectDelay: 5000,
        onConnect: () => {
          // 1) 구독: /topic/{broadcastNo}
          stompClient.value.subscribe(
              `/topic/${broadcastNo}`,
              (msg) => {
                messages.value.push(msg.body);
                scrollToBottom();
              }
          );
          // 2) 입장 알림 전송
          stompClient.value.publish({
            destination: "/app/chat.addUser",
            body: JSON.stringify({ broadcastNo, nickname })
          });
        },
        onStompError: (frame) => {
          console.error("STOMP error:", frame);
        }
      });
      stompClient.value.activate();
    };

    const sendMessage = () => {
      const trimmed = message.value.trim();
      if (!trimmed || !stompClient.value?.connected) return;

      // 메시지 전송
      stompClient.value.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify({ broadcastNo, nickname, message: trimmed })
      });
      message.value = "";
      scrollToBottom();
    };
    onBeforeUnmount(() => stompClient.value?.deactivate());

    return {
      message,
      messages,
      sendMessage,
      messageContainer,
      videoContainer,
    };
  },
});
</script>

<template>
  <UserFrame>
    <div class="position-relative w-100 vh-100">
      <!-- 영상 출력 영역 -->
      <div
          ref="videoContainer"
          class="position-absolute top-0 start-0 bg-dark shadow rounded d-flex align-items-center justify-content-center"
          style="width: calc(100% - 480px); height: 520px; margin: 2rem; overflow: hidden;"
      >
        <!-- OpenVidu 영상이 여기에 붙음 -->
      </div>

      <!-- 채팅 입력 영역 -->
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
              placeholder="메시지를 입력하세요"
              @keyup.enter="sendMessage"/>
        </div>
      </div>
    </div>
  </UserFrame>
</template>

<style scoped>
.scroll-hidden::-webkit-scrollbar {
  display: none;
}
.scroll-hidden {
  -ms-overflow-style: none;     /* IE, Edge */
}
</style>
