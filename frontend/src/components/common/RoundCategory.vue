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
        <div class="row">
          <div
            v-for="(category, index) in props.categories"
            :key="index"
            class="col-lg-2 col-md-4 col-6"
          >
            <div class="text-center">
              <a :href="category.link">
                <div
                  class="card-image rounded-circle d-flex align-items-center justify-content-center bg-light"
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
    </div>
  </section>
</template>
