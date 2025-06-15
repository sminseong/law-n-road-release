<script setup>
const props = defineProps({ totalPrice: Number })
</script>

<template>
  <div class="card p-4">
    <h5 class="mb-3">Summary</h5>
    <ul class="list-group list-group-flush mb-3">
      <li class="list-group-item d-flex justify-content-between">
        <span>총 금액</span>
        <strong>{{ totalPrice.toLocaleString() }}원</strong>
      </li>
    </ul>
    <button class="btn btn-primary w-100">
      결제하기 <span class="float-end">{{ totalPrice.toLocaleString() }}원</span>
    </button>
  </div>
</template>
