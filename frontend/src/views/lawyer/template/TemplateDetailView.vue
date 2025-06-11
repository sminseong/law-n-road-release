<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'

const route = useRoute()
const router = useRouter()

const templateNo = route.params.no

const name = ref('')
const price = ref('')
const discount_rate = ref('')
const categoryName = ref('')
const description = ref('')
const previewUrl = ref(null)

onMounted(() => {
  const templates = JSON.parse(localStorage.getItem('templateList') || '[]')
  const selected = templates.find(t => String(t.no) === String(templateNo))
  if (selected) {
    name.value = selected.name
    price.value = selected.price
    discount_rate.value = selected.discountRate
    categoryName.value = selected.categoryName
    description.value = selected.description || ''
    previewUrl.value = selected.imageUrl
  }
})
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">템플릿 상세 조회</h3>
      <div class="card p-4">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">상품 이미지</label>
            <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
              <img v-if="previewUrl" :src="previewUrl" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
              <span v-else class="text-muted">이미지가 없습니다</span>
            </div>
          </div>
          <div class="col-md-8">
            <label class="form-label">상품명</label>
            <div class="form-control mb-2">{{ name }}</div>
            <label class="form-label">금액</label>
            <div class="form-control mb-2">{{ price }}</div>
            <label class="form-label">할인율</label>
            <div class="form-control mb-2">{{ discount_rate }}</div>
            <label class="form-label">카테고리</label>
            <div class="form-control">{{ categoryName }}</div>
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">상세 설명</label>
          <div class="form-control" style="min-height: 120px">{{ description }}</div>
        </div>
        <div class="d-flex justify-content-end">
          <button class="btn btn-secondary" @click="router.push('/lawyer/templates')">목록으로</button>
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
