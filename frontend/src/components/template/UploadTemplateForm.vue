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
const MAX_FILE_SIZE = 10 * 1024 * 1024  // 10MB

function handleFileChange(e) {
  const files = Array.from(e.target.files)

  // 1) 확장자 검사
  const isPdf = file => {
    const ext = file.name?.split('.').pop()?.toLowerCase()
    return ext === 'pdf'
  }

  const pdfs = files.filter(isPdf)
  const invalids = files.filter(f => !isPdf(f))
  if (invalids.length) {
    const names = invalids.map(f => f.name).join(', ')
    alert(`❌ 다음 파일은 PDF가 아닙니다:\n${names}`)
  }

  // 2) 크기 검사
  const tooBig = []
  const ok     = []
  pdfs.forEach(file => {
    if (file.size > MAX_FILE_SIZE) {
      tooBig.push(file)
    } else {
      ok.push(file)
    }
  })

  if (tooBig.length) {
    const names = tooBig.map(f => f.name).join(', ')
    alert(`❌ 다음 파일들은 10MB를 초과했습니다:\n${names}`)
  }

  // 3) emit
  if (ok.length) {
    emit('update:templateFiles', [
      ...props.templateFiles,
      ...ok
    ])
  }

  e.target.value = null
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
    <small class="mb-3" >* PDF 파일만 업로드 가능하며, 최대 10페이지까지 지원됩니다.</small>
    <input
        type="file"
        class="form-control mb-3"
        accept=".pdf"
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