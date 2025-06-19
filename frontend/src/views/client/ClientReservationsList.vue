<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-3xl font-bold mb-6">내 상담 예약 내역</h2>

      <div v-if="loading" class="text-center py-10 text-lg">
        로딩 중…
      </div>
      <div v-else>
        <div class="overflow-x-auto">
          <table class="w-full table-fixed border border-gray-300">
            <thead class="bg-gray-100">
            <tr>
              <th class="px-6 py-3 text-left font-medium">예약번호</th>
              <th class="px-6 py-3 text-left font-medium">날짜</th>
              <th class="px-6 py-3 text-left font-medium">시간</th>
              <th class="px-6 py-3 text-left font-medium">금액</th>
              <th class="px-6 py-3 text-left font-medium">상태</th>
              <th class="px-6 py-3 text-left font-medium w-[12ch]">상담 상세</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="res in paginated"
                :key="res.no"
                class="border-t hover:bg-gray-50"
            >
              <td class="px-6 py-4">{{ res.no }}</td>
              <td class="px-6 py-4">{{ formatDate(res.slotDate) }}</td>
              <td class="px-6 py-4">{{ res.slotTime.slice(0,5) }}</td>
              <td class="px-6 py-4">{{ res.amount.toLocaleString() }}원</td>
              <td class="px-6 py-4">
                  <span
                      :class="res.status === 'REQUESTED'
                      ? 'text-yellow-600'
                      : 'text-green-600'"
                  >
                    {{ statusLabel(res.status) }}
                  </span>
              </td>
              <td class="px-6 py-4">
                <button
                    class="px-2 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
                    @click="viewDetails(res)"
                >
                  상세보기
                </button>
              </td>
            </tr>
            <tr v-if="!reservations.length">
              <td colspan="6" class="px-6 py-4 text-center text-gray-500">
                예약 내역이 없습니다.
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div
            v-if="totalPages > 1"
            class="mt-4 flex justify-center space-x-2"
        >
          <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
              'px-3 py-1 rounded',
              page === currentPage
                ? 'bg-blue-600 text-white'
                : 'bg-gray-200 hover:bg-gray-300'
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- 상세 모달 -->
    <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
    >
      <div
          class="bg-white w-96 max-w-full p-6 relative rounded-lg border border-gray-300 shadow-lg"
      >
        <button
            class="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
            @click="closeModal"
        >
          ✕
        </button>
        <h3 class="text-xl font-semibold mb-4">
          예약 상세 (#{{ detail.no }})
        </h3>
        <ul class="space-y-2 text-gray-700">
          <li><strong>날짜:</strong> {{ formatDate(detail.slotDate) }}</li>
          <li><strong>시간:</strong> {{ detail.slotTime.slice(0,5) }}</li>
          <li><strong>금액:</strong> {{ detail.amount.toLocaleString() }}원</li>
          <li><strong>상태:</strong> {{ statusLabel(detail.status) }}</li>
          <li><strong>상담 내용:</strong> {{ detail.content || '-' }}</li>
        </ul>
        <div class="mt-6 flex justify-end space-x-2">
          <button
              v-if="detail.status === 'REQUESTED'"
              class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
              @click="cancelReservation(detail.no)"
          >
            예약 취소
          </button>
          <button
              class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
              @click="closeModal"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const loading      = ref(true)
const reservations = ref([])
const currentPage  = ref(1)
const perPage      = 5

const showModal = ref(false)
const detail    = ref({})

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
  return code
}
</script>

<style scoped>
table {
  border-collapse: collapse;
}
th, td {
  font-size: 1rem;
}
</style>
