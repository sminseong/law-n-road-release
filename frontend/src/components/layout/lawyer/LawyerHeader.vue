<script setup>
const props = defineProps({
  title: String
})
</script>

<template>
  <header>
    <div class="border-bottom sticky-top bg-white shadow-extra-light" style="z-index: 9999;">
      <div class="container-fluid d-flex justify-content-between align-items-center px-4 py-2">
        <div class="d-flex align-items-center gap-2">
          <span class="fw-bold text-primary fs-2">{{ title }}</span>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.shadow-extra-light {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
</style>
