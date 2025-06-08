<script setup>
defineProps({
  id: Number,
  imageUrl: String,
  title: String,
  originalPrice: [String, Number],
  discountPercent: [String, Number],
  discountedPrice: [String, Number],
})
</script>

<template>
  <div class="card card-product rounded-3 position-relative overflow-hidden">
    <a :href="`/product/${id}`" class="stretched-link"></a>

    <div class="text-center">
      <img
          :src="imageUrl"
          class="img-fluid"
          style="height: 160px; object-fit: cover;"
          alt="상품 이미지"
      />
    </div>

    <div class="card-body px-2 py-3">
      <p class="lh-sm mb-2 fs-5-1" style="height: 38px; overflow: hidden; text-overflow: ellipsis;
               display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">
        <a :href="`/product/${id}`" class="text-muted text-decoration-none title-link position-relative">
          {{ title }}
        </a>
      </p>

      <p class="text-gray-400 lh-sm text-decoration-line-through fs-6 mb-1">
        {{ originalPrice }}
      </p>

      <div class="d-flex align-items-center justify-content-between">
        <p class="fw-bold mb-0 d-flex align-items-center" style="font-size: 18px;">
          <span class="text-danger me-2">{{ discountPercent }}</span>
          <span class="text-dark">{{ discountedPrice }}</span>
        </p>
        <a href="/cart.html" class="btn btn-outline-primary btn-sm ms-2 cart-btn">
          장바구니
        </a>
      </div>
    </div>
  </div>
</template>
