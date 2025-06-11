<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-3xl font-bold mb-6">
        {{ lawyerName }} 변호사 상담 예약
      </h2>

      <div v-if="loading" class="text-center py-10">로딩 중…</div>
      <div v-else>
        <!-- 날짜별 카드 (최대 7일치만) -->
        <div
            v-for="day in weeklySlots"
            :key="day.date"
            class="mb-6 bg-white rounded-lg shadow p-4"
        >
          <!-- 날짜 헤더 -->
          <h3 class="text-xl font-semibold mb-3">
            {{ formatDate(day.date) }}
          </h3>

          <!-- 오전 구간 -->
          <div class="mb-4">
            <p class="text-sm font-medium text-gray-700 mb-2">
              오전 (08:00 ~ 11:00)
            </p>
            <div class="grid grid-cols-4 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) < 12)"
                  :key="slot.no"
                  :disabled="slot.status !== 0"
                  @click="select(slot)"
                  :class="[
                  'px-3 py-2 rounded border',
                  slot.status !== 0
                    ? 'bg-gray-200 cursor-not-allowed'
                    : selectedNo === slot.no
                      ? 'bg-green-200 border-green-500'
                      : 'hover:bg-green-50'
                ]"
              >
                {{ slot.slotTime.slice(0,5) }}
              </button>
            </div>
          </div>

          <!-- 오후 구간 -->
          <div>
            <p class="text-sm font-medium text-gray-700 mb-2">
              오후 (12:00 ~ 22:00)
            </p>
            <div class="grid grid-cols-6 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) >= 12)"
                  :key="slot.no"
                  :disabled="slot.status !== 0"
                  @click="select(slot)"
                  :class="[
                  'px-3 py-2 rounded border',
                  slot.status !== 0
                    ? 'bg-gray-200 cursor-not-allowed'
                    : selectedNo === slot.no
                      ? 'bg-green-200 border-green-500'
                      : 'hover:bg-green-50'
                ]"
              >
                {{ slot.slotTime.slice(0,5) }}
              </button>
            </div>
          </div>
        </div>

        <!-- 예약 신청 버튼 -->
        <div class="text-right">
          <button
              class="px-5 py-2 bg-blue-600 text-white rounded disabled:opacity-50"
              :disabled="!selectedNo"
              @click="apply"
          >
            예약 신청
          </button>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

// 라우트 & 라우터
const route = useRoute()
const router = useRouter()

const lawyerNo   = Number(route.params.lawyerNo)
const lawyerName = route.params.lawyerName
const userNo     = 6  // 임시 하드코딩

// 상태
const loading    = ref(true)
const slotsFlat  = ref([])
const selectedNo = ref(null)

// API 호출: flat 리스트로 받아옴
onMounted(async () => {
  try {
    const today = new Date().toISOString().slice(0,10)
    const res = await axios.get(
        `/api/lawyers/${lawyerNo}/slots`,
        { params: { startDate: today } }
    )
    slotsFlat.value = res.data
  } catch (err) {
    console.error(err)
    alert('슬롯 조회에 실패했습니다.')
  } finally {
    loading.value = false
  }
})

// 그룹핑: 날짜별로 모은 뒤 정렬
function groupByDate(list) {
  const map = {}
  list.forEach(s => {
    if (!map[s.slotDate]) map[s.slotDate] = []
    map[s.slotDate].push(s)
  })
  return Object.entries(map)
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([date, daySlots]) => ({
        date,
        slots: daySlots.sort((a,b) => a.slotTime.localeCompare(b.slotTime))
      }))
}

// computed: 오늘부터 최대 7일치 그룹만
const weeklySlots = computed(() => {
  return groupByDate(slotsFlat.value).slice(0, 7)
})

// 날짜 한글 포맷팅
function formatDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    month: 'long', day: 'numeric', weekday: 'short'
  })
}

// 슬롯 선택
function select(slot) {
  if (slot.status !== 0) return
  selectedNo.value = slot.no
}

// 예약 신청
async function apply() {
  try {
    await axios.post(
        `/api/client/${userNo}/reservations`,
        { slotNo: selectedNo.value, userNo, content: '' }
    )
    alert('예약이 완료되었습니다.')
    router.push({ name: 'ClientMypage' })
  } catch (err) {
    console.error(err)
    alert('예약 신청에 실패했습니다.')
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
}
</style>
