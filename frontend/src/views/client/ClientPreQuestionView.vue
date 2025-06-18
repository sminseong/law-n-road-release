<script setup>
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import { onMounted, ref } from "vue";
import axios from "axios";
import { useRoute } from "vue-router";
import { computed } from "vue";

const nickname = ref('');
const inputContent = ref('');
const preQuestion = ref({});
const route = useRoute();
const scheduleNo = route.params.scheduleNo;
const myUserNo = ref(null);


// 방송 정보 + 사전 질문 불러오기 (GET, 토큰 없이)
onMounted(async () => {
  const token = localStorage.getItem('token');
  if (token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    myUserNo.value = payload.no;
  }

  const preQRes = await axios.get(`/api/broadcasts/schedule/${scheduleNo}/preQuestion`);
  const data = preQRes.data;

  // 실제 값 콘솔로 찍어보기
  console.log("preQuestions 실제값:", data.preQuestions);

  if (Array.isArray(data.preQuestions)) {
    data.preQuestions = data.preQuestions.filter(
        q =>
            q && typeof q === 'object' && !Array.isArray(q)
            && Object.keys(q).length > 0
            && q.nickname
            && q.preQuestionContent
    );
  }

  preQuestion.value = data;
});

const alreadyAsked = computed(() => {
  // preQuestions 배열과 내 userNo가 모두 있을 때만 체크
  if (!preQuestion.value.preQuestions || !myUserNo.value) return false;
  // preQuestions 배열에 내 userNo와 일치하는 질문이 하나라도 있으면 true
  return preQuestion.value.preQuestions.some(
      q => q.userNo === myUserNo.value
  );
});

const submitQuestion = async () => {
  if (!inputContent.value.trim()) {
    alert('내용을 입력해주세요!');
    return;
  }
  const token = localStorage.getItem('token');
  if (!token) {
    alert("로그인이 필요합니다!");
    return;
  }
  try {
    await axios.post(
        `/api/broadcasts/schedule/${scheduleNo}/preQuestion`,
        {
          scheduleNo: scheduleNo,
          nickname: nickname.value,
          preQuestionContent: inputContent.value
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    );
    inputContent.value = '';
    alert('질문이 등록되었습니다.');
    // 등록 후 리스트 갱신
    const preQRes = await axios.get(`/api/broadcasts/schedule/${scheduleNo}/preQuestion`);
    preQuestion.value = preQRes.data;
  } catch (e) {
    alert('등록에 실패했습니다.');
  }
};

const deleteQuestion = async (q) => {
  if (!confirm('정말 삭제하시겠습니까?')) return;
  try {
    await axios.delete(
        `/api/broadcasts/schedule/${scheduleNo}/preQuestion/${q.no}`,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        }
    );
    location.reload();


  } catch (e) {
    alert('삭제에 실패했습니다.');
  }
};

function getQuestionStyle(index) {
  const colors = ['bg-success bg-opacity-10', 'bg-warning bg-opacity-10', 'bg-danger bg-opacity-10'];
  return colors[index % colors.length];
}

function getTextColorClass(index) {
  const colors = ['text-success', 'text-warning', 'text-danger'];
  return colors[index % colors.length];
}

</script>


