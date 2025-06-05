<script setup>
import {onMounted} from 'vue'

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
    autoplaySpeed: 3000
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
            background: url(${banner.image}) no-repeat 80% center / cover;
            border-radius: 0.5rem;
            position: relative;
          `"
        >
          <div class="banner-overlay"></div>
          <div class="ps-lg-12 py-lg-16 col-xxl-12 col-md-8 py-14 px-8 text-xs-center position-relative">
            <span class="badge text-bg-warning">
              {{ banner.badge || props.defaultBadgeText }}
            </span>
            <h2 class="text-white display-5 fw-bold mt-4" v-html="banner.title"></h2>
            <p class="lead text-white" v-html="banner.desc"></p>
            <router-link
                :to="getReservationLink(banner)"
                class="btn btn-light mt-3">
              상담 신청 <i class="feather-icon icon-arrow-right ms-1"></i>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
