<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'

// 1) 부모로부터 받을 props 정의 (주간 전체 데이터)
const props = defineProps({
  weeklySlots: {
    type: Array,
    required: true,
    default: () => []
  }
})

// 2) 부모에게 “선택된 날짜+시간”을 알려줄 이벤트 정의
const emit = defineEmits(['select'])

// 3) 내부 상태: 현재 선택된 날짜(date)와 시간(hourString)
const selectedDate = ref(null)   // ex: '2025-05-17'
const selectedTime = ref(null)   // ex: '14:00'

// 4) 08시~22시 배열 생성
const allHours = Array.from({ length: 22 - 8 + 1 }, (_, idx) => 8 + idx)
const morningHours = computed(() => allHours.filter(h => h < 12))
const afternoonHours = computed(() => allHours.filter(h => h >= 12))

function formatHour(hour) {
  return hour.toString().padStart(2, '0') + ':00'
}

function formatDateKorean(dateString) {
  const d = new Date(dateString + 'T00:00:00')
  const month = d.getMonth() + 1
  const day = d.getDate()
  const weekKor = ['일','월','화','수','목','금','토']
  const dow = weekKor[d.getDay()]
  return `${month}월 ${day}일 (${dow})`
}

/**
 * 해당 일자(dayObj)와 시간(hour)을 기반으로 활성 상태(status === 1)인지 체크
 */
function isActive(dayObj, hour) {
  const hhmm = formatHour(hour)
  const found = dayObj.slots.find(s => s.slotTime === hhmm)
  return found?.status === 1
}

/**
 * 현재 클릭된 버튼인지 체크
 * @returns boolean
 */
function isSelected(dayObj, hour) {
  const hhmm = formatHour(hour)
  return selectedDate.value === dayObj.date && selectedTime.value === hhmm
}

/**
 * 버튼 클래스 계산
 * - 비활성(status≠1) → 회색, 클릭 불가
 * - 활성(status=1) & 선택됨 → 파란 테두리 또는 강조
 * - 활성(status=1) & 선택 안 됨 → 주황색 배경
 */
function getButtonClass(dayObj, hour) {
  const hhmm = formatHour(hour)
  const active = isActive(dayObj, hour)
  const selected = isSelected(dayObj, hour)

  if (!active) {
    return 'px-3 py-2 text-sm font-medium rounded-lg w-full bg-gray-100 text-gray-400 cursor-not-allowed'
  }
  // 활성 상태인 경우
  if (selected) {
    // 선택된 상태: 진한 테두리와 배경
    return 'px-3 py-2 text-sm font-medium rounded-lg w-full bg-orange-600 text-white ring-2 ring-offset-1 ring-orange-400'
  }
  // 활성 상태지만 선택 안 된 상태
  return 'px-3 py-2 text-sm font-medium rounded-lg w-full bg-orange-400 text-white hover:bg-orange-500'
}

/**
 * 버튼 클릭 시: 활성 상태인 경우에만
 * 1) 내부 상태(selectedDate, selectedTime) 업데이트
 * 2) 부모에게 “{ date, slotTime }” 형태로 emit
 */
function onClickHour(dayObj, hour) {
  if (!isActive(dayObj, hour)) return

  const hhmm = formatHour(hour)
  // 이미 같은 버튼을 클릭한 경우: 선택 해제
  if (isSelected(dayObj, hour)) {
    selectedDate.value = null
    selectedTime.value = null
    return
  }

  selectedDate.value = dayObj.date
  selectedTime.value = hhmm

  emit('select', {
    date: dayObj.date,
    slotTime: hhmm
  })
}
</script>

<template>
  <div class="space-y-6">
    <!-- 날짜별 반복 -->
    <div
        v-for="(dayObj, idx) in props.weeklySlots"
        :key="`day-${dayObj.date}-${idx}`"
        class="bg-white rounded-lg shadow-md p-5"
    >
      <!-- 날짜 헤더 -->
      <h2 class="text-xl font-semibold text-gray-800 mb-4">
        {{ formatDateKorean(dayObj.date) }} 예약 가능한 시간
      </h2>

      <!-- 오전 섹션 -->
      <div class="mb-6">
        <div class="flex items-center mb-2">
          <span class="text-base font-medium text-gray-700">오전 (08:00 ~ 11:00)</span>
        </div>
        <div class="grid grid-cols-4 gap-3">
          <button
              v-for="hour in morningHours"
              :key="`m-${dayObj.date}-${hour}`"
              :class="getButtonClass(dayObj, hour)"
              @click="onClickHour(dayObj, hour)"
              :disabled="!isActive(dayObj, hour)"
          >
            {{ formatHour(hour) }}
          </button>
        </div>
      </div>

      <!-- 오후 섹션 -->
      <div>
        <div class="flex items-center mb-2">
          <span class="text-base font-medium text-gray-700">오후 (12:00 ~ 22:00)</span>
        </div>
        <div class="grid grid-cols-4 gap-3">
          <button
              v-for="hour in afternoonHours"
              :key="`a-${dayObj.date}-${hour}`"
              :class="getButtonClass(dayObj, hour)"
              @click="onClickHour(dayObj, hour)"
              :disabled="!isActive(dayObj, hour)"
          >
            {{ formatHour(hour) }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
button {
  transition: background-color 0.2s ease, transform 0.1s ease;
}
button:active {
  transform: scale(0.97);
}
</style>
