<script setup>
// Vue와 라우터, 레이아웃 컴포넌트 불러오기
import {ref, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const router = useRouter()
const qaNo = route.params.no // /qna/:no 에서 넘어온 글 NO

// Q&A 상세 데이터
const qa = ref({
  category: '',
  title: '',
  content: '',
  date: '',
  writer: '',
})

// 변호사 답변 더미 데이터
const answers = ref([
  {no: 1, avatar: '/img/profiles/kim.png', author: '김서연 변호사', content: '첫 번째 답변 예시입니다.', isSelected: false},
  {no: 2, avatar: '/img/profiles/lee.png', author: '유재석 변호사', content: '두 번째 답변 예시입니다.', isSelected: false},
  {no: 3, avatar: '/img/profiles/park.png', author: '이재용 변호사', content: '세 번째 답변 예시입니다.', isSelected: false}
])

// 모달 표시 여부
const showDeleteModal = ref(false)

// 삭제 진행 함수
function confirmDelete() {
  // TODO: 실제 API 호출
  // await api.delete(`/qna/${qaNo}`)
  router.push('/qna')
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

// 답변 채택 함수 (하나만 true)
function selectAnswer(answerNo) {
  answers.value = answers.value.map(a => ({
    ...a,
    isSelected: a.no === answerNo
  }))
}

onMounted(async () => {
  // TODO: 실제 API 호출
  // const { data } = await api.get(`/qna/${qaNo}`)
  // qa.value = data
  // 현재는 더미 데이터
  qa.value = {
    category: '교통사고 · 보상',
    title: `상담 사례 #${qaNo}`,
    content: `여기에 상담 내용이 들어갑니다. (no: ${qaNo})`,
    date: '2025-06-01',
    writer: '닉네임',
    tags: ['#교통사고', '#보상', '#과실비율']
  }
})
</script>
<template>
  <ClientFrame>
    <div class="qa-detail py-5 px-3 px-lg-5">

      <!-- 카테고리·제목·정보 -->
      <div class="mb-4">
        <small class="text-muted">{{ qa.category }}</small>
        <h2 class="fw-bold mt-1">{{ qa.title }}</h2>
        <div class="text-secondary">
          {{ qa.date }} · 작성자: {{ qa.writer }}
        </div>
      </div>

      <!-- 질문 본문 -->
      <div class="p-4 mb-5">
        <p class="mb-0" style="white-space: pre-wrap">{{ qa.content }}</p>
      </div>

      <!-- 수정/삭제 버튼 -->
      <div class="d-flex justify-content-end mb-4">
        <button @click="onEdit" class="btn btn-link text-secondary p-0 me-2 edit-btn">
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
      <div class="answers">
        <h4 class="fw-semibold mb-3">변호사 답변</h4>
        <div
            v-for="ans in sortedAnswers"
            :key="ans.no"
            class="answer-card border rounded p-3 mb-3"
        >
          <!-- 작성자(이미지+이름) + 채택 버튼 -->
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center">
              <img v-if="ans.avatar" :src="ans.avatar"
                  class="rounded-circle me-2" style="width:32px; height:32px;" />
              <small class="text-secondary">{{ ans.author }}</small>
            </div>
            <div>
              <button v-if="!ans.isSelected" @click="selectAnswer(ans.no)"
                  class="btn btn-outline-primary btn-sm"> 채택 </button>
              <span v-else class="badge bg-primary"> 채택됨 </span>
            </div>
          </div>
           <!-- 답변 본문 -->
           <p class="mb-0">{{ ans.content }}</p>
        </div>
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
</style>