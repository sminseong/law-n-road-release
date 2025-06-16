<script setup>
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";
import { onMounted, ref, computed } from "vue";
import axios from "axios";
import { useRoute } from "vue-router";

const route = useRoute();
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
onMounted(async () => {
  const scheduleNo = route.params.scheduleNo;
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
});

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
    console.error("등록 실패:", e);
    alert("등록 실패");
  }
};

// 자동응답 삭제
const deleteNightbotMessage = async (id) => {
  try {
    const token = localStorage.getItem('token');
    await axios.delete(`/api/nightBot/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    await loadNightbotMessages();
  } catch (e) {
    alert("삭제 실패");
  }
};
</script>


<template>
  <LawyerFrame>
    <div class="container-fluid my-5 d-flex justify-content-center">
      <div class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100" style="min-height: 80vh; max-width: 1600px;">
        <div class="row w-100">
          <!-- 왼쪽: 방송 콘텐츠 영역 -->
          <div class="col-md-7 d-flex flex-column justify-content-center align-items-center">
            <div class="position-relative d-flex justify-content-center align-items-center" style="min-width: 1100px;">
              <!-- 방송 콘텐츠 삽입 자리 -->
            </div>
          </div>

          <!-- 오른쪽: 사전 질문 + 나이트봇 -->
          <div class="col-md-5">
            <!-- ✅ 사전질문 -->
            <div class="mt-4 border rounded-3 p-3 shadow-sm d-flex flex-column">
              <div class="mb-3">
                <span class="fs-4 fw-bold text-dark">사전 질문 선택</span>
              </div>
              <!-- 전체 선택 -->
              <label class="d-flex align-items-center mb-2 ms-2">
                <input type="checkbox" class="form-check-input me-2" v-model="allChecked" />
                <span>전체 선택</span>
              </label>
              <!-- 사전 질문 목록 -->
              <div class="overflow-auto mb-3" style="max-height: 300px; min-height: 300px;">
                <div
                    v-for="(q, index) in preQuestions"
                    :key="q.no"
                    class="rounded-3 p-3 mb-2 d-flex"
                    :class="getQuestionStyle(index)">
                  <input type="checkbox" v-model="q.checked" class="form-check-input me-3 mt-1" />
                  <div>
                    <div :class="['fw-bold', getTextColorClass(index)]">[{{ q.nickname }}]</div>
                    <div>{{ q.content }}</div>
                  </div>
                </div>
              </div>
              <!-- 저장 버튼 -->
              <div class="text-center">
                <button class="btn btn-primary px-5 py-2" style="min-width: 100px;" @click="saveSelectedQuestions">
                  저장
                </button>
              </div>
            </div>

            <!-- ✅ 나이트봇 - 구조 및 배치 변경 -->
            <div class="mt-5 border rounded-3 p-3 shadow-sm">
              <div class="mb-3">
                <span class="fs-4 fw-bold text-dark">나이트봇 자동응답 설정</span>
              </div>
              <!-- 입력 영역 -->
              <div class="position-relative mb-2">
                <!-- 트리거 입력 -->
                <input v-model="newKeyword"
                       type="text"
                       class="form-control mb-2"
                       placeholder="ex) !상담" />
                <!-- 내용 입력 -->
                <textarea v-model="newMessage"
                          class="form-control mb-2"
                          rows="2"
                          placeholder="내용"></textarea>
                <!-- 등록 버튼 (오른쪽 위) -->
                <button class="btn btn-success position-absolute"
                        style="top:0; right:0; height:38px; z-index:2"
                        @click="addNightbotMessage">
                  등록
                </button>
              </div>
              <!-- 목록 -->
              <li v-for="msg in nightbotMessages" :key="msg.id"
                  class="list-group-item d-flex align-items-center border-0 px-0 py-2">
                <span class="fw-bold me-1">{{ msg.keyword }}</span>
                <span class="fw-bold me-1">:</span>
                <span class="text-muted small flex-grow-1 text-truncate">
                {{ msg.message.length > 28 ? msg.message.slice(0, 28) + " ..." : msg.message }}
              </span>
                <button class="btn btn-sm btn-danger ms-2"
                        @click="deleteNightbotMessage(msg.id)">
                  삭제
                </button>
              </li>


            </div>
          </div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.nightbot-list {
  max-height: 140px;
  overflow-y: auto;
}
</style>
