<script setup>
import { defineProps, toRefs, ref, computed, onMounted } from 'vue'
import http from '@/libs/HttpRequester'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import ProductCard from "@/components/common/ProductCard.vue"
import CardTable   from "@/components/table/CardTable.vue"

// 라우터가 계산해서 뿌려주는 값
const props = defineProps({
  keyword:     { type: String, required: true },
  category:    { type: [String, Number], default: null },
  onlyLawyers: { type: Boolean, default: false }
})

// props 안의 값들을 ref 형태로 분해
const { keyword, category, onlyLawyers } = toRefs(props)

const results = ref({
  lawyers:   [],
  qnas:      [],
  templates: []
})

async function fetchResults() {
  const params = { q: keyword.value }
  if (category.value != null) params.category = category.value
  if (onlyLawyers.value) params.onlyLawyers = true

  try {
    const res = await http.get('/api/public/search',  params)
    results.value = res.data
    console.log("🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴 \n", results.value)
  } catch (e) {
    console.error('검색 실패:', e)
    results.value = { lawyers: [], qnas: [], templates: [] }
  }
}

onMounted(fetchResults)

// 1) 첫 번째 변호사에게만 featured=true 주기
const displayedLawyers = computed(() =>
    results.value.lawyers.map((lawyer, idx) => ({
      ...lawyer,
      featured: idx === 0
    }))
)

// 2) 나머지 섹션들은 그대로 배열 바인딩
const qnaList      = computed(() => results.value.qnas)
const templateList = computed(() => results.value.templates)

</script>

<template>
  <ClientFrame>
    <div class="search-results-header mb-10">
      <h1 class="fs-4 fw-bold">"{{ keyword }}" 검색 결과</h1>
    </div>

    <div class="lawyers-section mb-5">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="fs-5 fw-bold mb-0">추천 변호사 리스트</h2>
        <a href="#" class="text-primary small" @click.prevent="viewAllLawyers">전체보기 →</a>
      </div>

      <div class="row g-4">
        <div
            v-for="lawyer in displayedLawyers"
            :key="lawyer.no"
            class="col-12 col-md-6 col-lg-4"
        >
          <div class="card h-100 shadow-sm position-relative p-3 border border-light"
               :class="{ 'border-primary': lawyer.featured }"
               @click="selectLawyer(lawyer)"
               style="cursor: pointer;">
            <!-- 추천 뱃지 -->
            <span
                v-if="lawyer.featured"
                class="badge bg-primary position-absolute top-0 end-0 m-2"
            >추천</span>

            <!-- 프로필 영역 -->
            <div class="d-flex align-items-center mb-2">
              <div
                  class="rounded-circle d-flex align-items-center justify-content-center me-3"
                  style="width: 48px; height: 48px; background: #6f42c1; color: #fff; font-weight: bold;"
              >
                {{ lawyer.name.charAt(0) }}
              </div>
              <div>
                <h5 class="fw-bold mb-0">{{ lawyer.name }}</h5>
                <small class="text-muted">{{ lawyer.specialty || '전문 분야 미입력' }}</small>
              </div>
            </div>

            <!-- 통계 -->
            <div class="mb-2 small text-muted">
              📋 {{ lawyer.point || 0 }}건 &nbsp; ⭐ {{ lawyer.consultPrice || 0 }} 원
            </div>

            <!-- 소개글 -->
            <p class="mb-0 text-truncate-2">
              {{ lawyer.lawyerIntro || '소개글이 없습니다.' }}
            </p>
          </div>
        </div>
      </div>
    </div>

<!--    &lt;!&ndash; 3) Templates &ndash;&gt;-->
<!--    <div class="card mb-4 p-4">-->
<!--      <h5>{{ /* e.g. results.value.lawyers[0].name */ }} 변호사의 법률 템플릿</h5>-->
<!--      <div v-if="templateList.length" class="row g-4 row-cols-lg-5 row-cols-2 row-cols-md-3">-->
<!--        <div-->
<!--            class="col-md-3 mb-4"-->
<!--            v-for="product in templateList"-->
<!--            :key="product.no"-->
<!--        >-->
<!--          <ProductCard-->
<!--              :no="product.no"-->
<!--              :imageUrl="product.imageUrl"-->
<!--              :title="product.title"-->
<!--              :originalPrice="product.originalPrice"-->
<!--              :discountPercent="product.discountPercent"-->
<!--              :discountedPrice="product.discountedPrice"-->
<!--          />-->
<!--        </div>-->
<!--      </div>-->
<!--      <div v-else class="text-center text-muted py-5">-->
<!--        등록된 상품이 없습니다.-->
<!--      </div>-->
<!--    </div>-->

<!--    &lt;!&ndash; 4) QNA &ndash;&gt;-->
<!--    <div class="card mb-4 p-4">-->
<!--      <h5>{{ /* 동일 */ }} 변호사가 답변한 상담글</h5>-->
<!--      <CardTable-->
<!--          v-if="qnaList.length"-->
<!--          :List="qnaList"-->
<!--          :maxLines="4"-->
<!--      />-->
<!--      <div v-else class="text-center text-muted py-5">-->
<!--        답변한 게시글이 없습니다.-->
<!--      </div>-->
<!--    </div>-->
  </ClientFrame>
</template>

<style scoped>
.lawyer-card { position: relative; /* … */ }
.lawyer-card.featured { border-color: gold; }
.featured-badge {
  position: absolute;
  top: 0.5rem; right: 0.5rem;
  background: #ffca28;
  color: #fff;
  padding: 0.2rem 0.5rem;
  border-radius: 0.2rem;
  font-size: 0.8rem;
}
</style>