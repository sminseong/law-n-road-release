<!-- src/views/lawyer/TemplateDetailView.vue -->
<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import AiTemplateEditor from '@/components/template/AiTemplateEditor.vue'
import http from '@/libs/HttpRequester'
import {getValidToken} from "@/libs/axios-auth.js";

// 라우터
const router     = useRouter()
const route      = useRoute()
const templateNo = route.params.no
const templateType = route.query.type || 'EDITOR'

// 조회 전용 탭 상태
const selectedTab = ref(templateType === 'EDITOR' ? 'ai' : 'upload')

// 공통 필드
const name         = ref('')
const price        = ref(0)
const discountRate = ref(0)
const categoryNo   = ref(null)
const description  = ref('')
const thumbnailUrl = ref('')
const createdAt    = ref('')

// AI 전용
const editorContent   = ref('')
const editorVariables = ref([])

// FILE 전용
const uploadedFiles = ref([])

// 수정 페이지로 이동
function goEdit() {
  router.push({
    name: 'TemplateEdit',
    params: { no: templateNo },
    query:  { type: templateType }
  })
}

// 삭제
async function handleDelete() {
  // name은 템플릿명(ref로 받아온 값)
  if (!confirm(`'${name.value}' 템플릿을 삭제하시겠습니까?`)) return

  try {
    // ID는 templateNo
    await http.delete(`/api/lawyer/templates/${templateNo}`)
    alert('삭제되었습니다.')
    // 목록으로 복귀
    router.push({ name: 'LawyerTemplateList' })
  } catch (e) {
    console.error('삭제 실패:', e)
    // 백엔드가 보내준 메시지가 있으면 우선 보여주고, 없으면 기본
    const message = e?.response?.data?.message || '삭제 중 오류가 발생했습니다.'
    alert(message)
  }
}

onMounted(async () => {
  try {
    const token = await getValidToken()
    if (!token) {
      alert('로그인이 필요합니다.')
      return
    }

    const { data } = await http.get(
        `/api/lawyer/templates/${templateNo}`,
        { type: templateType }
    )

    // 공통
    name.value         = data.name
    price.value        = data.price
    discountRate.value = data.discountRate
    categoryNo.value   = data.categoryNo
    description.value  = data.description
    createdAt.value    = data.createdAt
    thumbnailUrl.value = data.thumbnailPath

    // 타입별
    if (templateType === 'EDITOR') {
      selectedTab.value   = 'ai'
      editorContent.value = data.content
      editorVariables.value = JSON.parse(data.varJson || '[]')
    } else {
      selectedTab.value   = 'upload'
      uploadedFiles.value = JSON.parse(data.pathJson || '[]')
    }
  } catch (e) {
    console.error(e)
    alert('템플릿 정보를 불러오지 못했습니다.')
    router.back()
  }
})
</script>

<template>
  <LawyerFrame>
    <div class="container py-3">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">템플릿 상세 조회</h3>

        <div>
          <button class="btn btn-outline-primary me-2" @click="goEdit">수정</button>
          <button class="btn btn-outline-danger" @click="handleDelete">삭제</button>
        </div>
      </div>

      <!-- 공통 정보 카드 -->
      <div class="card p-4 mb-4">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">썸네일</label>
            <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
              <img
                  v-if="thumbnailUrl"
                  :src="thumbnailUrl"
                  alt="썸네일"
                  class="img-fluid h-100"
                  style="object-fit: contain"
              />
              <p v-else class="text-muted text-center">이미지 없음</p>
            </div>
          </div>
          <div class="col-md-8">
            <label class="form-label">상품명</label>
            <input v-model="name" disabled class="form-control mb-2" />

            <label class="form-label">가격 (원)</label>
            <input v-model="price" disabled type="number" class="form-control mb-2" />

            <label class="form-label">할인율 (%)</label>
            <input v-model="discountRate" disabled type="number" class="form-control mb-2" />

            <label class="form-label">카테고리</label>
            <select v-model="categoryNo" disabled class="form-select mb-2">
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

        <label class="form-label">상세 설명</label>
        <textarea v-model="description" disabled rows="6" class="form-control"></textarea>
      </div>

      <!-- 본문 / 첨부파일 -->
      <div class="template-wrapper mb-4 p-3">
        <!-- AI 타입일 때 -->
        <AiTemplateEditor
            v-if="selectedTab === 'ai'"
            :content="editorContent"
            :variables="editorVariables"
            :is-detail=true
        />

        <!-- 파일 타입일 때 -->
        <div v-else>
          <h5 class="mb-3">첨부 파일</h5>
          <ul class="list-group">
            <li
                v-for="(file, i) in uploadedFiles"
                :key="i"
                class="list-group-item d-flex justify-content-between align-items-center"
            >
              <a :href="file.savedPath" target="_blank">{{ file.originalName }}</a>
            </li>
          </ul>
          <p v-if="!uploadedFiles.length" class="text-muted mt-2">
            첨부된 파일이 없습니다.
          </p>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.preview-box {
  width: 100%;
  height: 285px;
  background-color: #f8f9fa;
  border: 1px dashed #ccc;
  overflow: hidden;
}
.template-wrapper {
  background-color: #f7f8fb;
  border: 1px solid #d6dbe8;
  border-radius: 0.5rem;
}
</style>
