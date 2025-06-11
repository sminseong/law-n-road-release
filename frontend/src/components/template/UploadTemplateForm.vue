<script setup>
import { ref } from 'vue'

const props = defineProps({
  templateFiles: Array // 부모는 v-model:templateFiles로 넘겨야 함
})

const emit = defineEmits(['update:templateFiles'])

const fileList = ref([])

const handleFileChange = (e) => {
  const files = Array.from(e.target.files)
  fileList.value.push(...files)
  emit('update:templateFiles', fileList.value)
}

const removeFile = (index) => {
  fileList.value.splice(index, 1)
  emit('update:templateFiles', fileList.value)
}
</script>

<template>
  <div class="card p-3 mb-4 bg-light-subtle template-guide">
    <strong class="mb-2">업로드 방법</strong>
    <div class="d-flex justify-content-between align-items-center">
      <p class="mt-2 mb-1 text-muted small">
        미리 작성된 PDF 또는 워드(.doc, .docx) 문서를 업로드하여 템플릿으로 등록할 수 있습니다.
        등록된 파일은 구매자가 다운로드하거나 편집 가능한 형태로 제공됩니다.
      </p>
    </div>
    <ul class="mt-2 mb-1 text-muted small">
      <li>지원 형식: <code>.pdf</code>, <code>.doc</code>, <code>.docx</code></li>
      <li>최대 파일 크기: 10 MB (파일이 클 경우 압축을 권장)</li>
      <li>여러 파일을 한 번에 선택하여 업로드 가능</li>
      <li>잘못 추가된 파일은 우측 ‘삭제’ 버튼으로 제거</li>
    </ul>
  </div>

  <div class="card p-3 mb-4">
    <span class="form-label fw-bold mb-2">템플릿 파일 업로드</span>
    <p class="text-muted mb-3">PDF 또는 문서 파일 선택<br></p>

    <input type="file" class="form-control mb-3" accept=".pdf,.doc,.docx" multiple @change="handleFileChange" />

    <ul v-if="fileList.length" class="list-group">
      <li v-for="(file, i) in fileList" :key="i" class="list-group-item d-flex justify-content-between align-items-center">
        {{ file.name }}
        <button class="btn btn-sm btn-outline-danger" @click="removeFile(i)">삭제</button>
      </li>
    </ul>

    <p v-else class="text-muted">선택된 파일 없음</p>
  </div>
</template>

<style scoped>
.alert {
  font-size: 0.875rem;
  padding: 0.5rem 1rem;
  margin-top: 1rem;
}
</style>
