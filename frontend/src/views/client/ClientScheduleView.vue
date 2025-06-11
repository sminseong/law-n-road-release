<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const router = useRouter()
const calendarRef = ref(null)
const events = ref([])

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  height: 'calc(100vh - 150px)',
  locale: 'ko',
  dayMaxEvents: 3,
  events: events.value,
  dateClick: (arg) => {
    const cell = document.querySelector(`td[data-date="${arg.dateStr}"]`)
    if (cell) {
      cell.classList.add('clicked')
      setTimeout(() => cell.classList.remove('clicked'), 150)
    }
    router.push(`/client/broadcasts/schedule/${arg.dateStr}`)
  },
  eventClick: (info) => {
    const dateStr = info.event.startStr.slice(0, 10)
    const cell = document.querySelector(`td[data-date="${dateStr}"]`)
    if (cell) {
      cell.classList.add('clicked')
      setTimeout(() => cell.classList.remove('clicked'), 150)
    }
    router.push(`/client/broadcasts/schedule/${dateStr}`)
  },
  eventContent: function (info) {
    const title = info.event.title
    const start = new Date(info.event.start).toTimeString().slice(0, 5)
    const lawyer = info.event.extendedProps.original?.lawyerName || ''
    const tooltip = `[${start}] ${title} (${lawyer} 변호사)`
    return {
      html: `
        <div title="${tooltip}" class="fc-custom-event">
          <div class="fc-event-title fw-semibold">${title}</div>
          <div class="fc-lawyer-name text-muted small">${lawyer} 변호사</div>
        </div>
      `
    }
  }
})

const fetchMonthlySchedule = async () => {
  try {
    const now = new Date()
    const month = now.toISOString().slice(0, 7)
    const res = await axios.get(`/api/schedule/month?month=${month}`)
    events.value = res.data.map(ev => ({
      title: ev.title,
      start: ev.startTime,
      extendedProps: {
        lawyerName: ev.lawyerName,
        original: ev
      }
    }))
    calendarOptions.value.events = events.value
  } catch (err) {
    console.error('스케줄 로드 실패:', err)
  }
}

onMounted(fetchMonthlySchedule)
</script>

<template>
  <ClientFrame>
    <div class="w-100 vh-100 d-flex flex-column p-4">
      <h2 class="fs-3 fw-bold mb-4 text-primary">📅 방송 스케줄</h2>
      <div class="flex-grow-1">
        <FullCalendar
            ref="calendarRef"
            :options="calendarOptions"
        />
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
::v-deep(.fc) {
  background-color: white;
}

/* 요일 헤더 */
::v-deep(.fc-col-header) {
  background-color: #e9ecef;
  font-weight: 600;
}

/* 본문 배경 */
::v-deep(.fc-daygrid-body) {
  background-color: #f8f9fb;
  border-radius: 0 0 0.5rem 0.5rem;
}

/* 셀 hover & 클릭 */
::v-deep(.fc-daygrid-day) {
  transition: background-color 0.2s;
}
::v-deep(.fc-daygrid-day:hover) {
  background-color: #e2e6ea;
  cursor: pointer;
}
::v-deep(td.clicked) {
  animation: clickFlash 0.2s ease-in-out;
}
@keyframes clickFlash {
  0% { background-color: rgba(33, 150, 243, 0.08); }
  100% { background-color: transparent; }
}

/* 셀 내부 이벤트 */
::v-deep(.fc-daygrid-day-events) {
  overflow: hidden;
  position: relative;
}

/* 개별 이벤트 */
::v-deep(.fc-event) {
  max-width: 100% !important;
  overflow: hidden;
  border-radius: 0.25rem;
  padding: 2px 4px;
  background-color: #ffffff;
  border: 1px solid #dee2e6;
  transition: background-color 0.15s;
}
::v-deep(.fc-event:hover) {
  background-color: #ebedf0;
}

/* 커스텀 이벤트 콘텐츠 */
.fc-custom-event {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.fc-event-title {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.85rem;
  font-weight: 600;
}
.fc-lawyer-name {
  display: block;
  font-size: 0.75rem;
  color: #6c757d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
