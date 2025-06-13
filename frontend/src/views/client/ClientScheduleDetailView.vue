<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const router = useRouter()
const dateStr = route.params.date
const schedules = ref([])

const hourHeight = 60
const timeLabelWidth = 80
const colors = [
  '#69a8ff', '#20c997', '#ffc107', '#e07777',
  '#9b7bcc', '#fca344', '#a3a7ff','#8fd565'
]

onMounted(async () => {
  try {
    const res = await axios.get(`/api/schedule/${dateStr}`)
    const arranged = arrangeSchedulePositions(res.data)
    schedules.value = arranged
  } catch (e) {
    console.error('스케줄 불러오기 실패:', e)
  }
})

const getMinutes = (timeStr) => {
  const [h, m] = timeStr.split(':').map(Number)
  return h * 60 + m
}

function arrangeSchedulePositions(scheduleList) {
  const hourMap = Array.from({ length: 24 }, () => [])
  scheduleList.forEach((s, index) => {
    const startMin = getMinutes(s.startTime.slice(11, 16))
    s.startMinute = startMin
    s.startHour = Math.floor(startMin / 60)
    s.color = colors[index % colors.length]
    hourMap[s.startHour].push(s)
  })
  return hourMap
}

const goToSchedule = (scheduleNo) => {
  router.push(`/client/broadcasts/schedule/${scheduleNo}/preQuestion`)
}

const goBackToCalendar = () => {
  router.push('/client/broadcasts/schedule')
}
</script>

<template>
  <ClientFrame>
    <div class="w-100 min-vh-100 px-4 py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fs-3 fw-bold text-primary mb-0">📅 {{ dateStr }} 방송 스케줄</h2>
        <button class="btn btn-outline-secondary" @click="goBackToCalendar">← 달력으로 돌아가기</button>
      </div>

      <div class="timeline-wrapper border rounded overflow-auto position-relative">
        <div class="timeline-inner" style="min-width: 1200px;">

          <div
              v-for="hour in 24"
              :key="hour"
              class="d-flex"
              :style="{ minHeight: hourHeight + 'px', borderTop: '1px solid #adb5bd' }"
          >
            <!-- 시간 라벨 -->
            <div :style="{ width: timeLabelWidth + 'px' }" class="px-2 pt-1 text-muted fw-semibold small">
              {{ String(hour).padStart(2, '0') }}:00
            </div>

            <!-- 해당 시간대 스케줄 -->
            <div class="d-flex flex-wrap gap-2 ps-2" style="flex: 1;">
              <div
                  v-for="schedule in schedules[hour]"
                  :key="schedule.no"
                  class="schedule-card"
                  :style="{ backgroundColor: schedule.color }"
                  @click="goToSchedule(schedule.no)"
              >
                <div class="fw-semibold text-truncate">📺 {{ schedule.name }}</div>
                <div class="small text-black-50">{{ schedule.lawyerName }} 변호사</div>
                <div class="small">🕒 {{ schedule.startTime.slice(11, 16) }} ~ {{ schedule.endTime.slice(11, 16) }}</div>
                <div class="small schedule-content">{{ schedule.content }}</div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.timeline-wrapper {
  background-color: #f8f9fa;
  overflow-x: auto;
}

.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.schedule-card {
  padding: 10px 12px;
  border-radius: 8px;
  color: #000000;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  width: 250px;
  min-height: 60px;
  margin-top: 4px;
  margin-bottom: 4px;
  transition: transform 0.1s ease, box-shadow 0.1s ease;
  overflow: hidden;
  position: relative;
}

.schedule-card:hover {
  z-index: 2;
  transform: scale(1.02);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
}

.schedule-card:active {
  transform: scale(0.98);
  opacity: 0.9;
}

.schedule-card:hover .schedule-content {
  overflow: visible;
  white-space: normal;
  text-overflow: unset;
  display: block;
  z-index: 10;
  position: relative;
}

.schedule-content {
  font-size: 13px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
