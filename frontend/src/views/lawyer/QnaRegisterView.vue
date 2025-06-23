<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRoute} from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import {fetchBoardDetail, registerComment, fetchCommentList} from '@/service/boardService.js'
import {useLawyerStore} from '@/stores/lawyer.js'

const store = useLawyerStore()
const route = useRoute()
const id = route.params.id
const comments = ref([])
const currentUserNo = ref(Number(localStorage.getItem('no'))) //

// 게시글 상세 정보
const qa = ref({
  categoryName: '',
  title: '',
  content: '',
  incidentDate: '',
  createdAt: ''
})
const loadCommentList = async () => {
  try {
    const res = await fetchCommentList(route.params.id)
    // console.log('🔁 댓글 API 응답:', res)
    // console.log('🧪 댓글 데이터:', res.data)
    comments.value = res.data
  } catch (e) {
    // console.error('댓글 목록 불러오기 실패:', e)
  }
}

// 답변 작성
const answerContent = ref('')
const maxLength = 1000

// 내가 이미 답변했는지 판별
const hasMyAnswer = computed(() => {
  return comments.value.some(comment => comment.userNo === currentUserNo.value)
})

const lengthColorClass = computed(() => {
  if (answerContent.value.length === 0) return ''
  return answerContent.value.length < 100 ? 'text-danger' : 'text-primary'
})

const showValidationMessage = computed(() => {
  return answerContent.value.length > 0 && answerContent.value.length < 100
})

onMounted(async () => {
  const lawyerNoFromStorage = localStorage.getItem('no')
  if (!store.lawyerInfo?.data) {
    await store.fetchLawyerInfo(lawyerNoFromStorage)
  }

  await loadCommentList()

  try {
    const res = await fetchBoardDetail(id)
    qa.value = {
      categoryName: res.data.categoryName,
      title: res.data.title,
      content: res.data.content,
      incidentDate: res.data.incidentDate,
      createdAt: res.data.createdAt
    }
  } catch (err) {
    // console.error('🚨 질문 정보 불러오기 실패:', err)
    alert('질문 정보를 불러오지 못했습니다.')
  }
})

const register = async () => {
  const userNo = store.lawyerInfo?.lawyerNo

  //console.log(' store.lawyerInfo:', store.lawyerInfo)

  if (!userNo) {
    alert('로그인 정보가 확인되지 않아 답변 등록이 불가합니다.')
    return
  }

  if (answerContent.value.trim().length < 100) {
    alert('답변은 100자 이상 입력해야 합니다.')
    return
  }

  const payload = {
    boardNo: Number(id),
    userNo,
    content: answerContent.value
  }

  //console.log(' 등록 요청 Payload:', payload)

  try {
    await registerComment(payload)
    await loadCommentList()       // 추가
    answerContent.value = ''      // 입력창 초기화
    alert('답변이 등록되었습니다.')
  } catch (err) {
    console.error('❌ 등록 실패:', err)
    alert('답변 등록에 실패했습니다.')
  }
}
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

      <!-- 답변 작성 가이드 -->
      <div class="alert small rounded-3 p-3 mb-4" style="background-color: #eaf4ff; color: #0c5460;">
        <div class="fw-semibold mb-2">
          <i class="bi bi-info-circle me-1"></i>
          답변 작성 가이드
        </div>
        <ul class="mb-0 ps-3">
          <li>온라인 상담은 의뢰인의 신뢰에 기반을 두므로, 반드시 변호사님이 직접 확인하고 답변해 주세요.</li>
          <li>질문과 관련된 내용만 작성해주세요.</li>
          <li>개인정보(개인/법인 실명, 전화번호, 이메일, 주민번호, 주소, 아이디 등)는 답변 내용에 포함하시면 안됩니다.</li>
          <li>변호사님의 사진, 이름은 답변과 함께 자동 노출됩니다.</li>
        </ul>
      </div>

      <!-- 이미 답변한 경우 -->
      <div v-if="hasMyAnswer" class="alert alert-warning text-center">
        이미 이 글에 답변을 작성하셨습니다.<br />
        수정은 [내가 답변한 상담글] 목록에서 진행해주세요.
      </div>

      <!-- 아직 답변 안 한 경우 -->
      <div v-else>
        <!-- 답변 작성 영역 -->
        <div class="mb-4">
          <label class="form-label fw-bold" style="font-size: 1.25rem;">상담글 답변</label>
          <textarea v-model="answerContent" class="form-control" rows="8"
              :maxlength="maxLength" placeholder="질문에 대한 답변을 입력하세요"></textarea>

          <div class="text-end small mt-1">
            <span v-if="showValidationMessage" class="text-danger">
              답변은 최소 100자 이상 입력해야 합니다.&nbsp;
              {{ answerContent.length }} / {{ maxLength }}
            </span>
            <span v-else :class="lengthColorClass">
              {{ answerContent.length }} / {{ maxLength }}
            </span>
          </div>
        </div>

        <!-- 등록 버튼 -->
        <div class="text-center">
          <button class="btn btn-primary px-4" :disabled="answerContent.length < 100" @click="register">
            답변 등록하기
          </button>
        </div>
      </div>

      <!-- 변호사 답변 목록 -->
      <div class="mt-5">
        <h5>변호사 답변</h5>
        <div v-if="comments.length > 0">
          <div v-for="(comment, idx) in comments" :key="idx" class="mb-3 p-3 border rounded bg-light">
            <div class="d-flex align-items-center mb-2">
              <img :src="comment.lawyerProfileImage" class="rounded-circle me-3"
                  width="48" height="48" alt="변호사 프로필"/>
              <div>
                <div class="fw-bold">{{ comment.lawyerName }}</div>
                <div class="text-muted small">{{ comment.createdAt }}</div>
              </div>
            </div>
            <p class="mb-0">{{ comment.content }}</p>
          </div>
        </div>

        <div v-else class="mt-4 text-center text-muted">
          아직 등록된 답변이 없습니다.
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
pre {
  white-space: pre-line;
  margin: 0;
}
</style>