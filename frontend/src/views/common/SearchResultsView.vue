<script setup>
import { defineProps, toRefs, onMounted, ref } from 'vue'
import http from '@/libs/HttpRequester'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";

// 라우터가 계산해서 뿌려주는 값
const props = defineProps({
  keyword:     { type: String, required: true },
  category:    { type: [String, Number], default: null },
  onlyLawyers: { type: Boolean, default: false }
})

// props 안의 값들을 ref 형태로 분해
const { keyword, category, onlyLawyers } = toRefs(props)

const results = ref([])

async function fetchResults() {
  // keyword.value, category.value, onlyLawyers.value 로 사용
  const params = { q: keyword.value }
  if (category.value != null) params.category = category.value
  if (onlyLawyers.value) params.onlyLawyers = true

  try {
    const res = await http.get('/api/public/search',  params)
    results.value = res.data
    console.log("🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴 \n", results.value)
  } catch (e) {
    console.error('검색 실패:', e)
    results.value = []
  }
}

onMounted(fetchResults)
</script>

<template>
  <ClientFrame>
    <ul>
      <li v-for="item in results" :key="item.id">
        {{ item.title }}
      </li>
    </ul>

    <div class="search-results-header">
      <!-- 검색 결과 타이틀 -->
      <div class="search-title-section">
        <h1 class="search-title">
          "{{ keyword }}" 검색 결과
        </h1>
        <p class="search-info">
          총 {{ totalResults }}개의 검색 결과를 찾았습니다.
        </p>
      </div>

      <!-- 전문 변호사 섹션 -->
      <div class="lawyers-section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">전문 변호사</h2>
          </div>
          <a href="#" class="view-all-link" @click.prevent="viewAllLawyers">
            전체보기 →
          </a>
        </div>

        <!-- 변호사 카드 그리드 -->
        <div class="lawyers-grid">
          <div
              v-for="lawyer in displayedLawyers"
              :key="lawyer.id"
              class="lawyer-card"
              :class="{ 'featured': lawyer.featured }"
              @click="selectLawyer(lawyer)"
          >
            <!-- 추천 뱃지 -->
            <div v-if="lawyer.featured" class="featured-badge">추천</div>

            <!-- 변호사 정보 -->
            <div class="lawyer-header">
              <div class="lawyer-avatar">
                {{ lawyer.name.charAt(0) }}
              </div>
              <div class="lawyer-info">
                <h3 class="lawyer-name">{{ lawyer.name }}</h3>
                <p class="lawyer-specialty">{{ lawyer.specialty }}</p>
              </div>
            </div>

            <!-- 변호사 통계 -->
            <div class="lawyer-stats">
            <span class="stat-item">
              📋 {{ lawyer.cases }}건
            </span>
              <span class="stat-item">
              ⭐ {{ lawyer.rating }}건
            </span>
            </div>

            <!-- 변호사 설명 -->
            <p class="lawyer-description">
              {{ lawyer.description }}
            </p>
          </div>

          <!-- 라이브방송 그리드 -->
          <!-- VOD 그리드 -->

          <!-- QNA 그리드 - 키워드 관련 내용 상위 10개 -->

          <!-- 템플릿 그리드 - 키워드 관련 내용 상위 20개 -->

          <!-- 서브베너 그리드 - 2개 (활성/승인/기한내 광고 상품 중 랜덤) -->

        </div>
      </div>
    </div>
  </ClientFrame>
</template>