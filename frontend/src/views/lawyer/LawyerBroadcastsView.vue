<script setup>
import {ref, onMounted, onBeforeUnmount, nextTick} from "vue";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";
import {OpenVidu} from "openvidu-browser";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import axios from "axios";
import {useRoute, useRouter} from "vue-router";
import {getValidToken, makeApiRequest} from "@/libs/axios-auth.js";
import fixWebmDuration from 'webm-duration-fix';

const route = useRoute();
const router = useRouter();
const scheduleNo = route.query.scheduleNo;

const OV = ref(null);
const session = ref(null);
const publisher = ref(null);
const videoContainer = ref(null);

const broadcastInfo = ref({});
const broadcastNo = ref(null);
const elapsedTime = ref("00:00:00");
const viewerCount = ref(1);
let timerInterval = null;

// 방송 녹화 (VOD)
const mediaRecorder = ref(null)
const recordedChunks = ref([])
let startRecordTime = 0;

const preventReload = (e) => {
  e.preventDefault();
  e.returnValue = "";
};

const loadBroadcastInfo = async () => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: `/api/lawyer/broadcast/view-detail/${scheduleNo}`
    })

    if (res?.data) {
      broadcastInfo.value = res.data
      console.log("📺 방송 정보 로딩 성공:", res.data)
    }
  } catch (e) {
    console.error("❌ 방송 정보 조회 실패:", e)
  }
}

const startTimerFrom = (startTime) => {
  timerInterval = setInterval(() => {
    const now = new Date();
    const diff = new Date(now.getTime() - new Date(startTime).getTime());
    const hh = String(diff.getUTCHours()).padStart(2, "0");
    const mm = String(diff.getUTCMinutes()).padStart(2, "0");
    const ss = String(diff.getUTCSeconds()).padStart(2, "0");
    elapsedTime.value = `${hh}:${mm}:${ss}`;
  }, 1000);
};

const updateViewerCount = () => {
  if (!session.value) return;
  const count = session.value.remoteConnections?.size || 0;
  console.log("👥 현재 시청자 수 (방송자 제외):", count);
  viewerCount.value = count;
};

