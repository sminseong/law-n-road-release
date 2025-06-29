<script setup>
import { defineProps, toRefs, ref, computed, onMounted } from 'vue'
import http from '@/libs/HttpRequester'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import ProductCard from "@/components/common/ProductCard.vue"
import CardTable   from "@/components/table/CardTable.vue"
import AdBannerPair from "@/components/common/SubBannerSlider.vue";

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

// 서브 베너
const banners = ref([])

// 변호사 프로필 슬라이더
const sliderRef = ref(null)

async function fetchResults() {
  const params = { q: keyword.value }
  if (category.value != null) params.category = category.value
  if (onlyLawyers.value) params.onlyLawyers = true

  try {
    const res = await http.get('/api/public/search',  params)
    results.value = res.data
    console.log(results.value)
  } catch (e) {
    console.error('검색 실패:', e)
    results.value = { lawyers: [], qnas: [], templates: [] }
  }
}

onMounted(async () => {
  await fetchResults()

  try {
    const res4 = await http.get('/api/public/main/sub-banners')
    console.log(res4.data)
    banners.value = res4.data;
  } catch (e) {
    console.error('서브 베너 조회 실패:', e)
  }

  window.tns?.({
    container: sliderRef.value,
    items: 1, // 기본값 (모바일)
    slideBy: 1,
    nav: false,
    controls: false,
    mouseDrag: true,
    autoplay: true,
    autoplayTimeout: 5000,
    autoplayButtonOutput: false,
    responsive: {
      768: { items: 2 }, // 768px 이상이면 2개
      992: { items: 3 }  // 992px 이상이면 3개
    }
  })
})

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
      <h1 class="fs-3 fw-bold">"{{ keyword }}" 검색 결과</h1>
    </div>

    <div class="lawyers-section mb-7">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="fs-5 fw-bold mb-0">추천 변호사 리스트</h2>
<!--        <a href="#" class="text-primary small" @click.prevent="viewAllLawyers">전체보기 →</a>-->
      </div>

      <div class="tiny-slider pb-2" ref="sliderRef">
        <div
            class="item px-2"
            v-for="lawyer in displayedLawyers"
            :key="lawyer.no"
        >
          <div
              class="card h-100 shadow-sm position-relative p-3"
              :class="{ featured: lawyer.featured }"
              @click="selectLawyer(lawyer)"
              style="cursor: pointer;"
          >
            <span
                v-if="lawyer.featured"
                class="badge bg-primary text-white position-absolute top-0 end-0 m-2"
            >추천</span>

            <div class="d-flex align-items-center mb-2">
              <div
                  class="rounded-circle d-flex align-items-center justify-content-center me-3 overflow-hidden"
                  style="width: 48px; height: 48px;"
              >
                <template v-if="lawyer.profile">
                  <img
                      :src="lawyer.profile"
                      alt="변호사 프로필"
                      style="width: 100%; height: 100%; object-fit: cover;"
                  />
                </template>
              </div>
              <div>
                <h5 class="fw-bold mb-0">{{ lawyer.name }} 변호사</h5>
                <small class="text-muted">{{ lawyer.officeName || '사무실 미입력' }}</small>
              </div>
            </div>

            <div class="mb-2 small text-muted">
              📝 {{ lawyer.qnaAnswerCount || 0 }}건 &nbsp;
              📋 {{ lawyer.templateCount || 0 }}건 &nbsp;
              ⭐ {{ lawyer.point || 0 }}점
            </div>

            <p class="mb-0 text-truncate-2">
              {{ lawyer.lawyerIntro || '소개글이 없습니다.' }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 3) Templates -->
    <h5> 추천 법률 템플릿 리스트</h5>
    <div v-if="templateList.length" class="row g-4 row-cols-lg-5 row-cols-2 row-cols-md-3">
      <div
          class="col-md-3 mb-4"
          v-for="product in templateList"
          :key="product.no"
      >
        <ProductCard
            :no="product.no"
            :imageUrl="product.imageUrl"
            :title="product.title"
            :originalPrice="product.originalPrice"
            :discountPercent="product.discountPercent"
            :discountedPrice="product.discountedPrice"
        />
      </div>
    </div>
    <div v-else class="text-center text-muted py-5 mb-5">
      등록된 상품이 없습니다.
    </div>

    <!-- 2) 서브 베너 -->
    <AdBannerPair :banners="banners" />

    <!-- 4) QNA -->
    <h5 class="mb-3">추천 상담글 리스트</h5>
    <CardTable
        v-if="qnaList.length"
        :List="qnaList"
        :maxLines="4"
    />
    <div v-else class="text-center text-muted py-5">
      답변한 게시글이 없습니다.
    </div>
</ClientFrame>
</template>

<style scoped>
.tiny-slider {
  display: flex !important;
  justify-content: flex-start !important;
}

.tiny-slider > div {
  padding: 0 8px;
}

::v-deep(.tns-outer) {
  overflow: visible !important;
}

::v-deep(.tns-outer) .tiny-slider {
  display: flex !important;
  justify-content: flex-start !important;
}

::v-deep(.tiny-slider) {
  margin-left: 10 !important;
  padding-left: 0 !important;
}


.card {
  margin-right: 10px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card.featured {
  border: 2px solid #4c8df1; /* 연한 파랑 */
  box-shadow: 0 0 0 2px rgba(76, 141, 241, 0.25); /* 부드러운 강조 */
}

</style>