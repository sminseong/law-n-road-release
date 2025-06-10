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
