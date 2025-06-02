<script setup>
  import UserFrame from '@/components/layout/User/UserFrame.vue'
  import MainBannerSlider from '@/components/common/MainBannerSlider.vue'
  import RoundCategory from '@/components/common/RoundCategory.vue'
  import LiveBroadcastCard from '@/components/common/LiveBroadcastCard.vue'
  import CardSlider from '@/components/common/CardSlider.vue'
  import CardTable from '@/components/table/CardTable.vue'
  import BasicTable from '@/components/table/BasicTable.vue'

  // 메인 베너
  const mainBanners = [
    {
      title: '횡단보도 뺑소니 전문<br />김수영 변호사',
      desc: '전문가와 함께라면 사고 처리도, 합의도<br />더 이상 어렵지 않습니다.',
      image: '/img/ads/slider-1-1.png',
      link: '/lawyer.html',
      // badge 생략 → 본문에서 전달한 defaultBadgeText 사용됨
    },
    {
      title: '교통사고 전문<br />정은혜 변호사',
      desc: '신속한 대응과 확실한 전략으로<br />당신의 권리를 지켜드립니다.',
      image: '/img/ads/slider-2-1.png',
      link: '/lawyer.html',
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
      id: 1,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      id: 2,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      id: 3,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },
    {
      id: 4,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      id: 5,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      id: 6,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },
    
    {
      id: 1,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      id: 2,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      id: 3,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    },
    {
      id: 4,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '음주운전 대처법',
      link: '/replay.html'
    },
    {
      id: 5,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '합의 시 유의사항',
      link: '/replay.html'
    },
    {
      id: 6,
      thumbnail: '/img/vod/thumbnails/category-dairy-bread-eggs.jpg',
      title: '블랙박스 제출 전략',
      link: '/replay.html'
    }
  ]

  // qna 테이블
  const qnaSampleList = [
  {
    id: 1,
    question: '음주운전 삼진아웃이 세 번까지 봐준다는 말인가요?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며...'
  },
  {
    id: 2,
    question: '무면허 운전.. 구제 가능한가요...?',
    answerPreview: '구제 가능 여부는 사안에 따라 다르며, 음주·무면허 사안은...'
  },
  {
    id: 3,
    question: '음주운전 삼진아웃이 세 번까지 봐준다는 말인가요?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며...'
  },
  {
    id: 4,
    question: '무면허 운전.. 구제 가능한가요...?',
    answerPreview: '아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 아닙니다. 삼진아웃은 형벌 경중에 따라 달라지며 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 두줄 구제 가능 여부는 사안에 따라 다르며, 음주·무면허 사안은...'
  },
  {
    id: 5,
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
  id: i + 1,
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

</script>

<template>
  <!-- 의뢰인 타입 본문 콘텐츠 -->
  <UserFrame>

    <p>
      <a href="/lawyer">변호사 대시보드 이동하기</a>
    </p>
    <p>
      <a href="/user/mypage">의뢰인 대시보드 이동하기</a>
    </p>

    <!-- 메인 베너 -->
    <MainBannerSlider :banners="mainBanners" defaultBadgeText="로앤로드 대표 서비스" />

    <!-- 동그라미 카테고리 -->
    <RoundCategory :categories="roundCategories" />

    <!-- 라이브 방송박스 -->
    <div class="row">
        <div class="col-12 mb-6">
          <h3 class="mb-0">변호사와 함께하는 실시간 라이브 방송</h3>
        </div>
      </div>
      
    <!-- <LiveBroadcastCard :broadcast="liveBroadcast" /> -->
    <LiveBroadcastCard :broadcast="liveBroadcast2" />

    <!-- VOD 방송 다시보기 -->
    <div class="row">
      <div class="col-12 mb-6">
        <h3 class="mb-0">VOD 방송 다시보기</h3>
      </div>
    </div>
    <!-- <CardSlider :replayItems="vodList" :itemsPerRow="4" :rowsPerSlide="1" /> -->
    <CardSlider :replayItems="vodList" :itemsPerRow="3" :rowsPerSlide="2" />

    <!-- QNA 게시판 테이블 -->
    <div class="row">
      <div class="col-12 mb-6">
        <h3 class="mb-0">나와 비슷한 사례 찾아보기</h3>
      </div>
    </div>
    <CardTable :List="qnaSampleList" :maxLines="4" />

    <!-- 일반 테이블 -->
     
    <BasicTable
      :columns="columns"
      :fullData="fullData"
      :pageSize="5"
    />
  </UserFrame>
</template>

<style scoped>
</style>
