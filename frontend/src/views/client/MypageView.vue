<script setup>
import { ref } from 'vue'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {
  sendBroadcastStartAlimtalk,
  sendVerificationCodeAlimtalk,
  sendClientReservationStartedAlimtalk,
  sendLawyerReservationStartedAlimtalk,
  sendClientReservationCreatedAlimtalk,
  sendLawyerReservationCreatedAlimtalk,
  sendLawyerReservationCanceledAlimtalk
} from "@/service/notification.js";

const notifyKeywordEnabled = ref(true)
const notifyConsultEnabled = ref(true)

function toggleKeyword() {
  console.log('방송 키워드 알림 수신 여부:', notifyKeywordEnabled.value ? '수신함' : '수신 안 함')
}
function toggleConsultation() {
  console.log('상담 관련 알림 수신 여부:', notifyConsultEnabled.value ? '수신함' : '수신 안 함')
}

// 각 알림톡 테스트 함수
async function testBroadcastStart() {
  try {
    await sendBroadcastStartAlimtalk({
      to: "01081272572",
      name: "박건희",
      title: "음주운전 뺑소니 사고",
      start: "22:00"
    });
    alert("✅ 방송 시작 알림톡 발송 완료");
  } catch (e) {
    alert("❌ 방송 시작 알림 실패");
  }
}

async function testVerificationCode() {
  try {
    await sendVerificationCodeAlimtalk({
      to: "01081272572",
      code: "928374"
    });
    alert("✅ 인증번호 발송 완료");
  } catch (e) {
    alert("❌ 인증번호 발송 실패");
  }
}

async function testClientReservationStarted() {
  try {
    await sendClientReservationStartedAlimtalk({
      to: "01081272572",
      client: "홍길동",
      lawyer: "박건희",
      datetime: "2025-06-05 15:00"
    });
    alert("✅ 상담 임박(의뢰인) 발송 완료");
  } catch (e) {
    alert("❌ 상담 임박(의뢰인) 실패");
  }
}

async function testLawyerReservationStarted() {
  try {
    await sendLawyerReservationStartedAlimtalk({
      to: "01081272572",
      lawyer: "박건희",
      client: "홍길동",
      datetime: "2025-06-05 15:00",
      summary: "음주운전 관련 문의"
    });
    alert("✅ 상담 임박(변호사) 발송 완료");
  } catch (e) {
    alert("❌ 상담 임박(변호사) 실패");
  }
}

async function testClientReservationCreated() {
  try {
    await sendClientReservationCreatedAlimtalk({
      to: "01081272572",
      client: "홍길동",
      lawyer: "박건희",
      datetime: "2025-06-05 15:00",
      summary: "음주운전 벌금 문의"
    });
    alert("✅ 상담 신청 완료(의뢰인) 발송 완료");
  } catch (e) {
    alert("❌ 상담 신청(의뢰인) 실패");
  }
}

async function testLawyerReservationCreated() {
  try {
    await sendLawyerReservationCreatedAlimtalk({
      to: "01081272572",
      lawyer: "박건희",
      client: "홍길동",
      datetime: "2025-06-05 15:00",
      summary: "음주운전 벌금 문의"
    });
    alert("✅ 상담 신청 완료(변호사) 발송 완료");
  } catch (e) {
    alert("❌ 상담 신청(변호사) 실패");
  }
}

async function testLawyerReservationCanceled() {
  try {
    await sendLawyerReservationCanceledAlimtalk({
      to: "01081272572",
      lawyer: "박건희",
      client: "홍길동",
      datetime: "2025-06-05 15:00"
    });
    alert("✅ 상담 취소(변호사) 발송 완료");
  } catch (e) {
    alert("❌ 상담 취소(변호사) 실패");
  }
}
</script>

