<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import http from '@/libs/HttpRequester'

const route = useRoute()
const router = useRouter()

const templateNo = ref(route.params.no)
const isEditMode = ref(!!templateNo.value)

const name = ref('')
const price = ref('')
const discount_rate = ref('')
const category_no = ref('')
const description = ref('')
const file = ref(null)
const previewUrl = ref(null)
const oldThumbnailPath = ref(null)

function onFileChange(event) {
  const uploaded = event.target.files[0]
  file.value = uploaded
  previewUrl.value = uploaded ? URL.createObjectURL(uploaded) : null
}

function loadTemplate(no) {
  const categoryMap = {
    '사고 발생/처리': 1,
    '중대사고·형사처벌': 2,
    '음주·무면허 운전': 3,
    '보험·행정처분': 4,
    '과실 분쟁': 5,
    '차량 외 사고': 6
  }

  const templates = JSON.parse(localStorage.getItem('templateList') || '[]')
  const selected = templates.find(t => String(t.no) === String(no))
  if (selected) {
    name.value = selected.name
    price.value = selected.price?.replace(/[^0-9]/g, '') ?? ''
    discount_rate.value = selected.discountRate?.replace('%', '') ?? ''
    category_no.value = categoryMap[selected.categoryName] || ''
    description.value = selected.description || ''
    previewUrl.value = selected.imageUrl
    oldThumbnailPath.value = selected.imageUrl
  }
}

onMounted(() => {
  if (isEditMode.value) loadTemplate(templateNo.value)
})

watch(() => route.params.no, (newNo) => {
  templateNo.value = newNo
  isEditMode.value = !!newNo
  if (newNo) loadTemplate(newNo)
})

async function handleSubmit() {
  const mode = isEditMode.value ? '수정' : '등록'
  const formData = new FormData()

  formData.append('category_no', category_no.value)
  formData.append('name', name.value)
  formData.append('price', price.value)
  formData.append('discount_rate', discount_rate.value)
  formData.append('description', description.value)
  formData.append('thumbnail_path', oldThumbnailPath.value)

  if (!file.value && oldThumbnailPath.value) {
    const response = await fetch(oldThumbnailPath.value)
    const blob = await response.blob()
    const filename = oldThumbnailPath.value.split('/').pop()
    const reconstructedFile = new File([blob], filename, { type: blob.type })
    formData.append('file', reconstructedFile)
  } else if (file.value) {
    formData.append('file', file.value)
  }

  try {
    if (isEditMode.value) {
      await http.put(`/api/templates/lawyer/${templateNo.value}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
    } else {
      await http.post('/api/templates/lawyer', formData)
    }
    alert(`${mode}이 완료되었습니다.`)
    router.push('/lawyer/templates')
  } catch (e) {
    console.error(`${mode} 실패:`, e)
    alert(`${mode} 중 오류가 발생했습니다.`)
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">{{ isEditMode ? '템플릿 수정' : '템플릿 등록' }}</h3>
      <div class="card p-4">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">상품 이미지</label>
            <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
              <img v-if="previewUrl" :src="previewUrl" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
              <span v-else class="text-muted">이미지를 등록해 주세요</span>
            </div>
            <input type="file" class="form-control" @change="onFileChange" />
          </div>
          <div class="col-md-8">
            <label class="form-label">상품명</label>
            <input v-model="name" class="form-control mb-2" />
            <label class="form-label">금액</label>
            <input v-model="price" type="number" class="form-control mb-2" />
            <label class="form-label">할인율 (%)</label>
            <input v-model="discount_rate" type="number" class="form-control mb-2" />
            <label class="form-label">카테고리</label>
            <select v-model.number="category_no" class="form-select">
              <option value="" disabled>카테고리 선택</option>
              <option :value="1">사고 발생/처리</option>
              <option :value="2">중대사고·형사처벌</option>
              <option :value="3">음주·무면허 운전</option>
              <option :value="4">보험·행정처분</option>
              <option :value="5">과실 분쟁</option>
              <option :value="6">차량 외 사고</option>
            </select>
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">상세 설명</label>
          <textarea v-model="description" rows="5" class="form-control" />
        </div>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="router.push('/lawyer/templates')">취소</button>
          <button class="btn btn-primary" @click="handleSubmit">
            {{ isEditMode ? '수정 완료' : '등록하기' }}
          </button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.preview-box {
  width: 100%;
  height: 200px;
  background-color: #f8f9fa;
  border: 1px dashed #ccc;
  overflow: hidden;
}
</style>
