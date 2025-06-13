<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

// 상태
const route = useRoute()
const template = ref(null)

onMounted(async () => {
  console.log('templateNo:', route.params.no)
  try {
    const templateNo = route.params.no

    const res = await http.get(`/api/client/templates/${templateNo}`)
    template.value = res.data
    console.log('template:', template.value)
    console.log('template.value:', template.value)
  } catch (err) {
    console.error('템플릿 조회 실패:', err)
  }
})
</script>
<template>
  <ClientFrame>
    <div class="container py-5" v-if="template">
      <!-- 상단: 썸네일 + 템플릿 정보 -->
      <div class="row g-4 mb-4">
        <!-- 썸네일 -->
        <div class="col-md-5">
          <div class="card shadow-sm h-100">
            <img :src="template.thumbnailPath" class="card-img-top" alt="썸네일" style="object-fit: cover; height: 100%; max-height: 400px;">
          </div>
        </div>

        <!-- 템플릿 정보 -->
        <div class="col-md-7">
          <div class="card shadow-sm h-100 p-4 d-flex flex-column justify-content-between">

            <!-- 프로필 & 카테고리 -->
            <div class="d-flex justify-content-between align-items-start mb-2">
              <div>
                <p class="text-muted mb-1">{{ template.categoryName }} / 누적 판매 {{ template.salesCount }}건</p>
              </div>
            </div>

            <!-- 제목 -->
            <h2 class="fw-bold fs-1 mb-3">{{ template.name }}</h2>

            <!-- 가격 정보 -->
            <div class="my-2">
              <span class="text-danger h5">{{ template.discountRate }}%</span>
              <span class="text-muted text-decoration-line-through ms-2">{{ template.price.toLocaleString() }}원</span>
              <div class="h4 fw-bold mt-1">
                {{ (template.price * (1 - template.discountRate / 100)).toLocaleString() }}원
              </div>
            </div>

            <!-- 변호사 정보 -->
            <div class="mb-3">
              <div v-if="template.lawyerProfileImage" class="ms-3">
                <img :src="template.lawyerProfileImage" alt="변호사 프로필" class="rounded-circle" style="width: 60px; height: 60px; object-fit: cover;">
              </div>
              <strong>판매자:</strong> {{ template.lawyerName }}<br />
              <small class="text-muted">{{ template.lawyerIntro }}</small><br />
              <a :href="`/lawyer/profile/${template.lawyerNo}`">프로필 보기</a>
              <span v-if="template.hasLive" class="badge bg-success ms-2">LIVE 가능</span>
            </div>

            <!-- 버튼 -->
            <div class="d-flex gap-2 mt-auto">
              <button class="btn btn-primary flex-fill">구매하기</button>
              <button class="btn btn-outline-secondary flex-fill">장바구니</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 상품 설명 -->
      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">상품 설명</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <!-- 변호사 상세 설명 -->
      <div class="card shadow-sm p-4">
        <h5 class="fw-bold">변호사 경력 등 상세 설명</h5>
        <p class="mb-0">{{ template.lawyerDetail }}</p>
      </div>
    </div>
  </ClientFrame>
</template>