<script setup>
    const props = defineProps({
    categories: {
        type: Array,
        required: true
    }
    })
</script>

<template>
  <section class="my-lg-14 my-8">
    <div class="container">
      <div class="row">
        <div
            v-for="(category, index) in props.categories"
            :key="index"
            class="col-lg-2 col-md-4 col-6"
        >

          <div class="text-center category-item">
            <a :href="category.link">
              <div
                  class="card-image rounded-circle d-flex align-items-center justify-content-center bg-light-primary"
                  style="width: 80px; height: 80px; margin: 0 auto;"
              >
                <i :class="`${category.icon} fa-2x text-dark-gray`" />
              </div>
            </a>
            <div class="mt-4">
              <h5 class="fs-6 mb-0">
                <a :href="category.link" class="text-inherit fs-4">
                  {{ category.label }}
                </a>
              </h5>
            </div>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.category-item {
  /* 트랜지션을 그룹 전체에 걸어 줍니다 */
  transition: color 0.2s, background-color 0.2s;
}

/* .text-center 을 hover 하면 내부 두 요소 모두 스타일 변경 */
.category-item:hover .card-image {
  background-color: #394663 !important;
}
.category-item:hover .card-image i {
  color: white !important;
}
.category-item:hover .text-inherit {
  color: #394663 !important;
}
</style>