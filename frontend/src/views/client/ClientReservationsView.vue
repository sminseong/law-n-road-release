<template>
  <ClientFrame>
    <div class="container mx-auto py-6">
      <h2 class="text-3xl font-bold mb-6">
        {{ lawyerName }} 변호사 상담 예약
      </h2>

      <!-- 로딩 스피너 -->
      <div v-if="loading" class="text-center py-10">로딩 중…</div>

      <!-- 슬롯 리스트 -->
      <div v-else>
        <div
            v-for="day in weeklySlots"
            :key="day.date"
            class="mb-6 bg-white rounded-lg shadow p-4"
        >
          <h3 class="text-xl font-semibold mb-3">
            {{ formatDate(day.date) }}
          </h3>

          <!-- 오전 -->
          <div class="mb-4">
            <p class="text-sm font-medium text-gray-700 mb-2">
              오전 (08:00 ~ 11:00)
            </p>
            <div class="grid grid-cols-4 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) < 12)"
                  :key="slot.no"
                  :disabled="slot.status !== 1"
                  @click="select(slot)"
                  :class="[
                  'px-3 py-2 rounded border',
                  slot.status !== 1
                    ? 'bg-gray-200 cursor-not-allowed'
                    : selectedNo === slot.no
                      ? 'bg-green-200 border-green-500'
                      : 'hover:bg-green-50'
                ]"
              >
                {{ slot.slotTime.slice(0, 5) }}
              </button>
            </div>
          </div>

          <!-- 오후 -->
          <div>
            <p class="text-sm font-medium text-gray-700 mb-2">
              오후 (12:00 ~ 22:00)
            </p>
            <div class="grid grid-cols-6 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) >= 12)"
                  :key="slot.no"
                  :disabled="slot.status !== 1"
                  @click="select(slot)"
                  :class="[
                  'px-3 py-2 rounded border',
                  slot.status !== 1
                    ? 'bg-gray-200 cursor-not-allowed'
                    : selectedNo === slot.no
                      ? 'bg-green-200 border-green-500'
                      : 'hover:bg-green-50'
                ]"
              >
                {{ slot.slotTime.slice(0, 5) }}
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
import {ref, onMounted, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

// 라우팅
const route = useRoute()
const router = useRouter()

// 파라미터
const lawyerNo = Number(route.params.lawyerNo)
const lawyerName = route.params.lawyerName
const userNo = 6  // TODO: 로그인된 유저 정보로 대체

// 상태
const loading = ref(true)
const slotsFlat = ref([])
const selectedNo = ref(null)

// 마운트 시 슬롯 조회
onMounted(async () => {
  try {
    const today = new Date().toISOString().slice(0, 10)
    const res = await axios.get(
        `/api/lawyers/${lawyerNo}/slots`,
        {params: {startDate: today}}
    )
    slotsFlat.value = res.data
  } catch (err) {
    console.error(err)
    alert('슬롯 조회에 실패했습니다.')
  } finally {
    loading.value = false
  }
})

// 슬롯을 날짜별로 그룹핑
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
        slots: daySlots.sort((a, b) => a.slotTime.localeCompare(b.slotTime))
      }))
}

// 최대 7일치만
const weeklySlots = computed(() =>
    groupByDate(slotsFlat.value).slice(0, 7)
)

// 날짜 포맷
function formatDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    month: 'long', day: 'numeric', weekday: 'short'
  })
}

// 슬롯 선택
function select(slot) {
  if (slot.status !== 1) return
  selectedNo.value = slot.no
}

// 예약 생성 후 결제 페이지로 이동
async function apply() {
  try {
    // 1) API 호출
    const res = await axios.post(
        `/api/client/${userNo}/reservations`,
        {slotNo: selectedNo.value, userNo, content: ''}
    )
    const dto = res.data
    console.log('예약 생성 응답 DTO:', dto)

    // 2) 페이지 이동 — 여기서도 에러가 발생할 수 있습니다.
    const pushResult = await router.push({
      name: 'ClientReservationsPayment',   // 혹은 path: '/client/reservations/payment'
      query: {
        orderCode: dto.orderCode,
        reservationNo: dto.no,
        slotDate: dto.slotDate,
        slotTime: dto.slotTime,
        amount: dto.amount,
        lawyerName
      }
    })
    console.log('router.push 결과:', pushResult)
  } catch (err) {
    if (err.response && err.response.data) {
      console.error('🚨 reservations 500 응답:', err.response.data)
      alert(`예약 실패: ${err.response.data.message || JSON.stringify(err.response.data)}`)
    } else {
      console.error('apply() 에서 에러 발생', err)
      alert('예약 신청에 실패했습니다.')
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
}
</style>
