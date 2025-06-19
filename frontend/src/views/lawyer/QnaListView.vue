<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

import { fetchBoardList } from '@/service/boardService.js'
import { ref, watch, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const page = ref(1)
const size = ref(10)
const list = ref([])
const totalElements = ref(null)
const totalPages = ref(null)
const isLoading = ref(false)
const error = ref(null)

const pagesInGroup = 10
const startPage = computed(() => Math.floor((page.value - 1) / pagesInGroup) * pagesInGroup + 1)
const pageNumbers = computed(() => {
  if (totalPages.value == null) return []
  const endPage = Math.min(startPage.value + pagesInGroup - 1, totalPages.value)
  return Array.from({ length: endPage - startPage.value + 1 }, (_, i) => startPage.value + i)
})

// 프론트 코드 상단에 미리 매핑
const categoryMap = {
  1: '사고 발생/처리',
  2: '중대사고·형사처벌',
  3: '음주·무면허 운전',
  4: '보험·행정처분',
  5: '과실 분쟁',
  6: '차량 외 사고'
}

function gotoPage(p) {
  if (p < 1 || (totalPages.value != null && p > totalPages.value)) return
  page.value = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function loadList() {
  isLoading.value = true
  error.value = null
  try {
    const res = await fetchBoardList(page.value, size.value) // ← 나중에 lawyer 전용 API로 교체
    const data = res.data
    console.log('🟢 변호사용 답변가능 목록 응답:', data)

    list.value = Array.isArray(data.content) ? data.content : (Array.isArray(data) ? data : [])
    if (data.totalPages != null) {
      totalPages.value = data.totalPages
    } else if (data.totalElements != null) {
      totalElements.value = data.totalElements
      totalPages.value = Math.ceil(totalElements.value / size.value)
    }
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

onMounted(() => loadList())
watch(page, () => loadList())
</script>

<template>
  <LawyerFrame>
    <a href="/">메인 화면 이동하기</a>
    <br>
    <br>
    <div class="container py-0">
      <h2>Q&A 답변하기</h2>
    </div>
    <!-- 답변 가능한 Q&A 목록 섹션 -->
    <section class="qa-section py-5 px-3 px-lg-5 mt-0">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="fw-bold fs-3">답변 가능한 상담글</h2>
          <p class="text-muted mb-0">답변 가능한 Q&A를 확인하고, 답변을 작성해주세요.</p>
        </div>
      </div>

      <div v-if="!isLoading && list.length === 0" class="text-center text-muted py-5">
        현재 답변 가능한 글이 없습니다.
      </div>

      <div v-else class="qa-list">
        <div
            v-for="qa in list"
            :key="qa.no"
            class="qa-card bg-white rounded shadow-sm p-4 mb-3"
            @click="router.push({ name: 'ReplyRegister', params: { id: qa.no } })"
            style="cursor: pointer;"
        >
          <small class="badge bg-light text-secondary mb-1">
            {{ categoryMap[qa.categoryNo] || '미지정' }}
          </small>
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
    </section>


  </LawyerFrame>
</template>