<template>
  <ClientFrame>
    <div class="mypage-home p-4">
      <h3 class="mb-3 text-muted">마이페이지 홈</h3>
      <p class="text-muted">환영합니다! 여기는 사용자님의 마이페이지 홈입니다.</p>

      <!-- 1:1 상담내역 카드 -->
      <div class="card mb-4 border-light">
        <!-- .title-bg-primary 클래스를 붙이면 파란 배경이 적용됩니다 -->
        <div class="card-header title-bg-primary text-white">
          1:1 상담내역
        </div>
        <div class="card-body p-0">
          <ul class="list-group list-group-flush">
            <li class="list-group-item d-flex justify-content-between align-items-center">
              상담 대기
              <span class="badge bg-warning text-dark">1건</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-center">
              상담 완료
              <span class="badge bg-success text-white">5건</span>
            </li>
            <li class="list-group-item text-center bg-white">
              <a href="/client/reservation" class="text-decoration-none btn small">자세히 보기</a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 템플릿 구매 내역 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">
          템플릿 구매 내역
        </div>
        <div class="card-body">
          <p class="mb-2 text-muted small">최근 구매한 템플릿이 없습니다.</p>
          <a href="/client/templates" class="btn small">구매 내역 보기</a>
        </div>
      </div>

      <!-- Q&A 작성한 글 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">
          Q&A 작성한 글 보러가기
        </div>
        <div class="card-body">
          <p class="mb-2 text-muted small">작성하신 글이 없습니다.</p>
          <a href="/client/qna" class="btn small">글 보러가기</a>
        </div>
      </div>

      <!-- 키워드 알림 설정 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">
          키워드 알림 설정 수정하기
        </div>
        <div class="card-body">
          <p class="mb-2 text-muted small">관심 키워드를 설정하여 관련 알림을 받아보세요.</p>
          <a href="/client/profile" class="btn small">설정 페이지로 이동</a>
        </div>
      </div>

      <!-- 알림 수신 여부 토글 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">
          알림 수신 여부 체크하기
        </div>
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <span class="text-muted small">카카오톡 방송 키워드 알림</span>
            <div class="form-check form-switch m-0">
              <input
                  class="form-check-input"
                  type="checkbox"
                  no="keywordSwitch"
                  v-model="notifyKeywordEnabled"
                  @change="toggleKeyword"
              />
            </div>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span class="text-muted small">카카오톡 상담 관련 알림</span>
            <div class="form-check form-switch m-0">
              <input
                  class="form-check-input"
                  type="checkbox"
                  no="consultationSwitch"
                  v-model="notifyConsultEnabled"
                  @change="toggleConsultation"
              />
            </div>
          </div>
          <hr />
          <p><a href="#" @click.prevent="testBroadcastStart">🟡 방송 시작 알림톡 테스트</a></p>
          <p><a href="#" @click.prevent="testVerificationCode">🔵 인증번호 발송 테스트</a></p>
          <p><a href="#" @click.prevent="testClientReservationStarted">🟢 상담 임박 (의뢰인)</a></p>
          <p><a href="#" @click.prevent="testLawyerReservationStarted">🟠 상담 임박 (변호사)</a></p>
          <p><a href="#" @click.prevent="testClientReservationCreated">🟤 신규 상담 (의뢰인)</a></p>
          <p><a href="#" @click.prevent="testLawyerReservationCreated">⚪ 신규 상담 (변호사)</a></p>
          <p><a href="#" @click.prevent="testLawyerReservationCanceled">🔴 상담 취소 (변호사)</a></p>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
/* ------------------------------------------------ */
/* 1. 전체 마이페이지 기본 폰트 크기 조정             */
/* ------------------------------------------------ */
.mypage-home {
  background-color: #f8f9fa;
  min-height: 100%;
  /* 페이지 전체의 기본 폰트 크기를 1.1rem로 설정 */
  font-size: 1.1rem;
  line-height: 1.6;
}

/* ------------------------------------------------ */
/* 2. 제목(Heading) 폰트 사이즈                      */
/* ------------------------------------------------ */
.mypage-title {
  font-size: 1.5rem;  /* h3보다 조금 더 키움 */
  font-weight: 600;
}

.card-title-text {
  font-size: 1.25rem; /* 카드 헤더 내부 텍스트 크기 */
  font-weight: 600;
}

/* ------------------------------------------------ */
/* 3. 본문 텍스트, 리스트 항목 폰트 크기               */
/* ------------------------------------------------ */
.mypage-text {
  font-size: 1.1rem;
}

.list-item-text {
  font-size: 1.05rem;
}

/* ------------------------------------------------ */
/* 4. 배지 텍스트 크기                              */
/* ------------------------------------------------ */
.badge-text {
  font-size: 0.9rem;
}

/* 카드 헤더에 일괄로 클래스만 붙이면 색상을 지정할 수 있도록 정의 */
.title-bg-primary {
  background-color: #435879; /* Bootstrap Primary */
}

/* 카드 기본 텍스트/테두리 */
.card {
  background-color: #ffffff;
}
.card-header {
  font-size: 1rem;
  font-weight: 600;
}
.border-light {
  border-color: #e9ecef !important;
}

/* 배지 색상 */
.badge.bg-light {
  background-color: #f1f3f5 !important;
}

/* 링크, 텍스트 색상 */
.text-muted {
  color: #6c757d !important;
}
.small {
  font-size: 0.85rem;
}

/* form-switch 기본 스타일, 토글은 오른쪽에 위치 */
.form-check-input {
  width: 2rem;
  height: 1rem;
}

/* 버튼은 글로벌 CSS에 이미 정의된 기본 스타일 사용 */
.btn {
  /* 여기서는 별도 추가 스타일 없이, 전역에서 설정된 기본 버튼 스타일을 그대로 상속받습니다 */
}
</style>
