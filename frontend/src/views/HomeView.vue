<script setup>
  import ClientFrame from '@/components/layout/client/ClientFrame.vue'
  import MainBannerSlider from '@/components/common/MainBannerSlider.vue'
  import RoundCategory from '@/components/common/RoundCategory.vue'
  import LiveBroadcastCard from '@/components/common/LiveBroadcastCard.vue'
  import CardSlider from '@/components/common/CardSlider.vue'
  import CardTable from '@/components/table/CardTable.vue'
  import ProductCard from '@/components/common/ProductCard.vue'
  import AdBannerPair from '@/components/common/SubBannerSlider.vue'

  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  const router = useRouter()
  const nickname = ref('')
  const isLoggedIn = ref(false)


  onMounted(() => {
    const token = localStorage.getItem('token')
    const nick = localStorage.getItem('nickname')

    isLoggedIn.value = !!token
    if (nick && nick !== 'null') {
      nickname.value = nick
    }
  })

  // 로그아웃 처리
  const logout = async () => {
    const userNo = localStorage.getItem('no')

    try {
      // ✅ 0. 백엔드에 로그아웃 요청 → RefreshToken 삭제
      if (userNo) {
        await axios.post('/api/auth/logout', { userNo: Number(userNo) })
      }
    } catch (err) {
      console.error('🔴 로그아웃 요청 실패:', err)
    }

    // ✅ 1. 로컬스토리지 항목 삭제
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('accountType')
    localStorage.removeItem('name')
    localStorage.removeItem('nickname')
    localStorage.removeItem('no')

    // ✅ 2. Axios 인증 헤더 제거
    delete axios.defaults.headers.common['Authorization']

    // ✅ 3. 프론트 상태 초기화
    isLoggedIn.value = false
    nickname.value = '회원'

    // ✅ 4. 콘솔 로그 출력: 삭제 여부 확인
    console.log('[로그아웃 완료] localStorage 상태 확인:')
    console.log('token:', localStorage.getItem('token'))
    console.log('refreshToken:', localStorage.getItem('refreshToken'))
    console.log('accountType:', localStorage.getItem('accountType'))
    console.log('name:', localStorage.getItem('name'))
    console.log('nickname:', localStorage.getItem('nickname'))

    // ✅ 5. 로그인 페이지로 이동 + 새로고침
    router.push('/login')
    setTimeout(() => location.reload(), 300)
  }

  // 메인 베너
  const mainBanners = [
    {
      title: '횡단보도 뺑소니 전문<br />김수영 변호사',
      desc: '전문가와 함께라면 사고 처리도, 합의도<br />더 이상 어렵지 않습니다.',
      image: '/img/ads/slider-1-1.png',
      link: '/lawyer.html',
      lawyerNo:'1',
      lawyerName: '김수영'
      // badge 생략 → 본문에서 전달한 defaultBadgeText 사용됨
    },
    {
      title: '교통사고 전문<br />정은혜 변호사',
      desc: '신속한 대응과 확실한 전략으로<br />당신의 권리를 지켜드립니다.',
      image: '/img/ads/slider-2-1.png',
      link: '/lawyer.html',
      lawyerNo:'2',
      lawyerName: '정은혜',
      badge: '교통사고 전문 상담'
    }
  ]

  // 동그라미 카테고리
  const roundCategories = [
    { icon: 'fas fa-car-crash', label: '사고 발생/처리', link: '/search.html' },
    { icon: 'fas fa-balance-scale', label: '중대사고·형사처벌', link: '/search.html' },
    { icon: 'fas fa-beer', label: '음주·무면허 운전', link: '/search.html' },
    { icon: 'fas fa-gavel', label: '보험·행정처분', link: '/search.html' },
    { icon: 'fas fa-search', label: '과실 분쟁', link: '/search.html' },
    { icon: 'fas fa-bicycle', label: '차량 외 사고', link: '/search.html' }
  ]

  // 라이브 방송박스 (대기화면)
  const liveBroadcast = {
    thumbnail: '/img/vod/thumbnails/thumbnail_waiting.png',
    title: '뺑소니 합의금 - 이것만 기억하세요!',
    tags: ['합의', '뺑소니', '음주뺑소니'],
    hostImage: '/img/profiles/kim.png',
    hostName: '김서연 변호사',
    hostDesc: '교통사고 전문',
    link: '/live.html'
  }

  // 라이브 방송박스 (라이브온)
  const liveBroadcast2 = {
    isLive: true,
    videoEmbedUrl: 'https://www.youtube.com/embed/jfKfPfyJRdk?si=ldIipCvzo-aAVoKa',
    thumbnail: '/assets/images/thumbnail_waiting.png',
    title: '음주운전 대응 전략 공개!',
    tags: ['합의', '뺑소니', '음주뺑소니'],
    hostImage: '/img/profiles/kim.png',
    hostName: '김서연 변호사',
    hostDesc: '교통사고 전문',
    link: '/live.html'
  }

  // VOD 다시보기
  const vodList = [
    {
      no: 1,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      no: 2,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      no: 3,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },
    {
      no: 4,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      no: 5,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      no: 6,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },

    {
      no: 1,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      no: 2,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      no: 3,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },
    {
      no: 4,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      no: 5,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      no: 6,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    }
  ]

  // qna 테이블
  const qnaSampleList = [
  {
    no: 1,
    question: '음주운전 삼진아웃이 세 번까지 봐준다는 말인가요?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며...'
  },
  {
    no: 2,
    question: '무면허 운전.. 구제 가능한가요...?',
    answerPreview: '구제 가능 여부는 사안에 따라 다르며, 음주·무면허 사안은...'
  },
  {
    no: 3,
    question: '음주운전 삼진아웃이 세 번까지 봐준다는 말인가요?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며...'
  },
  {
    no: 4,
    question: '무면허 운전.. 구제 가능한가요...?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 구제 가능 여부는 사안에 따라 다르며, 음주·무면허 사안은...'
  },
  {
    no: 5,
    question: '음주운전 삼진아웃이 세 번까지 봐준다는 말인가요?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 .'
  }
]



