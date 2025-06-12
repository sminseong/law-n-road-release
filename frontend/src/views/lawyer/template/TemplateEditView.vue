<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import AiTemplateEditor from '@/components/template/AiTemplateEditor.vue'
import UploadTemplateForm from '@/components/template/UploadTemplateForm.vue'
import http from '@/libs/HttpRequester'

// 최대 썸네일 용량 (10MB)
const MAX_THUMBNAIL_SIZE = 10 * 1024 * 1024  // 10MB
const router = useRouter()
const route = useRoute()
const templateNo = route.params.no
const templateType = route.query.type || 'EDITOR'

// 공통 필드
const name = ref('')
const price = ref(0)
const discountRate = ref(0)
const categoryNo = ref(null)
const description = ref('')
const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)
const oldThumbnailPath = ref(null)
const createdAt = ref('')

// 템플릿 타입 고정 (탭 제거)
const selectedTab = ref(templateType === 'EDITOR' ? 'ai' : 'upload')

// AI용 데이터
const editorContent = ref('')
const editorVariables = ref([])

// 파일 업로드용
const uploadedFiles = ref([])

onMounted(async () => {
  try {
    const res = await http.get(`/api/lawyer/templates/${templateNo}`, { type: templateType })
    const data = res.data

    console.log(data)

    // 공통 필드 세팅
    name.value = data.name
    price.value = data.price
    discountRate.value = data.discountRate
    categoryNo.value = data.categoryNo
    description.value = data.description
    createdAt.value = data.createdAt
    oldThumbnailPath.value = data.thumbnailPath
    thumbnailPreview.value = data.thumbnailPath

    // 유형별 분기
    if (templateType === 'EDITOR') {
      selectedTab.value = 'ai'
      editorContent.value = data.content
      editorVariables.value = JSON.parse(data.varJson || '[]')
    } else if (templateType === 'FILE') {
      selectedTab.value = 'upload'
      uploadedFiles.value = JSON.parse(data.pathJson || '[]')
    } else {
      throw new Error(`알 수 없는 템플릿 유형: ${templateType}`)
    }
  } catch (e) {
    console.error(e)
    alert('템플릿 정보를 불러오지 못했습니다.')
  }
})

const removeExistingThumbnail = ref(false)
function clearThumbnail() {
  thumbnailFile.value = null
  thumbnailPreview.value = null
  removeExistingThumbnail.value = true  // 기존 썸네일도 지운다고 표시
}

function onThumbnailChange(e) {
  const file = e.target.files[0]
  if (!file) return

  // 이미지 타입 검증
  if (!file.type.startsWith('image/')) {
    alert('❌ 이미지 파일만 업로드할 수 있습니다.')
    e.target.value = null
    clearThumbnail()
    return
  }

  // 용량 제한 검증
  if (file.size > MAX_THUMBNAIL_SIZE) {
    alert('❌ 썸네일 이미지는 10MB 이하만 업로드 가능합니다.')
    e.target.value = null
    clearThumbnail()
    return
  }

  // 정상 통과 시
  thumbnailFile.value    = file
  thumbnailPreview.value = URL.createObjectURL(file)
}

async function handleUpdate() {
  if (!confirm('템플릿을 수정하면 판매기록이 초기화 됩니다. 정말 수정하시겠습니까?')) return

  try {
    const formData = new FormData()
    formData.append('no', templateNo) // 기존 템플릿 번호
    formData.append('name', name.value)
    formData.append('price', price.value)
    formData.append('discountRate', discountRate.value)
    formData.append('categoryNo', categoryNo.value)
    formData.append('description', description.value)
    formData.append('type', selectedTab.value === 'ai' ? 'EDITOR' : 'FILE')
    formData.append('removeThumbnail', removeExistingThumbnail.value ? 1 : 0)

    if (thumbnailFile.value) {
      formData.append('file', thumbnailFile.value)
    }

    if (selectedTab.value === 'ai') {
      formData.append('content', editorContent.value)
      formData.append('varJson', JSON.stringify(editorVariables.value))
      formData.append('aiEnabled', 1)
    } else {
      // 1) 기존 메타 객체만 뽑아서 JSON
      const existingMeta = uploadedFiles.value.filter(
          item => item.originalName && item.savedPath
      );
      formData.append('pathJson', JSON.stringify(existingMeta));

      // 2) 새로 추가된 실제 File 객체만
      uploadedFiles.value
          .filter(item => item instanceof File)
          .forEach(file => {
            formData.append('templateFiles', file);
          });
    }

    // 수정용 API 호출
    await http.post('/api/lawyer/templates/update', formData)

    alert('수정 완료되었습니다.')
    router.push('/lawyer/templates')
  } catch (e) {
    console.error(e)
    alert('수정 실패')
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">템플릿 수정</h3>

      <!-- 공통 필드 입력 -->
      <div class="card p-4 mb-4">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">썸네일 이미지</label>
            <div class="preview-box position-relative mb-2 d-flex align-items-center justify-content-center border rounded">
              <button
                  v-if="thumbnailPreview"
                  type="button"
                  class="btn-close position-absolute top-0 end-0 m-2"
                  aria-label="Remove thumbnail"
                  style="z-index:10"
                  @click="clearThumbnail"
              > </button>
              <img v-if="thumbnailPreview" :src="thumbnailPreview" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
              <p v-else class="text-muted" style="text-align: center">상품 목록에 보여질 대표 이미지입니다.<br>이미지를 등록해 주세요</p>
            </div>
            <input type="file" class="form-control" accept="image/*" @change="onThumbnailChange" />
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
          <label class="form-label">상세 설명</label>
          <textarea v-model="description" rows="6" class="form-control" />
        </div>
      </div>

      <!-- 템플릿 본문 (타입 고정) -->
      <div class="template-wrapper">
        <AiTemplateEditor
            v-if="selectedTab === 'ai'"
            v-model:content="editorContent"
            v-model:variables="editorVariables"
            :is-edit=true
        />

        <UploadTemplateForm
            v-if="selectedTab === 'upload'"
            v-model:templateFiles="uploadedFiles"
        />
      </div>

      <!-- 버튼 -->
      <div class="text-end">
        <button class="btn btn-secondary me-2" @click="router.back()">취소</button>
        <button class="btn btn-primary" @click="handleUpdate">수정 완료</button>
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

.template-wrapper {
  background-color: #f7f8fb;
  border: 1px solid #d6dbe8;
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
}
</style>
