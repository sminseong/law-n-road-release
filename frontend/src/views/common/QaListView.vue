<script setup>
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// 데모용 더미 데이터 300개
const dummyTrafficList = []
for (let i = 1; i <= 300; i++) {
  dummyTrafficList.push({
    no: i,
    category: '교통사고 · 보상',
    title: `교통사고 상담 사례 #${i}`,
    content: `교통사고 내용 예시입니다. (${i}번째 글)`,
    date: `2025.05.${String(i % 30 + 1).padStart(2, '0')}`,
  })
}

// 전체 Q&A 목록 (실제 사용 시 API 호출 후 할당)
const allQaList = ref(dummyTrafficList)

//  “교통사고” 카테고리 필터
const filteredList = computed(() =>
    allQaList.value.filter(qa => qa.category.startsWith('교통사고'))
)

// 페이징을 위한 상태 값들
const itemsPerPage = ref(10) // 한 페이지에 보여줄 아이템 수
const pagesInGroup = ref(10) // 한 그룹에 표시할 페이지 버튼 수
const curPage = ref(1) // 현재 페이지 번호

// 전체 아이템 수 계산
const totalItems = computed(() => filteredList.value.length)
// 전체 페이지 수 계산
const pages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))
// 현재 페이지 그룹의 시작 인덱스 계산(0-based)
const startPage = computed(() =>
    Math.floor((curPage.value - 1) / pagesInGroup.value) * pagesInGroup.value
)
// 화면에 보여줄 페이지 분량 데이터 (slice)
const paginatedList = computed(() => {
  const start = (curPage.value - 1) * itemsPerPage.value
  return filteredList.value.slice(start, start + itemsPerPage.value)
})
// 현재 그룹 내 페이지 번호 배열 생성
const pageNumbers = computed(() => {
  const nums = []
  const end = Math.min(startPage.value + pagesInGroup.value, pages.value)
  for (let i = startPage.value + 1; i <= end; i++) nums.push(i)
  return nums
})
// 페이지 이동 함수
function gotoPage(p) {
  if (p < 1 || p > pages.value) return
  curPage.value = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const router = useRouter()
</script>

<template>
  <ClientFrame>
    <section class="qa-section py-5 px-3 px-lg-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold fs-3">전체 상담사례 목록</h2>
        <router-link to="/client/qna/register" class="btn btn-outline-primary btn-sm">
          상담글 쓰기
        </router-link>
      </div>

      <div class="qa-list">
        <div
            v-for="qa in paginatedList"
            :key="qa.no"
            class="qa-card bg-white rounded shadow-sm p-4 mb-3"
            @click="router.push(`/qna/${qa.no}`)"
            style="cursor: pointer;"
        >
          <small class="text-muted">{{ qa.category }}</small>
          <h5 class="fw-semibold mt-1">{{ qa.title }}</h5>
          <p class="text-muted mb-2">{{ qa.content }}</p>
          <small class="text-secondary">{{ qa.date }}</small>
        </div>
      </div>

      <nav class="pagination-wrapper mt-4 d-flex justify-content-center align-items-center">
        <button
            v-if="startPage > 0"
            class="btn btn-link p-0 me-3"
            @click="gotoPage(startPage)"
        >
          <i class="lc lc-chevron-left"></i>
          <span class="lt-desktop">이전 {{ pagesInGroup }}페이지</span>
        </button>

        <div class="pagination-button-wrap">
          <button v-for="num in pageNumbers" :key="num"
              class="pagination-button mx-1 px-2 py-1 border rounded"
              :class="{ on: curPage === num }" @click="gotoPage(num)">
            {{ num }}
          </button>
        </div>

        <button v-if="pages > startPage + pagesInGroup" class="btn btn-link p-0 ms-3"
            @click="gotoPage(startPage + pagesInGroup + 1)">
          <span class="lt-desktop">다음 {{ pagesInGroup }}페이지</span>
          <i class="lc lc-chevron-right"></i>
        </button>
      </nav>
    </section>
  </ClientFrame>
</template>

<style scoped>
.qa-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

/* 페이지 버튼 기본 */
.pagination-button {
  font-size: 0.9rem;
  color: #495057;
  background: #fff;
  border: 1px solid #dee2e6;
  cursor: pointer;
}
.pagination-button:hover {
  background: #f8f9fa;
}
.pagination-button.on {
  background: #353f8e;
  color: #fff;
  border-color: #353f8e;
  font-weight: bold;
}

/* 숨겨진 텍스트 (모바일) */
.lt-desktop {
  display: none;
}
@media (min-width: 992px) {
  .lt-desktop {
    display: inline;
    margin: 0 0.5rem;
  }
}

/* 버튼 래퍼 */
.pagination-button-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

/* 화살표 아이콘 */
.lc {
  font-size: 1rem;
  vertical-align: middle;
}
</style>