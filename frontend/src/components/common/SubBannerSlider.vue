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
                class="py-10 px-8 rounded"
                :style="`
                background: url(${banner.image}) no-repeat;
                background-size: cover;
                background-position: center;
              `"
            >
              <div>
                <h3 class="fw-bold mb-1">{{ banner.title }}</h3>
                <p class="mb-4">
                  {{ banner.desc }}
                  <span class="fw-bold">{{ banner.badge }}</span>
                </p>
                <a :href="`${banner.link}`" class="btn btn-dark">
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
