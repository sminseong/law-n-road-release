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
    await axios.post("/api/preQuestions/save", selectedNos);
    alert("저장되었습니다.");
    location.reload();
  } catch (e) {
    alert("1개 이상 선택해 주세요");
  }
};

// 데이터 불러오기
onMounted(async () => {
  const scheduleNo = route.params.scheduleNo;
  try {
    const res = await axios.get(`/api/Lawyer/broadcasts/schedule/${scheduleNo}/preQuestion`);

    const data = Array.isArray(res.data) ? res.data : res.data.data;
    preQuestions.value = data.map(q => ({
      ...q,
      checked: false
    }));

    console.log("응답 전체:", res.data);
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
</script>

<template>
  <LawyerFrame>
    <div class="container-fluid my-5 d-flex justify-content-center">
      <div
          class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100"
          style="min-height: 80vh; max-width: 1600px;">
        <div class="row w-100">
          <!-- 왼쪽: 방송 콘텐츠 영역 -->
          <div class="col-md-7 d-flex flex-column justify-content-center align-items-center">
            <div
                class="position-relative d-flex justify-content-center align-items-center"
                style="min-width: 1100px;">
              <!-- 방송 콘텐츠 삽입 자리 -->
            </div>
          </div>

          <!-- 오른쪽: 사전 질문 카드 -->
          <div class="col-md-5">
            <div class="mt-4 border rounded-3 p-3 shadow-sm d-flex flex-column">
              <!-- 제목 -->
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
                <button
                    class="btn btn-primary px-5 py-2"
                    style="min-width: 100px;"
                    @click="saveSelectedQuestions">
                  저장
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
</style>
