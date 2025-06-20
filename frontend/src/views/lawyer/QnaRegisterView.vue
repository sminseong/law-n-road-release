<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { fetchBoardDetail, registerComment } from '@/service/boardService.js'
import { useLawyerStore } from '@/stores/lawyer.js'
import http from '@/libs/HttpRequester.js' // 실제 경로 기준

const store = useLawyerStore()
const route = useRoute()
const id = route.params.id
const comments = ref([])

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
    const res = await http.get(`/api/lawyer/comment/${route.params.id}`)
    comments.value = res.data
  } catch (e) {
    console.error('댓글 목록 불러오기 실패:', e)
  }
}
// 답변 작성
const answerContent = ref('')
const maxLength = 1000

// 변호사 소개 관련 UI 상태
const showIntro = ref(true)
const agreed = ref(false)
const lawyerIntro = ref(
    `수천건의 이혼소송 경험과 소송 노하우를 나누어 드립니다.\n제가 직접 법률분석, 직접상담, 전화・카톡 문자 이외의 온라인들과 직접소통을 합니다.\n36년차 이혼전문변호사\n대한변호사협회 이혼, 가사법 전문변호사 등록`
)

onMounted(async () => {
  const lawyerNoFromStorage = localStorage.getItem('no')
  if (!store.lawyerInfo?.data) {
    await store.fetchLawyerInfo(lawyerNoFromStorage)
  }

  await loadCommentList()

  // ⭐ 여기서 찍기
  console.log('🟢 lawyerInfo 구조:', store.lawyerInfo)
  console.log('🟢 getLawyerNo getter:', store.getLawyerNo)
  console.log('🟢 lawyerInfo.data.lawyerNo:', store.lawyerInfo?.data?.lawyerNo)

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
    console.error('🚨 질문 정보 불러오기 실패:', err)
    alert('질문 정보를 불러오지 못했습니다.')
  }
})

const register = async () => {
  const userNo =store.lawyerInfo?.lawyerNo

  // 여기서 한 번 더 체크
  console.log('🛠️ register 시점의 userNo:', userNo)
  console.log('🛠️ store.lawyerInfo:', store.lawyerInfo)

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

  console.log('✅ 등록 요청 Payload:', payload)

  try {
    await registerComment(payload)
    await loadCommentList()       // 추가
    answerContent.value = ''      // 입력창 초기화
    alert('답변이 등록되었습니다.')
    // TODO: 목록 새로고침 or 이동 로직 추가 가능
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

      <!-- 답변 가이드 -->
      <div class="alert small rounded-3 p-3 mb-4" style="background-color: #eaf4ff; color: #0c5460;">
        <div class="fw-semibold mb-2">
          <i class="bi bi-info-circle me-1"></i>
          답변 작성 가이드
        </div>
        <ul class="mb-0 ps-3">
          <li>온라인 상담은 의뢰인의 신뢰에 기반을 두므로, 반드시 변호사님이 직접 확인하고 답변해 주세요.</li>
          <li>질문과 관련된 내용만 작성해주세요.</li>
          <li>개인정보 포함 금지 (이름, 전화번호, 이메일, 주소 등)</li>
          <li>성함과 사무소명은 자동 표시됩니다.</li>
          <li>답변 작성은 하루 50개, 한달 최대 1,000개까지 가능합니다.</li>
          <li class="text-danger">운영 정책 위반 시 답변은 임의로 중단될 수 있습니다.</li>
        </ul>
      </div>

      <!-- 답변 작성 영역 -->
      <div class="mb-4">
        <label class="form-label fw-bold" style="font-size: 1.25rem;">상담글 답변</label>
        <textarea
            v-model="answerContent"
            class="form-control"
            rows="8"
            :maxlength="maxLength"
            placeholder="질문에 대한 답변을 입력하세요"
        ></textarea>
        <div class="text-end small text-muted">
          {{ answerContent.length }} / {{ maxLength }}
        </div>
      </div>

      <!-- 등록 버튼 -->
      <div class="text-center">
        <button class="btn btn-primary px-4" :disabled="answerContent.length < 100" @click="register">
          답변 등록하기
        </button>
      </div>

      <!-- ✅ 변호사 답변 목록도 container 내부에 유지 -->
      <div class="mt-5">
        <h5>변호사 답변</h5>
        <div v-if="comments.length > 0">
          <div
              v-for="(comment, idx) in comments"
              :key="idx"
              class="mb-3 p-3 border rounded bg-light"
          >
            <div class="d-flex align-items-center mb-2">
              <img
                  :src="comment.lawyerProfileImage"
                  class="rounded-circle me-3"
                  width="48"
                  height="48"
                  alt="변호사 프로필"
              />
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