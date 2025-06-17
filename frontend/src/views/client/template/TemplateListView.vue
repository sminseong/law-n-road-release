<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import http from '@/libs/HttpRequester'
import ProductCard from '@/components/common/ProductCard.vue'
import ClientFrame from "@/components/layout/client/ClientFrame.vue"

// --- 페이징 & 필터 상태 ---
const currentPage    = ref(1)
const totalPages     = ref(1)
const pageSize       = 40

const searchKeyword  = ref('')
const categoryFilter = ref('')
const tagFilter      = ref('')      // 'ai' | 'free' | 'special' | 'file' | ''

const sortOption     = ref('latest') // 'latest' | 'popular' | 'priceAsc' | 'priceDesc'

// 필터 옵션
const categoryOptions = [
  { value: '', label: '전체' },
  { value: 1, label: '사고 발생/처리' },
  { value: 2, label: '중대사고·형사처벌' },
  { value: 3, label: '음주·무면허 운전' },
  { value: 4, label: '보험·행정처분' },
  { value: 5, label: '과실 분쟁' },
  { value: 6, label: '차량 외 사고' },
]

const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'popular', label: '인기순' },
  { value: 'priceAsc', label: '최저가순' },
  { value: 'priceDesc', label: '최고가순' },
]

// 실제 보여줄 한 페이지 아이템
const pageItems = ref([])

// --- 서버 호출 함수 ---
async function fetchPage() {
  const query = {
    page: currentPage.value,
    limit: pageSize,
    sort: sortOption.value,
    keyword: searchKeyword.value || null,
    categoryNo: categoryFilter.value || null,
    type: tagFilter.value || null
  }

  try {
    const res = await http.get('/api/public/templates', query)

    // 응답 형태: { templates: [...], totalPages: n, totalCount: m }
    pageItems.value = res.data.templates.map(item => ({
      no: item.no,
      imageUrl: item.thumbnailPath,
      title: item.name,
      originalPrice: item.price,
      discountPercent: item.discountRate ?? 0,
      discountedPrice: Math.floor(item.price * (1 - (item.discountRate ?? 0) / 100))
    }))
    totalPages.value = res.data.totalPages
  } catch (e) {
    console.error('페이지 로드 실패:', e)
  }
}

// 최초 로드
onMounted(fetchPage)

// 페이지 번호가 바뀌면 재호출
watch(currentPage, fetchPage)

// 검색·필터·정렬이 바뀌면 1페이지로 리셋 후 재호출
watch([searchKeyword, categoryFilter, tagFilter, sortOption], () => {
  currentPage.value = 1
  fetchPage()
})

// Pagination group logic
const pageGroup = computed(() => {
  const groupSize = 5
  const gIndex = Math.floor((currentPage.value - 1) / groupSize)
  const start = gIndex * groupSize + 1
  const end = Math.min(start + groupSize - 1, totalPages.value)
  const pages = Array.from({ length: end - start + 1 }, (_, i) => start + i)
  return {
    pages,
    hasPrevGroup: start > 1,
    hasNextGroup: end < totalPages.value,
    prevPage: start - 1,
    nextPage: end + 1
  }
})

function changePage(page) {
  if (page >= 1 && page <= totalPages.value && page !== currentPage.value) {
    currentPage.value = page
  }
}
</script>

<template>
  <ClientFrame>
    <div class="container py-5">

      <!-- 전체 상품 보기 제목 -->
      <div class="row mb-3">
        <div class="col-12">
          <h4 class="fw-bold">전체 상품 보기</h4>
        </div>
      </div>

      <!-- 필터 카드 -->
      <div class="card bg-white shadow-sm p-4 mb-4">
        <div class="row align-items-end gx-3">
          <!-- 좌측: 검색창 -->
          <div class="col-md-6">
            <div class="mb-0">
              <label for="searchKeyword" class="form-label">검색</label>
              <input
                  id="searchKeyword"
                  v-model="searchKeyword"
                  type="text"
                  class="form-control"
                  placeholder="템플릿명을 입력하세요"
              />
            </div>
          </div>

          <!-- 우측: 유형, 카테고리, 정렬 -->
          <div class="col-md-6">
            <div class="d-flex flex-wrap justify-content-end gap-3">
              <!-- 유형 -->
              <div class="mb-0">
                <label for="tagFilter" class="form-label">유형</label>
                <select
                    id="tagFilter"
                    v-model="tagFilter"
                    class="form-select"
                >
                  <option value="">전체</option>
                  <option value="free">무료 템플릿</option>
                  <option value="special">특가 템플릿</option>
                  <option value="ai">AI 생성형 템플릿</option>
                  <option value="file">문서 기반 템플릿</option>
                </select>
              </div>

              <!-- 카테고리 -->
              <div class="mb-0">
                <label for="categoryFilter" class="form-label">카테고리</label>
                <select
                    id="categoryFilter"
                    v-model.number="categoryFilter"
                    class="form-select"
                >
                  <option
                      v-for="c in categoryOptions"
                      :key="c.value"
                      :value="c.value"
                  >{{ c.label }}</option>
                </select>
              </div>

              <!-- 정렬 -->
              <div class="mb-0">
                <label for="sortOption" class="form-label">정렬</label>
                <select
                    id="sortOption"
                    v-model="sortOption"
                    class="form-select"
                >
                  <option
                      v-for="s in sortOptions"
                      :key="s.value"
                      :value="s.value"
                  >{{ s.label }}</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 카드 그리드 (5개씩 4줄 = 20개) -->
          <div class="row g-4 row-cols-lg-5 row-cols-md-4 row-cols-2">
            <div v-for="item in pageItems" :key="item.no" class="col">
              <ProductCard
                  :no="item.no"
                  :imageUrl="item.imageUrl"
                  :title="item.title"
                  :originalPrice="item.originalPrice"
                  :discountPercent="item.discountPercent"
                  :discountedPrice="item.discountedPrice"
              />
            </div>
          </div>

          <!-- 페이지네이션 -->
          <div class="d-flex justify-content-center mt-4">
            <ul class="pagination">
              <!-- 이전 그룹 버튼 -->
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
              </li>
              <li class="page-item" :class="{ disabled: !pageGroup.hasPrevGroup }">
                <a class="page-link" @click="changePage(pageGroup.prevPage)">«</a>
              </li>
              <!-- 현재 그룹의 페이지 번호들 -->
              <li
                  v-for="p in pageGroup.pages"
                  :key="p"
                  class="page-item"
                  :class="{ active: currentPage === p }"
              >
                <a class="page-link" @click="changePage(p)">{{ p }}</a>
              </li>
              <!-- 다음 그룹 버튼 -->
              <li class="page-item" :class="{ disabled: !pageGroup.hasNextGroup }">
                <a class="page-link" @click="changePage(pageGroup.nextPage)">»</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.page-link {
  cursor: pointer;
}
</style>