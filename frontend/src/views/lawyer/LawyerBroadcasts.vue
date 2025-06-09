<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { OpenVidu } from "openvidu-browser";
import UserFrame from "@/components/layout/User/UserFrame.vue";
import axios from "axios";

export default defineComponent({
  components: { UserFrame },
  setup() {
    //openvidu
    const OV = ref(null)
    const session = ref(null)
    const publisher = ref(null)

    const videoContainer = ref(null) // 방송화면 div

    const connectSession = async () => {
      try {
        // 1. 세션 ID 요청
        const sessionRes = await axios.post('/api/broadcast/start')
        const sessionId = sessionRes.data // ex: ses_XXXX
        console.log("세션ID: " + sessionId); // 테스트할때 확인하려고 잠시 넣음

        // 2. 토큰 요청
        const tokenRes = await axios.post('/api/broadcast/token', { sessionId, role: 'PUBLISHER' })
        const token = tokenRes.data // ex: tok_XXXX

        // 3. OpenVidu 세션 연결
        OV.value = new OpenVidu()
        session.value = OV.value.initSession()

        await session.value.connect(token)

        // 4. 퍼블리셔 초기화 (카메라 화면)
        publisher.value = await OV.value.initPublisherAsync(videoContainer.value, {
          videoSource: undefined, // 기본 카메라
          audioSource: undefined, // 기본 마이크
          publishAudio: true,
          publishVideo: true,
          resolution: '640x480',
          frameRate: 30,
          insertMode: 'APPEND',
          mirror: false
        });

        const videoElement = videoContainer.value.querySelector('video')
        if (videoElement) {
          videoElement.style.width = '100%'
          videoElement.style.height = '100%'
          videoElement.style.objectFit = 'cover'  // 비율 유지 + 꽉 채움
          videoElement.style.borderRadius = '1rem' // 부모 div의 rounded와 맞춤
        }

        // 5. 방송화면에 내 영상 붙이기
        session.value.publish(publisher.value)
        publisher.value.addVideoElement(videoContainer.value)

      } catch (err) {
        console.error('오류 발생:', err)
      }
    }

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
      connectSession();
      connect();
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
      messageContainer, // 템플릿에 바인딩
      videoContainer,
    };
  },
});
</script>

<template>
  <UserFrame>
    <!-- 전체 화면 기준으로 방송화면 + 채팅 분리 -->
    <div class="position-relative w-100 vh-100">

      <!-- 방송화면 + 방송 정보 영역을 묶는 wrapper -->
      <div
          class="position-absolute top-0 start-0 bg-dark shadow rounded d-flex flex-column"
          style="width: calc(100% - 480px); margin: 2rem;"
      >
        <!-- 방송 영상 -->
        <div
            ref="videoContainer"
            style="height: 520px;"
            class="rounded-top"
        ></div>

        <!-- 방송 정보 -->
        <div class="bg-light text-dark p-7  rounded-bottom">

          <!-- 제목 + 키워드 -->
          <div class="d-flex justify-content-between align-items-start mb-3">
            <!-- 방송 제목 -->
            <h2 class="fs-3 fw-bold mb-0">교통사고 과실비율 및 손해배상 관련 라이브</h2>

            <!-- 키워드 -->
            <div>
              <span class="badge bg-primary me-1 fs-6">#교통사고</span>
              <span class="badge bg-success me-1 fs-6">#과실비율</span>
              <span class="badge bg-warning text-dark fs-6">#손해배상</span>
            </div>
          </div>

          <!-- 프로필 + 이름 -->
          <div class="d-flex align-items-center mt-5">
            <!-- 동그란 이미지 -->
            <img
                src="/img/profiles/lee.png"
                alt="변호사 프로필"
                class="rounded-circle me-3"
                style="width: 75px; height: 75px; border: 3px solid #15ea7e;"
            />
            <!-- 이름 -->
            <div class="fs-5 fw-bold">이영훈 변호사</div>
          </div>
        </div>
      </div>

      <div
          class="position-absolute border rounded shadow p-4 d-flex flex-column"
          style="width: 400px; height: 700px; top: 2rem; right: 2rem;"
      >
        <!-- 메시지 출력 영역 -->
        <div
            ref="messageContainer"
            class="flex-grow-1 overflow-auto mb-3 scroll-hidden"
            style="scroll-behavior: smooth;"
        >
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
              @keyup.enter="sendMessage"
          />
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
