<script setup>
defineProps({
  banners: {
    type: Array,
    required: true,
    default: () => [],
  },
})
</script>

<template>
  <section>
    <div class="container mb-10">
      <div class="row">
        <div
            class="col-12 col-md-6 mb-3 mb-lg-0"
            v-for="(banner, index) in banners"
            :key="index"
        >
          <div>
            <div
                class="py-10 px-8 rounded position-relative overflow-hidden"
                :style="`
                background: url(${banner.image}) no-repeat;
                background-size: cover;
                background-position: center;
              `"
            >
              <!-- 반투명 어두운 오버레이 -->
              <div class="banner-overlay"></div>

              <!-- 텍스트 콘텐츠 -->
              <div class="position-relative z-1">
                <h3 class="fw-bold mb-1 text-white shadow-text">{{ banner.title }}</h3>
                <p class="mb-4 text-white shadow-text">
                  {{ banner.desc }}
                  <span class="fw-bold">{{ banner.badge }}</span>
                </p>
                <a :href="banner.link" target="_blank" rel="noopener noreferrer" class="btn btn-light fw-bold">
                  변호사 보기
                </a>
              </div>
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

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(4, 57, 110, 0.4), rgba(0, 77, 153, 0));
  z-index: 1;
  border-radius: 0.5rem;
}
</style>