<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

// 상태
const route = useRoute()
const template = ref(null)

onMounted(async () => {
  const res = await http.get(`/api/client/templates/${route.params.id}`)
  template.value = res.data
})
</script>

<template>
  <ClientFrame>
    <div class="container py-5" v-if="template">
      <div class="row mb-4">
        <!-- 썸네일 -->
        <div class="col-md-5">
          <img :src="template.thumbnailPath" class="img-fluid rounded border" alt="템플릿 썸네일" />
        </div>

        <!-- 템플릿 정보 -->
        <div class="col-md-7">
          <p class="text-muted mb-1">
            {{ template.categoryName }} / 누적 판매 {{ template.salesCount }}건
          </p>
          <h4 class="fw-bold">{{ template.name }}</h4>

          <div class="my-3">
            <span class="text-danger h5">{{ template.discountRate }}%</span>
            <span class="text-muted text-decoration-line-through ms-2">{{ template.price.toLocaleString() }}원</span>
            <div class="h4 fw-bold mt-2">
              {{ (template.price * (1 - template.discountRate / 100)).toLocaleString() }}원
            </div>
          </div>

          <!-- 판매자 정보 -->
          <div class="mb-4">
            <strong>판매자:</strong> {{ template.lawyerName }}
            <div class="text-muted small mt-1">
              {{ template.lawyerIntro }}
              <br />
              <a :href="`/lawyer/profile/${template.lawyerNo}`">프로필 보기</a>
              <span v-if="template.hasLive" class="ms-2 badge bg-success">LIVE 가능</span>
            </div>
          </div>

          <!-- 버튼 -->
          <div class="d-flex gap-3">
            <button class="btn btn-primary">구매하기</button>
            <button class="btn btn-outline-secondary">장바구니</button>
          </div>
        </div>
      </div>

      <!-- 상품 설명 -->
      <div class="mb-4">
        <h5 class="fw-bold">상품 설명</h5>
        <p>{{ template.description }}</p>
      </div>

      <!-- 변호사 상세 설명 -->
      <div>
        <h5 class="fw-bold">변호사 경력 등 상세 설명</h5>
        <p>{{ template.lawyerDetail }}</p>
      </div>
    </div>
  </ClientFrame>
</template>
