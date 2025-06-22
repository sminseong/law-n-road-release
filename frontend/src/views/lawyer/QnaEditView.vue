<script setup>
import { ref, onMounted } from 'vue'
import { useRoute,  useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { fetchBoardDetail ,fetchCommentDetail, updateComment , deleteComment   } from '@/service/boardService.js'

const route = useRoute()
const router = useRouter()
const commentId = route.params.id
// 게시글 정보 및 답변
const qa = ref({})
const answerContent = ref('')
const maxLength = 1000

const boardNo = ref(null)
const userNo = ref(null)

const submitEdit = async () => {
  try {
    const payload = {
      boardNo: boardNo.value,
      userNo: userNo.value,
      content: answerContent.value
    }
    await updateComment(commentId, payload)
    alert('답변이 수정되었습니다.')
  } catch (err) {
    console.error('❌ 수정 실패:', err)
    alert('답변 수정에 실패했습니다.')
  }
}

const handleDelete = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  console.log('🧪 삭제 시도 commentId:', commentId)
  try {
    await deleteComment(commentId)
    alert('삭제가 완료되었습니다.')
    router.push('/lawyer/qna')  // 삭제 후 이동할 경로
  } catch (error) {
    console.error('삭제 실패:', error)
    console.error('🧪 error.response:', error?.response)
    console.error('🧪 error.config:', error?.config)
    alert('삭제 중 문제가 발생했습니다.')
  }
}

//데이터 불러오기 (게시글 + 답변)
onMounted(async () => {
  try {
    const commentRes = await fetchCommentDetail(commentId)
    console.log('💬 댓글 상세 응답:', commentRes.data)

    answerContent.value = commentRes.data.content
    boardNo.value = commentRes.data.boardNo
    userNo.value = commentRes.data.userNo  // 👈 이것도 있음!

    const boardRes = await fetchBoardDetail(boardNo.value)
    qa.value = boardRes.data
  } catch (err) {
    console.error('❌ 불러오기 실패:', err)
    alert('데이터를 불러오는 데 실패했습니다.')
  }
})
</script>

<template>
  <LawyerFrame>
    <div class="container py-5 px-3 px-lg-5">
      <!-- 질문 정보 -->
      <div class="mb-4">
        <div class="text-secondary small">{{ qa.categoryName }}</div>
        <div class="small text-muted">
          사건 발생일: {{ qa.incidentDate }} | 작성일: {{ qa.createdAt }}
        </div>
        <h2 class="mt-3">{{ qa.title }}</h2>
        <p class="mt-2">{{ qa.content }}</p>
      </div>

      <!-- 답변 수정 UI -->
      <div class="mb-4">
        <label class="form-label fw-bold" style="font-size: 1.25rem;">답변 수정</label>
        <textarea
            v-model="answerContent"
            class="form-control"
            rows="8"
            :maxlength="maxLength"
            placeholder="수정할 답변 내용을 입력하세요"
        ></textarea>
        <div class="text-end small text-muted">
          {{ answerContent.length }} / {{ maxLength }}
        </div>
      </div>

      <!-- 버튼 (UI 전용) -->
      <div class="text-center">
        <button class="btn btn-primary me-2" @click="submitEdit">수정하기</button>
        <button class="btn btn-outline-danger" @click="handleDelete">삭제하기</button>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
textarea {
  resize: vertical;
}
</style>