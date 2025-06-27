<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";
import { fetchTodaySchedule, fetchTomorrowConsultationRequests } from '@/service/dashboardService.js'

import {
  Chart,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend,
  Filler,
  BarController,
  LineController
} from 'chart.js'

// Chart.js 컴포넌트 등록
Chart.register(
    CategoryScale,
    LinearScale,
    BarElement,
    LineElement,
    PointElement,
    Title,
    Tooltip,
    Legend,
    Filler,
    BarController,
    LineController
)

// 반응형 데이터
const currentTime = ref('')
const loading = ref(false)
const lawyerInfo = ref({
  name: '강민영',
  id: 32
})

const dashboardStats = ref([
  {
    title: '내일 상담신청',
    value: '데이터 없음',
    icon: '👥',
    color: '#3b82f6',
    trend: false,
    trendValue: null,
    loading: false
  },
  {
    title: '예정된 방송',
    value: '데이터 없음',
    icon: '📺',
    color: '#10b981',
    trend: false,
    trendValue: null,
    loading: false
  },
  {
    title: '이달의 수익',
    value: '데이터 없음',
    icon: '💰',
    color: '#f59e0b',
    trend: false,
    trendValue: null,
    loading: false
  },
  {
    title: '템플릿 판매 수',
    value: '데이터 없음',
    icon: '📄',
    color: '#8b5cf6',
    trend: false,
    trendValue: null,
    loading: false
  }
])

const todaySchedule = ref([])
const scheduleLoading = ref(false)

const tomorrowConsultationRequests = ref([])
const consultationLoading = ref(false)

// 차트 참조
const weeklyChart = ref(null)
const revenueChart = ref(null)
let weeklyChartInstance = null
let revenueChartInstance = null

// 시간 업데이트 타이머
let timeInterval = null

// 메서드
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getScheduleIcon = (type) => {
  switch(type) {
    case 'consultation': return '👤'
    case 'broadcast': return '📺'
    case 'qa': return '💬'
    default: return '📅'
  }
}

const getScheduleColor = (type) => {
  switch(type) {
    case 'consultation': return 'bg-blue-100 border-blue-300'
    case 'broadcast': return 'bg-red-100 border-red-300'
    case 'qa': return 'bg-green-100 border-green-300'
    default: return 'bg-gray-100 border-gray-300'
  }
}

const createWeeklyChart = (data = null) => {
  if (!weeklyChart.value) return

  const ctx = weeklyChart.value.getContext('2d')

  const chartData = data || {
    consultations: [0, 0, 0, 0, 0, 0, 0],
    broadcasts: [0, 0, 0, 0, 0, 0, 0]
  }

  weeklyChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'],
      datasets: [
        {
          label: '상담',
          data: chartData.consultations,
          backgroundColor: '#3b82f6',
          borderRadius: 6,
          borderSkipped: false,
        },
        {
          label: '방송',
          data: chartData.broadcasts,
          backgroundColor: '#ef4444',
          borderRadius: 6,
          borderSkipped: false,
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            boxWidth: 20,
            padding: 20,
            font: {
              size: 14
            }
          }
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: 'white',
          bodyColor: 'white',
          borderColor: '#e5e7eb',
          borderWidth: 1
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            font: {
              size: 12
            }
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            color: '#f3f4f6'
          },
          ticks: {
            font: {
              size: 12
            }
          }
        }
      }
    }
  })
}

const createRevenueChart = (data = null) => {
  if (!revenueChart.value) return

  const ctx = revenueChart.value.getContext('2d')

  const chartData = data || {
    months: ['1월', '2월', '3월', '4월', '5월', '6월'],
    revenues: [0, 0, 0, 0, 0, 0]
  }

  revenueChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: chartData.months,
      datasets: [{
        label: '수익 (만원)',
        data: chartData.revenues,
        borderColor: '#f59e0b',
        backgroundColor: 'rgba(245, 158, 11, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#f59e0b',
        pointBorderColor: '#ffffff',
        pointBorderWidth: 2,
        pointRadius: 6,
        pointHoverRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            boxWidth: 20,
            padding: 20,
            font: {
              size: 14
            }
          }
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: 'white',
          bodyColor: 'white',
          borderColor: '#e5e7eb',
          borderWidth: 1
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            font: {
              size: 12
            }
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            color: '#f3f4f6'
          },
          ticks: {
            font: {
              size: 12
            },
            callback: function(value) {
              return value + '만원'
            }
          }
        }
      }
    }
  })
}

const loadTodaySchedule = async () => {
  scheduleLoading.value = true
  try {
    console.log('오늘 일정 로드 시작 - lawyerNo:', lawyerInfo.value.id)

    const response = await fetchTodaySchedule (lawyerInfo.value.id)
    console.log('API 응답:', response)

    if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
      todaySchedule.value = response.data.map(item => ({
        time: item.time,
        event: item.event,
        type: item.type,
        clientName: item.clientName || null
      }))
      console.log('일정 데이터 매핑 완료:', todaySchedule.value)
    } else {
      console.log('일정 데이터 없음')
      todaySchedule.value = []
    }
  } catch (error) {
    console.error('오늘 일정 로딩 실패:', error)
    todaySchedule.value = []
  } finally {
    scheduleLoading.value = false
  }
}

