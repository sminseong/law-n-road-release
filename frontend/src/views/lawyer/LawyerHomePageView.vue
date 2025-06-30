<script setup>
import HomepageFrame from "@/components/layout/homepage/HomepageFrame.vue"
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'
import ProductCard from "@/components/common/ProductCard.vue";
import CardTable from "@/components/table/CardTable.vue";

const route = useRoute()
const data = ref({
  name: '',
  shortIntro: '',
  longIntro: '',
  email: '',
  officePhone: '',
  officeAddress: '',
  officeName: '',
  consultPrice: 0,
  profileImagePath: '',
  recentTemplates: [],
  recentBoards: [],
  lawyerNo: null
})

const productList = computed(() => {
  return (data.value.recentTemplates || []).map(tmpl => {
    const price = tmpl.price || 0
    const discountRate = tmpl.discountRate || 0
    const discountedPrice = Math.floor(price * (1 - discountRate / 100))

    return {
      no: tmpl.no,
      title: tmpl.name,
      imageUrl: tmpl.thumbnailPath || 'https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png',
      originalPrice: `${price.toLocaleString()}원`,
      discountPercent: `${discountRate}%`,
      discountedPrice: `${discountedPrice.toLocaleString()}원`,
    }
  })
})

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
          <div class="card shadow-sm" style="min-height: 400px; max-height: 400px;">

            <!-- 변호사 정보 -->
            <div class="d-flex align-items-start pb-5 position-relative bg-light-primary" style="min-height: 55px;">
              <div class=" mb-2 mt-5 me-4 ms-4">
                <strong class="fw-semibold fs-1" style="color: #5a5a78;"> {{ data.shortIntro }} </strong>
                <br />
                <strong class="fw-semibold fs-5 ms-3"> {{ data.name }} 변호사 </strong>
              </div>
            </div>

            <!--pre 태그 엔터, 띄어쓰기 그대로 반영, 쓸데없이 엔터 금지-->
            <div class="d-flex align-items-start justify-content-between gap-4 mb-5 mt-5 me-4 ms-4">
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
                         font-family: inherit;
                         min-height: 160px;
                         max-height: 160px;
                         overflow-y: auto;">{{ data.longIntro }}</pre>
            </div>


            <router-link
                v-if="data.lawyerNo"
                :to="{ name: 'ClientReservations', params: { lawyerNo: data.lawyerNo, lawyerName: data.name } }"
                class="btn btn-primary w-auto text-center ms-4 me-4 mb-3"
            >
              전화상담 예약하러 가기
            </router-link>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-12">
          <div class="card shadow-sm mb-4 p-4 d-flex">
            <h5 class="fw-bold">{{ data.name }} 변호사의 방송 다시보기</h5>
            <p class="mb-0">{{  }}</p>
          </div>

          <div class="card shadow-sm mb-4 p-4 d-flex">
            <h5 class="fw-bold">{{ data.name }} 변호사의 법률 템플릿</h5>
            <p class="mb-0">{{  }}</p>

            <div v-if="productList.length > 0" class="row g-4 row-cols-lg-5 row-cols-2 row-cols-md-3">
              <div class="col-md-3 mb-4" v-for="product in productList" :key="product.no">
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

            <div v-else class="text-center text-muted py-5">
              등록된 상품이 없습니다.
            </div>
          </div>

          <div class="card shadow-sm mb-4 p-4 d-flex">
            <h5 class="fw-bold">{{ data.name }} 변호사가 답변한 상담글</h5>
            <CardTable v-if="data.recentBoards.length > 0" :List="data.recentBoards" :maxLines="4" />

            <div v-else class="text-center text-muted py-5">
              답변한 게시글이 없습니다.
            </div>
          </div>
        </div>
      </div>
    </div>
  </HomepageFrame>
</template>
