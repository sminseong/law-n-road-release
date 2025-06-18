<script setup>
import HomepageFrame from "@/components/layout/homepage/HomepageFrame.vue"
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'

const route = useRoute()
const tmplNo = route.params.tmplNo

async function fetchLawyerHomepage(lawyerNo) {
  try {
    const res = await http.get(`/api/public/homepage/${lawyerNo}`)
    console.log(res.data) // 또는 return response.data;
  } catch (error) {
    console.error('❌ 변호사 홈화면 불러오기 실패:', error)
  }
}

onMounted(async () => {
  fetchLawyerHomepage(1);
})
const template = ref({
  name: '교통사고 합의서 양식',
  price: 30000,
  discountRate: 20,
  salesCount: 128,
  thumbnailPath: 'https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/profile.png',
  profile: 'https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/profile.png',
  lawyerName: '김수영',
  lawyerIntro: '36년 경력의 교통사고 전문 변호사입니다.',
  type: 'EDITOR',
  userNo: 1
})

function handleAddToCart() {
  alert("장바구니에 추가되었습니다.") // TODO: 장바구니 API 연결
}
</script>

<template>
  <HomepageFrame>
    <div class="container py-5">
      <div class="row g-4 mb-4">
        <!-- 썸네일 -->
        <div class="col-md-4">
          <div class="card shadow-sm h-100">
            <img :src="template.thumbnailPath" class="card-img-top" alt="프로필" style="object-fit: cover; height: 100%; max-height: 400px;">
          </div>
        </div>

        <!-- 오른쪽 카드 -->
        <div class="col-md-8">
          <div class="card shadow-sm p-4">
            <!-- 🔹 누적 판매수 -->
            <div class="text-muted text-end small mb-2">
              누적 판매수: {{ template.salesCount }}건
            </div>

            <!-- 🔹 변호사 정보 -->
            <div class="d-flex align-items-start mb-3 position-relative" style="min-height: 55px;">
              <img
                  :src="template.profile"
                  class="rounded-circle me-3"
                  style="width: 50px; height: 50px; object-fit: cover;"
              />
              <div>
                <strong class="fw-semibold">{{ template.lawyerName }} 변호사 | {{ template.lawyerIntro }}</strong><br />
                <small class="text-muted">템플릿 유형: {{ template.type }}</small>
              </div>
              <a
                  :href="`/lawyer/${template.userNo}`"
                  class="text-muted small text-decoration-underline me-2"
                  style="position: absolute; bottom: 0; right: 0;"
              >
                프로필 보러가기
              </a>
            </div>

            <hr>

            <h1 class="fw-bold mb-2">{{ template.name }}</h1>

            <div class="d-flex align-items-baseline mb-3">
              <span class="text-danger fw-bold fs-3">{{ template.discountRate }}%</span>
              <div class="d-flex align-items-baseline ms-auto">
                <del class="text-muted me-2 fs-6">{{ template.price.toLocaleString() }}원</del>
                <span class="text-danger fw-bold fs-3">
                  {{ (template.price * (1 - template.discountRate / 100)).toLocaleString() }}원
                </span>
              </div>
            </div>

            <div class="mt-4 d-flex gap-2">
              <button class="btn btn-primary flex-fill">전화상담 예약하러 가기</button>
            </div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{}} 변호사의 방송 다시보기</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{}} 변호사의 법률 템플릿</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{}} 변호사가 답변한 상담글</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{}} 변호사의 사무실 정보</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>
    </div>
  </HomepageFrame>
</template>
