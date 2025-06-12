<script setup>
import { ref, computed ,onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { fetchBoardDetail, updateQna } from '@/service/boardService'

const router = useRouter()
const route = useRoute()
const id = route.params.id

// 입력 필드
const title = ref('')
const incidentDate = ref('')
const content = ref('')
const categoryNo = ref('')

// 카테고리 목록
const categories = ref([
  { no: 1, name: '사고 발생/처리' },
  { no: 2, name: '중대사고·형사처벌' },
  { no: 3, name: '음주·무면허 운전' },
  { no: 4, name: '보험·행정처분' },
  { no: 5, name: '과실 분쟁' },
  { no: 6, name: '차량 외 사고' }
])

// 상세 데이터 가져오기
onMounted(async () => {
  try {
    const response = await fetchBoardDetail(id)
    const qna = response.data

    if (!qna) throw new Error('데이터 없음')

    title.value = qna.title
    content.value = qna.content
    incidentDate.value = qna.incidentDate?.slice(0, 10)
    categoryNo.value = qna.categoryNo
  } catch (err) {
    console.error('❌ Q&A 불러오기 실패:', err)
    alert('게시글 정보를 불러오지 못했습니다.')
  }
})

// 유효성 검사
const isTitleValid     = computed(() => title.value.trim().length >= 10)
const isContentValid   = computed(() => content.value.trim().length >= 100)
const isDateValid      = computed(() => !!incidentDate.value)
const isCategoryValid  = computed(() => !!categoryNo.value)
const isFormValid      = computed(() => isTitleValid.value && isContentValid.value && isDateValid.value && isCategoryValid.value)

// 제출
async function onSubmit() {
  if (!isFormValid.value) return

  try {
    await updateQna(id, {
      title: title.value,
      content: content.value,
      incidentDate: incidentDate.value,
      categoryNo: categoryNo.value
    })

    alert('수정 완료!')
    router.push('/client/qna/list')
  } catch (err) {
    console.error('❌ Q&A 수정 실패:', err)
    alert('수정에 실패했습니다.')
  }
}
</script>

<template>
  <ClientFrame>
    <div class="qa-create py-5 px-3 px-lg-5">
      <h2 class="fw-bold fs-3 mb-4">상담글 수정</h2>
      <form @submit.prevent="onSubmit">
        <!-- 카테고리 + 사건일자 -->
        <div class="row mb-4">
          <div class="col-12 col-md-6">
            <label class="form-label fw-semibold">카테고리 <span class="text-danger">*</span></label>
            <select v-model="categoryNo" class="form-select"
                    :class="{ 'is-valid': isCategoryValid, 'invalid-hover': !isCategoryValid }">
              <option disabled value="">카테고리를 선택하세요</option>
              <option v-for="cat in categories" :key="cat.no" :value="cat.no">{{ cat.name }}</option>
            </select>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label fw-semibold">최초 사건 발생 일자 <span class="text-danger">*</span></label>
            <input v-model="incidentDate" type="date" class="form-control"
                   :class="{ 'is-valid': isDateValid, 'invalid-hover': !isDateValid }" />
          </div>
        </div>

        <!-- 제목 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">제목 (10자 이상) <span class="text-danger">*</span></label>
          <input v-model="title" type="text" class="form-control"
                 :class="{ 'is-valid': isTitleValid, 'invalid-hover': !isTitleValid }" placeholder="질문을 입력하세요" />
        </div>

        <!-- 내용 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">내용 (100자 이상) <span class="text-danger">*</span></label>
          <textarea v-model="content" rows="6" class="form-control"
                    :class="{ 'is-valid': isContentValid, 'invalid-hover': !isContentValid }" placeholder="사건 내용을 상세히 입력하세요"></textarea>
        </div>

        <div class="text-center">
          <button type="submit" class="btn btn-primary" :disabled="!isFormValid">수정</button>
        </div>
      </form>

    </div>
  </ClientFrame>
</template>

<style scoped>
/* invalid 상태에서 hover 시만 빨간 테두리 */
.invalid-hover:hover {
  border-color: #dc3545 !important;
}

/* valid 상태일 때 파란 테두리 */
.is-valid {
  border-color: #0d6efd !important;
}
</style>