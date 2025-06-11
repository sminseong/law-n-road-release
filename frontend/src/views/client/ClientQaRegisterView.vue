<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const router = useRouter()
const title = ref('')
const date  = ref('')
const content = ref('')

// 유효성
const isTitleValid   = computed(() => title.value.trim().length >= 10)
const isDateValid    = computed(() => !!date.value)
const isContentValid = computed(() => content.value.trim().length >= 100)
const isFormValid    = computed(() => isTitleValid.value && isDateValid.value && isContentValid.value)

function onSubmit(){
  if(!isFormValid.value) return
  // TODO: API 호출...
  router.push('/qna')
}
</script>

<template>
  <ClientFrame>
    <div class="qa-create py-5 px-3 px-lg-5">
      <h2 class="fw-bold fs-3 mb-4">상담글 작성</h2>
      <form @submit.prevent="onSubmit">
        <!-- 제목 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">
            제목 (10자 이상<span class="text-danger">*</span>)
          </label>
          <input v-model="title" type="text" class="form-control"
              :class="{ 'is-valid': isTitleValid,'invalid-hover': !isTitleValid }"
              placeholder="질문을 입력하세요"/>
        </div>

        <!-- 날짜 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">
            최초 사건 발생 일자<span class="text-danger">*</span>
          </label>
          <input v-model="date" type="date" class="form-control"
              :class="{ 'is-valid': isDateValid,'invalid-hover': !isDateValid}" />
        </div>

        <!-- 내용 -->
        <div class="mb-4">
          <label class="form-label fw-semibold">
            내용 (100자 이상<span class="text-danger">*</span>)
          </label>
          <textarea v-model="content" rows="6" class="form-control"
              :class="{ 'is-valid': isContentValid, 'invalid-hover': !isContentValid }"
              placeholder="사건 내용을 상세히 입력하세요" ></textarea>
        </div>

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