<template>
  <ClientFrame>
    <div class="container my-5 min-vh-80 d-flex">
      <div class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100" style="min-height: 70vh;">
        <div class="row" style="height: 100%;">
          <!-- 왼쪽: 방송 정보 -->
          <div class="col-md-7 d-flex flex-column justify-content-center align-items-center" style="min-height: 70vh;">
            <!-- 상단 이미지: 더 라운드하고, 그림자 강조 -->
            <div class="w-100 d-flex justify-content-center mb-4 mt-3">
              <img :src="preQuestion.thumbnailPath " alt="방송 이미지"
                   style="max-width: 100%; height: 100%; border-radius: 18px;">
            </div>

            <!-- 방송 상세 카드 -->
            <div class="bg-white border rounded-4 shadow p-4 w-100 d-flex flex-row align-items-center gap-4" style="min-height: 230px;">
              <!-- 프로필: 더 크게, 그림자, 엣지 강조 -->
              <div class="d-flex flex-column align-items-center" style="min-width: 130px;">
                <img
                    src="/img/profiles/kim.png"
                    alt="프로필"
                    class="rounded-circle border border-2 shadow-sm"
                    style="width: 108px; height: 108px; object-fit: cover;"
                />
                <span class="mt-3 fw-semibold fs-5 text-primary text-center" style="letter-spacing:0.03em;">
        {{ preQuestion.lawyerName }} 변호사
      </span>
              </div>

              <!-- 방송 정보 텍스트 -->
              <div class="text-start flex-grow-1">
                <div class="fw-bold fs-2 mb-2">{{ preQuestion.name }}</div>
                <div class="text-muted fs-5 mb-2">{{ preQuestion.scheduleContent }}</div>
                <div class="fs-5 mb-1">
                  <i class="bi bi-calendar2-week text-primary me-1"></i>
                  {{ preQuestion.date }}
                  <span v-if="preQuestion.startTime">｜{{ preQuestion.startTime?.slice(11, 16) }} ~ {{ preQuestion.endTime?.slice(11, 16) }}</span>
                </div>
                <div class="mt-2">
        <span class="badge bg-primary-100 text-primary me-1" v-for="(kw, idx) in preQuestion.keywords" :key="idx" style="font-size:1.07em;">
          #{{ kw }}
        </span>
                </div>
              </div>
            </div>
          </div>



          <!-- 오른쪽: 사전 질문 영역 -->
          <div class="col-md-5 d-flex flex-column" style="min-height: 70vh;">
            <div class="mb-3">
              <span class="fs-4 fw-bold text-dark">사전 질문 등록</span>
            </div>
            <!-- 사전 질문 목록 -->
            <div class="flex-grow-1 overflow-auto" style="min-height: 0; max-height: 900px;">
              <!-- 사전질문이 있을 때만 목록 렌더링 -->
              <div
                  v-if="preQuestion.preQuestions && preQuestion.preQuestions.length > 0"
              >
                <div
                    v-for="(q, index) in preQuestion.preQuestions"
                    :key="index"
                    class="rounded-3 p-3 mb-2"
                    :class="getQuestionStyle(index)">
                  <div class="d-flex align-items-center justify-content-between">
                    <span class="fw-bold" :class="getTextColorClass(index)">[{{ q.nickname }}]</span>
                    <button
                        v-if="q.userNo === myUserNo"
                        class="btn btn-link btn-sm text-danger px-2"
                        @click="deleteQuestion(q)"
                        style="text-decoration: underline;">
                      삭제
                    </button>
                  </div>
                  <div>{{ q.preQuestionContent }}</div>
                </div>
              </div>
              <!-- 사전질문이 하나도 없으면 아무것도 표시 X (아래 주석 참고) -->
              <div v-else class="text-muted text-center py-4">
                아직 등록된 사전질문이 없습니다.
              </div>

            </div>


            <!-- 질문 입력창 -->
            <!-- 질문 등록 폼: 이미 등록한 경우엔 아예 안 보이게 -->
            <form
                class="row g-2 align-items-center mt-auto"
                style="margin-bottom: 0;"
                @submit.prevent="submitQuestion"
                v-if="!alreadyAsked"
            >
              <div class="col">
                <input type="text" class="form-control fs-5" placeholder="사전질문을 등록하세요"
                       v-model="inputContent"/>
              </div>
              <div class="col-auto">
                <button type="submit" class="btn btn-primary fs-5 px-4">등록</button>
              </div>
            </form>
            <!-- 이미 등록했으면 안내문만 표시 -->
            <div v-else class="text-success text-center py-2">
              이미 사전질문을 등록하셨습니다.
            </div>
          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>
