<script setup>
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";
import { onMounted, ref, computed } from "vue";
import axios from "axios";
import {useRoute, useRouter} from "vue-router";

const route = useRoute();
const router = useRouter();
const scheduleNo = route.params.scheduleNo;

const scheduleDetail = ref(null);
const isScheduleLoading = ref(true);
const isScheduleError = ref(false);

// 🔽 스케줄 상세 정보 불러오기
const loadScheduleDetail = async () => {
  try {
    const token = localStorage.getItem('token');
    const { data } = await axios.get(`/api/schedule/my/${scheduleNo}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    scheduleDetail.value = data;
    isScheduleLoading.value = false;
  } catch (e) {
    console.error("스케줄 정보 불러오기 실패:", e);
    isScheduleError.value = true;
    isScheduleLoading.value = false;
  }
};

// ⏰ 시간 포맷 도우미
const formatTime = (datetime) => {
  if (!datetime) return '';
  const d = new Date(datetime);
  const hh = d.getHours().toString().padStart(2, '0');
  const mm = d.getMinutes().toString().padStart(2, '0');
  return `${hh}:${mm}`;
};

const goBackToSchedule = () => {
  router.push('/lawyer/broadcasts/schedule');
};

const confirmStartLive = () => {
  if (confirm('라이브 방송을 시작하시겠습니까?')) {
    router.push({ path: '/lawyer/broadcasts/live', query: { scheduleNo } });
  }
};





onMounted(() => {
  loadAllData();
});


// 모든 초기 데이터 로딩
const loadAllData = async () => {
  await loadScheduleDetail();
  await loadPreQuestions();
};







/** 사전질문, 자동응답 */
const preQuestions = ref([]);

// 색상 리스트
const bgColors = ['bg-success bg-opacity-10', 'bg-warning bg-opacity-10', 'bg-danger bg-opacity-10'];
const textColors = ['text-success', 'text-warning', 'text-danger'];

// 전체 선택 체크박스
const allChecked = computed({
  get: () => preQuestions.value.length > 0 && preQuestions.value.every(q => q.checked),
  set: v => preQuestions.value.forEach(q => (q.checked = v)),
});

// 저장 함수 (선택된 no만 서버로 전송)
const saveSelectedQuestions = async () => {
  const selectedNos = preQuestions.value
      .filter(q => q.checked)
      .map(q => q.no);

  try {
    const token = localStorage.getItem('token');
    await axios.post("/api/preQuestions/save", selectedNos, {
      headers: { Authorization: `Bearer ${token}` }
    });
    alert("저장되었습니다.");
    location.reload();
  } catch (e) {
    alert("1개 이상 선택해 주세요");
  }
};

// 사전 질문 데이터 불러오기
const loadPreQuestions = async () => {
  try {
    const token = localStorage.getItem('token');
    const res = await axios.get(`/api/Lawyer/broadcasts/schedule/${scheduleNo}/preQuestion`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = Array.isArray(res.data) ? res.data : res.data.data;
    preQuestions.value = data.map(q => ({
      ...q,
      checked: false
    }));
    await loadNightbotMessages();
  } catch (e) {
    console.error("사전 질문 불러오기 실패:", e);
  }
};

// 색상 클래스 함수
function getQuestionStyle(index) {
  return bgColors[index % bgColors.length];
}
function getTextColorClass(index) {
  return textColors[index % textColors.length];
}

// 🔽 나이트봇 관련 코드
const nightbotMessages = ref([]);
const newKeyword = ref('');
const newMessage = ref('');

// 자동응답 목록 불러오기
const loadNightbotMessages = async () => {
  try {
    const token = localStorage.getItem('token');
    // scheduleNo를 반드시 쿼리파라미터로 포함!
    const scheduleNo = route.params.scheduleNo;
    const res = await axios.get("/api/Lawyer/nightBot", {
      params: { scheduleNo },
      headers: { Authorization: `Bearer ${token}` }
    });
    nightbotMessages.value = res.data;
  } catch (e) {
    console.error("나이트봇 메시지 불러오기 실패:", e);
  }
};

// 자동응답 추가
const addNightbotMessage = async () => {
  if (!newKeyword.value || !newMessage.value) {
    alert("트리거와 메시지를 모두 입력해주세요.");
    return;
  }
  try {
    const token = localStorage.getItem('token');
    const scheduleNo = route.params.scheduleNo;
    await axios.post("/api/Lawyer/nightBot", {
      scheduleNo, // 반드시 포함!
      keyword: newKeyword.value,
      message: newMessage.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    newKeyword.value = '';
    newMessage.value = '';
    await loadNightbotMessages();
    alert("등록되었습니다.");
  } catch (e) {
    alert("등록 실패");
  }
};

// 자동응답 삭제
const deleteNightbotMessage = async (no) => {
  try {
    const token = localStorage.getItem('token');
    await axios.delete(`/api/nightBot/${no}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    await loadNightbotMessages();
    alert("삭제 되었습니다.");

  } catch (e) {
    alert("삭제 실패");
  }
};


function goToLawyerLive() {
  window.location.href = 'http://localhost:5173/lawyer/broadcasts/live?scheduleNo=1';
}
</script>


<template>
  <LawyerFrame>
    <div class="container-fluid my-5 d-flex justify-content-center">
      <div class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100" style="min-height: 120vh; max-width: 1600px;">
        <div class="row w-100 align-items-start">
          <!-- 왼쪽: 방송 콘텐츠 영역 (수정 불가 보기 전용) -->
          <div class="col-md-7 d-flex flex-column justify-content-start align-items-start pe-5">
            <div class="w-100 border rounded-3 p-4 shadow-sm mb-4" v-if="scheduleDetail">
              <div class="mb-4">
                <span class="fs-4 fw-bold text-dark">방송 설정 확인</span>
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">방송 제목</label>
                <input :value="scheduleDetail.name" type="text" class="form-control bg-light text-dark" readonly />
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">카테고리</label>
                <input :value="scheduleDetail.categoryName" type="text" class="form-control bg-light text-dark" readonly />
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">방송 시간</label>
                <div class="row row-cols-2 g-2">
                  <div>
                    <div class="form-label text-muted small">시작 시간</div>
                    <input :value="`${scheduleDetail.date} ${formatTime(scheduleDetail.startTime)}`" type="text" class="form-control bg-light text-dark" readonly />
                  </div>
                  <div>
                    <div class="form-label text-muted small">종료 시간</div>
                    <input :value="`${scheduleDetail.date} ${formatTime(scheduleDetail.endTime)}`" type="text" class="form-control bg-light text-dark" readonly />
                  </div>
                </div>
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">썸네일 이미지</label>
                <div class="preview-box mb-2 border rounded d-flex justify-content-center align-items-center" style="height: 240px;">
                  <img v-if="scheduleDetail.thumbnailPath" :src="scheduleDetail.thumbnailPath" alt="썸네일" class="img-fluid h-100" style="object-fit: contain;" />
                  <span v-else class="text-muted">이미지 없음</span>
                </div>
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">방송 설명</label>
                <textarea :value="scheduleDetail.content" class="form-control bg-light text-dark" rows="4" readonly></textarea>
              </div>
              <div class="mb-3 w-100">
                <label class="form-label fw-bold">방송 키워드</label>
                <div class="d-flex flex-wrap gap-2">
                  <span v-for="(kw, i) in scheduleDetail.keywords" :key="i" class="badge bg-secondary px-3 py-2"># {{ kw }}</span>
                </div>
              </div>
            </div>
            <div v-else class="w-100 text-center py-5">
              <div v-if="isScheduleLoading" class="text-muted">⏳ 방송 정보를 불러오는 중입니다...</div>
              <div v-else-if="isScheduleError" class="text-danger">❌ 방송 정보를 불러오지 못했습니다.</div>
            </div>
          </div>

          <!-- 오른쪽: 사전 질문 + 나이트봇 -->
          <div class="col-md-5 d-flex flex-column justify-content-between">
            <div>
              <div class="border rounded-3 p-3 shadow-sm mb-4">
                <div class="mb-3">
                  <span class="fs-4 fw-bold text-dark">사전 질문 선택</span>
                </div>
                <label class="d-flex align-items-center mb-2 ms-2">
                  <input type="checkbox" class="form-check-input me-2" v-model="allChecked" />
                  <span>전체 선택</span>
                </label>
                <div class="overflow-auto mb-3" style="max-height: 300px; min-height: 300px;">
                  <div v-for="(q, index) in preQuestions" :key="q.no" class="rounded-3 p-3 mb-2 d-flex" :class="getQuestionStyle(index)">
                    <input type="checkbox" v-model="q.checked" class="form-check-input me-3 mt-1" />
                    <div>
                      <div :class="['fw-bold', getTextColorClass(index)]">[{{ q.nickname }}]</div>
                      <div>{{ q.content }}</div>
                    </div>
                  </div>
                </div>
                <div class="text-center">
                  <button class="btn btn-primary px-5 py-2" style="min-width: 100px;" @click="saveSelectedQuestions">
                    저장
                  </button>
                </div>
              </div>

              <div class="border rounded-3 p-3 shadow-sm">
                <div class="mb-3">
                  <span class="fs-4 fw-bold text-dark">나이트봇 자동응답 설정</span>
                </div>
                <div class="position-relative mb-2">
                  <input v-model="newKeyword" type="text" class="form-control mb-2" placeholder="ex) !상담" />
                  <textarea v-model="newMessage" class="form-control mb-2" rows="2" placeholder="내용"></textarea>
                  <button class="btn btn-primary position-absolute" style="top:0; right:0; height:38px; z-index:2" @click="addNightbotMessage">
                    등록
                  </button>
                </div>
                <li v-for="msg in nightbotMessages" :key="msg.no" class="list-group-item d-flex align-items-center border-0 px-0 py-2">
                  <span class="fw-bold me-1">{{ msg.keyword }}</span>
                  <span class="fw-bold me-1">:</span>
                  <span class="text-muted small flex-grow-1 text-truncate">
                    {{ msg.message.length > 28 ? msg.message.slice(0, 28) + " ..." : msg.message }}
                  </span>
                  <button class="btn btn-sm btn-danger ms-2" @click="deleteNightbotMessage(msg.no)">삭제</button>
                </li>
              </div>
            </div>
          </div>

          <!-- 하단 버튼 영역: 목록/방송시작 같이 위치 -->
          <div class="d-flex justify-content-between align-items-center mt-5 px-4 w-100">
            <button class="btn btn-outline-secondary" @click="goBackToSchedule">← 목록으로 돌아가기</button>
            <button class="btn btn-primary px-4" @click="confirmStartLive">라이브 방송 시작하기</button>
          </div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>
