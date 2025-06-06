<script setup>
import UserFrame from '@/components/layout/User/UserFrame.vue'
import { ref, computed } from 'vue'

// ─── 1) “교통사고” 카테고리로만 이루어진 예시 데이터 300개 생성 ───
// 실제로는 API로 받아온 데이터를 filter만 해주시면 됩니다.
// 아래 코드는 데모용으로, “교통사고 · 보상” 카테고리만 300개 만듭니다.
const dummyTrafficList = []
for (let i = 1; i <= 300; i++) {
  dummyTrafficList.push({
    id: i,
    category: '교통사고 · 보상',
    title: `교통사고 상담 사례 #${i}`,
    content: `교통사고 내용 예시입니다. (${i}번째 글)`,
    date: `2025.05.${String(i % 30 + 1).padStart(2, '0')}`, // 예시 날짜
  })
}

// ─── 2) 전체 Q&A 리스트 (API 호출 결과가 있다고 가정) ───
// 여기서는 demo 목적으로 dummyTrafficList만 사용합니다.
// 실제로는 API로부터 전체 리스트를 받아온 뒤, 아래처럼 필터링만 해주시면 됩니다.
const allQaList = ref(dummyTrafficList)

// ─── 3) “교통사고” 카테고리만 필터링 (필요시 다른 키워드로 수정) ───
const filteredList = computed(() => {
  return allQaList.value.filter((qa) =>
      qa.category.startsWith('교통사고')
  )
})

// ─── 4) 페이징 관련 상태 ───
const itemsPerPage = ref(20)    // 한 페이지에 카드 5개씩
const pagesInGroup = ref(10)   // 한 그룹에 최대 10개 페이지 버튼
const curPage = ref(1)         // 현재 페이지 (1부터 시작)

// ─── 5) 계산된 값 (computed) ───
// 필터링된 전체 아이템 개수
const totalItems = computed(() => filteredList.value.length)

// 전체 페이지 수 → Math.ceil(필터링된 개수 / 한 페이지당 개수)
const pages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))

// 현재 페이지(curPage)가 속한 “페이지 그룹”의 시작(0-based)
const startPage = computed(() => {
  return Math.floor((curPage.value - 1) / pagesInGroup.value) * pagesInGroup.value
})

// 화면에 렌더링할 “현재 페이지” 분량 데이터
const paginatedList = computed(() => {
  const startIdx = (curPage.value - 1) * itemsPerPage.value
  const endIdx = startIdx + itemsPerPage.value
  return filteredList.value.slice(startIdx, endIdx)
})

// 현재 그룹에 표시할 페이지 번호 배열 (1-based)
const pageNumbers = computed(() => {
  const arr = []
  // 그룹 끝 번호 = min(startPage + pagesInGroup, 전체 pages)
  const endPageInGroup = Math.min(startPage.value + pagesInGroup.value, pages.value)
  for (let p = startPage.value + 1; p <= endPageInGroup; p++) {
    arr.push(p)
  }
  return arr
})

// 페이지 이동 함수
function gotoPage(page) {
  if (page < 1 || page > pages.value) return
  curPage.value = page
  // (선택) 페이지 이동 후 스크롤 최상단으로 올리고 싶으면 주석 해제
  // window.scrollTo({ top: 0, behavior: 'smooth' })
}

</script>

<template>
  <UserFrame>
    <div class="qa-section py-5 px-3 px-lg-5">
      <!-- 타이틀 + 글쓰기 버튼 -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold fs-3">전체 상담사례 목록</h2>
        <button class="btn btn-outline-primary btn-sm">상담글 쓰기</button>
      </div>

      <!-- ─── 6) paginatedList로 “교통사고 카테고리” 중 한 페이지 분량만 렌더링 ─── -->
      <div class="qa-list">
        <div
            v-for="qa in paginatedList"
            :key="qa.id"
            class="qa-card bg-white rounded shadow-sm p-4 mb-3"
        >
          <!-- 카테고리 -->
          <div class="text-muted small mb-1">{{ qa.category }}</div>

          <!-- 제목 + 날짜 -->
          <div class="d-flex justify-content-between align-items-start mb-2">
            <h5 class="fw-semibold mb-0">{{ qa.title }}</h5>
          </div>

          <!-- 내용 미리보기 -->
          <p class="text-muted mb-2">{{ qa.content }}</p>
          <small class="text-muted">{{ qa.date }}</small>
        </div>
      </div>

      <!-- ─── 7) 로톡 스타일 “페이지 그룹 페이징 UI” (총 11페이지까지 표시) ─── -->
      <div
          class="pagination-wrapper mt-4 d-flex justify-content-center align-items-center"
      >
        <!-- “이전 10페이지” 버튼 (startPage > 0일 때만 보임) -->
        <div
            v-if="startPage > 0"
            class="prev me-3"
            @click="gotoPage(startPage)"
            style="cursor: pointer;"
        >
          <i class="lc lc-chevron-left"></i>
          <span class="lt-desktop">이전 {{ pagesInGroup }}페이지</span>
        </div>

        <!-- 실제 페이지 번호 버튼 묶음 -->
        <div class="pagination-button-wrap">
          <a
              v-for="page in pageNumbers"
              :key="page"
              href="javascript:void(0)"
              class="pagination-button mx-1 px-2 py-1 border rounded"
              :class="{ on: curPage === page }"
              @click="gotoPage(page)"
          >
            {{ page }}
          </a>
        </div>

        <!-- “다음 10페이지” 버튼 (더 많은 페이지가 있을 때만 보임) -->
        <div
            v-if="pages > startPage + pagesInGroup"
            class="next ms-3"
            @click="gotoPage(startPage + pagesInGroup + 1)"
            style="cursor: pointer;"
        >
          <span class="lt-desktop">다음 {{ pagesInGroup }}페이지</span>
          <i class="lc lc-chevron-right"></i>
        </div>
      </div>
    </div>
  </UserFrame>
</template>

<style scoped>
/* ─── 페이지 버튼 기본 스타일 ─── */
.pagination-button {
  font-size: 0.9rem;
  color: #495057;
  background-color: white;
  border: 1px solid #dee2e6;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}
.pagination-button:hover {
  background-color: #f8f9fa;
}
/* 현재 페이지(on) 강조 스타일 */
.pagination-button.on {
  background-color: #353f8e;
  color: #ffffff;
  border-color:  #353f8e;
  font-weight: bold;
}

/* “이전 x페이지” / “다음 x페이지” 텍스트: 모바일에서는 숨기고, ≥992px 이상에서만 보이게 */
.lt-desktop {
  display: none;
}
@media (min-width: 992px) {
  .lt-desktop {
    display: inline;
    margin: 0 0.5rem;
    font-size: 0.9rem;
    color: #495057;
  }
}
/* 화살표(아이콘) 크기 */
.lc {
  font-size: 1rem;
  vertical-align: middle;
  color: #495057;
}

/* 페이지 버튼 래퍼: 버튼 간 간격 (gap)을 주고, 여러 줄도 가능하게 설정 */
.pagination-button-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

/* 카드 호버 시 그림자 효과 (선택사항) */
.qa-list .qa-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
</style>
