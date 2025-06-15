<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const props = defineProps({ item: Object })
const emit = defineEmits(['remove'])

const router = useRouter()

function goToDetail() {
  router.push(`/templates/${props.item.tmplNo}`)
}

const handleRemove = (e) => {
  e.stopPropagation()
  e.preventDefault()
  emit('remove', props.item.no)
}

const typeLabel = computed(() =>
    props.item.type === 'EDITOR' ? 'AI 생성형 템플릿' : '문서 기반 템플릿'
)
</script>

<template>
  <li class="list-group-item py-3 px-0 border-top" @click="goToDetail">
    <div class="row align-items-center">
      <!-- 썸네일 -->
      <div class="col-3 col-md-2">
        <img :src="item.thumbnailPath" class="img-fluid" alt="썸네일" />
      </div>

      <!-- 상품명, 부가정보, 삭제 -->
      <div class="col-6 col-md-7">
        <h6 class="mb-1">{{ item.name }}</h6>
        <div class="small text-muted">
          {{ typeLabel }} / {{ item.categoryName }}
        </div>
        <div class="small text-muted">
          {{ item.lawyerName }} 변호사
        </div>
        <div class="mt-2">
          <span
              @click.stop.prevent="handleRemove"
              class="text-danger small"
              style="cursor: pointer;"
          >
            <i class="bi bi-trash me-1"></i> 삭제
          </span>
        </div>
      </div>

      <!-- 가격 -->
      <div class="col-3 col-md-3 text-end">
        <div class="fw-bold text-danger">
          {{ (item.price * (1 - item.discountRate / 100)).toLocaleString() }}원
        </div>
        <div class="text-muted text-decoration-line-through small" >
          {{ item.price.toLocaleString() }}원
        </div>
      </div>
    </div>
  </li>
</template>

<style scoped>
li.list-group-item {
  cursor: default; /* 전체는 pointer 아님 */
}
</style>