const initPublisherWithDelay = async () => {
  await nextTick();
  publisher.value = await OV.value.initPublisherAsync(videoContainer.value, {
    videoSource: undefined,
    audioSource: undefined,
    publishAudio: true,
    publishVideo: true,
    resolution: "640x480",
    frameRate: 30,
    insertMode: "APPEND",
    mirror: false,
  });

  publisher.value.on("videoElementCreated", (event) => {
    requestAnimationFrame(() => {
      const video = event.element;
      video.style.width = "100%";
      video.style.height = "100%";
      video.style.objectFit = "cover";
      video.style.border = "2px solid red";
      video.style.borderRadius = "0.5rem";
    });
  });

  if (publisher.value) {
    await session.value.publish(publisher.value);
    console.log("✅ 방송 송출 시작됨");
  }

  if (publisher.value && publisher.value.stream) {
    const mediaStream = publisher.value.stream.getMediaStream()
    mediaRecorder.value = new MediaRecorder(mediaStream, {
      mimeType: "video/webm; codecs=vp8"
    });

    recordedChunks.value = [];
    startRecordTime = performance.now();
    mediaRecorder.value.start();

    mediaRecorder.value.ondataavailable = (event) => {
      if (event.data.size > 0) {
        recordedChunks.value.push(event.data);
      }
    }

    mediaRecorder.value.onstop = async () => {
      // 녹화된 Blob 생성
      const blob = new Blob(recordedChunks.value, {type: "video/webm"});

      // 재생 시간 측정 (밀리초 단위)
      const durationMs = Math.floor((performance.now() - startRecordTime));

      // duration fix 적용!
      const fixedBlob = await fixWebmDuration(blob, durationMs);

      // FormData 구성
      const formData = new FormData();
      formData.append("file", fixedBlob, `vod-${broadcastNo.value}.webm`);
      formData.append("duration", Math.floor(durationMs / 1000).toString());

      try {
        const token = await getValidToken();
        if (!token) {
          alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
          return;
        }

        // 파일 업로드
        await axios.post(`/api/lawyer/vod/upload/${broadcastNo.value}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
          timeout: 10000,
        });

        alert("✅ 녹화 영상 업로드 완료!");
      } catch (err) {
        console.error("❌ 녹화 파일 업로드 실패 :", err);
      }
    }
  }
}

const connectSession = async () => {
  try {
    const saved = localStorage.getItem("currentBroadcast");
    if (saved) {
      const parsed = JSON.parse(saved);
      if (parsed.scheduleNo === Number(scheduleNo)) {
        console.log("🧷 저장된 세션 복구됨:", parsed);
        await reconnectBroadcast(parsed.sessionId);
        broadcastNo.value = parsed.broadcastNo;
        return;
      } else {
        localStorage.removeItem("currentBroadcast");
      }
    }

    const res = await makeApiRequest({
      method: 'post',
      url: '/api/lawyer/broadcast/start',
      data: {
        scheduleNo: Number(scheduleNo)
      }
    });

    const {sessionId, token, broadcastNo: newBroadcastNo, startTime} = res.data;

    console.log("📡 sessionId:", sessionId);
    console.log("🔑 token:", token);
    console.log("🎯 broadcastNo:", newBroadcastNo);
    console.log("🕒 startTime:", startTime);

    broadcastNo.value = newBroadcastNo;

    localStorage.setItem("currentBroadcast", JSON.stringify({
      sessionId,
      scheduleNo,
      broadcastNo: newBroadcastNo,
    }));

    OV.value = new OpenVidu();
    session.value = OV.value.initSession();

    session.value.on("connectionCreated", updateViewerCount);
    session.value.on("connectionDestroyed", updateViewerCount);
    session.value.on("streamCreated", (event) => {
      console.log("📡 방송자: streamCreated 발생 (시청자 연결)");
      updateViewerCount();
    });
    session.value.on("streamDestroyed", (event) => {
      console.log("📴 방송자: streamDestroyed 발생 (시청자 퇴장)");
      updateViewerCount();
    });
    session.value.on("exception", (exception) => {
      console.warn("OpenVidu 예외:", exception);
    });
    session.value.on("sessionDisconnected", (event) => {
      console.warn("세션 연결 종료:", event.reason);
    });

    await session.value.connect(token);
    await initPublisherWithDelay();
    startTimerFrom(startTime);
    updateViewerCount();

    // 종료시간 알림 타이머
    setupBroadcastEndAlert();
  } catch (e) {
    console.error("❌ 방송 연결 오류:", e);
  }
};

const reconnectBroadcast = async (existingSessionId) => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: `/api/lawyer/broadcast/reconnect/${existingSessionId}`
    })

    if (res?.data) {
      const {token, startTime} = res.data

      OV.value = new OpenVidu()
      session.value = OV.value.initSession()

      session.value.on("exception", (exception) => {
        console.warn("OpenVidu 예외:", exception)
      })

      await session.value.connect(token)
      await initPublisherWithDelay()
      startTimerFrom(startTime)
    }
  } catch (err) {
    console.error("❌ 재접속 실패:", err)
    localStorage.removeItem("currentBroadcast")
  }
}

const setupBroadcastEndAlert = () => {
  const { endTime } = broadcastInfo.value;

  console.log("⏰ 종료 시각 endTime:", endTime); // "2025-06-26T11:46:00"

  if (!endTime) {
    console.warn("⛔ 종료 시각 정보가 없습니다.");
    return;
  }

  const endDateTime = new Date(endTime);

  if (isNaN(endDateTime.getTime())) {
    console.warn("⛔ 종료 시각이 유효하지 않습니다:", endTime);
    return;
  }

  const now = new Date();
  const diffMs = endDateTime.getTime() - now.getTime();

  if (diffMs <= 0) {
    console.warn("⏰ 방송 종료 시간이 이미 지났습니다.", { now, endDateTime });
    setTimeout(() => {
      handleAutoEndBroadcast();
    }, 30 * 60 * 1000); // 30분 후
    return;
  }

  console.log(`⏱️ 방송 종료 알림까지 ${Math.floor(diffMs / 1000)}초 남음`);

  // 1단계: 종료 예정 시각 도달 알림
  setTimeout(() => {
    alert("📢 방송 종료 시간이 되었습니다!");

    // 2단계: 종료 시간으로부터 30분 후 자동 종료
    setTimeout(() => {
      handleAutoEndBroadcast();
    }, 30 * 60 * 1000); // 30분 후
  }, diffMs);
};


const handleEndBroadcast = async () => {
  if (!broadcastNo.value) {
    alert("방송 번호가 유효하지 않습니다.")
    return
  }

  const confirmEnd = confirm("정말 방송을 종료하시겠습니까?")
  if (!confirmEnd) return

  try {
    await makeApiRequest({
      method: 'post',
      url: `/api/lawyer/broadcast/end/${broadcastNo.value}`
    })

    alert("✅ 방송이 종료되었습니다.")

    if (mediaRecorder.value && mediaRecorder.value.state !== "inactive") {
      mediaRecorder.value.stop();
    }
    if (session.value) session.value.disconnect()
    if (timerInterval) clearInterval(timerInterval)
    router.push("/lawyer")
  } catch (e) {
    console.error("❌ 방송 종료 실패:", e)
    alert("방송 종료 중 문제가 발생했습니다.")
  }
};

// 자동 방송 종료
const handleAutoEndBroadcast = async () => {
  if (!broadcastNo.value) {
    console.warn("❌ 방송 번호가 유효하지 않습니다.");
    return;
  }

  try {
    await makeApiRequest({
      method: 'post',
      url: `/api/lawyer/broadcast/end/${broadcastNo.value}`
    });

    alert("⏰ 방송 종료 시간이 30분 초과되어 자동으로 방송이 종료되었습니다.");

    if (mediaRecorder.value && mediaRecorder.value.state !== "inactive") {
      mediaRecorder.value.stop();
    }
    if (session.value) session.value.disconnect();
    if (timerInterval) clearInterval(timerInterval);
    router.push("/lawyer");
  } catch (e) {
    console.error("❌ 자동 방송 종료 실패:", e);
    alert("자동 방송 종료 중 문제가 발생했습니다.");
  }
};

const goToLawyerHomepage = () => {
  const userNo = broadcastInfo.value.userNo
  if (!userNo || userNo === 0) {
    alert('변호사 정보가 없습니다.')
    return
  }
  router.push(`/lawyer/${userNo}/homepage`)
}


onMounted(async () => {
  window.addEventListener("beforeunload", preventReload);

  if (!scheduleNo) {
    alert("❌ 유효하지 않은 스케줄 번호입니다.");
    return;
  }

  await loadBroadcastInfo();
  await connectSession();
  connect();
});

onBeforeUnmount(() => {
  window.removeEventListener("beforeunload", preventReload);
  if (timerInterval) clearInterval(timerInterval);
  stompClient.value?.deactivate();
  closeDropdown();
});


// --- 채팅 WebSocket 관련 ---
/** 채팅 */
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
  "#ec8d85", "#603a37", "#246667", "#e4de0d"
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

async function fetchMyNo() {
  const token = await getValidToken();
  if (!token) {
    alert("로그인이 필요합니다!");
    return false;
  }
  const res = await axios.get("/api/Lawyer/my-no", {
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
                  if (data.type === "WARNING") {
                    // 나의 userNo와 일치할 때만 알림
                    if (data.userNo === myNo.value) {
                      alert(data.message || "🚨욕설 또는 부적절한 내용이 포함되어 있습니다!");
                    }
                    return;
                  }

                  if (data.no !== undefined && data.no !== null) {
                    const idx = messages.value.findIndex((m) => m.no === data.no);
                    if (idx !== -1) {
                      // 기존 메시지 내용을 갱신 (메시지, blind 등 모든 필드 교체)
                      messages.value[idx] = { ...messages.value[idx], ...data };
                      return;
                    }
                  }
                  // 그 외(일반 채팅)는 채팅창에 추가
                  messages.value.push(data);
                  scrollToBottom();
                }
            );

        //입장 시 type: "ENTER"만 전달
        stompClient.value.publish({
          destination: "/app/chat.addUser",
          body: JSON.stringify({
            broadcastNo: broadcastNo.value,
            name: broadcastInfo.value.lawyerName
          }),

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
  });
};

// 채팅 메시지 전송 (type: "CHAT"만 전달)
const sendMessage = async () => {
  const trimmed = message.value.trim();
  const token = await getValidToken();
  if (!trimmed || !stompClient.value?.connected) return;
  if (!token) {
    alert("로그인이 필요합니다!");
    return;
  }
  stompClient.value.publish({
    destination: "/app/chat.sendMessage",
    body: JSON.stringify({
      broadcastNo: broadcastNo.value,
      message: trimmed,
      type: "Lawyer",
      userNo: myNo.value,
    }),
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
    const token = await getValidToken();
    await axios.post(
        "/api/Lawyer/chat/report",
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
    const token = await getValidToken();
    const res = await axios.get(`/api/Lawyer/broadcasts/schedule/${broadcastNo.value}`, {
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

          <!-- 👤 변호사 정보 + 종료 버튼 같은 라인 -->
          <div class="d-flex justify-content-between align-items-end mt-4">

            <!-- 프로필 영역 -->
            <div class="d-flex align-items-center">
              <!-- ✅ 초록 원 컨테이너 -->
              <div
                  @click="goToLawyerHomepage"
                  role="button"
                  class="profile-border-hover position-relative d-flex justify-content-center align-items-center"
              >
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

              <!-- 변호사 이름 -->
              <div class="fs-5 fw-bold ms-3">
                <span
                    @click="goToLawyerHomepage"
                    role="button"
                    class="fs-5 fw-bold me-3 text-primary text-decoration-none"
                    style="cursor: pointer;"
                >
                  {{ broadcastInfo.lawyerName }} 변호사
                </span>
              </div>
            </div>

            <!-- 방송 종료 버튼 -->
            <div>
              <button class="btn btn-danger px-4 py-2 fw-bold" @click="handleEndBroadcast">
                📴 방송 종료
              </button>
            </div>
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
          <div style="display: flex; flex-direction: column; align-items: center;">
            <button class="btn btn-link px-1 py-0 text-decoration-none d-flex flex-column align-items-center"
                    style="font-size:1.23rem;"
                    @click="togglePreQDropdown"
                    ref="preQBtnRef"
                    title="사전질문 보기">
              <span>📝</span>
              <span style="font-size:0.7rem; color:#222; margin-top:-5px;">사전 질문</span>
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
                 style="color: #435879; font-size: 0.80rem;">
              {{ msg.message }}
            </div>
            <div v-else-if="msg.type === 'AUTO_REPLY'"
                 class="w-100 text-center"
                 v-html="msg.message.replace(/\n/g, '<br>')"
                 style="background: #ffffff; color: #34559c; border-radius: 12px; font-size: 0.85rem; font-weight: 500; padding: 10px 2px; margin: 6px 0;">
            </div>
            <div v-else-if="msg.type === 'Lawyer'"
                 style="font-size: 0.95rem; display: flex; align-items: center;">
              <!-- 닉네임: 검정색 고정 -->
              <span style="color: #222; user-select: text;">
       👑 {{ broadcastInfo.lawyerName }} 변호사
      </span>
              <!-- 메시지: 빨간색 -->
              <span style="color: #fd1900; margin-left: 0.6em;">
         {{ msg.message }}
      </span>
            </div>
            <div v-else-if="msg.type === 'NOTICE'"
                 class="w-100 text-center"
                 style="color: #7e7e7e; background: #e3eaff; border-radius: 12px; font-size: 0.8rem; font-weight: 600; padding: 9px 2px;">
              <span style="margin-right:6px;"></span>
              {{ msg.message }}
            </div>
            <div v-else style="font-size: 0.97rem; display: flex; align-items: center;">
              <!-- 닉네임 드롭다운 & 랜덤 색상 -->
              <span
                  @click.stop="Number(msg.no) !== Number(myNo) && openDropdown(index, msg)"
                  :style="{
                        color: getNicknameColor(msg.nickname),
                        fontWeight: Number(msg.no) === Number(myNo) ? 700 : 600,
                        cursor: Number(msg.no) === Number(myNo) ? 'default' : 'pointer',
                        userSelect: 'text',
                        position: 'relative',
                        padding: '2px 7px',
                        borderRadius: '7px',
                        transition: 'background 0.14s'
                  }"
                  :class="{'nickname-hoverable': Number(msg.no) !== Number(myNo)}">
                  {{ msg.nickname }}

                <!-- 드롭다운 메뉴 -->
             <span
                 v-if="dropdownIdx === index && Number(msg.no) !== Number(myNo)"
                 class="nickname-dropdown"
                 style="position:absolute;top:120%;left:0;z-index:10000;">
                    <ul class="dropdown-custom-menu">
                      <li class="menu-report" @click.stop="onReportClick">
                        🚨 메시지 신고
                      </li>
                    </ul>
                  </span>
                </span>
              <!-- 메시지 본문 -->
              <span style="color:#222; margin-left:0.7em; line-height:1.6; word-break:break-all;">
              {{ msg.message }}
            </span>
            </div>

          </div>
        </div>
        <!-- 입력창 -->
        <div class="d-flex">
          <input v-model="message"
                 type="text"
                 class="form-control bg-body-secondary text-dark border-0 rounded-pill px-3 py-2"
                 placeholder="채팅을 입력해 주세요."
                 @keyup.enter="sendMessage"
                 maxlength="100"/>
        </div>
      </div>

      <!-- 신고 확인 모달 -->
      <div v-if="isConfirmModal" class="modal-overlay-dark">
        <div class="modal-custom-box shadow">
          <div class="modal-custom-content">
            <div class="modal-custom-msg">
              <div class="modal-custom-text text-center">
                <span class="fs-5 fw-bold text-danger">⚠️ 신고 확인</span><br>
                <span class="d-block mt-2"><strong>{{ selectedUser }}</strong>님의 메시지를 신고하시겠습니까?</span>
                <span class="d-block mt-2 text-muted small">신고된 메시지는 처리를 위해 수집됩니다.</span>
                <div class="reported-message-box mt-3 mb-1">
                  "{{ selectedMessage }}"
                </div>
              </div>
            </div>
            <div class="modal-custom-btns d-flex gap-2 justify-content-center mt-3">
              <button class="modal-btn-cancel" @click="isConfirmModal=false">취소</button>
              <button class="modal-btn-ok" @click="confirmReport">확인</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 신고 완료 모달 -->
      <div v-if="isCompleteModal" class="modal-overlay-dark">
        <div class="modal-custom-box shadow">
          <div class="modal-custom-content">
            <div class="modal-custom-msg">
              <div class="modal-custom-text text-center">
                <span class="fs-5 fw-bold text-success">✔️ 신고 접수 완료</span><br>
                <span class="d-block mt-2">
            메시지 신고가 정상 접수되었습니다.<br>
            가이드 위반 여부 검토 후 조치 예정입니다.<br>
            감사합니다.
          </span>
              </div>
            </div>
            <div class="modal-custom-btns d-flex justify-content-center mt-3">
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

/* 드롭다운 전체 영역 */
.nickname-dropdown {
  min-width: 140px;
  background: #c5c5c5;
  border-radius: 10px;
  box-shadow: 0 4px 18px 0 rgba(40, 55, 100, 0.12);
  padding: 2px 0;
  margin-top: 2px;
  animation: dropdownPop 0.18s cubic-bezier(.4, 1.6, .6, 1);
}

/* 드롭다운 내부 메뉴 */
.dropdown-custom-menu {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu-report {
  font-size: 1.03rem;
  font-weight: 500;
  color: #d73737;
  padding: 7px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.11s, color 0.13s;
  text-align: left;
}

.menu-report:hover {
  background: #a6a4a4;
  color: #b90000;
}

/* 드롭다운 애니메이션 */
@keyframes dropdownPop {
  0% {
    transform: translateY(-8px) scale(0.92);
    opacity: 0;
  }
  100% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}


/* 모달 전체 어두운 오버레이 */
.modal-overlay-dark {
  position: fixed;
  z-index: 11000;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 모달 컨테이너 */
.modal-custom-box {
  background: #fff;
  border-radius: 18px;
  min-width: 340px;
  max-width: 95vw;
  box-shadow: 0 6px 36px 0 rgba(60, 60, 60, 0.16);
  padding: 32px 26px 20px 26px;
  animation: modalPop 0.21s cubic-bezier(.4, 1.6, .6, 1);
}

/* 모달 내부 컨텐츠 */
.modal-custom-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 메시지 텍스트 */
.modal-custom-text {
  font-size: 1.06rem;
  color: #242424;
}

/* 신고 메시지 강조 박스 */
.reported-message-box {
  background: #f7f7f8;
  border: 1px solid #f2b3b3;
  color: #ce2222;
  border-radius: 8px;
  padding: 7px 14px;
  font-size: 0.98rem;
  font-style: italic;
  word-break: break-all;
  margin-top: 0.9em;
  max-width: 280px;
}

/* 버튼 컨테이너 */
.modal-custom-btns {
  width: 100%;
  margin-top: 12px;
}

/* 취소/확인 버튼 공통 */
.modal-btn-cancel, .modal-btn-ok {
  min-width: 90px;
  padding: 8px 0 7px 0;
  border-radius: 7px;
  border: none;
  font-size: 1.07rem;
  font-weight: 500;
  letter-spacing: 0.01em;
  cursor: pointer;
  transition: background 0.13s;
}

/* 취소 버튼 */
.modal-btn-cancel {
  background: #f7f8fa;
  color: #555;
  border: 1px solid #ddd;
}

.modal-btn-cancel:hover {
  background: #e4e8eb;
}

/* 확인 버튼 */
.modal-btn-ok {
  background: #347dff;
  color: #fff;
  border: 1px solid #2d6bd7;
  margin-left: 8px;
}

.modal-btn-ok:hover {
  background: #1955bf;
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
