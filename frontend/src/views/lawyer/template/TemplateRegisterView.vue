<script setup>
import {onMounted, ref} from 'vue'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import AiTemplateEditor from '@/components/template/AiTemplateEditor.vue'
import UploadTemplateForm from '@/components/template/UploadTemplateForm.vue'
import http from '@/libs/HttpRequester'
import { useRouter } from 'vue-router'
import { Tooltip } from 'bootstrap'

onMounted(() => {
  const elements = document.querySelectorAll('[data-bs-toggle="tooltip"]')
  elements.forEach(el => new Tooltip(el))
})

const router = useRouter()

// 공통 필드
const name = ref('')
const price = ref(0)
const discountRate = ref(0)
const categoryNo = ref(null)
const description = ref('')
const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)
const content = ref('')
const varJson = ref('')
const aiEnabled = ref(1)
const uploadedFiles = ref([])

// 탭 상태
const selectedTab = ref('ai') // 'ai' or 'upload'

// AI용 데이터
const editorContent = ref('')
const editorVariables = ref([])

// 제출 핸들러
async function handleSubmit() {
  const formData = new FormData()

  formData.append('name', name.value)
  formData.append('price', price.value)
  formData.append('discountRate', discountRate.value)
  formData.append('categoryNo', categoryNo.value)
  formData.append('description', description.value)
  formData.append('type', selectedTab.value === 'ai' ? 'EDITOR' : 'FILE')

  if (thumbnailFile.value) {
    formData.append('file', thumbnailFile.value)
  }

  if (selectedTab.value === 'ai') {
    formData.append('content', editorContent.value)
    formData.append('varJson', JSON.stringify(editorVariables.value))
    formData.append('aiEnabled', 1)
  } else {
    uploadedFiles.value.forEach(file => {
      formData.append('templateFiles', file)
    })
  }

  try {
    await http.post('/api/lawyer/templates/register', formData)
    alert('템플릿이 등록되었습니다.')
    router.push('/lawyer/templates')
  } catch (e) {
    console.error(e)
    alert('등록 실패')
  }
}

function onThumbnailChange(e) {
  const file = e.target.files[0]
  thumbnailFile.value = file
  thumbnailPreview.value = file ? URL.createObjectURL(file) : null
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">템플릿 등록</h3>

      <!-- 공통 필드 입력 -->
      <div class="card p-4 mb-4">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">썸네일 이미지</label>
            <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
              <img v-if="thumbnailPreview" :src="thumbnailPreview" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
              <p v-else class="text-muted" style="text-align: center">상품 목록에 보여질 대표 이미지입니다.<br>이미지를 등록해 주세요</p>
            </div>
            <input type="file" class="form-control" @change="onThumbnailChange" />
          </div>
          <div class="col-md-8">
            <label class="form-label">상품명</label>
            <input v-model="name" class="form-control mb-2" />
            <label class="form-label">가격 (원)</label>
            <input v-model="price" type="number" class="form-control mb-2" />
            <label class="form-label">할인율 (%)</label>
            <input v-model="discountRate" type="number" class="form-control mb-2" />
            <label class="form-label">카테고리</label>
            <select v-model.number="categoryNo" class="form-select">
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

        <div>
          <label class="form-label">상세 설명
            <i
                class="bi bi-question-circle text-muted ms-1"
                data-bs-toggle="tooltip"
                data-bs-placement="right"
                data-bs-custom-class="my-tooltip-style"
                title="누가, 언제, 어떻게 사용할 수 있는지 설명해 주세요"
            />
          </label>
          <textarea v-model="description" rows="4" class="form-control" />
        </div>
      </div>

      <!-- 탭 버튼 -->
      <div class="template-wrapper">
        <div class="btn-tab-wrapper d-flex w-100">
          <button
              class="btn-tab flex-fill"
              :class="{ active: selectedTab === 'ai' }"
              @click="selectedTab = 'ai'"
          >
            AI 템플릿
          </button>
          <button
              class="btn-tab flex-fill"
              :class="{ active: selectedTab === 'upload' }"
              @click="selectedTab = 'upload'"
          >
            파일 업로드
          </button>
        </div>

        <!-- 탭 별 컴포넌트 -->
        <AiTemplateEditor
            v-if="selectedTab === 'ai'"
            v-model:content="editorContent"
            v-model:variables="editorVariables"
        />

        <UploadTemplateForm
            v-if="selectedTab === 'upload'"
            v-model:templateFiles="uploadedFiles"
        />
      </div>

      <!-- 제출 버튼 -->
      <div class="text-end">
        <button class="btn btn-success" @click="handleSubmit">등록</button>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.preview-box {
  width: 100%;
  height: 235px;
  background-color: #f8f9fa;
  border: 1px dashed #ccc;
  overflow: hidden;
}

.btn-tab-wrapper {
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  margin-bottom: 1rem;
}

.btn-tab {
  border: none;
  background-color: white;
  color: #666;
  font-weight: 500;
  padding: 0.75rem 1.2rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-tab.active {
  background-color: #445b7c;
  color: white;
  font-weight: 700;
}

.template-wrapper {
  background-color: #f7f8fb;/* 부드러운 옅은 회색-파랑 */
  border: 1px solid #d6dbe8;
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
}
</style>
