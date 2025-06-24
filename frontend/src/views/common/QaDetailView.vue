<script setup>
// Vue와 라우터, 레이아웃 컴포넌트 불러오기
import {ref, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { fetchBoardDetail, deleteQna ,fetchBoardComments } from '@/service/boardService.js'

const route = useRoute()
const router = useRouter()
const id = route.params.id

const isLoggedIn = !!localStorage.getItem('accountType')

// 게시글 상세 데이터
const qa = ref({
  categoryName: '',
  title: '',
  content: '',
  incidentDate: '',
  createdAt: ''
})

// 변호사 답변
const answers = ref([])

// 상세 조회
async function loadDetail() {
  try {
    const res = await fetchBoardDetail(id)
    const data = res.data
    qa.value = {
      categoryName: data.categoryName,
      title: data.title,
      content: data.content,
      incidentDate: data.incidentDate,
      createdAt: data.createdAt,
      userNo: data.userNo
    }
  } catch (err) {
    console.error('게시글 상세 실패:', err)
    alert('게시글을 불러오지 못했습니다.')
  }
}

// 답변 불러오기
async function loadComments() {
  try {
    const res = await fetchBoardComments(id)
    console.log('📥 답변 API 응답:', res)
    console.log('📦 데이터:', res.data)
    answers.value = res.data
  } catch (err) {
    console.error('답변 불러오기 실패:', err)
  }
}

function goEditPage() {
  router.push(`/client/qna/edit/${id}`)
}

// 모달 표시 여부
const showDeleteModal = ref(false)

// 삭제 진행 함수
async function confirmDelete() {
  try {
    await deleteQna(id) // 🟢 삭제 API 호출
    alert('삭제되었습니다.')
    router.push('/qna') // 목록으로 이동
  } catch (err) {
    console.error('❌ 삭제 실패:', err)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

// 버튼 핸들러: 모달 띄우기
function onDeleteClick() {
  showDeleteModal.value = true
}

// 선택된 답변은 항상 맨 위로
const sortedAnswers = computed(() => [
  ...answers.value.filter(a => a.isSelected),
  ...answers.value.filter(a => !a.isSelected)
])

// 답변 채택 함수
async function selectAnswer(answerNo) {
  const myUserNo = localStorage.getItem('no') // 비회원이면 null
  if (myUserNo != qa.value.userNo) {
    alert('작성자만 답변을 채택할 수 있습니다.')
    return
  }

  try {
    await selectCommentAnswer(id, answerNo)
    alert('답변이 채택되었습니다.')
    await loadComments()
  } catch (e) {
    console.error('채택 실패:', e)
    alert('채택에 실패했습니다.')
  }
}

onMounted(async () => {
  await loadDetail()
  await loadComments()
})
</script>
<template>
  <ClientFrame>
    <div class="qa-detail py-5 px-3 px-lg-5">

      <!-- 카테고리 -->
      <div class="text-sm text-muted mb-2">
        {{ qa.categoryName }}
      </div>

      <div class="mb-4 small text-muted">
        사건 발생일: {{ qa.incidentDate }}  |  작성일: {{ qa.createdAt }}
      </div>

      <!-- 제목 -->
      <h1 class="qa-title mb-4">
        {{ qa.title }}
      </h1>

      <!-- 본문 -->
      <p class="qa-content text-dark">
        {{ qa.content }}
      </p>

      <!-- 수정/삭제 버튼 -->
      <div v-if="isLoggedIn" class="d-flex justify-content-end mb-4">
        <button @click="goEditPage" class="btn btn-link text-secondary p-0 me-2 edit-btn" >
          <i class="fas fa-pencil-alt"></i> 수정하기
        </button>
        <button @click="onDeleteClick" class="btn btn-link text-secondary p-0 delete-btn">
          <i class="fas fa-trash-alt"></i> 삭제하기
        </button>
      </div>

      <!-- 커스텀 삭제 확인 모달 -->
      <div v-if="showDeleteModal" class="custom-backdrop">
        <div class="custom-modal">
          <p class="custom-modal__text">
            삭제한 글은 복구할 수 없습니다.<br>
            정말 삭제하시겠습니까?
          </p>
          <div class="custom-modal__actions">
            <button @click="confirmDelete" class="btn btn-danger btn-sm">예</button>
            <button @click="showDeleteModal = false" class="btn btn-secondary btn-sm">아니오</button>
          </div>
        </div>
      </div>

      <!-- 구분선 -->
      <hr class="my-4">

      <!-- 변호사 답변 섹션 -->
      <h3 class="fw-bold mt-5 mb-3">변호사 답변</h3>

        <div v-for="ans in sortedAnswers" :key="ans.commentId"
          class="answer-card border rounded p-3 mb-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center">
              <img v-if="ans.lawyerProfileImage" :src="ans.lawyerProfileImage"
                 class="rounded-circle me-2" style="width:32px; height:32px;" />
              <small class="text-secondary">{{ ans.lawyerName }} 변호사 </small>
            </div>
            <div>
            <button v-if="!ans.isSelected" @click="selectAnswer(ans.commentId)"
                    class="btn btn-outline-primary btn-sm"> 채택 </button>
            <span v-else class="badge bg-primary"> 채택됨 </span>
            </div>
          </div>
        <p class="mb-0">{{ ans.content }}</p>
        </div>
      </div>
  </ClientFrame>
</template>
<style scoped>
.answer-card {
  background: #fff;
}

.answer-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.btn-link {
  text-decoration: none !important;
}

/* 백드롭: 반투명 검정, 가운데 정렬 */
.custom-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050; /* ClientFrame 위에 뜨도록 충분히 크게 */
}

/* 모달 박스 */
.custom-modal {
  background: #fff;
  padding: 1.5rem;
  border-radius: 0.5rem;
  max-width: 360px;
  width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  text-align: center;
}

.custom-modal__text {
  margin-bottom: 1rem;
  font-size: 0.95rem;
  line-height: 1.4;
  color: #333;
}

.custom-modal__actions button + button {
  margin-left: 0.5rem;
}

/* 버튼 언더라인 제거 */
.btn-link {
  text-decoration: none !important;
}

.qa-title {
  font-size: 1.75rem;
  line-height: 1.4;
}

.qa-content {
  font-size: 1.05rem;
  line-height: 1.75;
  white-space: pre-line;
  padding-right: 0.5rem;
}
.custom-backdrop {
  z-index: 9999; /* 기존 1050보다 훨씬 높게 */
}
.answer-wrapper {
  background-color: #f3f6ff;

  border-radius: 1px;
}
</style>

