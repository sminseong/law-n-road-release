<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import axios from 'axios'

const router = useRouter()

// form fields
const userNo = 1 // 로그인 연동 시 동적으로 설정
const name = ref('')
const categoryNo = ref('')
const content = ref('')
const date = ref('')
const startTime = ref('')
const endTime = ref('')

// thumbnail
const fileInput = ref(null)
const previewUrl = ref(null)
const selectedFile = ref(null)

// category list
const categoryList = ref([])

onMounted(async () => {
  try {
    const response = await axios.get('/api/category/list') // 예시 엔드포인트
    categoryList.value = response.data
  } catch (e) {
    console.error('카테고리 불러오기 실패', e)
  }
})

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const submitSchedule = async () => {
  try {
    const formData = new FormData()
    formData.append('userNo', userNo)
    formData.append('categoryNo', categoryNo.value)
    formData.append('name', name.value)
    formData.append('content', content.value)
    formData.append('date', date.value)
    formData.append('startTime', `${date.value}T${startTime.value}:00`)
    formData.append('endTime', `${date.value}T${endTime.value}:00`)
    formData.append('thumbnail', selectedFile.value) // 실제 파일

    await axios.post('/api/schedule/register', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    alert('✅ 방송 스케줄 등록 성공!')
    router.push('/lawyer/schedule')
  } catch (err) {
    console.error(err)
    alert('⚠️ 등록 중 오류 발생')
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">📅 방송 스케줄 등록</h3>
      <div class="card p-4">

        <div class="mb-3">
          <label class="form-label">썸네일 이미지</label>
          <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
            <img v-if="previewUrl" :src="previewUrl" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
            <span v-else class="text-muted">이미지가 없습니다</span>
          </div>
          <input type="file" ref="fileInput" class="form-control" accept="image/*" @change="handleFileChange" />
        </div>

        <div class="mb-3">
          <label class="form-label">방송 제목</label>
          <input v-model="name" type="text" class="form-control" placeholder="방송 제목을 입력하세요" />
        </div>

        <div class="mb-3">
          <label class="form-label">카테고리</label>
          <select v-model="categoryNo" class="form-select">
            <option disabled value="">카테고리를 선택하세요</option>
            <option v-for="cat in categoryList" :key="cat.no" :value="cat.no">
              {{ cat.name }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">설명</label>
          <textarea v-model="content" class="form-control" rows="4" placeholder="방송 설명을 입력하세요"></textarea>
        </div>

        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">방송 날짜</label>
            <input v-model="date" type="date" class="form-control" />
          </div>
          <div class="col-md-4">
            <label class="form-label">시작 시간</label>
            <input v-model="startTime" type="time" class="form-control" />
          </div>
          <div class="col-md-4">
            <label class="form-label">종료 시간</label>
            <input v-model="endTime" type="time" class="form-control" />
          </div>
        </div>

        <div class="d-flex justify-content-end">
          <button class="btn btn-primary" @click="submitSchedule">등록</button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.preview-box {
  width: 100%;
  height: 400px;
  background-color: #f8f9fa;
  border: 1px dashed #ccc;
  overflow: hidden;
}
.card {
  background-color: #fff;
  border-radius: 1rem;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
}
</style>
