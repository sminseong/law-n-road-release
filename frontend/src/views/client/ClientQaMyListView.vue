<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { fetchMyQnaBoards } from '@/service/boardService.js'

const router = useRouter()
const myQnaBoards = ref([])
const page = ref(1)
const size = ref(10)
const pagedList = computed(() => {
  const start = (page.value - 1) * size.value
  const end = start + size.value
  return myQnaBoards.value.slice(start, end)
})

const totalPages = computed(() => Math.ceil(myQnaBoards.value.length / size.value))
const pagesInGroup = 10
const startPage = computed(() => Math.floor((page.value - 1) / pagesInGroup) * pagesInGroup + 1)
const pageNumbers = computed(() => {
  const endPage = Math.min(startPage.value + pagesInGroup - 1, totalPages.value)
  const pages = []
  for (let i = startPage.value; i <= endPage; i++) {
    pages.push(i)
  }
  return pages
})

function gotoPage(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const userNo = localStorage.getItem('no')

onMounted(async () => {
  try {
    const res = await fetchMyQnaBoards(userNo)
    myQnaBoards.value = res.data

  } catch (e) {
    console.error('내 Q&A 목록 조회 실패', e)
  }
})

function goToDetail(boardNo) {
  router.push(`/qna/${boardNo}`)
}
</script>

<template>
  <ClientFrame>
    <section class="qa-section py-5 px-3 px-lg-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold fs-3">내가 작성한 상담글</h2>
      </div>

      <div v-if="myQnaBoards.length === 0" class="text-center text-muted py-5">
        아직 작성한 상담글이 없습니다.
      </div>

      <div v-else>
        <div
            v-for="qa in pagedList"
            :key="qa.boardNo"
            class="qa-card bg-white rounded shadow-sm p-4 mb-3"
            @click="goToDetail(qa.boardNo)"
            style="cursor: pointer"
        >
          <small class="text-muted">{{ qa.categoryName }}</small>
          <h5 class="fw-semibold mt-1">{{ qa.title }}</h5>
          <small class="text-secondary">{{ new Date(qa.incidentDate).toLocaleDateString('ko-KR') }}</small>
        </div>

        <nav
            v-if="totalPages > 1"
            class="pagination-wrapper mt-4 d-flex justify-content-center align-items-center"
        >
          <button
              v-if="startPage > 1"
              class="btn btn-link p-0 me-3"
              @click="gotoPage(startPage - pagesInGroup)"
          >
            <i class="lc lc-chevron-left"></i>
            <span class="lt-desktop">이전</span>
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
            <span class="lt-desktop">다음</span>
            <i class="lc lc-chevron-right"></i>
          </button>
        </nav>
      </div>
    </section>
  </ClientFrame>
</template>

<style scoped>
.qa-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}
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
.lt-desktop {
  display: none;
}
@media (min-width: 992px) {
  .lt-desktop {
    display: inline;
    margin: 0 0.5rem;
  }
}
.pagination-button-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}
</style>