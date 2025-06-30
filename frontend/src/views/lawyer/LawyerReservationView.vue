<script setup>
import {ref, onMounted, computed, watch} from 'vue'
import {useRoute} from 'vue-router'
import axios from 'axios'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import {getValidToken} from '@/libs/axios-auth'
import BasicTable from '@/components/table/BasicTable.vue'

const route = useRoute()
const lawyerNo = Number(route.params.lawyerNo)

const loading = ref(true)
const reservations = ref([])
const currentPage = ref(1)
const perPage = 5

// 탭 상태
const selectedTab = ref('tab1')  // 초기 선택된 탭

const totalPages = computed(() =>
    Math.ceil(reservations.value.length / perPage)
)

const paginated = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return reservations.value.slice(start, start + perPage)
})

onMounted(async () => {
  const token = await getValidToken()
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  try {
    const token = localStorage.getItem('token')

    const res = await axios.get(`/api/lawyer/${lawyerNo}/reservations`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
    reservations.value = res.data
    console.log(reservations.value)
  } catch (err) {
    console.error('상담 내역 불러오기 실패', err)
    alert('상담 내역 조회에 실패했습니다.')
  } finally {
    loading.value = false
  }
})

function formatDate(str) {
  return new Date(str + 'T00:00:00').toLocaleDateString('ko', {
    year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'short'
  })
}

function statusLabel(code) {
  if (code === 'REQUESTED') return '대기중'
  if (code === 'DONE') return '완료'
  if (code === 'CANCELED') return '취소'
  return code
}

function statusClass(code) {
  switch (code) {
    case 'REQUESTED':
    case '대기중':
      return 'badge bg-warning text-dark'
    case 'DONE':
    case '완료':
      return 'badge bg-success'
    case 'CANCELED':
    case '취소':
      return 'badge bg-secondary'
    default:
      return 'badge bg-light text-dark'
  }
}

function truncate(text, maxLen) {
  if (!text) return '-'
  return text.length > maxLen
      ? text.slice(0, maxLen) + '…'
      : text
}

async function closeConsultation(reservationNo) {
  const token = await getValidToken()
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  console.log(reservationNo)
  try {
    const token = localStorage.getItem('token')
    await axios.patch(
        `/api/lawyer/${lawyerNo}/reservations/${reservationNo}/status`,
        null,
        {headers: {Authorization: `Bearer ${token}`}}
    )
    const idx = reservations.value.findIndex(r => r.no === reservationNo)
    if (idx !== -1) reservations.value[idx].status = 'DONE'
    alert('상담을 종료 처리했습니다.')
  } catch (err) {
    console.error('상담 종료 처리 실패', err)
    alert('상담 종료 처리에 실패했습니다.')
  }
}

const columns = [
  { key: 'no', label: '예약번호' },
  { key: 'userName', label: '의뢰인' },
  { key: 'slotDate', label: '날짜', format: (val) => formatDate(val) },
  { key: 'slotTime', label: '시간', format: (val) => val.slice(0, 5) },
  { key: 'amount', label: '금액', format: (val) => `${val.toLocaleString()}원` },
  { key: 'status', label: '상태' }, // format 제거
  { key: 'content', label: '상담 내용' }, // format 제거
  { key: 'actions', label: '상담 종료' }  // format 제거
]

// ---------------------
// -     tab2 관련     -
// ---------------------

const weeklySlots = ref([])

const activeClass   = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-green-800 border border-green-800'
const inactiveClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-gray-400 border border-gray-300'
const reservedClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full opacity-50 cursor-not-allowed border border-red-300'

function groupByDate(list) {
  const map = {}
  list.forEach(s => {
    if (!map[s.slotDate]) map[s.slotDate] = []
    map[s.slotDate].push({
      no:             s.no,
      slotTime:       s.slotTime,
      status:         s.status,
      requestedCount: s.requestedCount
    })
  })
  return Object.entries(map)
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([date, slots]) => ({
        date,
        slots: slots.sort((a, b) => a.slotTime.localeCompare(b.slotTime))
      }))
}

async function fetchSlots() {
  const token = await getValidToken()
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }
  loading.value = true
  try {
    const startDate = new Date().toISOString().slice(0, 10)
    const res = await axios.get(
        `/api/lawyer/${lawyerNo}/slots`,
        { params: { startDate } }
    )
    weeklySlots.value = groupByDate(res.data)
  } catch (err) {
    console.error(err)
    alert('주간 예약 정보를 불러오던 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

function getSlotClass(slot) {
  const baseClass = 'slot-button'

  if (slot.status === 1) {
    return `${baseClass} active-slot`
  } else if (slot.requestedCount > 0) {
    return `${baseClass} reserved-slot`
  } else {
    return `${baseClass} inactive-slot`
  }
}

function toggleSlot(dayIdx, slot) {
  if (slot.status === 0 && slot.requestedCount > 0) {
    alert('이미 예약된 슬롯은 활성화할 수 없습니다.')
    return
  }
  slot.status = slot.status === 1 ? 0 : 1
}

async function submitUpdates() {
  const updates = weeklySlots.value.flatMap(day =>
      day.slots.map(s => ({
        no:       s.no,
        slotDate: day.date,
        slotTime: s.slotTime,
        status:   s.status
      }))
  )

  try {
    const token = localStorage.getItem('token')
    await axios.put(
        `/api/lawyer/${lawyerNo}/slots`,
        updates,
        { headers: { Authorization: `Bearer ${token}` } }
    )
    alert('주간 예약 정보가 성공적으로 저장되었습니다.')
    selectedTab.value = 'tab1'
    fetchSlots()
  } catch (err) {
    console.error(err)
    if (err.response?.status === 409) {
      alert(err.response.data?.message || '변경할 수 없는 슬롯이 포함되어 있습니다.')
    } else {
      alert('저장 중 오류가 발생했습니다.')
    }
  }
}

function formatScheduleDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    month: 'long', day: 'numeric', weekday: 'short'
  })
}

