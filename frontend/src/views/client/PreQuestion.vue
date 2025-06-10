<script setup>
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
</script>

<template>
  <ClientFrame>
    <div class="container my-5 min-vh-80 d-flex align-items-center" style="min-height: 80vh;">
      <div class="bg-white border border-2 rounded-4 shadow px-5 py-4 w-100">
        <div class="row align-items-start" style="min-height: 70vh;">
          <!-- 왼쪽: 프로필+방송정보 한 박스 안에 가로 배치 -->
          <div class="col-md-7 d-flex flex-column justify-content-center align-items-center" style="min-height: 70vh;">
            <!-- 상단 이미지  -->
            <div class="w-100 d-flex justify-content-center" style="margin-top: 45px; margin-bottom: 48px;">
              <img src="/img/ads/slider-image-1.jpg" alt="방송 상단 이미지" style="max-width: 100%; height: auto; border-radius: 18px;">
            </div>
            <!-- 프로필 + 방송정보  -->
            <div class="bg-light rounded-3 p-4 w-100 d-flex flex-row align-items-center" style="gap: 10px; min-height: 220px;">
              <!-- 왼쪽: 프로필 이미지 -->
              <div class="position-relative d-flex justify-content-center align-items-center" style="min-width: 170px;">
                <img src="/img/profiles/kim.png" alt="프로필" class="rounded-circle border border-2" style="width: 96px; height: 96px;" />
              </div>
              <!-- 오른쪽: 방송 정보 텍스트 -->
              <div class="text-start flex-grow-1">
                <div class="fw-bold fs-5 mb-1">5월 22일</div>
                <div class="text-muted mb-1">[오후 6시 ~ 오후 7시]</div>
                <div class="fw-semibold mb-1 fs-2" >통합 교통법규 Q&amp;A 라이브</div>
                <div class="text-secondary">- 김수영 변호사 -</div>
              </div>
            </div>
          </div>

          <!-- 오른쪽: 사전 질문 등록 -->
          <div class="col-md-5 d-flex flex-column justify-content-between" style="min-height: 70vh;">
            <div>
              <div class="mb-3">
                <span class="fs-4 fw-bold text-dark">사전 질문 등록</span>
              </div>
              <!-- 질문 목록 -->
              <div class="mb-3">
                <div class="bg-success bg-opacity-10 rounded-3 p-3 mb-2">
                  <div class="fw-bold text-success">[닉네임1]</div>
                  <div>음주운전으로 가드레일을 받았는데, 자차 보험으로 해결되나요?</div>
                </div>
                <div class="bg-warning bg-opacity-10 rounded-3 p-3 mb-2">
                  <div class="fw-bold text-warning">[닉네임2]</div>
                  <div>상대방이 신호위반으로 저를 부딪쳤는데, 과실 비율이 100:0 아닌가요?</div>
                </div>
                <div class="bg-danger bg-opacity-10 rounded-3 p-3">
                  <div class="fw-bold text-danger">[닉네임3]</div>
                  <div>음주운전 첫 적발인데 벌금 말고 다른 처벌도 있나요?</div>
                </div>
              </div>
            </div>
            <!-- 입력 영역 -->
            <form class="row g-2 align-items-center mt-2">
              <div class="col">
                <input type="text" class="form-control fs-5" placeholder="사전질문을 등록하세요"/>
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
