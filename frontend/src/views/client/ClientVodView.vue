<script>
import { defineComponent, ref, onMounted, nextTick } from "vue";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import { OpenVidu } from "openvidu-browser";
import axios from "axios";
import { useRoute } from "vue-router";

export default defineComponent({
  components: { ClientFrame },
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
            video.style.objectFit = "cover";
            video.style.borderRadius = "1rem";
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

    // --- VOD 채팅 재생 ---
    const route = useRoute();
    const broadcastNo = route.params.broadcastNo;
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

    // 실시간처럼 채팅 하나씩 재생
    const playChatsLikeLive = async () => {
      const { data: chatLogs } = await axios.get(
          `/api/broadcast/${broadcastNo}/chats`
      );
      if (!chatLogs.length) return;

      messages.value = [];
      let lastTime = new Date(chatLogs[0].createdAt).getTime();

      for (let i = 0; i < chatLogs.length; i++) {
        const msg = chatLogs[i];
        const msgTime = new Date(msg.createdAt).getTime();
        let delay = 0;
        if (i > 0) {
          delay = msgTime - lastTime;
        }
        await new Promise((res) => setTimeout(res, delay));
        messages.value.push(msg);
        scrollToBottom();
        lastTime = msgTime;
      }
    };

    onMounted(() => {
      connectOpenVidu();
      playChatsLikeLive();
    });

    return {
      messages,
      messageContainer,
      videoContainer,
    };
  },
});
</script>

<template>
  <ClientFrame>
    <div class="position-relative w-100 vh-100">
      <!-- 영상 출력 영역 -->
      <div
          ref="videoContainer"
          class="position-absolute top-0 start-0 bg-dark shadow rounded d-flex align-items-center justify-content-center"
          style="width: calc(100% - 480px); height: 520px; margin: 2rem; overflow: hidden;"
      >
        <!-- OpenVidu 영상이 여기에 붙음 -->
      </div>

      <!-- 채팅 영역 전체 -->
      <div
          class="position-absolute border rounded shadow p-4 d-flex flex-column"
          style="width: 400px; height: 700px; top: 2rem; right: 2rem;"
      >
        <!-- 메시지 출력 영역 (스크롤 + 자동 아래로 이동) -->
        <div
            ref="messageContainer"
            class="flex-grow-1 overflow-auto mb-3 scroll-hidden"
            style="scroll-behavior: smooth;"
        >
          <div v-for="(msg, index) in messages" :key="index" class="mb-3">
            <div
                v-if="msg.type === 'ENTER'"
                class="w-100 text-center"
                style="color: #007bff; font-size: 0.9rem;"
            >
              {{ msg.message }}
            </div>
            <div
                v-else
                style="font-size: 1.0rem; font-weight: bold;"
            >
              {{ msg.nickname }} : {{ msg.message }}
            </div>
          </div>
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
