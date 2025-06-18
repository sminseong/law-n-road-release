<template>
  <ClientFrame>
    <div class="container mx-auto py-6 flex">
      <h2 class="text-3xl font-bold mb-6">내 상담 예약 내역</h2>

      <div v-if="loading" class="text-center py-10 text-lg">로딩 중…</div>
      <div v-else>
        <div class="overflow-x-auto">
          <!-- table에만 fixed layout 적용 -->
          <table class="w-full table-fixed border border-gray-300">
            <thead class="bg-gray-100">
            <tr>
              <th class="px-6 py-3 text-left font-medium">예약번호</th>
              <th class="px-6 py-3 text-left font-medium">날짜</th>
              <th class="px-6 py-3 text-left font-medium">시간</th>
              <th class="px-6 py-3 text-left font-medium">금액</th>
              <th class="px-6 py-3 text-left font-medium">상태</th>
              <!-- 상담 내용 열만 너비 고정 -->
              <th class="px-6 py-3 text-left font-medium w-[15ch]">상담 내용</th>
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
                  <span :class="res.status==='REQUESTED'? 'text-yellow-600':'text-green-600'">
                    {{ statusLabel(res.status) }}
                  </span>
              </td>
              <td class="px-6 py-4">
                {{ truncate(res.content, 10) }}
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

        <div v-if="totalPages > 1" class="mt-4 flex justify-center space-x-2">
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
  </ClientFrame>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const clientNo = Number(route.params.clientNo)

const loading = ref(true)
const reservations = ref([])
const currentPage = ref(1)
const perPage     = 5

onMounted(async () => {
  try {
    const token = localStorage.getItem('token')

    const res = await axios.get(`/api/client/reservations/list`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        })
    reservations.value = res.data
  } catch {
    alert('예약 내역 조회에 실패했습니다.')
  } finally {
    loading.value = false
  }
})

const totalPages = computed(() =>
    Math.ceil(reservations.value.length / perPage)
)
const paginated = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return reservations.value.slice(start, start + perPage)
})

function formatDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'short'
  })
}
function statusLabel(code) {
  if (code === 'REQUESTED') return '대기중'
  if (code === 'DONE')      return '완료'
  return code
}
function truncate(text, maxLen) {
  if (!text) return '-'
  return text.length > maxLen ? text.slice(0, maxLen) + '…' : text
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
