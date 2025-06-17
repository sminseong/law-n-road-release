<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import { OpenVidu } from "openvidu-browser";
import axios from "axios";
import { useRoute } from "vue-router";

export default defineComponent({
  components: { ClientFrame },
  setup() {
    /** =============== 방송 관련 =============== */
    const videoContainer = ref(null);
    const route = useRoute();
    const broadcastNo = ref(Number(route.params.broadcastNo));
    const session = ref(null);
    const broadcastInfo = ref({
      title: "",
      categoryName: "",
      keywords: [],
      lawyerName: "",
      lawyerProfilePath: ""
    });
    // 방송 실시간 시간
    const elapsedTime = ref("00:00:00");
    let streamStartTime = null;
    let timerInterval = null;
    // 시청자 수
    const viewerCount = ref(1);

    // 시간 계산
    const startTimer = () => {
      timerInterval = setInterval(() => {
        const now = new Date();
        const diff = new Date(now.getTime() - streamStartTime.getTime());
        const hh = String(diff.getUTCHours()).padStart(2, "0");
        const mm = String(diff.getUTCMinutes()).padStart(2, "0");
        const ss = String(diff.getUTCSeconds()).padStart(2, "0");
        elapsedTime.value = `${hh}:${mm}:${ss}`;
      }, 1000);
    };

    const loadBroadcastInfo = async () => {
      try {
        const { data } = await axios.get(`/api/client/broadcast/view-detail/${broadcastNo.value}`);
        broadcastInfo.value = data;
        console.log("📄 방송 정보 로딩 완료:", data);
      } catch (e) {
        console.error("❌ 방송 정보 조회 실패:", e);
      }
    };

    const connectOpenVidu = async () => {
      try {
        const { data } = await axios.get(`/api/client/broadcast/${broadcastNo.value}/token`);
        const { sessionId, token, startTime } = data;
        streamStartTime = new Date(startTime); // 방송 시작 시간 받아서 저장

        console.log("👁️ 시청자 sessionId:", sessionId);
        console.log("🔑 시청자 token:", token);

        const OV = new OpenVidu();
        session.value = OV.initSession();

        session.value.on("streamCreated", ({ stream }) => {
          console.log("📡 시청자: streamCreated 발생");

          const subscriber = session.value.subscribe(stream, undefined);
          console.log("Subscribing to", stream.connection.connectionId);
          // 시간 시작
          startTimer();
          // 시청자 수
          session.value.on("connectionCreated", () => {
            viewerCount.value = session.value.remoteConnections.size + 1;
          });
          session.value.on("connectionDestroyed", () => {
            viewerCount.value = session.value.remoteConnections.size + 1;
          });

          nextTick(() => {
            const video = document.createElement("video");
            video.autoplay = true;
            video.playsInline = true;
            video.muted = true;
            video.style.width = "100%";
            video.style.height = "100%";
            video.style.objectFit = "cover";

            subscriber.addVideoElement(video);

            if (videoContainer.value) {
              videoContainer.value.innerHTML = "";
              videoContainer.value.appendChild(video);
              console.log("✅ [시청자] video element append 완료");
            } else {
              console.warn("❌ videoContainer is null");
            }
          });
        });

        await session.value.connect(token);
        console.log("✅ [시청자] 방송 연결 완료");
      } catch (err) {
        console.error("❌ [시청자] 방송 연결 실패:", err);
      }
    };







    /** 언마운트 / 마운트 정리 */
    onBeforeUnmount(() => {
      console.log("시청자 페이지 종료 - 세션 종료");
      if (session.value) session.value.disconnect();
      if (timerInterval) clearInterval(timerInterval);
      stompClient.value?.deactivate?.();
      closeDropdown();
    });

    onMounted(() => {
      connect();
      loadBroadcastInfo();
      connectOpenVidu();
    });










    /** =============== 채팅 관련 =============== */
    const stompClient = ref(null);
    const message = ref("");
    const messages = ref([]);
    const messageContainer = ref(null);

    const nicknameColors = ref({});

    //드롭다운/신고 모달 상태
    const dropdownIdx = ref(null);
    const selectedUser = ref(null);
    const selectedMessage = ref(null);
    const isConfirmModal = ref(false);
    const isCompleteModal = ref(false);
    const selectedUserNo = ref(null);

    // 닉네임별 랜덤 색상
    const colorPalette = [
      "#1abc9c", "#034335", "#84ddaa", "#450978",
      "#184563", "#8bc2e4", "#c791dd", "#8e44ad",
      "#837225", "#876124", "#004aff", "#ff6400",
      "#ec8d85", "#c0392b", "#246667", "#e4de0d"
    ];

    function getRandomColor() {
      return colorPalette[Math.floor(Math.random() * colorPalette.length)];
    }

    function getNicknameColor(nick) {
      if (!nicknameColors.value[nick]) {
        nicknameColors.value[nick] = getRandomColor();
      }
      return nicknameColors.value[nick];
    }

    // STOMP 연결 및 입장 메시지 전송
    const connect = () => {
      const token = localStorage.getItem('token');
      if (!token) {
        alert("로그인이 필요합니다!");
        return;
      }
      stompClient.value = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
        reconnectDelay: 5000,
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        onConnect: () => {
          stompClient.value.subscribe(
              `/topic/${broadcastNo.value}`,
              (msg) => {
                const data = JSON.parse(msg.body);
                messages.value.push(data);
                scrollToBottom();
              }
          );
          //입장 시 type: "ENTER"만 전달
          stompClient.value.publish({
            destination: "/app/chat.addUser",
            body: JSON.stringify({ broadcastNo: broadcastNo.value }),
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
        },
        onStompError: (frame) => {
          if (frame.body && frame.body.includes("expired")) {
            alert("로그인이 만료되었습니다. 다시 로그인 해주세요.");
            localStorage.removeItem('token');
            location.href = "/login";
          } else {
            console.error("STOMP error:", frame);
          }
        },
      });
      stompClient.value.activate();
    };

    // 채팅 메시지 전송 (type: "CHAT"만 전달)
    const sendMessage = () => {
      const trimmed = message.value.trim();
      const token = localStorage.getItem('token');
      if (!trimmed || !stompClient.value?.connected) return;
      if (!token) {
        alert("로그인이 필요합니다!");
        return;
      }
      stompClient.value.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify({ broadcastNo: broadcastNo.value, message: trimmed }),
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.value = "";
      scrollToBottom();
    };

    // 스크롤 자동 하단 이동
    const scrollToBottom = () => {
      nextTick(() => {
        if (messageContainer.value) {
          messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
        }
      });
    };

    // 닉네임 드롭다운
    const openDropdown = (idx, msg) => {
      dropdownIdx.value = idx;
      selectedUser.value = msg.nickname;
      selectedMessage.value = msg.message;
      selectedUserNo.value = msg.no;
      setTimeout(() => {
        window.addEventListener("mousedown", onWindowClick);
      }, 0);
    };

    const closeDropdown = () => {
      dropdownIdx.value = null;
      window.removeEventListener("mousedown", onWindowClick);
    };

    const onWindowClick = (e) => {
      if (!e.target.closest(".nickname-dropdown")) closeDropdown();
    };

    // 신고 모달
    const onReportClick = () => {
      isConfirmModal.value = true;
      closeDropdown();
    };

    const confirmReport = async () => {
      try {
        const token = localStorage.getItem('token');
        await axios.post(
            "/api/client/chat/report",
            {
              userNo: selectedUserNo.value,
              nickname: selectedUser.value,
              message: selectedMessage.value,
            },
            {
              headers: { Authorization: `Bearer ${token}` }
            },
        );
      } catch (e) {}
      isConfirmModal.value = false;
      isCompleteModal.value = true;
    };

    const closeCompleteModal = () => {
      isCompleteModal.value = false;
    };

    return {
      videoContainer,
      broadcastInfo,
      broadcastNo,
      message,
      messages,
      sendMessage,
      messageContainer,
      dropdownIdx,
      openDropdown,
      closeDropdown,
      onReportClick,
      isConfirmModal,
      isCompleteModal,
      confirmReport,
      closeCompleteModal,
      selectedUser,
      selectedMessage,
      getNicknameColor,
      elapsedTime,
      viewerCount,
    };
  }
});
</script>


