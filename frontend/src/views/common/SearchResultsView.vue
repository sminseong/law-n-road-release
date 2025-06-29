<script setup>
import { defineProps, toRefs, onMounted, ref } from 'vue'
import http from '@/libs/HttpRequester'

const props = defineProps({
  keyword:     { type: String, required: true },
  category:    { type: [String, Number], default: null },
  onlyLawyers: { type: Boolean, default: false }
})

console.log("👉", props.keyword, props.category, props.onlyLawyers)

// props 안의 값들을 ref 형태로 분해
const { keyword, category, onlyLawyers } = toRefs(props)

const results = ref([])

async function fetchResults() {
  // keyword.value, category.value, onlyLawyers.value 로 사용
  const params = { q: keyword.value }
  if (category.value != null)    params.category    = category.value
  if (onlyLawyers.value)         params.onlyLawyers = true

  try {
    const res = await http.get('/api/public/search', { params })
    results.value = res.data
  } catch (e) {
    console.error('검색 실패:', e)
    results.value = []
  }
}

onMounted(fetchResults)
</script>

<template>
  <h2>
    “{{ keyword }}” 검색결과
    <span v-if="onlyLawyers"> (변호사만)</span>
  </h2>

  <ul>
    <li v-for="item in results" :key="item.id">
      {{ item.title }}
    </li>
  </ul>
</template>