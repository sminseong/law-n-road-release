<!-- UploadTemplateForm.vue -->
<script setup>
import { computed } from 'vue'

const props = defineProps({
  templateFiles: {
    type: Array,
    default: () => []
  }
})
const emit = defineEmits(['update:templateFiles'])

// 표시용 이름 꺼내기
const displayName = file => {
  if (typeof file === 'string') return file
  return file.originalName || file.name || '(이름 없음)'
}

// 두 가지 동작: 파일 추가 / 삭제
function handleFileChange(e) {
  const newFiles = Array.from(e.target.files)
  // 기존 + 새로 고른 파일 합친 새 배열을 부모로 보냄
  emit('update:templateFiles', [...props.templateFiles, ...newFiles])
}

function removeFile(i) {
  emit(
      'update:templateFiles',
      props.templateFiles.filter((_, idx) => idx !== i)
  )
}

// 그 외 필요한 computed
const count = computed(() => props.templateFiles.length)
</script>

<template>
  <div class="card p-3 mb-4">
    <span class="form-label fw-bold mb-2">템플릿 파일 업로드</span>
    <input
        type="file"
        class="form-control mb-3"
        accept=".pdf,.doc,.docx"
        multiple
        @change="handleFileChange"
    />

    <!-- 디버그: 몇 개 잡혔는지 -->
    <p v-if="count" class="text-muted">선택된 파일 수: {{ count }}</p>

    <ul
        v-if="count"
        class="bg-white border rounded"
        style="list-style: none; padding: 0;"
    >
      <li
          v-for="(file, i) in props.templateFiles"
          :key="displayName(file) + '_' + i"
          class="d-flex justify-content-between align-items-center"
          :style="{
          padding: '0.75rem 1rem',
          borderBottom: i !== count - 1 ? '1px solid #eee' : 'none'
        }"
      >
        <span>{{ displayName(file) }}</span>
        <button
            class="btn btn-outline-danger btn-sm"
            @click="removeFile(i)"
        >
          삭제
        </button>
      </li>
    </ul>

    <p v-else class="text-muted">선택된 파일 없음</p>
  </div>
</template>