<template>
  <ClientFrame>
    <div class="position-relative w-100 vh-100">
      <!-- 방송 카드 전체 영역 -->
      <div class="position-absolute top-0 start-0 bg-dark shadow rounded d-flex flex-column"
           style="width: calc(100% - 480px); margin: 2rem;">

        <!-- 방송 영상 영역 -->
        <div ref="videoContainer" style="height: 520px;" class="rounded-top"></div>

        <!-- 방송 정보 영역 -->
        <div class="bg-light text-dark p-5 rounded-bottom position-relative">

          <!-- 방송 제목 -->
          <div class="mb-3">
            <h2 class="fs-3 fw-bold mb-2">{{ broadcastInfo.title }}</h2>

            <!-- 키워드 & 방송시간/시청자수 같은 라인에 정렬 -->
            <div class="d-flex justify-content-between align-items-center">
              <!-- 키워드 -->
              <div>
          <span
              v-for="(keyword, index) in broadcastInfo.keywords"
              :key="index"
              class="text-muted me-3 fs-6 fw-semibold"
              style="opacity: 0.75;"
          ># {{ keyword }}</span>
              </div>

              <!-- 방송 시간 & 시청자 수 -->
              <div class="text-muted d-flex gap-4 align-items-center">
          <span>
            <span class="blinking-dot"></span>
            {{ elapsedTime }} 스트리밍 중
          </span>
                <span>👥 {{ viewerCount }}명 시청 중</span>
              </div>
            </div>
          </div>

          <!-- 👤 변호사 정보 + 알림신청 버튼 -->
          <div class="d-flex justify-content-between align-items-end mt-4">
            <!-- 프로필 영역 -->
            <div class="d-flex align-items-center">
              <!-- ✅ 초록 원 컨테이너 -->
              <div class="position-relative d-flex justify-content-center align-items-center"
                   style="width: 80px; height: 80px; border: 3px solid #15ea7e; border-radius: 50%;">
                <!-- 프로필 이미지 -->
                <img
                    :src="broadcastInfo.lawyerProfilePath"
                    alt="변호사 프로필"
                    class="rounded-circle"
                    style="width: 68px; height: 68px; object-fit: cover;"
                />

                <!-- LIVE 뱃지 -->
                <div
                    class="position-absolute bottom-0 start-50 translate-middle-x bg-danger text-white fw-bold px-2 py-1 rounded"
                    style="font-size: 0.8rem; line-height: 1; transform: translate(-30%, 70%);"
                >
                  LIVE
                </div>
              </div>

              <!-- 변호사 이름 + 알림신청 -->
              <div class="d-flex align-items-center ms-3">
                <div class="fs-5 fw-bold me-3">{{ broadcastInfo.lawyerName }} 변호사</div>
                <button class="btn btn-outline-primary btn-sm">🔔 알림신청</button>
              </div>
            </div>
          </div>

        </div>
      </div>


      <!-- 채팅 영역 -->
      <div class="position-absolute border rounded shadow p-4 d-flex flex-column bg-white"
           style="width: 400px; height: 700px; top: 2rem; right: 2rem;">

        <!-- 채팅 상단 제목 및 아이콘 -->
        <div class="d-flex align-items-center justify-content-between mb-3 pb-2 border-bottom">
          <div class="fw-bold fs-5">채팅</div>

        </div>

        <!-- 메시지 출력 -->
        <div ref="messageContainer"
             class="flex-grow-1 overflow-auto mb-3 scroll-hidden"
             style="scroll-behavior: smooth;">
          <div v-for="(msg, index) in messages" :key="index" class="mb-3" style="position:relative;">
            <div v-if="msg.type === 'ENTER'"
                 class="w-100 text-center"
                 style="color: #435879; font-size: 0.9rem;">
              {{ msg.message }}
            </div>
            <div v-else style="font-size: 1.0rem; font-weight: bold; display:flex; align-items:center;">
              <!-- 닉네임 드롭다운 & 랜덤 색상 -->
              <span
                  @click.stop="openDropdown(index, msg)"
                  :style="{
                    color: getNicknameColor(msg.nickname),
                    cursor: 'pointer',
                    userSelect: 'text',
                    position: 'relative',
                    fontWeight: 'bold'
                  }"
              >
                {{ msg.nickname }}
                <span
                    v-if="dropdownIdx === index"
                    class="nickname-dropdown"
                    style="position:absolute;top:120%;left:0;z-index:10000;">
                  <ul class="dropdown-custom-menu">
                    <li class="menu-report" @click.stop="onReportClick">🚨 메시지 신고 🚨</li>
                  </ul>
                </span>
              </span>
              <span style="margin-left:0.6em;">: {{ msg.message }}</span>
            </div>
          </div>
        </div>

        <!-- 입력창 -->
        <div class="d-flex">
          <input v-model="message"
                 type="text"
                 class="form-control bg-body-secondary text-dark border-0 rounded-pill px-3 py-2"
                 placeholder="채팅을 입력해 주세요."
                 @keyup.enter="sendMessage" />
        </div>
      </div>

      <!-- 신고 확인 모달 -->
      <div v-if="isConfirmModal" class="modal-overlay-dark">
        <div class="modal-custom-box">
          <div class="modal-custom-content">
            <div class="modal-custom-msg">
              <div class="modal-custom-text">
                <strong>{{ selectedUser }}</strong>님의 메시지를 신고하시겠습니까?<br />
                <p class="fw-light">신고된 메시지는 처리를 위해 수집됩니다.</p>
                <span style="font-size:0.9rem; color:#888;">"{{ selectedMessage }}"</span>
              </div>
            </div>
            <div class="modal-custom-btns">
              <button class="modal-btn-cancel" @click="isConfirmModal=false">취소</button>
              <button class="modal-btn-ok" @click="confirmReport">확인</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 신고 완료 모달 -->
      <div v-if="isCompleteModal" class="modal-overlay-dark">
        <div class="modal-custom-box">
          <div class="modal-custom-content">
            <div class="modal-custom-msg">
              <div class="modal-custom-text" style="text-align:center;">
                메시지 신고가 정상 접수되었습니다.<br />
                가이드 위반 여부 검토 후 조치 예정입니다.<br />
                감사합니다.
              </div>
            </div>
            <div class="modal-custom-btns">
              <button class="modal-btn-ok" @click="closeCompleteModal">확인</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.scroll-hidden::-webkit-scrollbar { display: none; }
