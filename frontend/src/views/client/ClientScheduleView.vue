<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import {makeApiRequest} from "@/libs/axios-auth.js";

const router = useRouter()
const calendarRef = ref(null)
const events = ref([])

const colors = ['#ebfde8', '#fff9d6', '#d6eeff', '#ffdfdf', '#eee4ff', '#fdead6']

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
  eventContent: (info) => {
    const title = info.event.title
    const original = info.event.extendedProps.original
    const startTime = new Date(original.startTime)
    const endTime = original.endTime ? new Date(original.endTime) : null

    const startHour = String(startTime.getHours()).padStart(2, '0')
    const startMin = String(startTime.getMinutes()).padStart(2, '0')
    const startStr = `${startHour}:${startMin}`

    let timeRange = `[${startStr}`
    if (endTime) {
      const endHour = String(endTime.getHours()).padStart(2, '0')
      const endMin = String(endTime.getMinutes()).padStart(2, '0')
      const endStr = `${endHour}:${endMin}`
      timeRange += ` ~ ${endStr}`
    }
    timeRange += `]`

    const lawyer = original.lawyerName || ''
    const isLive = info.event.extendedProps.isLive === true
    const tooltip = `${timeRange} ${title} (${lawyer} 변호사)`

    return {
      html: `
    <div title="${tooltip}" class="fc-custom-event">
      ${isLive ? '<div class="live-badge">LIVE</div>' : ''}
      <div class="fc-event-title text-dark fw-semibold">${title}</div>
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

    const res = await makeApiRequest({
      method: 'get',
      url: `/api/client/schedule/month?month=${month}`
    })

    if (res?.data) {
      const checkLiveList = await Promise.all(
          res.data.map(ev =>
              makeApiRequest({
                method: 'get',
                url: `/api/client/broadcast/live-check/${ev.scheduleNo}`
              }).then(liveRes => ({
                ...ev,
                isLive: liveRes.data.live
              })).catch(() => ({
                ...ev,
                isLive: false
              }))
          )
      )
      console.log("✅ 이벤트 리스트", events.value)
      events.value = checkLiveList.map((ev, index) => {
        const startDateOnly = ev.startTime.slice(0, 10)
        return {
          title: ev.title,
          start: startDateOnly,
          backgroundColor: colors[index % colors.length],
          borderColor: '#dee2e6',
          textColor: '#212529',
          extendedProps: {
            lawyerName: ev.lawyerName,
            isLive: ev.isLive,
            original: ev
          }
        }
      })

      calendarOptions.value.events = events.value
    }
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
        <FullCalendar ref="calendarRef" :options="calendarOptions" />
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
::v-deep(.fc) {
  background-color: white;
}
::v-deep(.fc-col-header) {
  background-color: #e9ecef;
  font-weight: 600;
}
::v-deep(.fc-daygrid-body) {
  background-color: #f8f9fb;
  border-radius: 0 0 0.5rem 0.5rem;
}
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
  0% {
    background-color: rgba(33, 150, 243, 0.08);
  }
  100% {
    background-color: transparent;
  }
}
::v-deep(.fc-daygrid-day-events) {
  overflow: hidden;
  position: relative;
}
::v-deep(.fc-event) {
  max-width: 100% !important;
  border-radius: 0.25rem;
  padding: 2px 4px;
  transition: background-color 0.15s;
}
::v-deep(.fc-event:hover) {
  background-color: #ebedf0;
}
.fc-custom-event {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.fc-event-title {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #212529;
}
.fc-lawyer-name {
  display: block;
  font-size: 0.75rem;
  color: #6c757d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
::v-deep(.fc-day-past) {
  background-color: #ebedef;
  opacity: 0.6;
}
::v-deep(.fc-day-past .fc-event) {
  opacity: 0.5;
}
::v-deep(.live-badge) {
  position: absolute;
  top: 1px;
  right: 1px;
  background-color: #dc3545;
  color: white;
  font-size: 7px;
  font-weight: bold;
  padding: 2px 5px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
  z-index: 1;
}
</style>
