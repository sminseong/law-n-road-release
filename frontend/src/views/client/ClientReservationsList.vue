<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-3xl font-bold mb-6">내 상담 예약 내역</h2>

      <div v-if="loading" class="text-center py-10 text-lg">
        로딩 중…
      </div>

      <div v-else>
        <BasicTable :columns="columns" :fullData="reservations" :pageSize="15">
          <!-- 상태 컬럼 -->
          <template #cell-status="{ row }">
            <span
                :class="row.status === 'REQUESTED'
                ? 'text-yellow-600'
                : 'text-green-600'"
            >
              {{ statusLabel(row.status) }}
            </span>
          </template>

          <!-- 상담 상세 버튼 -->
          <template #cell-actions="{ row }">
            <button
                class="px-2 py-1 btn btn-primary"
                @click="viewDetails(row)"
            >
              상세보기
            </button>
          </template>
        </BasicTable>
      </div>
    </div>

    <!-- 상세 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-container">
        <button class="modal-close-btn" @click="closeModal">✕</button>

        <h3 class="modal-title">
          예약 상세 (#{{ detail.no }})
        </h3>

        <ul class="modal-info-list">
          <li><strong>날짜:</strong> {{ formatDate(detail.slotDate) }}</li>
          <li><strong>시간:</strong> {{ detail.slotTime.slice(0,5) }}</li>
          <li><strong>금액:</strong> {{ detail.amount.toLocaleString() }}원</li>
          <li><strong>상태:</strong> {{ statusLabel(detail.status) }}</li>
          <li><strong>상담 내용:</strong> {{ detail.content || '-' }}</li>
        </ul>

        <div class="modal-footer">
          <!-- 취소 가능 여부에 따라 버튼 비활성화 -->
          <button
              v-if="detail.status === 'REQUESTED'"
              :disabled="!canCancel(detail)"
              @click="cancelReservation(detail.no)"
              class="btn btn-cancel-reservation me-2"
              :class="canCancel(detail) ? 'btn btn-primary' : ''"
          >
            예약 취소
          </button>
          <button class="btn btn-danger" @click="closeModal">
            닫기
          </button>
        </div>
      </div>
    </div>

<!--    &lt;!&ndash; 상세 모달 &ndash;&gt;-->
<!--    <div v-if="showModal" class="modal-overlay"-->
<!--    >-->
<!--      <div-->
<!--          class="bg-white w-96 max-w-full p-6 relative rounded-lg border border-gray-300 shadow-lg"-->
<!--      >-->
<!--        <button-->
<!--            class="absolute top-2 right-2 text-gray-500 hover:text-gray-700"-->
<!--            @click="closeModal"-->
<!--        >-->
<!--          ✕-->
<!--        </button>-->
<!--        <h3 class="text-xl font-semibold mb-4">-->
<!--          예약 상세 (#{{ detail.no }})-->
<!--        </h3>-->
<!--        <ul class="space-y-2 text-gray-700">-->
<!--          <li><strong>날짜:</strong> {{ formatDate(detail.slotDate) }}</li>-->
<!--          <li><strong>시간:</strong> {{ detail.slotTime.slice(0,5) }}</li>-->
<!--          <li><strong>금액:</strong> {{ detail.amount.toLocaleString() }}원</li>-->
<!--          <li><strong>상태:</strong> {{ statusLabel(detail.status) }}</li>-->
<!--          <li><strong>상담 내용:</strong> {{ detail.content || '-' }}</li>-->
<!--        </ul>-->
<!--        <div class="mt-6 flex justify-end space-x-2">-->
<!--          &lt;!&ndash; 취소 가능 여부에 따라 버튼 비활성화 &ndash;&gt;-->
<!--          <button-->
<!--              v-if="detail.status === 'REQUESTED'"-->
<!--              :disabled="!canCancel(detail)"-->
<!--              @click="cancelReservation(detail.no)"-->
<!--              class="px-4 py-2 rounded text-white disabled:opacity-50 disabled:cursor-not-allowed"-->
<!--              :class="canCancel(detail)-->
<!--                ? 'bg-red-600 hover:bg-red-700'-->
<!--                : 'bg-gray-400'"-->
<!--          >-->
<!--            예약 취소-->
<!--          </button>-->
<!--          <button-->
<!--              class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"-->
<!--              @click="closeModal"-->
<!--          >-->
<!--            닫기-->
<!--          </button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
  </ClientFrame>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import BasicTable from "@/components/table/BasicTable.vue";

const loading      = ref(true)
const reservations = ref([])
const currentPage  = ref(1)
const perPage      = 5

const showModal = ref(false)
const detail    = ref({})

// BasicTable에 사용할 컬럼 정의
const columns = [
  { key: 'no', label: '예약번호' },
  { key: 'slotDate', label: '날짜', format: (val) => formatDate(val) },
  { key: 'slotTime', label: '시간', format: (val) => val.slice(0, 5) },
  { key: 'amount', label: '금액', format: (val) => `${val.toLocaleString()}원` },
  { key: 'status', label: '상태' },
  { key: 'actions', label: '상담 상세' }
]

async function fetchReservations() {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const { data } = await axios.get(
        '/api/client/reservations/list',
        { headers: { Authorization: `Bearer ${token}` } }
    )
    reservations.value = data
  } catch {
    alert('예약 내역 조회에 실패했습니다.')
  } finally {
    loading.value = false
  }
}
onMounted(fetchReservations)

function viewDetails(r) {
  detail.value = { ...r }
  showModal.value = true
}
function closeModal() {
  showModal.value = false
}

async function cancelReservation(reservationNo) {
  if (!canCancel(detail.value)) {
    return alert('취소 가능 시간이 지났습니다. 예약은 시작 1시간 전까지만 가능합니다.')
  }
  if (!confirm('정말 이 예약을 취소하시겠습니까?')) return

  try {
    const token = localStorage.getItem('token')
    await axios.post(
        '/api/confirm/cancel',
        { reservationNo },
        { headers: { Authorization: `Bearer ${token}` } }
    )
    alert('예약이 취소되었습니다.')
    closeModal()
    await fetchReservations()
  } catch {
    alert('예약 취소에 실패했습니다.')
  }
}

const totalPages = computed(() =>
    Math.ceil(reservations.value.length / perPage)
)
const paginated = computed(() =>
    reservations.value.slice(
        (currentPage.value - 1) * perPage,
        currentPage.value * perPage
    )
)

function formatDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'short'
  })
}
function statusLabel(code) {
  if (code === 'REQUESTED') return '대기중'
  if (code === 'DONE')      return '완료'
  if (code === 'CANCELED')  return '취소'
  return code
}

// 예약 취소 가능 여부 체크: 예약시각 기준 1시간 전까지 가능
function canCancel(res) {
  const now = new Date()
  const [yyyy, mm, dd] = res.slotDate.split('-').map(Number)
  const [hh, min] = res.slotTime.slice(0,5).split(':').map(Number)
  const slotDateTime    = new Date(yyyy, mm - 1, dd, hh, min)
  const cancelDeadline  = new Date(slotDateTime.getTime() - 60 * 60 * 1000)
  return now < cancelDeadline
}
</script>

<style scoped>
table {
  border-collapse: collapse;
}
th, td {
  font-size: 1rem;
}

.modal-overlay {
  position: fixed;
  inset: 0;                          /* top:0; right:0; bottom:0; left:0; */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0,0,0,0.5);
  z-index: 10000;
}

.modal-container {
  background: #fff;
  padding: 1.5rem;
  border-radius: 0.5rem;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.modal-close-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
}
</style>
