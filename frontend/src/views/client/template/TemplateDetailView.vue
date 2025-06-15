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

// 장바구니 함수
const handleAddToCart = async () => {
  const accountType = localStorage.getItem('accountType')  // 또는 Pinia에서 가져올 수도 있음

  if (!accountType) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  try {
    await http.post('/api/cart/templates', {
      templateNo: template.value.no
    })
    alert('장바구니에 추가되었습니다.')
  } catch (err) {
    console.error('장바구니 추가 실패:', err)
    alert('이미 장바구니에 있는 상품입니다.')
  }
}

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

        <!-- 오른쪽 카드 -->
        <div class="col-md-7">
          <div class="card shadow-sm h-100 p-4 d-flex flex-column">

            <!-- 🔹 누적 판매수 (상단 작게) -->
            <div class="text-muted text-end small mb-2">

            </div>

            <!-- 🔹 변호사 정보 -->
            <div class="d-flex align-items-start mb-3 position-relative" style="min-height: 55px;">
              <!-- 프로필 이미지 -->
              <img
                  v-if="template.profile"
                  :src="template.profile"
                  alt="프로필"
                  class="rounded-circle me-3"
                  style="width: 50px; height: 50px; object-fit: cover;"
              />

              <!-- 변호사 이름 + 설명 -->
              <div>
                <strong class="fw-semibold">
                  {{ template.lawyerName }} 변호사 | 교통사고 1위, 36년 경력을 바탕으로 신뢰를 드립니다
                </strong><br />
                <small class="text-muted">
                  {{ template.type === 'EDITOR' ? 'AI 생성형 템플릿' : '문서 기반 템플릿' }} /
                  {{ template.categoryName }}
                </small>
              </div>

              <!-- 👉 오른쪽 하단에 고정된 링크 -->
              <a
                  :href="`/lawyers/${template.userNo}`"
                  class="text-muted small text-decoration-underline me-2"
                  style="position: absolute; bottom: 0; right: 0;"
              >
                프로필 보러가기
              </a>
            </div>

            <!-- 🔹 상품명 -->
            <h1 class="fw-bold mb-2 mt-6">{{ template.name }}</h1>

            <!-- 🔹 가격 -->
            <div class="d-flex align-items-baseline mb-0 mt-auto">
              <span class="text-danger fw-bold fs-1">{{ template.discountRate }}%</span>
              <div class="d-flex align-items-baseline ms-auto">
                <del class="text-muted me-2 fs-5">
                  {{ template.price.toLocaleString() }}원
                </del>
                <span class="text-danger fw-bold fs-1">
                  {{ (template.price * (1 - template.discountRate / 100)).toLocaleString() }}원
                </span>
              </div>
            </div>

            <!-- 🔹 CTA -->
            <div class="mt-5 d-flex gap-2">
              <button class="btn btn-primary flex-fill">구매하기</button>
              <button class="btn btn-outline-secondary flex-fill" @click="handleAddToCart">장바구니</button>
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
        <p class="mb-0"><strong>사무실 주소 :</strong> {{ template.fullAddress }}</p>
        <p class="mb-0"><strong>사무실 번호 :</strong> {{ template.officeNumber }}</p>
      </div>
    </div>
  </ClientFrame>
</template>