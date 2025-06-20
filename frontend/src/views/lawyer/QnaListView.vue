<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

import { fetchBoardList , fetchMyAnswers } from '@/service/boardService.js'
import { ref, watch, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

//  상태 변수들
const page = ref(1)
const size = ref(10)
const list = ref([])
const myAnswers = ref([]) //
const totalElements = ref(null)
const totalPages = ref(null)
const isLoading = ref(false)
const error = ref(null)

// 탭 상태
const selectedTab = ref('answer')  // 'answer' | 'my'

// 카테고리 번호 → 이름 매핑
const categoryMap = {
  1: '사고 발생/처리',
  2: '중대사고·형사처벌',
  3: '음주·무면허 운전',
  4: '보험·행정처분',
  5: '과실 분쟁',
  6: '차량 외 사고'
}

// 페이지네이션 계산
const pagesInGroup = 10
const startPage = computed(() => Math.floor((page.value - 1) / pagesInGroup) * pagesInGroup + 1)
const pageNumbers = computed(() => {
  if (totalPages.value == null) return []
  const endPage = Math.min(startPage.value + pagesInGroup - 1, totalPages.value)
  return Array.from({ length: endPage - startPage.value + 1 }, (_, i) => startPage.value + i)
})

// 페이지 이동
function gotoPage(p) {
  if (p < 1 || (totalPages.value != null && p > totalPages.value)) return
  page.value = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// Q&A 목록 불러오기
const loadAnswerList = async () => {
  isLoading.value = true
  error.value = null
  try {
    const res = await fetchBoardList(page.value, size.value)
    const data = res.data
    console.log('🟢 Q&A 응답:', data)

    list.value = Array.isArray(data.content) ? data.content : []
    totalPages.value = data.totalPages || Math.ceil((data.totalElements || 0) / size.value)
  } catch (err) {
    console.error('Q&A 목록 실패', err)
    error.value = err
    list.value = []
  } finally {
    isLoading.value = false
  }
}
// 내 답변 목록 불러오기
const loadMyAnswerList = async () => {
  isLoading.value = true
  error.value = null
  try {
    console.log(page.value)
    console.log(size.value)
    const res = await fetchMyAnswers(page.value, size.value)
    console.log('🔵 내 답변 API 응답:', res)
    console.log('🔵 응답 데이터:', res.data)
    const data = res.data
    console.log('🟢 내 답변 응답:', data)

    myAnswers.value = Array.isArray(res.data.content) ? res.data.content : []
    totalPages.value = data.totalPages || Math.ceil((data.totalElements || 0) / size.value)
  } catch (err) {
    console.error('내 답변 목록 실패', err)
    error.value = err
    myAnswers.value = []
  } finally {
    isLoading.value = false
  }
}

// 탭 전환 감지
watch(selectedTab, (tab) => {
  page.value = 1
  if (tab === 'answer') loadAnswerList()
  if (tab === 'my') loadMyAnswerList()
})

// 페이지 변경 감지
watch(page, () => {
  if (selectedTab.value === 'answer') loadAnswerList()
  if (selectedTab.value === 'my') loadMyAnswerList()
})

// 초기 로딩
onMounted(() => {
  loadAnswerList()
})
</script>

<template>
  <LawyerFrame>
    <div class="container py-0">
      <h2 v-if="selectedTab === 'answer'">Q&A 답변하기</h2>
      <h2 v-if="selectedTab === 'my'">내 답변한 상담글</h2>
    </div>

    <section class="qa-section py-5 px-3 px-lg-5 mt-0">
      <!-- 탭 버튼 - 템플릿 구조처럼 수정 -->
      <div class="mb-4">
        <div class="btn-tab-wrapper d-flex w-100">
          <button
              class="btn-tab flex-fill"
              :class="{ active: selectedTab === 'answer' }"
              @click="selectedTab = 'answer'"
          >
            Q&A 답변하기
          </button>
          <button
              class="btn-tab flex-fill"
              :class="{ active: selectedTab === 'my' }"
              @click="selectedTab = 'my'"
          >
            내 답변한 상담글
          </button>
        </div>
      </div>

      <!-- Q&A 목록 -->
      <div v-if="selectedTab === 'answer'">
        <div v-if="!isLoading && list.length === 0" class="text-center text-muted py-5">
          현재 답변 가능한 글이 없습니다.
        </div>
        <div v-else>
          <div
              v-for="qa in list"
              :key="qa.no"
              class="qa-card bg-white rounded shadow-sm p-4 mb-3"
              @click="router.push({ name: 'CommentRegister', params: { id: qa.no } })"
              style="cursor: pointer;">
            <small class="badge bg-light text-secondary mb-1">
              {{ categoryMap[qa.categoryNo] || '미지정' }}
            </small>
            <h5 class="fw-semibold mt-1">{{ qa.title }}</h5>
            <p class="text-muted mb-2">{{ qa.summary || qa.content }}</p>
            <small class="text-secondary">
              {{ qa.createdAt ? new Date(qa.createdAt).toLocaleString() : '' }}
            </small>
          </div>
        </div>
      </div>

      <!-- 내 답변 목록 -->
      <div v-if="selectedTab === 'my'">
        <div v-if="!isLoading && myAnswers.length === 0" class="text-center text-muted py-5">
          아직 등록한 답변이 없습니다.
        </div>
        <div v-else>
          <div
              v-for="ans in myAnswers"
              :key="ans.no"
              class="qa-card bg-white rounded shadow-sm p-4 mb-3">
            <div class="fw-bold mb-1">{{ ans.boardTitle }}</div>
            <p class="text-muted small mb-1">{{ ans.content }}</p>
            <small class="text-secondary">
              {{ ans.createdAt ? new Date(ans.createdAt).toLocaleString() : '' }}
            </small>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
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

<style scoped>
.template-wrapper {
  background-color: #f7f8fb; /* 배경 회색-파랑 */
  border: 1px solid #d6dbe8;
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.btn-tab-wrapper {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.btn-tab {
  border: none;
  background-color: white;
  color: #666;
  font-weight: 500;
  padding: 0.75rem 1.2rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-tab.active {
  background-color: #445b7c; /* 💙 강조 색 */
  color: white;
  font-weight: 700;
}
</style>