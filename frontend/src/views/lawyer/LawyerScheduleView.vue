<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const schedules = ref([])
const groupedSchedules = ref({})
const router = useRouter()

const fetchSchedules = async () => {
  try {
    const res = await axios.get('/api/schedule/my')
    schedules.value = res.data
    groupedSchedules.value = groupByDate(res.data)
  } catch (err) {
    console.error('방송 스케줄 로딩 실패:', err)
  }
}

const groupByDate = (items) => {
  return items.reduce((acc, item) => {
    const date = item.date
    if (!acc[date]) acc[date] = []
    acc[date].push(item)
    return acc
  }, {})
}

const goToDetail = (scheduleNo) => {
  if (scheduleNo) {
    router.push(`/lawyer/broadcasts/schedule/${scheduleNo}`)
  }
}

onMounted(fetchSchedules)
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h2 class="fs-3 fw-bold text-primary mb-4">등록한 방송 스케줄</h2>

      <div v-if="Object.keys(groupedSchedules).length === 0" class="text-muted">방송 스케줄이 없습니다.</div>

      <div v-for="(list, date) in groupedSchedules" :key="date" class="mb-5">
        <h4 class="mb-3 fw-semibold border-bottom pb-2">📆 {{ date }}</h4>

        <div v-for="schedule in list" :key="schedule.no" class="border rounded p-3 mb-3 shadow-sm schedule-item cursor-pointer" @click="goToDetail(schedule.no)">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="text-primary fw-semibold">
              🕒 {{ schedule.startTime.slice(11, 16) }} ~ {{ schedule.endTime.slice(11, 16) }}
              <span class="badge bg-secondary ms-2">{{ schedule.categoryName }}</span>
            </div>
          </div>
          <h5 class="fw-bold mb-1 text-dark">{{ schedule.title }}</h5>
          <div class="text-muted small">{{ schedule.content }}</div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.schedule-item:hover {
  background-color: #f8f9fa;
  transition: background-color 0.2s;
}
</style>
