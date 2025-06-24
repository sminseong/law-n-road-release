<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {
  sendBroadcastStartAlimtalk,
  sendVerificationCodeAlimtalk,
  sendClientReservationStartedAlimtalk,
  sendLawyerReservationStartedAlimtalk,
  sendClientReservationCreatedAlimtalk,
  sendLawyerReservationCreatedAlimtalk,
  sendLawyerReservationCanceledAlimtalk,
  sendBroadcastCreateAlimtalk
} from "@/service/notification.js"
import HttpRequester from '@/libs/HttpRequester'
import { fetchMyQnaBoards } from '@/service/boardService' //추가

// 라우터
const router = useRouter()
function goToProfileEdit() {
  router.push({ name: 'ClientProfileEdit' })
}

// 테스트용 임시 하드 코딩
const userNo = 11
const notifyKeywordEnabled = ref(true)
const notifyConsultEnabled = ref(true)
const nickname = ref('회원')
const requestedCount = ref(0)
const doneCount = ref(0)

const myQnaBoards = ref([]) //추가

onMounted(async () => {
  const storedNickname = localStorage.getItem('nickname')
  if (storedNickname && storedNickname !== 'null') {
    nickname.value = storedNickname
  }

  try {

    const userNo = 11
    const res = await HttpRequester.get(`/api/client/reservations/counts`)
    requestedCount.value = res.data.requestedCount
    doneCount.value = res.data.doneCount
  } catch (e) {
    console.error('예약 건수 조회 실패', e)
  }

  const res = await HttpRequester.get('/api/client/templates/orders/recent') // 최근 주문 5개
  console.log(res.data)
  orders_rows.value = res.data.orders || []

  // QnA 조회 및 정렬 → 상위 3개만 보여줌
  try {
    const res = await fetchMyQnaBoards(userNo)

    const sorted = res.data.sort((a, b) => new Date(b.incidentDate) - new Date(a.incidentDate))
    myQnaBoards.value = sorted.slice(0, 3)
  } catch (e) {
    console.error('QnA 조회 실패', e)
  }

})

// 토글 1
function toggleKeyword() {
  console.log('방송 키워드 알림 수신 여부:', notifyKeywordEnabled.value ? '수신함' : '수신 안 함')
}

// 토글 2
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
    alert(":흰색_확인_표시: 방송 시작 알림톡 발송 완료");
  } catch (e) {
    alert(":x: 방송 시작 알림 실패");
  }
}
async function testBroadcastCreate() {
  try {
    await sendBroadcastCreateAlimtalk({
      to: "01081272572",
      name: "박건희",
      lawyer: "김변",
      title: "음주운전 뺑소니 사고",
      start: "22:00"
    });
    alert(":흰색_확인_표시: 방송 등록 알림톡 발송 완료");
  } catch (e) {
    alert(":x: 방송 등록 알림 실패");
  }
}
async function testVerificationCode() {
  try {
    await sendVerificationCodeAlimtalk({
      to: "01081272572",
      code: "928374"
    });
    alert(":흰색_확인_표시: 인증번호 발송 완료");
  } catch (e) {
    alert(":x: 인증번호 발송 실패");
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
    alert(":흰색_확인_표시: 상담 임박(의뢰인) 발송 완료");
  } catch (e) {
    alert(":x: 상담 임박(의뢰인) 실패");
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
    alert(":흰색_확인_표시: 상담 임박(변호사) 발송 완료");
  } catch (e) {
    alert(":x: 상담 임박(변호사) 실패");
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
    alert(":흰색_확인_표시: 상담 신청 완료(의뢰인) 발송 완료");
  } catch (e) {
    alert(":x: 상담 신청(의뢰인) 실패");
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
    alert(":흰색_확인_표시: 상담 신청 완료(변호사) 발송 완료");
  } catch (e) {
    alert(":x: 상담 신청(변호사) 실패");
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
    alert(":흰색_확인_표시: 상담 취소(변호사) 발송 완료");
  } catch (e) {
    alert(":x: 상담 취소(변호사) 실패");
  }
}



/* --------------------------- */
/*      최근 5개 구매내역      */
/* --------------------------- */

const orders_rows = ref([])

function formatProductLabel(name, count) {
  return count > 1 ? `${name} 외 ${count - 1}건` : name
}

const statusLabel = {
  ORDERED: '결제완료',
  CANCELED: '취소',
  REFUNDED: '환불'
}

function handleRowClick(row) {
  router.push(`/client/template/orders/${row.orderNo}`)
}

</script>