// 공용 테이블 보기
const columns = [
  { label: '이름', key: 'name' },
  { label: '나이', key: 'age' }
]

const fullData = Array.from({ length: 300 }, (_, i) => ({
  no: i + 1,
  name: `홍길동 ${i + 1}`,
  age: 20 + (i % 10) // 20~29 반복
}))

// 더미 fetch 함수 (slice + total info)
const loadFn = async ({ page, size }) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const start = page * size
      const content = fullData.slice(start, start + size)

      resolve({
        status: 200,
        data: {
          content,
          number: page,
          totalPages: Math.ceil(fullData.length / size),
          totalElements: fullData.length
        }
      })
    }, 200)
  })
}

  const productList = [
    {
      no: 42,
      title: '1인 민사소송 키트 음주운전 관련 합의서 포함 설명서 세트',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '20,000원',
      discountPercent: '44%',
      discountedPrice: '11,200원',
    },
    {
      no: 108,
      title: '내용증명 작성 가이드 + 샘플 문서 모음',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '15,000원',
      discountPercent: '33%',
      discountedPrice: '10,000원',
    },
    {
      no: 203,
      title: '임대차 계약서 세트 (상가/주택 전용)',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '25,000원',
      discountPercent: '40%',
      discountedPrice: '15,000원',
    },
    {
      no: 304,
      title: '이혼 합의서 양식 + 재산분할 설명서',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '18,000원',
      discountPercent: '28%',
      discountedPrice: '13,000원',
    },
    {
      no: 405,
      title: '지급명령 신청서 + 설명서 세트',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '17,000원',
      discountPercent: '30%',
      discountedPrice: '11,900원',
    },
    {
      no: 506,
      title: '교통사고 합의서 키트 + 보험사 응대 매뉴얼',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '22,000원',
      discountPercent: '36%',
      discountedPrice: '14,000원',
    },
    {
      no: 607,
      title: '채무 변제 각서 + 확약서 작성 가이드',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '12,000원',
      discountPercent: '25%',
      discountedPrice: '9,000원',
    },
    {
      no: 708,
      title: '고소장 작성 키트 (형사 고소 전용)',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '19,000원',
      discountPercent: '31%',
      discountedPrice: '13,100원',
    },
    {
      no: 809,
      title: '위임장/동의서 통합 세트',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '14,000원',
      discountPercent: '20%',
      discountedPrice: '11,200원',
    },
    {
      no: 910,
      title: '내용증명 반송 대응 키트 + 체크리스트',
      imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
      originalPrice: '16,000원',
      discountPercent: '35%',
      discountedPrice: '10,400원',
    },
  ]

  const banners = [
    {
      title: '형사 전문 변호사 이민수',
      description: '상담시 최대 ',
      highlight: '30% 할인',
      image: '/img/ads/slider-image-1.jpg',
      lawyerNo: 101,
      lawyerName: '이민수',
    },
    {
      title: '교통사고 합의 전문가 김하늘',
      description: '첫 의뢰 시 ',
      highlight: '무료 전화상담 제공',
      image: '/img/ads/slider-image-3.jpg',
      lawyerNo: 205,
      lawyerName: '이민수',
    },
  ]
