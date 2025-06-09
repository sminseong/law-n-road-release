<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { OpenVidu } from "openvidu-browser";
import UserFrame from "@/components/layout/User/UserFrame.vue";
import axios from "axios";

export default defineComponent({
  components: { UserFrame },
  setup() {
    const socket = ref(null);
    const nickname = "홍길동"; // 실제 닉네임은 사용자 로그인 값으로 대체 예정
    const message = ref("");
    const messages = ref([]);
    const messageContainer = ref(null); // 메시지 영역 DOM 참조

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

        session.value.on('streamCreated', (event) => {
          const subscriber = session.value.subscribe(event.stream, undefined)
        })

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
      connectSession();
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
              placeholder="메시지를 입력하세요..."
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
