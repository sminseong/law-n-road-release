<script setup>
import {onMounted, computed, nextTick, watch} from 'vue'

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

const goToVod = (vod) => {
  if (vod.link) {
    window.location.href = vod.link
  }
}

const initializeSlider = () => {
  nextTick(() => {
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
}

// groupedItems가 바뀔 때마다 슬라이더 재초기화
watch(groupedItems, (newVal) => {
  if (newVal.length > 0) {
    initializeSlider()
  }
})

onMounted(() => {
  if (groupedItems.value.length > 0) {
    initializeSlider()
  }
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
          <div class="card h-100" @click="goToVod(vod)" style="cursor: pointer;">
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
