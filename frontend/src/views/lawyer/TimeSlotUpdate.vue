<template>
  <LawyerFrame>
    <div class="container py-4 mt-8">
      <h2 class="text-2xl font-semibold mb-4">주간 예약 설정</h2>

      <!-- 로딩 표시 -->
      <div v-if="loading" class="text-center py-10">로딩 중…</div>

      <!-- 슬롯 그리드 -->
      <div v-else class="space-y-8">
        <div
            v-for="(day, dIdx) in weeklySlots"
            :key="day.date"
            class="bg-white rounded-lg shadow p-4 mt-2"
        >
          <h3 class="text-lg font-medium mb-2">
            {{ formatDate(day.date) }}
          </h3>

          <!-- 오전 -->
          <div class="mb-4">
            <div class="text-sm font-medium text-gray-700 mb-1">
              오전 (08:00 ~ 11:00)
            </div>
            <div class="grid grid-cols-4 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) < 12)"
                  :key="slot.no"
                  @click="toggleSlot(dIdx, slot)"
                  :disabled="slot.status === 0 && slot.requestedCount > 0"
                  :class="buttonClass(slot)"
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
            <div class="grid grid-cols-4 gap-2">
              <button
                  v-for="slot in day.slots.filter(s => +s.slotTime.slice(0,2) >= 12)"
                  :key="slot.no"
                  @click="toggleSlot(dIdx, slot)"
                  :disabled="slot.status === 0 && slot.requestedCount > 0"
                  :class="buttonClass(slot)"
              >
                {{ slot.slotTime.slice(0,5) }}
              </button>
            </div>
          </div>
        </div>

        <!-- 확인 버튼 -->
        <div class="flex justify-end mt-4">
          <button
              @click="submitUpdates"
              class="px-4 py-2 text-white rounded hover:bg-green-700 bg-green-600"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { getValidToken } from '@/libs/axios-auth.js'

// route에서 lawyerNo 가져오기
const route = useRoute()
const router = useRouter()
const lawyerNo = route.params.lawyerNo

const loading = ref(true)
const weeklySlots = ref([])

// 버튼 스타일 정의
const activeClass   = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-green-800 border border-green-800'
const inactiveClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-gray-400 border border-gray-300'
const reservedClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full opacity-50 cursor-not-allowed border border-red-300'

// API 호출 및 데이터 가공
function groupByDate(list) {
  const map = {}
  list.forEach(s => {
    if (!map[s.slotDate]) map[s.slotDate] = []
    map[s.slotDate].push({
      no:             s.no,
      slotTime:       s.slotTime,
      status:         s.status,
      requestedCount: s.requestedCount  // 백엔드에서 내려주는 필드
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

onMounted(fetchSlots)

// 버튼마다 클래스 분기
function buttonClass(slot) {
  if (slot.status === 1) return activeClass
  if (slot.requestedCount > 0) return reservedClass
  return inactiveClass
}

// 슬롯 클릭 시 토글 (0→1 / 1→0), 단 예약된 닫힌 슬롯은 차단
function toggleSlot(dayIdx, slot) {
  if (slot.status === 0 && slot.requestedCount > 0) {
    alert('이미 예약된 슬롯은 활성화할 수 없습니다.')
    return
  }
  slot.status = slot.status === 1 ? 0 : 1
}

// 업데이트 요청 전송
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
    router.push('/lawyer')
  } catch (err) {
    console.error(err)
    if (err.response?.status === 409) {
      alert(err.response.data?.message || '변경할 수 없는 슬롯이 포함되어 있습니다.')
    } else {
      alert('저장 중 오류가 발생했습니다.')
    }
  }
}

// 날짜 한글 포맷팅
function formatDate(str) {
  const d = new Date(str + 'T00:00:00')
  return d.toLocaleDateString('ko', {
    month: 'long', day: 'numeric', weekday: 'short'
  })
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: auto;
}
</style>
