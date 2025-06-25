<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import { createQna } from '@/service/boardService.js'

const router = useRouter()

// 입력 필드 데이터
const title = ref('')
const incidentDate  = ref('')
const content = ref('')
const userNo = ref(11) // 🔐 임시: 로그인 후 실제 사용자 ID로 대체
// TODO: 로그인 상태에서 userNo 동적으로 주입
// const { userNo } = useAccountStore()
const categoryNo = ref('')
const categories = ref([
  { no: 1, name: '사고 발생/처리' },
  { no: 2, name: '중대사고·형사처벌' },
  { no: 3, name: '음주·무면허 운전' },
  { no: 4, name: '보험·행정처분' },
  { no: 5, name: '과실 분쟁' },
  { no: 6, name: '차량 외 사고' }
])

const dateInput = ref(null)
const openDatePicker = () => {
  dateInput.value?.showPicker?.()
}

// 유효성
const isTitleValid   = computed(() => title.value.trim().length >= 10)
const isContentValid = computed(() => content.value.trim().length >= 100)
const isCategoryValid = computed(() => !!categoryNo.value)
const isDateValid    = computed(() => !!incidentDate.value)
const isFormValid    = computed(() => isTitleValid.value && isDateValid.value && isContentValid.value && isCategoryValid.value)


// 등록 함수
const submit = async () => {
  try {
    const payload = {
      title: title.value,
      content: content.value,
      incidentDate: incidentDate.value,
      userNo: userNo.value,
      categoryNo: categoryNo.value
    }

    await createQna(payload) // boardService.js에서 불러온 함수
    alert('등록 성공!')
    router.push('/qna')
  } catch (err) {
    console.error('🚨 등록 실패:', err.response?.data || err)
    alert('등록 실패')
  }
}

</script>

<template>
  <ClientFrame>
    <div class="qa-create py-5 px-3 px-lg-5">
      <h2 class="fw-bold fs-3 mb-4">상담글 작성</h2>

      <form @submit.prevent="submit">

        <!-- 1️⃣ 카테고리 + 사건 일자 같은 줄 -->
        <div class="row mb-4">
          <div class="col-12 col-md-6">
            <label class="form-label fw-semibold">카테고리 <span class="text-danger">*</span></label>
            <select
                v-model="categoryNo" class="form-select"
                :class="{ 'is-valid': isCategoryValid, 'invalid-hover': !isCategoryValid }">
              <option disabled value="">카테고리를 선택하세요</option>
              <option v-for="cat in categories" :key="cat.no" :value="cat.no">{{ cat.name }}</option>
            </select>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label fw-semibold">최초 사건 발생 일자 <span class="text-danger">*</span></label>
            <input v-model="incidentDate" type="date" ref="dateInput" @focus="openDatePicker" class="form-control"
                :class="{ 'is-valid': isDateValid, 'invalid-hover': !isDateValid }" />
          </div>
        </div>

        <!-- 2️⃣ 제목 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">제목 (10자 이상) <span class="text-danger">*</span></label>
          <input v-model="title" type="text" class="form-control" :class="{ 'is-valid': isTitleValid, 'invalid-hover': !isTitleValid }" placeholder="질문을 입력하세요" />
        </div>

        <!-- 3️⃣ 내용 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">내용 (100자 이상) <span class="text-danger">*</span></label>
          <textarea v-model="content" rows="6" class="form-control" :class="{ 'is-valid': isContentValid, 'invalid-hover': !isContentValid }" placeholder="사건 내용을 상세히 입력하세요"></textarea>
        </div>

        <!-- 4️⃣ 등록 버튼 -->
        <div class="text-center">
          <button type="submit" class="btn btn-primary" :disabled="!isFormValid">등록</button>
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