const loadTomorrowConsultationRequests = async () => {
  consultationLoading.value = true
  try {
    console.log('내일 상담신청 로드 시작')

    const response = await fetchTomorrowConsultationRequests()
    console.log('내일 상담신청 API 응답:', response)

    if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
      tomorrowConsultationRequests.value = response.data

      dashboardStats.value[0].value = response.data.length + '건'
      dashboardStats.value[0].loading = false

      console.log('내일 상담신청 데이터 매핑 완료:', tomorrowConsultationRequests.value)
    } else {
      console.log('내일 상담신청 데이터 없음')
      tomorrowConsultationRequests.value = []
      dashboardStats.value[0].value = '0건'
      dashboardStats.value[0].loading = false
    }
  } catch (error) {
    console.error('내일 상담신청 로딩 실패:', error)
    tomorrowConsultationRequests.value = []
    dashboardStats.value[0].value = '데이터 없음'
    dashboardStats.value[0].loading = false
  } finally {
    consultationLoading.value = false
  }
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)

  setTimeout(() => {
    createWeeklyChart()
    createRevenueChart()
  }, 100)

  loadTodaySchedule()
  loadTomorrowConsultationRequests()
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  if (weeklyChartInstance) {
    weeklyChartInstance.destroy()
  }
  if (revenueChartInstance) {
    revenueChartInstance.destroy()
  }
})

</script>

<template>
  <LawyerFrame>
    <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">

      <!-- 헤더 -->
      <div class="bg-white shadow-md border-b border-gray-200 mb-4">
        <div class="max-w-7xl mx-auto px-6 py-1">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div>
                <h1 class="text-xl font-bold text-gray-800">로앤로드</h1>
              </div>
            </div>
            <div class="flex items-center">
              <div class="text-right">
                <p class="text-xs text-gray-600 mb-1">안녕하세요, {{ lawyerInfo.name }} 변호사님</p>
                <p class="text-lg font-bold text-blue-600 font-mono">{{ currentTime }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="max-w-7xl mx-auto px-6 py-4">

        <!-- 오늘 일정 -->
        <div class="mb-6">
          <div class="bg-white rounded-2xl shadow-xl p-6">
            <div class="flex items-center mb-4">
              <span class="text-xl mr-2">📅</span>
              <h3 class="text-xl font-bold text-gray-800">오늘 일정</h3>
            </div>

            <div v-if="scheduleLoading" class="flex justify-center py-6">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            </div>

            <div v-else-if="todaySchedule.length === 0" class="text-center py-6">
              <span class="text-4xl mb-3 block">📭</span>
              <p class="text-gray-500 text-base">오늘 일정이 없습니다</p>
            </div>

            <!-- 수정: 2열 그리드 -->
            <div v-else style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
              <div v-for="(schedule, index) in todaySchedule" :key="index"
                   class="flex items-center p-2.5 rounded-lg border-2 transition-all duration-200 hover:shadow-lg cursor-pointer"
                   :class="getScheduleColor(schedule.type)">
                <div class="flex-shrink-0 mr-2">
                  <span class="text-base">{{ getScheduleIcon(schedule.type) }}</span>
                </div>
                <div class="flex-1">
                  <p class="text-xs font-bold text-gray-800 mb-0.5">{{ schedule.time }}</p>
                  <p class="text-xs text-gray-600 leading-tight">{{ schedule.event }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 주요 지표 카드 -->
        <div class="overflow-x-auto">
          <div class="flex gap-6 mb-10 min-w-[1024px] px-1">
            <div v-for="stat in dashboardStats"
                 :key="stat.title"
                 class="w-[240px] flex-shrink-0 bg-white rounded-xl shadow p-4 border-l-4 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
                 :style="{ borderLeftColor: stat.color }">
              <div class="flex flex-col">
                <p class="text-gray-600 text-sm font-medium">{{ stat.title }}</p>
                <div class="mt-1 flex items-center gap-2 text-nowrap leading-tight"
                     :style="{ backgroundColor: stat.color + '15' }">
                  <span class="text-xl">{{ stat.icon }}</span>
                  <span class="text-xl font-bold"
                        :style="{ color: stat.value === '데이터 없음' ? '#9ca3af' : stat.color }">{{ stat.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 차트 영역 -->
        <div class="grid grid-cols-1 xl:grid-cols-2 gap-8">
          <!-- 주간 상담 & 방송 현황 -->
          <div class="bg-white rounded-2xl shadow-xl p-8">
            <div class="flex items-center mb-8">
              <span class="text-2xl mr-3">📊</span>
              <h3 class="text-2xl font-bold text-gray-800">주간 상담 & 방송 현황</h3>
            </div>
            <div class="h-80">
              <canvas ref="weeklyChart"></canvas>
            </div>
          </div>

          <!-- 월별 수익 트렌드 -->
          <div class="bg-white rounded-2xl shadow-xl p-8">
            <div class="flex items-center mb-8">
              <span class="text-2xl mr-3">💰</span>
              <h3 class="text-2xl font-bold text-gray-800">월별 수익 트렌드</h3>
            </div>
            <div class="h-80">
              <canvas ref="revenueChart"></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.bg-gradient-to-br {
  background: linear-gradient(to bottom right, #f8fafc, #dbeafe, #e0e7ff);
}

.hover\:shadow-2xl:hover {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.transition-all {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.transform {
  transform: translateZ(0);
}

.hover\:-translate-y-1:hover {
  transform: translateY(-0.25rem);
}

.font-mono {
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
}

/* 애니메이션 */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 커스텀 스크롤바 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 반응형 개선 */
@media (max-width: 640px) {
  .text-3xl {
    font-size: 1.5rem;
  }

  .text-2xl {
    font-size: 1.25rem;
  }

  .p-8 {
    padding: 1.5rem;
  }

  .p-6 {
    padding: 1rem;
  }
}
</style>