.scroll-hidden { -ms-overflow-style: none; }
.dropdown-custom-menu {
  background: #232428;
  color: #dedede;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.24);
  min-width: 190px;
  padding: 8px 0;
  margin: 0;
  list-style: none;
  border: 1px solid #282a30;
  font-size: 1.07rem;
}
.dropdown-custom-menu li {
  display: flex;
  align-items: center;
  padding: 10px 22px;
  cursor: pointer;
  transition: background 0.13s;
  gap: 10px;
  font-weight: 500;
}
.dropdown-custom-menu li:hover { background: #2d2f34; }
.dropdown-custom-menu .menu-report { color: #fd6262; background: #26272b; }
.dropdown-custom-menu .menu-report:hover { background: #33292c; }

.modal-overlay-dark {
  position: fixed; top:0; left:0; width:100vw; height:100vh;
  background: rgba(18, 19, 21, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-custom-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.28);
  min-width: 360px;
  padding: 0;
  overflow: hidden;
  color: black;
}
.modal-custom-content { padding: 36px 36px 24px 36px; }
.modal-custom-msg { margin-bottom: 34px; }
.modal-custom-text { font-size: 1.14rem; line-height: 1.7; font-weight: 600; }
.modal-custom-btns {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
}
.modal-btn-cancel, .modal-btn-ok {
  padding: 0 0;
  border: none;
  outline: none;
  border-radius: 8px;
  font-size: 1.07rem;
  font-weight: 600;
  height: 48px;
  min-width: 128px;
  cursor: pointer;
  transition: background 0.13s, color 0.12s;
}
.modal-btn-cancel { background: #f47e4a; color: #ffffff; }
.modal-btn-cancel:hover { background: #efb485; }
.modal-btn-ok { background: #435879; color: #ffffff; }
.modal-btn-ok:hover { background: #7d8bbd; }

.blinking-dot {
  width: 10px;
  height: 10px;
  background-color: red;
  border-radius: 50%;
  animation: blink 1s infinite;
  display: inline-block;
  margin-right: 6px;
  vertical-align: middle;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
