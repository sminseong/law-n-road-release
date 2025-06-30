<script setup>
import {ref, onMounted, onUnmounted, computed} from 'vue'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";
import { fetchTodaySchedule, fetchTomorrowConsultationRequests, fetchMonthlySalesRevenue } from '@/service/dashboardService.js'

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
import { useLawyerStore } from '@/stores/lawyer'
import {getUserNo} from "@/service/authService.js";

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

const store = useLawyerStore()
const info = computed(() => store.lawyerInfo)

// 반응형 데이터
const currentTime = ref('')
const loading = ref(false)
const lawyerInfo = ref({
  name: info.value.name,
  id: getUserNo()
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
        clientName: item.clientName || null,
        clientPhone: item.clientPhone || null
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

const loadMonthlySalesRevenue = async () => {
  dashboardStats.value[2].loading = true  // “이달의 수익” 카드 인덱스가 2번이라 가정
  try {
    console.log('월별 판매 수익 로드 시작')

    const response = await fetchMonthlySalesRevenue()
    console.log('월별 판매 수익 API 응답:', response)

    if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
      // 1) 차트 데이터 준비
      const months   = response.data.map(d => d.month)
      const revenues = response.data.map(d => Math.round(d.totalAmount / 10000))

      // 2) 차트 그리기
      createRevenueChart({ months, revenues })

      // 3) “이달의 수익” 카드 업데이트
      const currentMonth = new Date().toISOString().slice(0,7) // 'YYYY-MM'
      const thisMonthData = response.data.find(d => d.month === currentMonth)
      dashboardStats.value[2].value = thisMonthData
          ? `${Math.round(thisMonthData.totalAmount/10000)}만원`
          : '0만원'

      console.log('월별 판매 수익 데이터 매핑 완료')
    } else {
      console.log('월별 판매 수익 데이터 없음')
      // 차트 클리어 혹은 빈 데이터 처리
      createRevenueChart({ months: [], revenues: [] })
      dashboardStats.value[2].value = '0만원'
    }
  } catch (error) {
    console.error('월별 판매 수익 로딩 실패:', error)
    // 실패 시에도 빈 차트
    createRevenueChart({ months: [], revenues: [] })
    dashboardStats.value[2].value = '데이터 없음'
  } finally {
    dashboardStats.value[2].loading = false
  }
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)

  setTimeout(() => {
    createWeeklyChart()
  }, 100)

  loadTodaySchedule()
  loadTomorrowConsultationRequests()
  loadMonthlySalesRevenue()
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
    <div class="container py-4">

      <!-- 헤더 -->
      <div class="card mb-4">
        <div class="card-body d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">안녕하세요, {{ info.name }} 변호사님</h5>
          <div class="text-end">
            <small class="text-muted">현재 시간은</small>
            <div class="h5 mb-0">{{ currentTime }}</div>
          </div>
        </div>
      </div>

      <!-- 오늘 일정 -->
      <div class="card mb-4">
        <div class="card-header d-flex align-items-center">
          <i class="bi bi-calendar3 me-2"></i>
          <strong>오늘 일정</strong>
        </div>
        <div class="card-body p-3">
          <div class="row row-cols-1 gy-1">
            <div
                v-for="(s, i) in todaySchedule"
                :key="i"
                class="col"
            >
              <!-- border-0 으로 모든 테두리 제거 후 border-bottom 만 적용 -->
              <div class="d-flex align-items-center py-1 px-2 border-0 border-bottom">
                <i class="bi bi-person-fill text-primary fs-5 me-2"></i>
                <div>
                  <div class="small text-secondary">{{ s.time }}</div>
                  <div>{{ s.event }} ( {{ s.clientPhone }} )</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 주요 지표 카드 -->
      <div class="row row-cols-2 row-cols-md-4 g-3 mb-4">
        <div
            v-for="stat in dashboardStats"
            :key="stat.title"
            class="col d-flex"
        >
          <!-- border-start 제거, 대신 border 로 사방 테두리 -->
          <div
              class="card flex-fill border"
              :style="{ borderColor: stat.color }"
          >
            <div class="card-body">
              <h6 class="card-subtitle mb-2 text-muted">{{ stat.title }}</h6>
              <div class="d-flex align-items-center">
                <span class="fs-4 me-2">{{ stat.icon }}</span>
                <h5
                    class="mb-0"
                    :class="stat.value === '데이터 없음' ? 'text-secondary' : ''"
                    :style="stat.value !== '데이터 없음' ? { color: stat.color } : {}"
                >
                  {{ stat.value }}
                </h5>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 영역 -->
      <div class="row gy-4">
        <div class="col-lg-6">
          <div class="card h-100">
            <div class="card-header d-flex align-items-center">
              <i class="bi bi-bar-chart me-2"></i>
              <strong>주간 상담 & 방송 현황</strong>
            </div>
            <div class="card-body">
              <canvas ref="weeklyChart" class="w-100" style="height:300px;"></canvas>
            </div>
          </div>
        </div>
        <div class="col-lg-6">
          <div class="card h-100">
            <div class="card-header d-flex align-items-center">
              <i class="bi bi-graph-up me-2"></i>
              <strong>월별 수익 트렌드</strong>
            </div>
            <div class="card-body">
              <canvas ref="revenueChart" class="w-100" style="height:300px;"></canvas>
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