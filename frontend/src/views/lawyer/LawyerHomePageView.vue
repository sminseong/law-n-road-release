<script setup>
import HomepageFrame from "@/components/layout/homepage/HomepageFrame.vue"
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'

const route = useRoute()
const data = ref({})

async function fetchLawyerHomepage(lawyerNo) {
  try {
    console.log(lawyerNo)
    const res = await http.get(`/api/public/homepage/${lawyerNo}`)
    console.log(res.data) // 또는 return response.data;
    data.value = res.data
  } catch (error) {
    console.error('❌ 변호사 홈화면 불러오기 실패:', error)
  }
}

onMounted(async () => {
  fetchLawyerHomepage(route.params.lawyerNo);
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
            <img :src="data.profileImagePath" class="card-img-top" alt="프로필" style="object-fit: cover; height: 100%; max-height: 400px;">
          </div>
        </div>

        <!-- 오른쪽 카드 -->
        <div class="col-md-8">
          <div class="card shadow-sm p-4">
            <!-- 🔹 누적 판매수 -->
            <div class="text-muted text-end small mb-2">

            </div>

            <!-- 🔹 변호사 정보 -->
            <div class="d-flex align-items-start mb-3 position-relative" style="min-height: 55px;">
              <div>
                <strong class="fw-semibold fs-1"> {{ data.shortIntro }} </strong>
                <br />
                <br />
                <strong class="fw-semibold fs-5"> {{ data.name }} 변호사 </strong>
              </div>
            </div>

            <hr>

            <!--pre 태그 엔터, 띄어쓰기 그대로 반영, 쓸데없이 엔터 금지-->
            <div class="d-flex align-items-start justify-content-between gap-4 mb-8 mt-2">
              <!-- 왼쪽 -->
              <div class="ms-3 small w-100 w-md-50">
                <div><strong>이메일</strong> {{ data.email }}</div>
                <div><strong>전화</strong> {{ data.officePhone }}</div>
                <br>
                <div><strong>{{ data.officeName }}</strong><br>
                  {{ data.officeAddress }}
                </div>
                <br>
                <div><strong>상담비용</strong> {{ data.consultPrice }} 원</div>
              </div>

              <!-- 오른쪽 -->
              <pre class="w-100 w-md-50 mb-0"
                   style="white-space: pre-wrap;
               word-break: break-word;
               font-family: inherit;">{{ data.longIntro }}</pre>
            </div>


            <router-link
                :to="{ name: 'ClientReservations', params: { lawyerNo: 1, lawyerName: '김민수' } }"
                class="btn btn-primary w-100 text-center"
            >
              전화상담 예약하러 가기
            </router-link>
          </div>
        </div>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{ data.name }} 변호사의 방송 다시보기</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{ data.name }} 변호사의 법률 템플릿</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>

      <div class="card shadow-sm mb-4 p-4">
        <h5 class="fw-bold">{{ data.name }} 변호사가 답변한 상담글</h5>
        <p class="mb-0">{{ template.description }}</p>
      </div>
    </div>
  </HomepageFrame>
</template>
