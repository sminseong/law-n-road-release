<script>
import { defineComponent} from "vue";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";


export default defineComponent({
  components: {ClientFrame},

});
</script>
<template>
  <ClientFrame>
    <div class="container my-5 min-vh-80 d-flex">
      <div class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100" style="min-height: 70vh;">
        <div class="row" style="height: 100%;">
          <!-- 왼쪽: 방송 정보 -->
          <div class="col-md-7 d-flex flex-column justify-content-center align-items-center" style="min-height: 70vh;">
            <!-- 상단 이미지 -->
            <div class="w-100 d-flex justify-content-center" style="margin-top: 45px; margin-bottom: 48px;">
              <img :src="preQuestion.thumbnailPath || '/img/ads/slider-image-1.jpg'" alt="방송 이미지"
                   style="max-width: 100%; height: auto; border-radius: 18px;">
            </div>

            <!-- 방송 상세 정보 -->
            <div class="bg-light rounded-3 p-4 w-100 d-flex flex-row align-items-center" style="gap: 10px; min-height: 220px;">
              <!-- 프로필 이미지 -->
              <div class="position-relative d-flex justify-content-center align-items-center" style="min-width: 170px;">
                <img src="/img/profiles/kim.png" alt="프로필" class="rounded-circle border border-2"
                     style="width: 96px; height: 96px;" />
              </div>

              <!-- 방송 정보 텍스트 -->
              <div class="text-start flex-grow-1">
                <div class="fw-semibold mb-1 fs-2">{{ preQuestion.name }}</div>
                <div class="fw-bold fs-5 mb-1">{{ preQuestion.scheduleContent }}</div>
                <div class="fw-bold fs-5 mb-1">
                  {{ preQuestion.date }}　　
                  {{ preQuestion.startTime?.slice(11, 16) }} ~ {{ preQuestion.endTime?.slice(11, 16) }}
                </div>
                <div class="text-secondary mb-2">- {{ preQuestion.lawyerName }} 변호사 -</div>
                <div class="mt-2">
                  <span class="badge bg-primary me-1" v-for="(kw, idx) in preQuestion.keywords" :key="idx">
                    # {{ kw }}
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
            <div class="flex-grow-1 overflow-auto" style="min-height: 0;">
              <div
                  v-for="(q, index) in preQuestion.preQuestions"
                  :key="index"
                  class="rounded-3 p-3 mb-2"
                  :class="getQuestionStyle(index)">
                <div class="fw-bold" :class="getTextColorClass(index)">[{{ q.nickname }}]</div>
                <div>{{ q.preQuestionContent }}</div>
              </div>
            </div>

            <!-- 질문 입력창 -->
            <form class="row g-2 align-items-center mt-auto" style="margin-bottom: 0;">
              <div class="col">
                <input type="text" class="form-control fs-5" placeholder="사전질문을 등록하세요" />
              </div>
              <div class="col-auto">
                <button type="button" class="btn btn-dark fs-5 px-4">등록</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>

</style>