<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { fetchBoardDetail } from '@/service/boardService.js'

const route = useRoute()
const id = route.params.id

// 게시글 상세 데이터
const qa = ref({
  categoryName: '',
  title: '',
  content: '',
  incidentDate: '',
  createdAt: ''
})

// 답변 작성 관련
const answerContent = ref('')
const maxLength = 1000
const showIntro = ref(true)
const agreed = ref(false)
const lawyerIntro = ref(
    `수천건의 이혼소송 경험과 소송 노하우를 나누어 드립니다.\n제가 직접 법률분석, 직접상담, 전화・카톡 문자 이외의 온라인들과 직접소통을 합니다.\n36년차 이혼전문변호사\n대한변호사협회 이혼, 가사법 전문변호사 등록`
)

onMounted(async () => {
  try {
    const data = await fetchBoardDetail(id)
    qa.value = {
      categoryName: data.data.categoryName,
      title: data.data.title,
      content: data.data.content,
      incidentDate: data.data.incidentDate,
      createdAt: data.data.createdAt
    }
  } catch (err) {
    console.error(err)
    alert('질문 정보를 불러올 수 없습니다.')
  }
})
</script>

<template>
  <LawyerFrame>
    <div class="container py-5 px-3 px-lg-5">
      <div class="mb-4">
        <div class="text-secondary small">{{ qa.categoryName }}</div>
        <div class="small text-muted">사건 발생일: {{ qa.incidentDate }} | 작성일: {{ qa.createdAt }}</div>
        <h2 class="mt-3">{{ qa.title }}</h2>
        <p class="mt-2">{{ qa.content }}</p>
      </div>

      <hr>

      <div class="mb-4">
        <label class="form-label fw-bold">답변 내용</label>
        <textarea v-model="answerContent" class="form-control" rows="8" :maxlength="maxLength"
                  placeholder="질문에 대한 답변을 입력하세요"></textarea>
        <div class="text-end small text-muted">{{ answerContent.length }} / {{ maxLength }}</div>
      </div>

      <div class="mb-4">
        <label class="form-label fw-bold">변호사 소개글</label>
        <div class="form-check form-switch mb-2">
          <input class="form-check-input" type="checkbox" v-model="showIntro" id="toggleIntro">
          <label class="form-check-label" for="toggleIntro">소개글을 표시할게요</label>
        </div>
        <div v-if="showIntro" class="bg-light p-3 rounded border text-muted small">
          <pre class="mb-0">{{ lawyerIntro }}</pre>
        </div>
      </div>

      <div class="form-check mb-4">
        <input class="form-check-input" type="checkbox" v-model="agreed" id="agreeCheck">
        <label class="form-check-label" for="agreeCheck">
          네이버 지식인 전문가 답변 등록에 동의합니다.
        </label>
      </div>

      <div class="text-center">
        <button class="btn btn-primary px-4" :disabled="!agreed || answerContent.length === 0">
          답변 등록하기
        </button>
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
