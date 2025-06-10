<script setup>
import { ref, onMounted, computed, watch } from 'vue'
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
    const response = await axios.get('/api/category/list')
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

// 키워드
const keywords = ref([])
const newKeyword = ref('')

const addKeyword = () => {
  const value = newKeyword.value.trim()
  if (value && !keywords.value.includes(value)) {
    keywords.value.push(value)
  }
  newKeyword.value = ''
}

const removeKeyword = (index) => {
  keywords.value.splice(index, 1)
}

// 30분 단위 시간 생성
function generateTimeOptions(startHour = 0, startMinute = 0) {
  const options = []
  for (let h = 0; h < 24; h++) {
    for (let m of [0, 30]) {
      if (h > startHour || (h === startHour && m >= startMinute)) {
        options.push(`${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`)
      }
    }
  }
  return options
}

const fullTimeOptions = generateTimeOptions()
const startTimeOptions = computed(() => {
  if (!date.value) return fullTimeOptions
  const today = new Date().toISOString().split('T')[0]
  if (date.value !== today) return fullTimeOptions

  const now = new Date()
  const nextMinute = now.getMinutes() <= 30 ? 30 : 0
  const nextHour = nextMinute === 30 ? now.getHours() : now.getHours() + 1
  return generateTimeOptions(nextHour, nextMinute)
})

const endTimeOptions = computed(() => {
  if (!startTime.value) return fullTimeOptions
  const [startH, startM] = startTime.value.split(':').map(Number)
  return generateTimeOptions(startH, startM + 1) // +1은 같은 시간 제외 목적
})

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
    formData.append('thumbnail', selectedFile.value)
    formData.append('keywords', JSON.stringify(keywords.value))

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
      <div class="card p-4">

        <!-- 썸네일 -->
        <div class="mb-3">
          <label class="form-label">썸네일 이미지</label>
          <div class="preview-box mb-2 d-flex align-items-center justify-content-center border rounded">
            <img v-if="previewUrl" :src="previewUrl" alt="미리보기" class="img-fluid h-100" style="object-fit: contain" />
            <span v-else class="text-muted">이미지가 없습니다</span>
          </div>
          <input type="file" ref="fileInput" class="form-control" accept="image/*" @change="handleFileChange" />
        </div>

        <!-- 제목 -->
        <div class="mb-3">
          <label class="form-label">방송 제목</label>
          <input v-model="name" type="text" class="form-control" placeholder="방송 제목을 입력하세요" />
        </div>

        <!-- 카테고리 -->
        <div class="mb-3">
          <label class="form-label">카테고리</label>
          <select v-model="categoryNo" class="form-select">
            <option disabled value="">카테고리를 선택하세요</option>
            <option v-for="cat in categoryList" :key="cat.no" :value="cat.no">{{ cat.name }}</option>
          </select>
        </div>

        <!-- 설명 -->
        <div class="mb-3">
          <label class="form-label">설명</label>
          <textarea v-model="content" class="form-control" rows="4" placeholder="방송 설명을 입력하세요"></textarea>
        </div>

        <!-- 날짜/시간 -->
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label">방송 날짜</label>
            <input v-model="date" type="date" class="form-control" :min="new Date().toISOString().split('T')[0]" />
          </div>
          <div class="col-md-4">
            <label class="form-label">시작 시간</label>
            <select v-model="startTime" class="form-select">
              <option disabled value="">시간 선택</option>
              <option v-for="t in startTimeOptions" :key="t" :value="t">{{ t }}</option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label">종료 시간</label>
            <select v-model="endTime" class="form-select">
              <option disabled value="">시간 선택</option>
              <option v-for="t in endTimeOptions" :key="t + 'e'" :value="t">{{ t }}</option>
            </select>
          </div>
        </div>

        <!-- 키워드 -->
        <div class="mb-3">
          <label class="form-label">방송 키워드</label>
          <div class="input-group mb-2">
            <input v-model="newKeyword" type="text" class="form-control" placeholder="키워드를 입력하세요" @keyup.enter="addKeyword" />
            <button class="btn btn-outline-primary" type="button" @click="addKeyword">추가</button>
          </div>
          <div class="d-flex flex-wrap gap-2">
            <span v-for="(keyword, index) in keywords" :key="index" class="badge bg-secondary d-inline-flex align-items-center">
              {{ keyword }}
              <button type="button" class="btn-close btn-close-white ms-2" aria-label="Remove" @click="removeKeyword(index)"></button>
            </span>
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
.badge {
  font-size: 0.9rem;
  padding: 0.5em 0.75em;
}
.form-select {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  background-color: #fefefe;
  border: 1px solid #ced4da;
  border-radius: 0.5rem;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}
.form-select:hover {
  background-color: #f0f0f0;
  border-color: #86b7fe;
}
.form-select:focus {
  border-color: #5a9dfd;
  box-shadow: 0 0 0 0.2rem rgba(90, 157, 253, 0.25);
}
</style>
