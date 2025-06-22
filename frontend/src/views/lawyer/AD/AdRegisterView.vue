<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/libs/HttpRequester'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";

const router = useRouter()

const position = ref('MAIN') // MAIN or SUB
const startDate = ref('')
const endDate = ref('')
const mainText = ref('')
const detailText = ref('')
const tipText = ref('')
const imageFile = ref(null)
const imagePreview = ref('')

const today = new Date()
const yyyy = today.getFullYear()
const mm = String(today.getMonth() + 1).padStart(2, '0')
const dd = String(today.getDate()).padStart(2, '0')
const todayString = `${yyyy}-${mm}-${dd}`
// const today = new Date().toISOString().split('T')[0]

function handleImageChange(e) {
  const file = e.target.files[0]
  if (!file || !file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.')
    return
  }
  imageFile.value = file
  imagePreview.value = URL.createObjectURL(file)
}

function handleStartDateChange() {
  if (startDate.value) {
    const d = new Date(startDate.value)
    d.setMonth(d.getMonth() + 1)
    endDate.value = d.toISOString().split('T')[0]
  }
}

async function handleSubmit() {
  if (!mainText.value || !detailText.value || !startDate.value || !imageFile.value) {
    alert('필수 항목을 모두 입력해 주세요.')
    return
  }

  const formData = new FormData()
  formData.append('adType', position.value)
  formData.append('startDate', startDate.value)
  formData.append('endDate', endDate.value)
  formData.append('mainText', mainText.value)
  formData.append('detailText', detailText.value)
  formData.append('tipText', tipText.value || '')
  formData.append('file', imageFile.value)

  try {
    await http.post('/api/lawyer/ads/register', formData)
    alert('광고가 등록되었습니다.')
    router.push('/lawyer/ads')
  } catch (e) {
    alert('등록 실패: ' + (e.response?.data || '알 수 없는 오류'))
  }
}
</script>

<template>
  <lawyer-frame>
    <div class="container py-4">
      <h3 class="fw-bold mb-3">광고 등록</h3>

      <!-- 설명 -->
      <p class="text-muted small mb-2">
        * 메인 베너는 홈페이지 상단에 크게 노출되며,<br class="d-sm-none" />
        서브 베너는 중간 또는 하단 영역에 작게 표시됩니다.
      </p>

      <!-- 버튼 -->
      <div class="d-flex w-100 gap-3 mb-3">
        <button class="btn flex-fill"
                :class="{ 'btn-primary': position === 'MAIN', 'btn-outline-secondary': position !== 'MAIN' }"
                @click="position = 'MAIN'">
          메인 배너 (400,000원)
        </button>
        <button class="btn flex-fill"
                :class="{ 'btn-primary': position === 'SUB', 'btn-outline-secondary': position !== 'SUB' }"
                @click="position = 'SUB'">
          서브 배너 (150,000원)
        </button>
      </div>

    <div class="mb-3">
      <label class="form-label">광고 시작일</label>
      <input type="date" v-model="startDate" class="form-control" :min="todayString" @change="handleStartDateChange" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 종료일</label>
      <input type="date" v-model="endDate" class="form-control" disabled />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 1 (메인 문구)</label>
      <input v-model="mainText" class="form-control" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 2 (상세 문구)</label>
      <input v-model="detailText" class="form-control" />
    </div>

    <div class="mb-3">
      <label class="form-label">광고 문구 3 (CTA 문구)</label>
      <input v-model="tipText" class="form-control" />
    </div>

    <div class="preview-box mb-2 border d-flex justify-content-center align-items-center" style="height: 200px;">
      <img v-if="imagePreview" :src="imagePreview" class="img-fluid h-100" style="object-fit: contain" />
      <span v-else class="text-muted">이미지 미리보기</span>
    </div>

    <input type="file" accept="image/*" class="form-control mb-3" @change="handleImageChange" />

    <div class="text-end">
      <button class="btn btn-primary" @click="handleSubmit">결제하기</button>
    </div>
  </div>
  </lawyer-frame>
</template>

<style scoped>
.preview-box {
  background-color: #f9f9f9;
}
</style>
