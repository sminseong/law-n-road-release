<script setup>
import ClientFrame from '@/components/layout/Client/ClientFrame.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import 'v-calendar/style.css'
import { setupCalendar, Calendar } from 'v-calendar'

setupCalendar()

const router = useRouter()

const schedules = ref({}) // { '2025-06-15': [ { name: '헬멧 방송' }, { name: '주차 방송' } ] }

const fetchSchedule = async () => {
  try {
    const res = await axios.get('/api/schedule/list?month=2025-06') // 또는 현재 월 동적 계산
    const result = res.data

    const mapped = {}
    result.forEach(item => {
      const dateKey = item.date // 'YYYY-MM-DD'
      if (!mapped[dateKey]) mapped[dateKey] = []
      mapped[dateKey].push(item)
    })

    schedules.value = mapped
  } catch (err) {
    console.error('스케줄 불러오기 실패', err)
  }
}

onMounted(fetchSchedule)

const handleDayClick = (day) => {
  const clickedDate = day.id
  router.push(`/client/broadcasts/schedule/${clickedDate}`)
}
</script>

<template>
  <ClientFrame>
    <div class="container py-5">
      <h2 class="fs-3 fw-bold text-primary mb-4">📆 방송 일정 달력</h2>

      <Calendar
          class="bg-white rounded shadow p-3"
          is-expanded
          borderless
          color="blue"
          :attributes="Object.keys(schedules).map(date => ({
          key: date,
          dates: date,
          customData: schedules[date]
        }))"
          @dayclick="handleDayClick"
      >
        <template #day-content="{ day, attributes }">
          <div class="p-2">
            <div class="fw-bold">{{ day.day }}</div>
            <ul class="list-unstyled mt-1">
              <li
                  v-for="item in attributes[0]?.customData || []"
                  :key="item.name"
                  class="text-primary small text-truncate"
              >
                • {{ item.name }}
              </li>
            </ul>
          </div>
        </template>
      </Calendar>
    </div>
  </ClientFrame>
</template>

<style scoped>
.vc-pane {
  min-height: 600px;
}
.vc-day-content {
  min-height: 80px;
}
.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
