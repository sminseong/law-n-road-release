<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import { useRouter } from 'vue-router'

// 라우터에서 방송 번호 가져오기
const route = useRoute();
const router = useRouter();
const broadcastNo = route.params.broadcastNo;
// vod 불러오기
const vodInfo = ref(null);

// formatDuration 함수 정의
const formatDuration = (seconds) => {
  const h = String(Math.floor(seconds / 3600)).padStart(2, "0");
  const m = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
  const s = String(seconds % 60).padStart(2, "0");
  return `${h}:${m}:${s}`;
};

// vod 가져오는 함수
const fetchVodInfo = async () => {
  try {
    const { data } = await axios.get(`/api/public/vod/view/${broadcastNo}`);
    vodInfo.value = data;
  } catch (err) {
    console.error("❌ VOD 정보 가져오기 실패:", err);
  }
};

const goToLawyerHomepage = () => {
  const userNo = vodInfo.value.lawyerNo
  console.log(userNo)
  if (!userNo || userNo === 0) {
    alert('변호사 정보가 없습니다.')
    return
  }
  router.push(`/lawyer/${userNo}/homepage`)
}




// 컴포넌트 마운트 시 실행
onMounted(() => {
  fetchVodInfo();
  playChatsLikeLive();
});





// 메시지 관련 상태
const messages = ref([]);
const messageContainer = ref(null);

// 스크롤을 맨 아래로 내리는 함수
const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
  });
};

// 채팅을 실시간처럼 하나씩 재생
const playChatsLikeLive = async () => {
  const { data: chatLogs } = await axios.get(`/api/broadcast/${broadcastNo}/chats`);
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

</script>


<template>
  <ClientFrame>
    <div class="position-relative w-100 vh-100">
      <!-- VOD 카드 전체 영역 -->
      <div
          v-if="vodInfo"
            class="position-absolute top-0 start-0 bg-dark shadow rounded d-flex flex-column"
          style="width: calc(100% - 480px); margin: 2rem;"
        >
        <!-- 영상 출력 영역 -->
        <div
            ref="videoContainer"
            style="height: 520px;"
            class="rounded-top d-flex align-items-center justify-content-center bg-black"
        >
          <video
              v-if="vodInfo?.vodPath"
              :src="vodInfo.vodPath"
              controls
              class="w-100 h-100"
              style="object-fit: cover; border-radius: 0.5rem;"
          >
            해당 브라우저는 video 태그를 지원하지 않습니다.
          </video>
        </div>

        <!-- VOD 정보 영역 -->
        <div class="bg-light text-dark p-5 rounded-bottom position-relative">
          <!-- 방송 제목 -->
          <div class="mb-3">
            <h2 class="fs-3 fw-bold mb-2">{{ vodInfo.title }}</h2>

            <!-- 키워드 & 영상 길이/조회수/업로드일자 -->
            <div class="d-flex justify-content-between align-items-center">
              <!-- 키워드 -->
              <div>
                <span
                    v-for="(keyword, index) in vodInfo.keywords"
                    :key="index"
                    class="text-muted me-3 fs-6 fw-semibold"
                    style="opacity: 0.75;"
                ># {{ keyword }}</span>
              </div>

              <!-- 영상 정보 -->
              <div class="text-muted d-flex gap-4 align-items-center">
                <!-- 영상 길이 (⏱️ 테두리 강조) -->
                <span class="border px-2 py-1 rounded text-dark-gray">
                  ⏱️ {{ formatDuration(vodInfo.duration) }}
                </span>

                <!-- 조회수 (숫자만 진하게) -->
                <span>
                  조회수 <span class="fw-bold text-dark-gray">{{ vodInfo.viewCount }}</span>회
                </span>

                <!-- 업로드 일자 -->
                <span class="text-dark-gray">{{ vodInfo.createdAt.slice(0, 10) }}</span>
              </div>
            </div>
          </div>

          <!-- 변호사 정보 -->
          <div class="d-flex align-items-center mt-4">
            <!-- 프로필 이미지 -->
            <div
                @click="goToLawyerHomepage"
                role="button"
                class="profile-border-hover position-relative d-flex justify-content-center align-items-center"
            >
              <img
                  :src="vodInfo.lawyerProfile"
                  alt="변호사 프로필"
                  class="rounded-circle"
                  style="width: 68px; height: 68px; object-fit: cover;"
              />
            </div>
            <!-- 이름 -->
            <div class="d-flex align-items-center ms-3">
            <span
                @click="goToLawyerHomepage"
                role="button"
                class="fs-5 fw-bold me-3 text-primary text-decoration-none"
                style="cursor: pointer;"
            >
                  {{ vodInfo.lawyerName }} 변호사
                </span>
              <button class="btn btn-outline-primary btn-sm">🔔 알림신청</button>
            </div>
          </div>
        </div>
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
            <div v-else style="font-size: 1.0rem; font-weight: bold;">
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

/* 애니메이션 */
@keyframes modalPop {
  0% {
    transform: scale(0.85);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.profile-border-hover {
  width: 80px;
  height: 80px;
  border: 3px solid #15ea7e;
  border-radius: 50%;
  cursor: pointer;
  transition: border-width 0.2s ease;
}
.profile-border-hover:hover {
  border-width: 5px;
}
</style>
