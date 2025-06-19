<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRoute} from 'vue-router'
import axios from 'axios'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const route = useRoute()
const lawyerNo = Number(route.params.lawyerNo)

const loading = ref(true)
const reservations = ref([])
const currentPage = ref(1)
const perPage = 5

const totalPages = computed(() =>
    Math.ceil(reservations.value.length / perPage)
)

const paginated = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return reservations.value.slice(start, start + perPage)
})

onMounted(async () => {
  try {
    const token = localStorage.getItem('token')

    const res = await axios.get(`/api/lawyer/${lawyerNo}/reservations`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
    reservations.value = res.data
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
  return [
    'font-medium',
    code === 'REQUESTED' ? 'text-yellow-600'
        : code === 'DONE' ? 'text-green-600'
            : 'text-gray-400'
  ]
}

function truncate(text, maxLen) {
  if (!text) return '-'
  return text.length > maxLen
      ? text.slice(0, maxLen) + '…'
      : text
}

async function closeConsultation(reservationNo) {
  console.log(reservationNo)
  try {
    const token = localStorage.getItem('token')
    await axios.patch(
        `/api/lawyer/${lawyerNo}/reservations/${reservationNo}/status`,
        null,
        {headers: {Authorization: `Bearer ${token}`}}
    )
    const idx = reservations.value.findIndex(r => r.no === reservationNo)
    if (idx !== -1) reservations.value[idx].status = 'CLOSED'
    alert('상담을 종료 처리했습니다.')
  } catch (err) {
    console.error('상담 종료 처리 실패', err)
    alert('상담 종료 처리에 실패했습니다.')
  }
}
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a><br>
    <router-link
        :to="{ name: 'TimeSlotUpdate', params: { lawyerNo } }"
        class="inline-block mt-2 mb-4 text-blue-600 hover:underline"
    >
      예약 스케줄 설정
    </router-link>
    <div class="container mx-auto py-4">
      <h2 class="text-lg font-semibold mb-3">1:1 상담 예약 내역</h2>

      <div v-if="loading" class="text-center py-8 text-sm">로딩 중…</div>
      <div v-else>
        <div class="overflow-x-auto">
          <table class="w-full table-fixed border border-gray-300">
            <thead class="bg-gray-100">
            <tr>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">예약번호</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">의뢰인</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">날짜</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">시간</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">금액</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">상태</th>
              <th class="px-4 py-2 text-left font-medium w-[12ch] text-[10px] whitespace-nowrap">상담 내용</th>
              <th class="px-4 py-2 text-left font-medium text-[10px] whitespace-nowrap">상담 종료</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="res in paginated"
                :key="res.no"
                class="border-t hover:bg-gray-50"
            >
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ res.no }}</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ res.userName }}</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ formatDate(res.slotDate) }}</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ res.slotTime.slice(0, 5) }}</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ res.amount.toLocaleString() }}원</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">
                  <span :class="statusClass(res.status)">
                    {{ statusLabel(res.status) }}
                  </span>
              </td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">{{ truncate(res.content, 10) }}</td>
              <td class="px-4 py-1 text-[10px] whitespace-nowrap">
                <button
                    v-if="res.status === 'REQUESTED'"
                    @click="closeConsultation(res.no)"
                    class="px-1 py-0 text-[9px] bg-red-500 text-white rounded hover:bg-red-600"
                >
                  종료
                </button>
              </td>
            </tr>
            <tr v-if="!reservations.length">
              <td colspan="8" class="px-4 py-1 text-center text-gray-500 text-[10px] whitespace-nowrap">
                예약 내역이 없습니다.
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div v-if="totalPages > 1" class="mt-2 flex justify-center space-x-1">
          <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
              'px-2 py-1 rounded text-[10px] whitespace-nowrap',
              page === currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200 hover:bg-gray-300'
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.container {
  max-width: 900px;
}
</style>
