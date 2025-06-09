<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'
import ClientFrame from '@/components/layout/client/ClientFrame.vue'

const route = useRoute()
const templateNo = route.params.no

const template = ref(null)

onMounted(async () => {
  try {
    const res = await http.get(`/api/templates/${templateNo}`)
    template.value = res.data
  } catch (e) {
    console.error('템플릿 상세 조회 실패:', e)
  }
})

// 버튼 기능 (임시)
const addToCart = () => alert('장바구니에 담았습니다!')
const purchaseNow = () => alert('결제 페이지로 이동합니다!')
</script>

<template>
  <ClientFrame>
    <div class="container py-5" v-if="template">
      <div class="row">
        <!-- 이미지 -->
        <div class="col-md-6 text-center mb-4 mb-md-0">
          <img
              :src="template.thumbnail_path"
              alt="템플릿 이미지"
              class="img-fluid rounded shadow"
              style="max-height: 400px; object-fit: contain;"
          />
        </div>

        <!-- 상세 정보 -->
        <div class="col-md-6">
          <h2 class="fw-bold mb-3">{{ template.name }}</h2>
          <p class="text-muted mb-2">카테고리: {{ template.category_name }}</p>

          <div class="mb-3">
            <span class="text-muted text-decoration-line-through me-2" v-if="template.discount_rate">
              ₩{{ template.price.toLocaleString() }}
            </span>
            <span class="h4 text-danger fw-bold">
              ₩{{ (template.price * (1 - (template.discount_rate ?? 0) / 100)).toLocaleString() }}
            </span>
            <span v-if="template.discount_rate" class="badge bg-danger ms-2">
              {{ template.discount_rate }}% 할인
            </span>
          </div>

          <p class="lh-lg border p-3 rounded" style="white-space: pre-line;">
            {{ template.description }}
          </p>

          <div class="d-flex gap-3 mt-4">
            <button class="btn btn-outline-primary btn-lg px-4" @click="addToCart">
              장바구니 담기
            </button>
            <button class="btn btn-primary btn-lg px-4" @click="purchaseNow">
              결제하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.container {
  background-color: #fff;
  border-radius: 12px;
}
</style>
