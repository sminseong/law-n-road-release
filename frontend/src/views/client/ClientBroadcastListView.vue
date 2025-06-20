<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { makeApiRequest   } from "@/libs/axios-auth.js";

const router = useRouter()
const broadcasts = ref([])
const hoveredCard = ref(null)

const fetchLiveBroadcasts = async () => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: '/api/client/broadcast/live'
    })
    if (res?.data) {
      broadcasts.value = res.data
    }
  } catch (err) {
    console.error('❌ 방송 목록 불러오기 실패:', err)
  }
}


const goToBroadcast = (broadcastNo) => {
  router.push(`/client/broadcasts/${broadcastNo}`)
}

const formatStartTime = (isoString) => {
  const date = new Date(isoString)
  const MM = String(date.getMonth() + 1).padStart(2, '0')
  const DD = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  return `${MM}-${DD} ${hh}:${mm} 방송시작`
}

onMounted(() => {
  fetchLiveBroadcasts()
})
</script>

<template>
  <ClientFrame>
    <div class="container py-4">
      <h2 class="fs-3 fw-bold mb-4">📺 현재 라이브 방송 중</h2>

      <div class="row g-4">
        <div
            v-for="item in broadcasts"
            :key="item.broadcastNo"
            class="col-12 col-md-6 col-lg-4"
        >
          <div
              class="card h-100 shadow-sm hover-effect"
              @click="goToBroadcast(item.broadcastNo)"
              @mouseenter="hoveredCard = item.broadcastNo"
              @mouseleave="hoveredCard = null"
          >
            <!-- 썸네일 + 뱃지 -->
            <div class="position-relative rounded-top overflow-hidden" style="aspect-ratio: 16 / 9;">
              <img
                  :src="item.thumbnailPath || '/images/default-thumbnail.jpg'"
                  class="w-100 h-100 object-fit-cover"
                  alt="썸네일"
              />

              <!-- LIVE 뱃지 -->
              <div class="position-absolute d-flex align-items-center gap-2" style="top: 0.5rem; left: 0.75rem; z-index: 1;">
                <span class="text-white fw-bold px-2 py-1 rounded-pill small d-flex align-items-center gap-1 live-badge">
                  <span class="blink">🔴</span> LIVE
                </span>
              </div>

              <!-- 시청자 수 뱃지 (hover 시 표시) -->
              <div
                  class="position-absolute top-0 end-0 me-2 mt-2 viewer-count-badge"
                  :class="{ 'visible': hoveredCard === item.broadcastNo }"
              >
                 {{ item.viewerCount }}명 시청 중
              </div>

              <!-- 방송 시작 시간 (hover 시 표시) -->
              <div
                  class="position-absolute bottom-0 start-0 w-100 text-white text-center py-1 small start-time-label"
                  :class="{ 'visible': hoveredCard === item.broadcastNo }"
              >
                {{ formatStartTime(item.startTime) }}
              </div>
            </div>

            <!-- 카드 본문 -->
            <div class="card-body d-flex gap-3">
              <img
                  :src="item.profile || '/images/default-profile.png'"
                  alt="프로필"
                  class="rounded-circle border"
                  style="width: 56px; height: 56px; object-fit: cover;"
              />
              <div class="flex-grow-1">
                <h5 class="mb-1 fs-5 fw-bold text-primary text-wrap">
                  {{ item.title }}
                </h5>
                <p class="mb-2 text-dark fw-semibold" style="font-size: 0.95rem;">
                  {{ item.lawyerName }} 변호사
                </p>
                <div class="d-flex flex-wrap gap-1">
                  <span
                      v-for="(kw, i) in item.keywords"
                      :key="i"
                      class="badge bg-primary bg-opacity-10 text-primary fw-semibold"
                      style="font-size: 0.75rem;"
                  >
                    # {{ kw }}
                  </span>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.card:hover {
  box-shadow: 0 0 0.5rem rgba(0, 0, 0, 0.15);
  transform: scale(1.01);
  transition: all 0.2s;
}

.hover-effect {
  cursor: pointer;
}

.object-fit-cover {
  object-fit: cover;
}

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

.live-badge {
  box-shadow: 0 0 5px rgba(255, 0, 0, 0.6);
  background-color: rgba(220, 53, 69, 0.85);
  backdrop-filter: blur(2px);
}

/* 방송 시작 시간 라벨 */
.start-time-label {
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.5);
  transition: opacity 0.3s ease;
}

.start-time-label.visible {
  opacity: 1;
}

/* 시청자 수 뱃지 (hover 시 표시) */
.viewer-count-badge {
  opacity: 0;
  transition: opacity 0.3s ease;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 4px 10px;
  font-size: 0.75rem;
  border-radius: 1rem;
  font-weight: bold;
  backdrop-filter: blur(2px);
}

.viewer-count-badge.visible {
  opacity: 1;
}
</style>
