<script setup>
import { onMounted } from 'vue'

const props = defineProps({
  banners: {
    type: Array,
    required: true
  },
  defaultBadgeText: {
    type: String,
    default: '1:1 맞춤 법률 서비스'
  }
})

onMounted(() => {
  $('.hero-slider').slick({
    dots: true,
    arrows: false,
    autoplay: true,
    autoplaySpeed: 5000,
    speed: 2000
  })
})

function getReservationLink(banner) {
  if (banner.lawyerNo) {
    return {
      name: 'ClientReservations',
      params: {
        lawyerNo: banner.lawyerNo,
        lawyerName: banner.lawyerName
      }
    }
  }
  return '/'
}
</script>

<template>
  <section class="mt-8">
    <div class="container">
      <div class="hero-slider">
        <div
            v-for="(banner, i) in props.banners"
            :key="i"
            class="hero-banner"
            :style="`
            background: url(${banner.image}) no-repeat 90% center / cover;
            border-radius: 0.5rem;
            position: relative;
          `"
        >
          <div class="banner-overlay"></div>
          <div class="banner-content">
            <div class="ps-lg-12 py-lg-16 col-xxl-12 col-md-8 py-14 px-8 text-xs-center position-relative">
              <span class="badge text-bg-warning">
                {{ banner.badge || props.defaultBadgeText }}
              </span>
              <h2 class="text-white display-5 shadow-text fw-bold mt-4" v-html="banner.title"></h2>
              <p class="text-white fw-semibold shadow-text fs-1">{{ banner.lawyerName }} 변호사</p>
              <p class="lead text-white shadow-text " v-html="banner.desc"></p>
              <router-link
                  :to="getReservationLink(banner)"
                  class="btn btn-light mt-3">
                상담 신청 <i class="feather-icon icon-arrow-right ms-1"></i>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.shadow-text {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
}

.hero-slider .hero-banner {
  position: relative;
  width: 100%;
  aspect-ratio: 1265 / 530;  /* 1265×530 비율 */
  overflow: hidden;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center bottom;
  border-radius: 0.5rem;
}

.banner-content {
  position: absolute;
  inset: 0;                /* top:0; right:0; bottom:0; left:0; */
  display: flex;
  flex-direction: column;  /* 세로 정렬 */
  justify-content: center; /* 수직 중앙 */
  align-items: flex-start; /* 수평 위치: 왼쪽 정렬 */
  max-width: 90%;          /* 원하는 너비 */
}

@media (min-width: 768px) and (max-width: 991px) {
  .banner-content h2 {
    /* 예: 모바일(1rem)과 데스크탑(2.5rem)의 중간값 */
    font-size: 1.55rem;
    line-height: 1.2;
  }
  .banner-content .lead {
    font-size: 0.55rem;
  }
  .banner-content .badge {
    font-size: 0.55rem;
  }
  .banner-content .btn {
    font-size: 0.75rem;
    padding: 0.5rem 1.2rem;
  }
}

/* 반응형으로 너비 조절 */
@media (max-width: 768px) {
  .banner-content {
    max-width: 100%;
    align-items: center;   /* 모바일에서는 가운데 정렬 */
    text-align: center;
  }

  /* 배지 크기 */
  .banner-content .badge {
    font-size: 0.55rem;  /* 예: 12px */
  }

  /* 제목(h2) 크기 */
  .banner-content h2 {
    font-size: 1rem;   /* 예: 24px */
    line-height: 1.2;
    margin-bottom: 0.25rem;
  }

  /* 설명(lead) 크기 */
  .banner-content .lead {
    font-size: 0.5rem;   /* 예: 14px */
    margin-bottom: 0.5rem;
  }

  /* 버튼 크기 */
  .banner-content .btn {
    font-size: 0.5rem; /* 예: 14px */
    padding: 0.4rem 1rem;
  }
}
</style>