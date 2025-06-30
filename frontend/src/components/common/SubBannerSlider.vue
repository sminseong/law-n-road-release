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
              <div class="position-relative z-1 text-white">
                <h3 class="fw-bold mb-2 shadow-text fs-2 text-white">
                  {{ banner.title }}
                </h3>
                <p class="mb-2 shadow-text fs-5">
                  {{ banner.desc }}
                </p>
                <p class="mb-4 shadow-text fw-semibold">
                  <i class="bi bi-award-fill me-1"></i>{{ banner.badge }}
                </p>
                <a
                    :href="banner.link"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="btn btn-light px-4 py-2 fw-bold rounded-pill shadow"
                >
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
</style>