<template>
  <ClientFrame>
    <div class="mypage-home p-4">
      <h3 class="mb-3 text-muted d-flex justify-content-between align-items-center">
        마이페이지 홈
        <button class="btn small btn-outline-primary" @click="goToProfileEdit">정보 수정</button>
      </h3>
      <p class="text-muted">환영합니다! {{ nickname }}님 마이페이지 홈입니다.</p>

      <!-- 상담내역 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">1:1 상담내역</div>
        <div class="card-body p-0">
          <ul class="list-group list-group-flush">
            <li class="list-group-item d-flex justify-content-between align-items-center">
              상담 대기
              <span class="badge bg-warning text-dark">{{ requestedCount }}건</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-center">
              상담 완료
              <span class="badge bg-success text-white">{{ doneCount }}건</span>
            </li>
            <li class="list-group-item text-center bg-white">
              <router-link
                  :to="{ name: 'ClientReservationsList', params: { clientNo: userNo } }"
                  class="text-decoration-none btn small"
              >
                자세히 보기
              </router-link>
            </li>
          </ul>
        </div>
      </div>

      <!-- 템플릿 구매 내역 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">템플릿 구매 내역</div>
        <div class="card-body">

          <table class="table table-hover align-middle">
            <thead class="table">
            <tr>
              <th scope="col">주문번호</th>
              <th scope="col">주문일자</th>
              <th scope="col">주문상품</th>
              <th scope="col">총금액</th>
              <th scope="col">주문상태</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="row in orders_rows"
                :key="row.orderNo"
                style="cursor: pointer"
                @click="handleRowClick(row)"
            >
              <td>{{ row.orderNo }}</td>
              <td>{{ row.orderDate }}</td>
              <td>{{ formatProductLabel(row.firstTemplateName, row.templateCount) }}</td>
              <td>{{ row.amount.toLocaleString() }}원</td>
              <td>
            <span class="badge bg-warning text-dark">
              {{ statusLabel[row.status] || row.status }}
            </span>
              </td>
            </tr>
            <tr v-if="orders_rows.length === 0">
              <td colspan="5" class="text-muted text-center">최근 주문 내역이 없습니다.</td>
            </tr>
            </tbody>
          </table>

          <div class="text-center"><a href="/client/template/orders" class="btn small text-decoration-none">구매 내역 더보기</a>
          </div>
        </div>
      </div>

      <!-- Q&A 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">Q&A 작성한 글 보러가기</div>
        <div class="card-body">
          <table class="table table-hover align-middle">
            <thead class="table">
            <tr>
              <th scope="col">글번호</th>
              <th scope="col">카테고리</th>
              <th scope="col">제목</th>
              <th scope="col">사건발생일</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="row in myQnaBoards"
                :key="row.boardNo"
                style="cursor: pointer"
                @click="$router.push(`/qna/${row.boardNo}`)"
            >
              <td>{{ row.boardNo }}</td>
              <td>{{ row.categoryName }}</td>
              <td>{{ row.title }}</td>
              <td>{{ row.incidentDate }}</td>
            </tr>
            <tr v-if="myQnaBoards.length === 0">
              <td colspan="4" class="text-muted text-center">최근 상담글이 없습니다.</td>
            </tr>
            </tbody>
          </table>

          <div class="text-center"><router-link :to="{ name: 'ClientQaMyList' }" class="btn small text-decoration-none">
            내 상담글 더보기
          </router-link></div>
        </div>
      </div>

      <!-- 키워드 알림 설정 카드 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">키워드 알림 설정 수정하기</div>
        <div class="card-body">
          <p class="mb-2 text-muted small">관심 키워드를 설정하여 관련 알림을 받아보세요.</p>
          <a href="/client/profile" class="btn small">설정 페이지로 이동</a>
        </div>
      </div>

      <!-- 알림 수신 여부 및 테스트 -->
      <div class="card mb-4 border-light">
        <div class="card-header title-bg-primary text-white">알림 수신 여부 체크하기</div>
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <span class="text-muted small">카카오톡 방송 키워드 알림</span>
            <div class="form-check form-switch m-0">
              <input class="form-check-input" type="checkbox" no="keywordSwitch"  v-model="notifyKeywordEnabled" @change="toggleKeyword" />
            </div>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span class="text-muted small">카카오톡 상담 관련 알림</span>
            <div class="form-check form-switch m-0">
              <input class="form-check-input" type="checkbox" no="consultationSwitch" v-model="notifyConsultEnabled" @change="toggleConsultation" />
            </div>
          </div>
          <hr />
          <p><a href="#" @click.prevent="testBroadcastStart">🟡 방송 시작 알림톡 테스트</a></p>
          <p><a href="#" @click.prevent="testBroadcastCreate">🟡 방송 등록 알림톡 테스트</a></p>
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
.mypage-home {
  background-color: #f8f9fa;
  min-height: 100%;
  font-size: 1.1rem;
  line-height: 1.6;
}

.title-bg-primary {
  background-color: #435879;
}

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

.text-muted {
  color: #6c757d !important;
}

.small {
  font-size: 0.85rem;
}

.form-check-input {
  width: 2rem;
  height: 1rem;
}

.btn {
  /* 전역 버튼 스타일 사용 */
}

.badge {
  font-size: 0.9rem;
}
</style>
