<script setup>
const props = defineProps({
  item: Object
})

const emit = defineEmits(['remove'])

function handleRemove() {
  emit('remove', props.item.no)
}
</script>

<template>
  <li class="list-group-item py-3 px-0 border-top">
    <div class="row align-items-center">
      <!-- 썸네일 -->
      <div class="col-3 col-md-2">
        <img :src="item.thumbnailPath" class="img-fluid" alt="썸네일" />
      </div>

      <!-- 상품명, 삭제 버튼 -->
      <div class="col-6 col-md-7">
        <h6 class="mb-1">{{ item.name }}</h6>
        <div class="mt-2">
          <a href="#" @click.prevent="handleRemove" class="text-danger small">
            <i class="bi bi-trash me-1"></i> 삭제
          </a>
        </div>
      </div>

      <!-- 가격 -->
      <div class="col-3 col-md-3 text-end">
        <div class="fw-bold text-danger">
          {{ (item.price * (1 - item.discountRate / 100)).toLocaleString() }}원
        </div>
        <div v-if="item.discountRate > 0" class="text-muted text-decoration-line-through small">
          {{ item.price.toLocaleString() }}원
        </div>
      </div>
    </div>
  </li>
</template>