</script>

<template>
  <!-- 의뢰인 타입 본문 콘텐츠 -->
  <ClientFrame>
<!--    <p>-->
<!--      <a href="/lawyer">변호사 대시보드 이동하기</a>-->
<!--    </p>-->

    <!-- ① 예약 신청 섹션 추가 -->
<!--    <section class="my-8 p-4 bg-gray-50 rounded">-->
<!--      <h3 class="text-xl font-semibold mb-2">상담 예약 신청하기</h3>-->
<!--      <div class="flex space-x-4">-->
<!--        &lt;!&ndash; 여기에 실제 변호사 리스트를 넣어도 되고, 테스트용으로 하드코딩해도 됩니다. &ndash;&gt;-->
<!--        <router-link-->
<!--            :to="{ name: 'ClientReservations', params: { lawyerNo: 1, lawyerName: '김민수' } }"-->
<!--            class="px-3 py-1 "-->
<!--        >-->
<!--          김민수 변호사 예약하기-->
<!--        </router-link>-->
<!--      </div>-->
<!--    </section>-->

    <!-- 메인 베너 -->
    <MainBannerSlider :banners="mainBanners" defaultBadgeText="로앤로드 대표 서비스" />

    <!-- 동그라미 카테고리 -->
    <RoundCategory :categories="roundCategories" />

    <!-- 라이브 방송박스 -->
    <div class="row">
        <div class="col-12 mb-6">
          <div class="d-flex justify-content-between align-items-center">
            <h3 class="mb-0">변호사와 함께하는 실시간 라이브 방송</h3>
            <h5 class="mb-0 text-muted me-3" style="cursor: pointer;">더 보러가기 ></h5>
          </div>
        </div>
      </div>

    <!-- <LiveBroadcastCard :broadcast="liveBroadcast" /> -->
    <LiveBroadcastCard :broadcast="liveBroadcast2" />

    <!-- VOD 방송 다시보기 -->
    <div class="row">
      <div class="col-12 mb-6">
        <div class="d-flex justify-content-between align-items-center">
          <h3 class="mb-0">VOD 방송 다시보기</h3>
          <h5 class="mb-0 text-muted me-3" style="cursor: pointer;">더 보러가기 ></h5>
        </div>
      </div>
    </div>
    <!-- <CardSlider :replayItems="vodList" :itemsPerRow="4" :rowsPerSlide="1" /> -->
    <CardSlider :replayItems="vodList" :itemsPerRow="3" :rowsPerSlide="2" />

    <!-- QNA 게시판 테이블 -->
    <div class="row">
      <div class="col-12 mb-6">
        <div class="d-flex justify-content-between align-items-center">
          <h3 class="mb-0">나와 비슷한 사례 찾아보기</h3>
          <a href="/qna">
            <h5 class="mb-0 text-muted me-3" style="cursor: pointer;">더 보러가기 ></h5>
          </a>
        </div>
      </div>
    </div>
    <CardTable :List="qnaSampleList" :maxLines="4" />

    <!-- 서브 베너 -->
    <AdBannerPair :banners="banners" />

    <!-- 템플릿 상품 판매 -->
    <div class="row">
      <div class="col-12 mb-6">
        <div class="d-flex justify-content-between align-items-center">
          <h3 class="mb-0">많이 찾는 법률문서 베스트 10</h3>
          <a href="/templates">
            <h5 class="mb-0 text-muted me-3" style="cursor: pointer;">더 보러가기 ></h5>
          </a>
        </div>
      </div>
    </div>

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

  </ClientFrame>

</template>

<style scoped>

</style>
