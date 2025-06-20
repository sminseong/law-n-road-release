<script>
import {defineComponent, ref, onMounted, onBeforeUnmount, nextTick} from "vue";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import {OpenVidu} from "openvidu-browser";
import axios from "axios";
import {useRoute} from "vue-router";
import {getValidToken, makeApiRequest} from "@/libs/axios-auth.js";
import HttpRequester from "@/libs/HttpRequester.js";

export default defineComponent({
  components: {ClientFrame},
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
    const viewerCount = ref(0);
    // 신고 관련 상태
    const showReportModal = ref(false) // 모달 열기/닫기
    const reportReasonCode = ref('')   // 선택된 신고 사유 코드
    const reportDetail = ref('')       // 상세 사유

    // 신고 사유 코드 목록 (백엔드와 일치)
    const reportReasonOptions = ref([])

    const loadReportReasons = async () => {
      try {
        const {data} = await axios.get('/api/client/broadcast/report-reasons')
        reportReasonOptions.value = data
        console.log('✅ 신고 사유 목록 로딩 완료:', data)
      } catch (error) {
        console.error('❌ 신고 사유 목록 로딩 실패:', error)
      }
    }

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
        const {data} = await axios.get(`/api/client/broadcast/view-detail/${broadcastNo.value}`);
        broadcastInfo.value = data;
        console.log("📄 방송 정보 로딩 완료:", data);
      } catch (e) {
        console.error("❌ 방송 정보 조회 실패:", e);
      }
    };

    const connectOpenVidu = async () => {
      try {
        const {data} = await axios.get(`/api/client/broadcast/${broadcastNo.value}/token`);
        const {sessionId, token, startTime} = data;
        streamStartTime = new Date(startTime); // 방송 시작 시간 받아서 저장

        console.log("👁️ 시청자 sessionId:", sessionId);
        console.log("🔑 시청자 token:", token);

        const OV = new OpenVidu();
        session.value = OV.initSession();

        // 시청자 수 업데이트 함수
        const updateViewerCount = () => {
          if (!session.value) return;
          viewerCount.value = session.value.remoteConnections.size;
        };

        // 시청자 수 동기화 이벤트 (모든 사용자에게 적용됨)
        session.value.on("connectionCreated", updateViewerCount);
        session.value.on("connectionDestroyed", updateViewerCount);

        // 스트림 수신 처리
        session.value.on("streamCreated", ({stream}) => {
          console.log("📡 시청자: streamCreated 발생");

          const subscriber = session.value.subscribe(stream, undefined);
          console.log("Subscribing to", stream.connection.connectionId);

          // 방송 시간 시작
          startTimer();

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

    const submitReport = async () => {
      if (!reportReasonCode.value) {
        alert('신고 사유를 선택해주세요.');
        return;
      }

      try {
        await axios.post('/api/client/broadcast/report', {
          broadcastNo: broadcastNo.value,
          reasonCode: reportReasonCode.value,
          detailReason: reportDetail.value
        });

        alert('신고가 정상적으로 접수되었습니다.');
        showReportModal.value = false;
        reportReasonCode.value = '';
        reportDetail.value = '';
      } catch (err) {
        console.error('신고 실패', err);
        alert('신고 처리 중 오류가 발생했습니다.');
      }
    };


    /** 언마운트 / 마운트 정리 */
    onBeforeUnmount(() => {
      console.log("시청자 페이지 종료 - 세션 종료");
      if (session.value) session.value.disconnect();
      if (timerInterval) clearInterval(timerInterval);
      stompClient.value?.deactivate?.();
      closeDropdown();
      window.removeEventListener('mousedown', handlePreQClickOutside);

    });

    onMounted(() => {
      connect();
      loadBroadcastInfo();
      connectOpenVidu();
      loadReportReasons()
    });


    /** =============== 채팅 관련 =============== */
    const stompClient = ref(null);
    const message = ref("");
    const messages = ref([]);
    const messageContainer = ref(null);
    const nicknameColors = ref({});
    const myNo = ref(null);

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

    //  const fetchMyNo = async () => {
    //   try {
    //     const res = await makeApiRequest({method: 'get', url: "/api/client/my-no",})
    //     if (res?.data) {
    //       myNo.value = res.data
    //     }
    //   } catch (err) {
    //     console.error('프로필 조회 실패:', err)
    //   }
    // }

    //



    async function fetchMyNo() {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("로그인이 필요합니다!");
        return false;
      }
      const res = await axios.get("/api/client/my-no", {
        headers: {Authorization: `Bearer ${token}`}
      });
      myNo.value = res.data;
      return true;
    }




    // STOMP 연결 및 입장 메시지 전송
    const connect = () => {
      const token = localStorage.getItem('token');
      if (!token) {
        alert("로그인이 필요합니다!");
        return;
      }
      fetchMyNo().then((ok) => {
        if (!ok) return;
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
            //입장
            stompClient.value.publish({
              destination: "/app/chat.addUser",
              body: JSON.stringify({broadcastNo: broadcastNo.value}),
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            messages.value.push({
              type: "WELCOME",
              message: "📢 도로 위 질서만큼이나 채팅 예절도 중요합니다. 부적절한 내용은 전송이 제한되니 모두가 함께 즐기는 방송을 만들어주세요. 😊"
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
      });
    };

    // 채팅 메시지 전송 (type: "CHAT"만 전달)
    // const sendMessage = () => {
    //   const trimmed = message.value.trim();
    //   const token = localStorage.getItem('token');
    //   if (!trimmed || !stompClient.value?.connected) return;
    //   if (!token) {
    //     alert("로그인이 필요합니다!");
    //     return;
    //   }
    //   stompClient.value.publish({
    //     destination: "/app/chat.sendMessage",
    //     body: JSON.stringify({
    //       broadcastNo: broadcastNo.value,
    //       message: trimmed,
    //     }),
    //     headers: {
    //       Authorization: `Bearer ${token}`,
    //     },
    //   });
    //   message.value = "";
    //   scrollToBottom();
    // };


    const sendMessage = async () => {
      const trimmed = message.value.trim();
      if (!trimmed || !stompClient.value?.connected) return;

      try {
        // 항상 유효한 토큰 가져오기
        const token = await getValidToken();
        if (!token) {
          alert("로그인이 필요합니다!");
          return;
        }
        // publish 자체도 try 안에!
        stompClient.value.publish({
          destination: "/app/chat.sendMessage",
          body: JSON.stringify({
            broadcastNo: broadcastNo.value,
            message: trimmed,
          }),
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        message.value = "";
        scrollToBottom();
      } catch (err) {
        console.error('메시지 전송 실패:', err);
        alert('메시지 전송 중 오류가 발생했습니다.');
      }
    }




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
              reportedUserNo: myNo.value,
              nickname: selectedUser.value,
              message: selectedMessage.value,
            },
            {
              headers: {Authorization: `Bearer ${token}`}
            },
        );
      } catch (e) {
      }
      isConfirmModal.value = false;
      isCompleteModal.value = true;

    };

    const closeCompleteModal = () => {
      isCompleteModal.value = false;
    };


// 사전 질문 표시
    const showPreQDropdown = ref(false);
    const preQuestions = ref([]);
    const isPreQLoading = ref(false);
    const preQBtnRef = ref(null);
    const preQDropdownRef = ref(null);

    // API 호출
    const fetchPreQuestions = async () => {
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get(`/api/client/broadcasts/schedule/${broadcastNo.value}`, {
          headers: {Authorization: `Bearer ${token}`}
        });
        const data = Array.isArray(res.data) ? res.data : res.data.data;
        preQuestions.value = data.map(q => ({
          ...q,
          checked: false
        }));
      } catch (e) {
        console.error("사전 질문 불러오기 실패:", e);
      }
    };

    const togglePreQDropdown = async () => {
      showPreQDropdown.value = !showPreQDropdown.value;
      if (showPreQDropdown.value) {
        await fetchPreQuestions();
        // 클릭 바깥 감지
        nextTick(() => window.addEventListener('mousedown', handlePreQClickOutside));
      } else {
        window.removeEventListener('mousedown', handlePreQClickOutside);
      }
    };

    const handlePreQClickOutside = (e) => {
      // 드롭다운과 버튼 바깥 클릭시 닫힘
      if (
          preQDropdownRef.value && !preQDropdownRef.value.contains(e.target) &&
          preQBtnRef.value && !preQBtnRef.value.contains(e.target)
      ) {
        showPreQDropdown.value = false;
        window.removeEventListener('mousedown', handlePreQClickOutside);
      }
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
      showReportModal,
      reportReasonCode,
      reportDetail,
      reportReasonOptions,
      submitReport,
      myNo,
      showPreQDropdown, preQuestions, isPreQLoading,
      togglePreQDropdown, preQBtnRef, preQDropdownRef,
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

          <!-- 변호사 정보 + 알림신청 + 신고버튼 -->
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

            <!-- 📛 방송 신고 버튼 -->
            <button class="btn btn-outline-danger btn-sm" @click="showReportModal = true">
              🚨 방송 신고
            </button>
          </div>
        </div>
      </div>


      <!-- 신고 모달 -->
      <div v-if="showReportModal"
           class="position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-50 d-flex align-items-center justify-content-center"
           style="z-index: 1050;">
        <div class="bg-white p-4 rounded shadow" style="width: 480px;">

          <!-- 제목 -->
          <h5 class="fw-bold mb-3">🚨 방송 신고</h5>

          <!-- 방송 제목 표시 박스 -->
          <div class="bg-light p-3 rounded text-dark fw-semibold mb-3">
            {{ broadcastInfo.title }}
          </div>

          <hr class="my-3"/>

          <!-- 신고 사유 라디오 버튼 목록 -->
          <div class="mb-4">
            <label class="form-label d-block mb-3 fw-semibold">신고 사유</label>
            <div class="d-flex flex-column gap-3">
              <div
                  v-for="option in reportReasonOptions"
                  :key="option.code"
                  class="border rounded px-3 py-2 d-flex align-items-center"
                  :class="{
            'bg-light border-primary': reportReasonCode === option.code,
            'bg-white': reportReasonCode !== option.code
          }"
                  style="cursor: pointer; transition: background-color 0.2s;"
                  @click="reportReasonCode = option.code"
              >
                <input
                    class="form-check-input me-3"
                    type="radio"
                    :id="option.code"
                    name="reportReason"
                    :value="option.code"
                    v-model="reportReasonCode"
                    style="cursor: pointer;"
                />
                <label
                    class="form-check-label fs-6 fw-normal text-dark mb-0"
                    :for="option.code"
                    style="cursor: pointer;"
                >
                  {{ option.label }}
                </label>
              </div>
            </div>
          </div>

          <hr class="my-3"/>

          <!-- 상세 입력 -->
          <div class="mb-4">
            <label class="form-label fw-semibold">상세 내용 <span class="text-muted">(선택)</span></label>
            <textarea
                v-model="reportDetail"
                class="form-control"
                rows="3"
                placeholder="신고 내용을 구체적으로 작성해 주세요."
            ></textarea>
          </div>

          <!-- 버튼 -->
          <div class="d-flex justify-content-end gap-2">
            <button class="btn btn-secondary px-4" @click="showReportModal = false">취소</button>
            <button class="btn btn-danger px-4" @click="submitReport">신고 제출</button>
          </div>
        </div>
      </div>


      <!-- 채팅 영역 -->
      <div class="position-absolute border rounded shadow p-4 d-flex flex-column bg-white"
           style="width: 400px; height: 715px; top: 2rem; right: 2rem;">

        <!-- 채팅 상단 제목 및 아이콘 -->
        <div class="d-flex align-items-center justify-content-between mb-3 pb-2 border-bottom position-relative">
          <!-- 왼쪽: 채팅 타이틀 -->
          <div class="fw-bold fs-5">채팅</div>
          <!-- 오른쪽: 사전질문 버튼 -->
          <div>
            <button class="btn btn-link px-1 py-0 text-decoration-none"
                    style="font-size:1.23rem;"
                    @click="togglePreQDropdown"
                    ref="preQBtnRef"
                    title="사전질문 보기">📝
            </button>
          </div>
          <!-- 드롭다운(채팅 상단 전체 너비) -->
          <div v-if="showPreQDropdown"
               class="preq-dropdown"
               ref="preQDropdownRef"
               style="position:absolute; top:110%; left:0; right:0; width:100%; min-width:0; max-width:none; z-index:1000;">
            <div class="preq-dropdown-inner">
              <div class="fw-bold px-2 pt-2 pb-1" style="font-size:1.05rem;">사전 질문 목록</div>
              <div v-if="isPreQLoading" class="px-3 py-3 text-muted small">불러오는 중...</div>
              <div v-else-if="preQuestions.length === 0" class="px-3 py-3 text-muted small">등록된 사전 질문이 없습니다.</div>
              <ul v-else class="list-group preq-scroll" style="max-height:220px; overflow:auto;">
                <li v-for="q in preQuestions" :key="q.no"
                    class="border rounded-2 my-2 mx-2 shadow-sm px-3 py-2"
                    style="font-size:0.99rem; background: #fff;">
                  <div class="fw-semibold mb-1" style="color:#3180e3">{{ q.nickname }}</div>
                  <div style="color:#222">{{ q.content }}</div>
                </li>
              </ul>


            </div>
          </div>
        </div>


        <!-- 메시지 출력 -->
        <div ref="messageContainer"
             class="flex-grow-1 overflow-auto mb-3 scroll-hidden"
             style="scroll-behavior: smooth;">
          <div v-for="(msg, index) in messages" :key="index" class="mb-3" style="position:relative;">
            <div v-if="msg.type === 'ENTER'"
                 class="w-100 text-center"
                 style="color: #435879; font-size: 0.75rem;">
              {{ msg.message }}
            </div>
            <div v-else-if="msg.type === 'WELCOME'"
                 class="w-100 text-center"
                 style="color: rgb(120,118,118); background: #e4e4e4; border-radius: 12px; font-size: 0.84rem; padding: 9px 2px;">
              {{ msg.message }}
            </div>
            <div v-else-if="msg.type === 'Lawyer'"
                 style="font-size: 0.90rem; display: flex; align-items: center;">
              <!-- 닉네임: 검정색 고정 + 클릭 가능 -->
              <span
                  @click.stop="Number(msg.no) !== Number(myNo) && openDropdown(index, msg)"
                  :style="{
                    color: '#222',
                    userSelect: 'text',
                    cursor: Number(msg.no) === Number(myNo) ? 'default' : 'pointer',
                    fontWeight: 'bold'
                    }">👑 {{ msg.nickname }} 변호사
                <span v-if="dropdownIdx === index && Number(msg.no) !== Number(myNo)"
                      class="nickname-dropdown"
                      style="position:absolute;top:120%;left:0;z-index:10000;">
                  <ul class="dropdown-custom-menu">
                    <li class="menu-report" @click.stop="onReportClick">🚨 메시지 신고 🚨</li>
                </ul>
              </span>
            </span>
              <!-- 메시지: 빨간색 -->
              <span style="color: #fd1900; margin-left: 0.6em;">
              {{ msg.message }}
            </span>
            </div>

            <div v-else style="font-size: 0.90rem; display:flex; align-items:center;">
              <!-- 닉네임 드롭다운 & 랜덤 색상 -->
              <span
                  @click.stop="Number(msg.no) !== Number(myNo) && openDropdown(index, msg)"
                  :style="{
                        color: getNicknameColor(msg.nickname),
                        cursor: Number(msg.no) === Number(myNo) ? 'default' : 'pointer',
                        userSelect: 'text',
                        position: 'relative'
                    }">
                       {{ msg.nickname }}
              <span
                  v-if="dropdownIdx === index && Number(msg.no) !== Number(myNo)"
                  class="nickname-dropdown"
                  style="position:absolute;top:120%;left:0;z-index:10000;">
                  <ul class="dropdown-custom-menu">
                    <li class="menu-report" @click.stop="onReportClick">🚨 메시지 신고 🚨</li>
                  </ul>
                </span>
            </span>
              <span style="color: #222; margin-left:0.6em;"> {{ msg.message }}</span>
            </div>

          </div>
        </div>
        <!-- 입력창 -->
        <div class="d-flex">
          <input v-model="message"
                 type="text"
                 class="form-control bg-body-secondary text-dark border-0 rounded-pill px-3 py-2"
                 placeholder="채팅을 입력해 주세요."
                 @keyup.enter="sendMessage"/>
        </div>
      </div>

      <!-- 신고 확인 모달 -->
      <div v-if="isConfirmModal" class="modal-overlay-dark">
        <div class="modal-custom-box">
          <div class="modal-custom-content">
            <div class="modal-custom-msg">
              <div class="modal-custom-text">
                <strong>{{ selectedUser }}</strong>님의 메시지를 신고하시겠습니까?<br/>
                <p class="fw-light">신고된 메시지는 처리를 위해 수집됩니다.</p>
                <span style="font-size:0.85rem; color:#888;">"{{ selectedMessage }}"</span>
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
                메시지 신고가 정상 접수되었습니다.<br/>
                가이드 위반 여부 검토 후 조치 예정입니다.<br/>
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
.scroll-hidden::-webkit-scrollbar {
  display: none;
}

.scroll-hidden {
  -ms-overflow-style: none;
}

.dropdown-custom-menu {
  background: #232428;
  color: #dedede;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.24);
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

.dropdown-custom-menu li:hover {
  background: #2d2f34;
}

.dropdown-custom-menu .menu-report {
  color: #fd6262;
  background: #26272b;
}

.dropdown-custom-menu .menu-report:hover {
  background: #33292c;
}

.modal-overlay-dark {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(18, 19, 21, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal-custom-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.28);
  min-width: 360px;
  padding: 0;
  overflow: hidden;
  color: black;
}

.modal-custom-content {
  padding: 36px 36px 24px 36px;
}

.modal-custom-msg {
  margin-bottom: 34px;
}

.modal-custom-text {
  font-size: 1.14rem;
  line-height: 1.7;
  font-weight: 600;
}

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

.modal-btn-cancel {
  background: #f47e4a;
  color: #ffffff;
}

.modal-btn-cancel:hover {
  background: #efb485;
}

.modal-btn-ok {
  background: #435879;
  color: #ffffff;
}

.modal-btn-ok:hover {
  background: #7d8bbd;
}

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


.preq-dropdown {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(24, 36, 72, 0.12);
  border: 1px solid #e4e4e7;
  animation: preq-drop-in 0.17s;
}

.preq-dropdown-inner {
  padding: 0 18px 10px 18px;
}


.preq-scroll::-webkit-scrollbar {
  width: 5px;
  background: #eee;
}

.preq-scroll::-webkit-scrollbar-thumb {
  background: #d3d3d3;
  border-radius: 5px;
}

@keyframes preq-drop-in {
  from {
    opacity: 0;
    transform: translateY(-14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

</style>
