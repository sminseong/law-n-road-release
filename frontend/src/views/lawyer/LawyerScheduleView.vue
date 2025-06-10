<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { ref } from 'vue'
import { Calendar } from 'v-calendar'
import 'v-calendar/style.css'

const selectedDate = ref(null)
const isDayView = ref(false)
const showTooltip = ref(false)
const tooltipContent = ref('')
const tooltipX = ref(0)
const tooltipY = ref(0)

const events = [
  { date: '2025-06-10T10:00:00', title: '🚗 교통사고 법률 방송 교통사고 법률 방송 교통사고 법률 방송' },
  { date: '2025-06-10T14:00:00', title: '⚖️ 헬멧 미착용 처벌 해설' },
  { date: '2025-06-10T18:00:00', title: '🛵 이륜차 사고 집중 토론 이륜차 사고 집중 토론 이륜차 사고 집중 토론' },
  { date: '2025-06-10T20:00:00', title: '📻 도로 위 소음 Q&A' },
  { date: '2025-06-12T18:30:00', title: '🍷 음주운전 Q&A 방송' }
]

const toDayStr = (date) => {
  if (!date) return ''
  return new Date(date).toISOString().slice(0, 10)
}

const handleDateSelect = ({ date }) => {
  selectedDate.value = date
  isDayView.value = true
}

const backToMonth = () => {
  isDayView.value = false
}

const getEventsForDay = (date) => {
  const dayStr = toDayStr(date)
  return events
      .filter(e => toDayStr(e.date) === dayStr)
      .sort((a, b) => new Date(a.date) - new Date(b.date))
}

// 툴팁 표시 제어
const handleBadgeMouseEnter = (event, events) => {
  tooltipContent.value = events.map(e => e.title).join('<br>')
  showTooltip.value = true
  // 마우스 위치로 툴팁 이동
  tooltipX.value = event.clientX + 10
  tooltipY.value = event.clientY + 10
}
const handleBadgeMouseLeave = () => {
  showTooltip.value = false
}
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a>
    <br><br>
    <div class="container py-4">

      <!-- ✅ 월 달력 -->
      <Calendar
          v-if="!isDayView"
          is-expanded
          color="indigo"
          class="calendar-full"
          :attributes="[]"
          @dayclick="handleDateSelect"
      >
        <!-- 각 날짜 셀 커스터마이징 -->
        <template #day-content="{ day }">
          <div class="day-cell">
            <div class="day-number">{{ day.day }}</div>
            <div class="event-list">
              <template v-if="getEventsForDay(day.date).length <= 3">
                <div
                    class="event-item"
                    v-for="event in getEventsForDay(day.date)"
                    :key="event.title"
                >
                  {{ event.title }}
                </div>
              </template>
              <template v-else>
                <div
                    class="event-item"
                    v-for="event in getEventsForDay(day.date).slice(0, 2)"
                    :key="event.title"
                >
                  {{ event.title }}
                </div>
                <!-- 숫자 뱃지에 커스텀 툴팁 -->
                <span
                    class="event-more-badge"
                    @mouseenter="e => handleBadgeMouseEnter(e, getEventsForDay(day.date))"
                    @mousemove="e => handleBadgeMouseEnter(e, getEventsForDay(day.date))"
                    @mouseleave="handleBadgeMouseLeave"
                >
                  +{{ getEventsForDay(day.date).length - 2 }}
                </span>
              </template>
            </div>
          </div>
        </template>
      </Calendar>

      <!-- Day View 예시 -->
      <div v-else class="day-view-box p-4 rounded shadow-sm">
        <button class="btn btn-outline-primary mb-3" @click="backToMonth">← 월 보기로 돌아가기</button>
        <h5 class="fw-bold text-primary mb-2">{{ selectedDate ? new Date(selectedDate).toLocaleDateString() : '' }}</h5>
        <ul>
          <li
              v-for="event in getEventsForDay(selectedDate)"
              :key="event.title"
          >
            🕒 {{ new Date(event.date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }}
            - {{ event.title }}
          </li>
        </ul>
        <div v-if="getEventsForDay(selectedDate).length === 0" class="text-muted">
          📭 해당 날짜에 방송이 없습니다.
        </div>
      </div>

      <!-- 즉시 뜨는 커스텀 툴팁 -->
      <div
          v-if="showTooltip"
          class="custom-tooltip"
          :style="{ left: tooltipX + 'px', top: tooltipY + 'px' }"
          v-html="tooltipContent"
      ></div>
    </div>
  </LawyerFrame>
</template>

<style>
.calendar-full {
  width: 100%;
  min-height: 75vh;
}

.day-cell {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 10px;
  min-height: 110px;
  font-size: 0.9rem;
}

.day-number {
  font-weight: bold;
  color: #1c2942;
  margin-bottom: 6px;
  font-size: 1rem;
}

.event-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow-y: auto;
  max-height: 90px;
}

.event-item {
  background-color: #3b5bdb;
  color: white;
  font-size: 0.72rem;
  border-radius: 4px;
  padding: 2px 6px;
  line-height: 1.3;
  word-break: keep-all;
  white-space: normal;
}

/* +N 더보기 뱃지 */
.event-more-badge {
  display: inline-block;
  background-color: #364fc7;
  color: #fff;
  border-radius: 1rem;
  padding: 1px 8px 1px 8px;
  font-size: 0.74rem;
  font-weight: bold;
  margin-top: 2px;
  cursor: pointer;
  transition: background 0.1s;
}
.event-more-badge:hover {
  background-color: #5c7cfa;
  z-index: 10;
}

.vc-highlight-content {
  background-color: transparent !important;
  padding: 0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

.day-view-box {
  background-color: #f8f9fc;
  border: 1px solid #3b5bdb;
}

/* 커스텀 툴팁 */
.custom-tooltip {
  position: fixed;
  background: #272a37;
  color: #fff;
  padding: 8px 12px;
  border-radius: 7px;
  box-shadow: 0 3px 12px rgba(0,0,0,0.13);
  z-index: 9999;
  font-size: 0.82rem;
  pointer-events: none;
  white-space: pre-line;
  max-width: 220px;
}
</style>