// tab 2의 온마운티드 격 존재
watch(selectedTab, (newVal) => {
  if (newVal === 'tab2') fetchSlots()
})

</script>
<template>
  <LawyerFrame>

    <div class="py-0">
      <h2 v-if="selectedTab === 'tab1'">1:1 상담 예약 내역</h2>
      <h2 v-if="selectedTab === 'tab2'">주간 예약 스케줄 설정</h2>
    </div>

    <div class="mb-4 mt-5">
      <div class="btn-tab-wrapper d-flex w-100">
        <button
            class="btn-tab flex-fill"
            :class="{ active: selectedTab === 'tab1' }"
            @click="selectedTab = 'tab1'"
        >
          1:1 상담 예약 내역
        </button>
        <button
            class="btn-tab flex-fill"
            :class="{ active: selectedTab === 'tab2' }"
            @click="selectedTab = 'tab2'"
        >
          주간 예약 스케줄 설정
        </button>
      </div>
    </div>

    <!-- tab1: 예약 내역 -->
    <div class="" v-if="selectedTab === 'tab1'">
      <BasicTable :columns="columns" :fullData="reservations" :pageSize="15">
        <!-- 상태 한글 -->
        <template #cell-status="{ row }">
          <span
              class="badge"
              :class="statusClass(row.status)"
          >
            {{ statusLabel(row.status) }}
          </span>
        </template>

        <!-- 상담 내용 항상 '-' -->
        <template #cell-content>
          -
        </template>

        <!-- 상담 종료 버튼 -->
        <template #cell-actions="{ row }">
          <button
              v-if="row.status === 'REQUESTED'"
              class="btn btn-sm btn-primary"
              @click="closeConsultation(row.no)"
          >
            종료
          </button>
          <p v-else>완료</p>
        </template>
      </BasicTable>
    </div>

    <!-- tab2: 예약 스케줄 설정 -->
    <div v-if="selectedTab === 'tab2'" class="space-y-8 w-full">
      <div v-if="loading" class="text-center py-10">로딩 중…</div>

      <div v-else class="space-y-8">
        <p>변호사님이 가능한 시간대의 예약을 활성화 해주세요</p>
        <!-- 날짜별 카드 -->
        <div
            v-for="(day, dIdx) in weeklySlots"
            :key="day.date"
            class="schedule-card"
        >
          <h3 class="text-lg font-medium mb-2">
            {{ formatScheduleDate(day.date) }}
          </h3>

          <!-- 오전 -->
          <div class="mb-4">
            <div class="text-sm font-medium text-gray-700 mb-1 time-label">
              오전 (08:00 ~ 11:00)
            </div>
            <div class="slots-grid">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) < 12)"
                  :key="slot.no"
                  @click="toggleSlot(dIdx, slot)"
                  :disabled="slot.status === 0 && slot.requestedCount > 0"
                  :class="getSlotClass(slot)"
              >
                {{ slot.slotTime.slice(0,5) }}
              </button>
            </div>
          </div>

          <!-- 오후 -->
          <div>
            <div class="text-sm font-medium text-gray-700 mb-1">
              오후 (12:00 ~ 22:00)
            </div>
            <div class="slots-grid">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) >= 12)"
                  :key="slot.no"
                  @click="toggleSlot(dIdx, slot)"
                  :disabled="slot.status === 0 && slot.requestedCount > 0"
                  :class="getSlotClass(slot)"
              >
                {{ slot.slotTime.slice(0,5) }}
              </button>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-end mt-4 w-100">
          <button
              @click="submitUpdates"
              class="btn btn-primary px-4 py-2 rounded"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style>
.btn-tab-wrapper {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.btn-tab {
  border: none;
  background-color: white;
  color: #666;
  font-weight: 500;
  padding: 0.75rem 1.2rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-tab.active {
  background-color: #445b7c; /* 강조 색상 */
  color: white;
  font-weight: 700;
}

/* 카드 컨테이너 */
.schedule-card {
  background-color: #ffffff;              /* 흰 배경 */
  padding: 1rem;                          /* 안쪽 여백 */
  border-radius: 0.75rem;                 /* 둥근 모서리 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);/* 부드러운 그림자 */
  margin-bottom: 1rem;                    /* 카드 사이 간격 */
}

/* 날짜 제목 */
.schedule-card h3 {
  margin: 0 0 0.75rem;                    /* 아래 여백 */
  font-size: 1.125rem;                    /* 글자 크기 */
  font-weight: 600;                       /* 글자 굵기 */
  color: #2d3748;                         /* 진한 회색 */
}

/* 오전/오후 레이블 */
.schedule-card .time-label {
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #4a5568;
}

/* 슬롯 버튼 그리드 */
.schedule-card .slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
}

/* 기본 슬롯 버튼 */
.schedule-card .slots-grid button {
  display: block;
  width: 100%;
  padding: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  color: #a0aec0;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

/* 호버 애니메이션 */
.schedule-card .slots-grid button:hover:not(:disabled) {
  transform: scale(1.05);
}

/* 상태별 스타일 */
.slot-button.active-slot {
  background-color: #f1f5f9 !important;
  color: #475569 !important;
  border-color: #64748b !important;
}

.slot-button.inactive-slot {
  background-color: #ffffff;
  color: #a0aec0;
  border-color: #e2e8f0;
}

.slot-button.reserved-slot {
  background-color: #fee2e2;
  color: #c53030;
  border-color: #f56565;
  opacity: 0.5;
  cursor: not-allowed;
}

</style>
