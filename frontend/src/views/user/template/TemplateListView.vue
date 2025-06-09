<script setup>
import { ref, computed, onMounted } from 'vue'
import http from '@/libs/HttpRequester'
import ProductCard from '@/components/common/ProductCard.vue'
import UserFrame from "@/components/layout/User/UserFrame.vue";

// 상태 정의
const fullList = ref([])
const currentPage = ref(1)
const rowsPerPage = 20

// API 호출
onMounted(async () => {
  try {
    const res = await http.get('/api/templates') // 변호사/유저 구분 없이 전체 템플릿
    console.log('이미지 URL 확인:', res)
    fullList.value = res.data.map(item => ({
      no: item.no,
      imageUrl: item.thumbnail_path,
      title: item.name,
      originalPrice: item.price,
      discountPercent: item.discount_rate ?? 0,
      discountedPrice: Math.floor(item.price * (1 - (item.discount_rate ?? 0) / 100))
    }))
  } catch (e) {
    console.error('전체 템플릿 목록 조회 실패:', e)
  }
})

// 페이지네이션 계산
const totalPages = computed(() => Math.ceil(fullList.value.length / rowsPerPage))

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * rowsPerPage
  return fullList.value.slice(start, start + rowsPerPage)
})

// 페이지 변경
function changePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}
</script>

<template>
  <userFrame>
    <div class="container py-5">
      <div class="row mb-4">
        <div class="col-12">
          <h3 class="fw-bold">법률 템플릿</h3>
        </div>
      </div>

      <div class="row g-4 row-cols-lg-5 row-cols-md-3 row-cols-2">
        <div class="col" v-for="product in paginatedList" :key="product.no">
          <ProductCard
              :no="product.no"
              :imageUrl="product.imageUrl"
              :title="product.title"
              :originalPrice="product.originalPrice"
              :discountPercent="product.discountPercent"
              :discountedPrice="product.discountedPrice"
          />
        </div>
      </div>

      <div class="d-flex justify-content-center mt-4">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <a class="page-link" @click="changePage(currentPage - 1)">이전</a>
          </li>
          <li class="page-item" v-for="n in totalPages" :key="n" :class="{ active: currentPage === n }">
            <a class="page-link" @click="changePage(n)">{{ n }}</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <a class="page-link" @click="changePage(currentPage + 1)">다음</a>
          </li>
        </ul>
      </div>
    </div>
  </userFrame>
</template>

<style scoped>
.container {
  background-color: #fff;
}

.page-link {
  cursor: pointer;
}
</style>
