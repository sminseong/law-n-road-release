<script setup>
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {makeApiRequest} from "@/libs/axios-auth.js";

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

const liveScheduleMap = ref({})

onMounted(async () => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: `/api/public/schedule/${dateStr}`
    })

    if (res?.data) {
      const rawSchedules = res.data

      // 라이브 여부 병렬 조회
      const liveChecks = await Promise.all(
          rawSchedules.map(schedule =>
              makeApiRequest({
                method: 'get',
                url: `/api/public/broadcast/live-check/${schedule.no}`
              }).then(res => ({
                scheduleNo: schedule.no,
                live: res.data.live
              })).catch(() => ({
                scheduleNo: schedule.no,
                live: false
              }))
          )
      )

      // liveScheduleMap 업데이트
      liveScheduleMap.value = Object.fromEntries(
          liveChecks.map(entry => [entry.scheduleNo, entry.live])
      )

      // 시간대별 포지션 정리
      schedules.value = arrangeSchedulePositions(rawSchedules)
    }
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

const goToSchedule = async (scheduleNo) => {
  try {
    // 방송 상태 조회 (RECORD / DONE / null)
    const res = await makeApiRequest({
      method: 'get',
      url: `/api/public/broadcast/status/${scheduleNo}`
    })

    const { status, broadcastNo } = res.data;

    if (status === 'DONE' && broadcastNo) {
      // 방송이 종료된 경우 → VOD로 이동
      router.push(`/vod/${broadcastNo}`);
    } else if (status === 'RECORD' && broadcastNo) {
      // 방송이 진행 중인 경우 → 방송 시청 페이지
      router.push(`/client/broadcasts/${broadcastNo}`);
    } else {
      // 방송이 시작되지 않은 경우 → 사전 질문 페이지
      router.push(`/client/broadcasts/schedule/${scheduleNo}/preQuestion`);
    }

  } catch (err) {
    console.error('방송 상태 확인 실패:', err);
    // fallback - 기본 이동
    router.push(`/client/broadcasts/schedule/${scheduleNo}/preQuestion`);
  }
};


const goBackToCalendar = () => {
  router.push('/broadcasts/schedule')
}
</script>

<template>
  <ClientFrame>
    <div class="w-100 min-vh-100 px-4 py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fs-3 fw-bold text-primary mb-0">{{ dateStr }} 방송 스케줄</h2>
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
                  :class="{ 'live-card': liveScheduleMap[schedule.no] }"
                  :style="{ backgroundColor: schedule.color }"
                  @click="goToSchedule(schedule.no)"
              >
                <span
                    v-if="liveScheduleMap[schedule.no]"
                    class="live-badge">
                  <span class="blink">🔴</span> LIVE 중
                </span>
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
  border: 2px solid transparent;
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

/* 라이브 중이면 붉은 테두리 */
.live-card {
  border: 2px solid red !important;
}

/* LIVE 뱃지 */
.live-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: #dc3545;
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 6px;
  z-index: 5;
  box-shadow: 0 0 5px rgba(0,0,0,0.15);
  opacity: 1;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

/* 마우스 올리면 뱃지 사라짐 */
.schedule-card:hover .live-badge {
  opacity: 0;
}

/* 빨간 원 깜빡임 */
@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.2;
  }
}

.blink {
  animation: blink 1s infinite;
}

</style>
