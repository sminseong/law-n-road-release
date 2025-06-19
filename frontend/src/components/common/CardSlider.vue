<script setup>
import { onMounted, computed } from 'vue'

const props = defineProps({
  replayItems: {
    type: Array,
    required: true
  },
  itemsPerRow: {
    type: Number,
    default: 3
  },
  rowsPerSlide: {
    type: Number,
    default: 2
  }
})

const itemsPerSlide = computed(() => props.itemsPerRow * props.rowsPerSlide)

const groupedItems = computed(() => {
  const result = []
  for (let i = 0; i < props.replayItems.length; i += itemsPerSlide.value) {
    result.push(props.replayItems.slice(i, i + itemsPerSlide.value))
  }
  return result
})

onMounted(() => {
  window.tns?.({
    container: '.vod-slider',
    items: 1,
    gutter: 16,
    slideBy: 1,
    mouseDrag: true,
    controls: false,
    nav: false,
    autoplay: true,
    autoplayTimeout: 6000,
    autoplayButtonOutput: false,
    speed: 3000
  })
})
</script>

<template>
  <div class="vod-slider mb-10 ">
    <div class="item" v-for="chunk in groupedItems" :key="chunk[0].no">
      <div class="row">
        <div
          v-for="(vod, index) in chunk"
          :key="vod.no"
          :class="`col-md-${12 / itemsPerRow} mb-4`"
        >
          <div class="card h-100">
            <div class="card-body text-center py-4">
              <img :src="vod.thumbnail" class="img-fluid mb-2" />
              <div class="text-truncate">{{ vod.title }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.vod-slider .item {
  padding: 0 8px;
}
.card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
</style>
