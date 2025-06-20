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

const dummyAnswers = ref([
  {
    lawyerName: '이재용 변호사',
    lawyerProfileImage: '/img/profiles/lawyer1.jpg',
    createdAt: '34분 전',
    content: `상간녀에 대한 손해배상청구는 ‘혼인관계가 실질적으로 유지되고 있는 동안 제3자가 배우자와 부정행위를 하여 혼인 평온을 침해한 경우’에 가능합니다.

남편이 3월에 가출하였고 이후 이혼조정 중이며, 남편이 여성과 9개월 전부터 교류했다는 점에서 부정행위 시점이 혼인관계 존속 중이었다면 상간소송이 원칙적으로 가능합니다.

다만 상대 여성의 신원을 특정할 수 없거나, 상대방이 남편의 유부남임을 몰랐다고 주장할 경우 입증책임은 본인에게 있습니다...`
  },
  {
    lawyerName: '김수영 변호사',
    lawyerProfileImage: '/img/profiles/lawyer2.jpg',
    createdAt: '20분 전',
    content: `질문 주신 상황에서는 남편과의 교류 시점, 증거자료(통화녹음, 문자 등) 확보가 중요합니다.`
  }
])
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

      <div class="alert small rounded-3 p-3 mb-3"
           style="background-color: #eaf4ff; color: #0c5460;">
        <div class="fw-semibold mb-2">
          <i class="bi bi-info-circle me-1"></i>
          답변 작성 가이드
        </div>
        <ul class="mb-0 ps-3">
          <li>온라인 상담은 의뢰인의 신뢰에 기반을 두므로, 반드시 변호사님이 직접 확인하고 답변해 주셔야 합니다.</li>
          <li>상담글 답변에는 <strong>질문과 관련된 내용만 작성</strong>해주세요.</li>
          <li>개인정보(이름, 전화번호, 이메일, 주민번호, 주소 등)는 포함하지 않도록 주의해주세요.</li>
          <li>변호사님 성함과 사무소명은 답변과 함께 자동 표시됩니다.</li>
          <li>답변 작성은 <strong>하루 50개, 한달 최대 1,000개까지</strong>가능 합니다.</li>
          <li class="text-danger">이용약관 및 상담사례 운영정책에 위배된 답변은 임의로 게시중단 되거나 정책에 따른 제재가 취해질 수 있습니다.</li>
        </ul>
      </div>


      <div class="mb-4">
        <label class="form-label fw-bold" style="font-size: 1.25rem;">상담글 답변</label>
        <textarea v-model="answerContent" class="form-control" rows="8" :maxlength="maxLength"
                  placeholder="질문에 대한 답변을 입력하세요"></textarea>
        <div class="text-end small text-muted">{{ answerContent.length }} / {{ maxLength }}</div>
      </div>

      </div>

      <div class="text-center">
        <button class="btn btn-primary px-4" :disabled="!agreed || answerContent.length === 0">
          답변 등록하기
        </button>
      </div>

    <div v-if="dummyAnswers.length" class="container mt-5 px-3 px-lg-5">
      <h5 class="fw-bold mb-3">변호사 답변 {{ dummyAnswers.length }}개</h5>

      <div
          v-for="(answer, idx) in dummyAnswers"
          :key="idx"
          class="border rounded-3 px-4 py-3 mb-4"
          style="background-color: #ffffff;"
      >
        <div class="d-flex justify-content-between">
          <div class="d-flex align-items-center">
            <img
                :src="answer.lawyerProfileImage"
                class="rounded-circle me-3"
                width="48"
                height="48"
                style="object-fit: cover;"
            />
            <div>
              <div class="fw-bold">{{ answer.lawyerName }}</div>
              <div class="text-muted small">{{ answer.createdAt }}</div>
            </div>
          </div>
        </div>

        <div class="mt-3" style="white-space: pre-line; line-height: 1.6;">
          {{ answer.content }}
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
