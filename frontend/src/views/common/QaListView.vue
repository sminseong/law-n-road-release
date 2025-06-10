<script setup>
import { fetchBoardList } from '@/service/boardService.js'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { ref ,watch ,onMounted, computed} from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 페이징 및 데이터 상태
const page = ref(1)            // 현재 페이지. 기본 1
const size = ref(10)           // 페이지당 항목 수. 기본 10
const list = ref([])           // API로 받아온 게시글 목록
const totalElements = ref(null) // 전체 개수 (백엔드가 제공하면 사용)
const totalPages = ref(null)   // 전체 페이지 수 (백엔드가 제공하거나 계산)
const isLoading = ref(false)   // 로딩 상태
const error = ref(null)        // 오류 상태

// 페이지네이션 그룹 계산 (예: 5개씩 묶음)
const pagesInGroup = 5
const startPage = computed(() => Math.floor((page.value - 1) / pagesInGroup) * pagesInGroup + 1)
const pageNumbers = computed(() => {
  const endPage = Math.min(startPage.value + pagesInGroup - 1, totalPages.value)
  const pages = []
  for (let i = startPage.value; i <= endPage; i++) {
    pages.push(i)
  }
  return pages
})

// 페이지 이동 함수
function gotoPage(p) {
  if (p < 1) return
  if (totalPages.value != null && p > totalPages.value) return
  page.value = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
  // page ref가 바뀌면 watch에서 loadList() 호출되도록 설정했으면 호출됨
}

// 데이터 로드 함수
async function loadList() {
  isLoading.value = true
  error.value = null
  try {
    // API 호출: fetchBoardList가 { content: [...], page, size, totalElements, totalPages } 형태로 반환한다고 가정
    const data = await fetchBoardList(page.value, size.value)
    // list에 할당: content 프로퍼티 확인
    if (data.content && Array.isArray(data.content)) {
      list.value = data.content
    } else if (Array.isArray(data)) {
      // 만약 백엔드가 단순 배열만 반환할 경우
      list.value = data
    } else {
      // 예상치 못한 구조
      list.value = []
    }

    // ✅ 임시 하드코딩
    totalPages.value = 20

    // ✅ 아래는 나중에 백엔드 연결하면 쓰기
    // // totalElements, totalPages 처리
    // if (data.totalElements != null) {
    //   totalElements.value = data.totalElements
    // }
    // if (data.totalPages != null) {
    //   totalPages.value = data.totalPages
    // } else if (totalElements.value != null) {
    //   totalPages.value = Math.ceil(totalElements.value / size.value)
    // } else {
    //   totalPages.value = null
    // }
  } catch (err) {
    console.error('목록 조회 실패', err)
    error.value = err
    list.value = []
    totalElements.value = null
    totalPages.value = null
  } finally {
    isLoading.value = false
  }
}

// 페이지 최초 로딩시 -> 데이터 로드
onMounted(() => {
  loadList()
})

// 페이지 변경 될 때마다 -> 데이터 로드
watch(page, () => {
  loadList()
})

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
            v-for="qa in list"
            :key="qa.no"
            class="qa-card bg-white rounded shadow-sm p-4 mb-3"
            @click="router.push(`/qna/${qa.no}`)"
            style="cursor: pointer;"
        >
          <small class="text-muted">{{ qa.categoryName || '' }}</small>
          <h5 class="fw-semibold mt-1">{{ qa.title }}</h5>
          <p class="text-muted mb-2">{{ qa.summary || qa.content }}</p>
          <small class="text-secondary">{{ qa.createdAt ? new Date(qa.createdAt).toLocaleString() : '' }}</small>
        </div>
      </div>

      <nav v-if="totalPages > 1" class="pagination-wrapper mt-4 d-flex justify-content-center align-items-center">
        <button v-if="startPage > 1" class="btn btn-link p-0 me-3" @click="gotoPage(startPage - pagesInGroup)">
          <i class="lc lc-chevron-left"></i>
          <span class="lt-desktop">이전 {{ pagesInGroup }}페이지</span>
        </button>

        <div class="pagination-button-wrap">
          <button
              v-for="num in pageNumbers"
              :key="num"
              class="pagination-button mx-1 px-2 py-1 border rounded"
              :class="{ on: page === num }"
              @click="gotoPage(num)"
          >
            {{ num }}
          </button>
        </div>

        <button
            v-if="startPage + pagesInGroup <= totalPages"
            class="btn btn-link p-0 ms-3"
            @click="gotoPage(startPage + pagesInGroup)"
        >
          <span class="lt-desktop">다음 {{ pagesInGroup }}페이지</span>
          <i class="lc lc-chevron-right"></i>
        </button>
      </nav>

<!--      <nav-->
<!--          v-if="totalPages == null ? list.length === size : totalPages > 1"-->
<!--          class="pagination-wrapper mt-4 d-flex justify-content-center align-items-center"-->
<!--      >-->
<!--        <button-->
<!--            v-if="page > 1"-->
<!--            class="btn btn-link p-0 me-3"-->
<!--            @click="gotoPage(page - 1)"-->
<!--        >-->
<!--          <i class="lc lc-chevron-left"></i>-->
<!--          <span class="lt-desktop">이전</span>-->
<!--        </button>-->

<!--        <div class="pagination-button-wrap">-->
<!--          <button-->
<!--              v-for="p in (totalPages != null ? totalPages : (list.length === size ? page + 1 : page))"-->
<!--              :key="p"-->
<!--              class="pagination-button mx-1 px-2 py-1 border rounded"-->
<!--              :class="{ on: page === p }"-->
<!--              @click="gotoPage(p)"-->
<!--          >-->
<!--            {{ p }}-->
<!--          </button>-->
<!--        </div>-->

<!--        <button-->
<!--            v-if="totalPages == null ? list.length === size : page < totalPages"-->
<!--            class="btn btn-link p-0 ms-3"-->
<!--            @click="gotoPage(page + 1)"-->
<!--        >-->
<!--          <span class="lt-desktop">다음</span>-->
<!--          <i class="lc lc-chevron-right"></i>-->
<!--        </button>-->
<!--      </nav>-->
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