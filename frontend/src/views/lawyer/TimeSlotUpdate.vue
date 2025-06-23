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
            class="bg-white rounded-lg shadow p-4"
            style="margin-top: 10px">
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
                  :key="slot.slotTime"
                  @click="toggleSlot(dIdx, day.slots.indexOf(slot))"
                  :class="slot.status === 1 ? activeClass : inactiveClass"
              >
                {{ slot.slotTime.slice(0, 5) }}
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
                  :key="slot.slotTime"
                  @click="toggleSlot(dIdx, day.slots.indexOf(slot))"
                  :class="slot.status === 1 ? activeClass : inactiveClass"
              >
                {{ slot.slotTime.slice(0, 5) }}
              </button>
            </div>
          </div>
        </div>

        <!-- 확인 버튼 -->
        <div class="flex justify-end">
          <button
              @click="submitUpdates"
              class="px-4 py-2 text-white rounded hover:bg-green-700"
              style="background-color: green; margin-top: 10px">
            확인
          </button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import axios from 'axios'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";
import {getValidToken} from "@/libs/axios-auth.js";

// route에서 lawyerNo 가져오기
const route = useRoute()
const lawyerNo = route.params.lawyerNo

const loading = ref(true)
const rawSlots = ref([])
const weeklySlots = ref([])

// 버튼 스타일 정의
const activeClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-green-800'
const inactiveClass = 'px-3 py-2 text-sm font-medium rounded-lg w-full text-gray-400'

// DB에서 받아온 예약을 날짜별로 묶기
function groupByDate(list) {
  const map = {}
  list.forEach(s => {
    if (!map[s.slotDate]) map[s.slotDate] = []
    map[s.slotDate].push({
      slotTime: s.slotTime,
      status: s.status
    })
  })
  return Object.entries(map)
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([date, slots]) => ({
        date,
        slots: slots.sort((a, b) => a.slotTime.localeCompare(b.slotTime))
      }))
}

// API 호출
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
        {params: {startDate}}
    )
    rawSlots.value = res.data
    weeklySlots.value = groupByDate(res.data)
  } catch (err) {
    console.error(err)
    alert('주간 예약 정보를 불러오던 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

onMounted(fetchSlots)

// 슬롯 클릭 시 0 <-> 1 토글
function toggleSlot(dayIdx, slotIdx) {
  const slot = weeklySlots.value[dayIdx].slots[slotIdx]
  slot.status = slot.status === 1 ? 0 : 1
}

// 업데이트 요청 전송
async function submitUpdates() {
  console.log(lawyerNo)
  const updates = weeklySlots.value.flatMap(day =>
      day.slots.map(s => ({
        slotDate: day.date,
        slotTime: s.slotTime,
        status: s.status
      }))
  )

  try {
    await axios.put(
        `/api/lawyer/${lawyerNo}/slots`,
        updates
    )
    alert('주간 예약 정보가 성공적으로 저장되었습니다.')
  } catch (err) {
    console.error(err)
    alert('저장 중 오류가 발생했습니다.')
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
