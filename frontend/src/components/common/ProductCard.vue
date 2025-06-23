<script setup>
import { useRouter } from 'vue-router'
import http from "@/libs/HttpRequester.js";

const router = useRouter()

const props = defineProps({
  no: Number,
  imageUrl: String,
  title: String,
  originalPrice: [String, Number],
  discountPercent: [String, Number],
  discountedPrice: [String, Number],
})

function goToDetail() {
  router.push(`/templates/${props.no}`)
}

// 장바구니 함수
const handleAddToCart = async (e) => {
  e.stopPropagation()  // 카드 클릭 이벤트와 중첩되지 않도록 방지

  const accountType = localStorage.getItem('accountType')

  if (!accountType) {
    alert('로그인이 필요합니다.')
    // return router.push(`/login?redirect=${encodeURIComponent(router.currentRoute.value.fullPath)}`)
    return
  }

  if (accountType === 'lawyer') {
    alert('변호사 계정은 장바구니를 이용할 수 없습니다.')
    return
  }

  try {
    await http.post('/api/client/cart', {
      tmplNo: props.no
    })

    const goToCart = confirm('장바구니에 상품이 추가되었습니다.\n장바구니로 이동하시겠습니까?')
    if (goToCart) {
      await router.push('/client/cart')
    }
  } catch (err) {
    if (err.response?.status === 409) {
      alert('이미 장바구니에 담긴 상품입니다.')
    } else {
      console.error('장바구니 추가 실패:', err)
      alert('장바구니 추가 중 오류가 발생했습니다.')
    }
  }
}
</script>

<template>
  <div
      class="card card-product rounded-3 position-relative overflow-hidden"
      @click="goToDetail"
      style="cursor: pointer;"
  >
    <!-- 이미지 -->
    <div class="text-center" style="cursor: default;">
      <img
          :src="props.imageUrl"
          class="img-fluid"
          style="height: 160px; object-fit: cover;"
          alt="상품 이미지"
      />
    </div>

    <!-- 텍스트 내용 -->
    <div class="card-body px-2 py-3" style="cursor: default; position: relative;">
      <p
          class="lh-sm mb-2 fs-5-1"
          style="height: 38px; overflow: hidden; text-overflow: ellipsis;
               display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;"
      >
        {{ props.title }}
      </p>

      <p class="text-gray-400 lh-sm text-decoration-line-through fs-6 mb-1">
        {{ props.originalPrice }}
      </p>

      <div class="d-flex align-items-center justify-content-between">
        <p class="fw-bold mb-0 d-flex align-items-center" style="font-size: 18px;">
          <span class="text-danger me-2">{{ props.discountPercent }}%</span>
          <span class="text-dark">{{ props.discountedPrice }}</span>
        </p>

        <!-- 장바구니 버튼: z-index로 링크보다 위에 올림 -->
        <a
            @click="handleAddToCart"
            class="btn btn-outline-primary btn-sm ms-2 cart-btn"
            style="cursor: pointer; position: relative; z-index: 2;"
        >
          장바구니
        </a>
      </div>

      <!-- 카드 전체 클릭용 링크 (뒤에 위치) -->
      <span class="stretched-link" style="z-index: 1;"></span>
    </div>
  </div>
</template>
