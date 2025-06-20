<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import axios from 'axios'
import {getValidToken, makeApiRequest} from "@/libs/axios-auth.js";

const route = useRoute()
const router = useRouter()
const scheduleNo = route.params.scheduleNo

const userNo = 1
const name = ref('')
const categoryNo = ref('')
const content = ref('')
const date = ref('')
const startTime = ref('')
const endDate = ref('')
const endTime = ref('')
const categoryList = ref([])

const fileInput = ref(null)
const previewUrl = ref(null)
const selectedFile = ref(null)
const originalThumbnailPath = ref(null)

const keywords = ref([])
const newKeyword = ref('')

onMounted(async () => {
  try {
    const [catRes, schedRes] = await Promise.all([
      makeApiRequest({
        method: 'get',
        url: '/api/public/category/list'
      }),
      makeApiRequest({
        method: 'get',
        url: `/api/lawyer/schedule/my/${scheduleNo}`
      })
    ])

    categoryList.value = catRes.data
    const s = schedRes.data

    name.value = s.name
    categoryNo.value = s.categoryNo
    content.value = s.content
    date.value = s.date
    startTime.value = s.startTime.slice(11, 16)
    endDate.value = s.endTime.slice(0, 10)
    endTime.value = s.endTime.slice(11, 16)
    previewUrl.value = s.thumbnailPath || null
    originalThumbnailPath.value = s.thumbnailPath || null
    keywords.value = s.keywords || []
  } catch (e) {
    alert('스케줄 데이터를 불러오는 데 실패했습니다.')
    console.error(e)
  }
})


const addKeyword = () => {
  const val = newKeyword.value.trim()
  if (val && !keywords.value.includes(val)) {
    keywords.value.push(val)
  }
  newKeyword.value = ''
}

const removeKeyword = (i) => {
  keywords.value.splice(i, 1)
}

const formattedTimeRange = computed(() => {
  if (!date.value || !startTime.value || !endDate.value || !endTime.value) return ''
  const fmt = d => new Date(d).toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
  return `${fmt(date.value)} ${startTime.value} ~ ${fmt(endDate.value)} ${endTime.value}`
})

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const goBack = () => {
  router.push('/lawyer/broadcasts/schedule')
}

const deleteSchedule = async () => {
  const confirmDelete = confirm('❗ 정말 이 방송 스케줄을 삭제하시겠습니까?')
  if (!confirmDelete) return

  try {
    await makeApiRequest({
      method: 'delete',
      url: `/api/lawyer/schedule/delete/${scheduleNo}`
    })

    alert('🗑️ 방송 스케줄이 삭제되었습니다.')
    await router.push('/lawyer/broadcasts/schedule')
  } catch (err) {
    alert('⚠️ 삭제 실패')
    console.error(err)
  }
}


const updateSchedule = async () => {
  const confirmUpdate = confirm('✏️ 방송 스케줄을 수정하시겠습니까?')
  if (!confirmUpdate) return

  try {
    const form = new FormData()
    form.append('scheduleNo', scheduleNo)
    form.append('userNo', userNo)
    form.append('categoryNo', categoryNo.value)
    form.append('name', name.value)
    form.append('content', content.value)
    form.append('keywords', JSON.stringify(keywords.value))

    if (selectedFile.value) {
      form.append('thumbnail', selectedFile.value)
    }

    const token = await getValidToken()
    await axios.post('/api/lawyer/schedule/update?_method=PUT', form, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      }
    })

    alert('✅ 방송 스케줄 수정 완료')
    await router.push('/lawyer/broadcasts/schedule')
  } catch (err) {
    alert('⚠️ 수정 실패')
    console.error(err)
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <div class="card p-4">
        <div class="row">
          <!-- 왼쪽 -->
          <div class="col-md-6 pe-md-4">
            <div class="mb-3">
              <label class="form-label">방송 제목</label>
              <input v-model="name" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">카테고리</label>
              <select v-model="categoryNo" class="form-select">
                <option disabled value="">카테고리 선택</option>
                <option v-for="cat in categoryList" :key="cat.no" :value="cat.no">{{ cat.name }}</option>
              </select>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">시작 날짜</label>
                <input :value="date" type="date" class="form-control bg-light text-muted no-edit" readonly />
              </div>
              <div class="col-md-6">
                <label class="form-label">시작 시간</label>
                <input :value="startTime" type="text" class="form-control bg-light text-muted no-edit" readonly />
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">종료 날짜</label>
                <input :value="endDate" type="date" class="form-control bg-light text-muted no-edit" readonly />
              </div>
              <div class="col-md-6">
                <label class="form-label">종료 시간</label>
                <input :value="endTime" type="text" class="form-control bg-light text-muted no-edit" readonly />
              </div>
            </div>
            <div class="mb-3" v-if="formattedTimeRange">
              <small class="text-muted">{{ formattedTimeRange }}</small>
            </div>
            <div class="mb-3">
              <label class="form-label">설명</label>
              <textarea v-model="content" class="form-control" rows="4"></textarea>
            </div>
          </div>

          <!-- 오른쪽 -->
          <div class="col-md-6 ps-md-4">
            <div class="mb-3">
              <label class="form-label">썸네일 이미지</label>
              <div class="preview-box mb-2 border rounded">
                <img v-if="previewUrl" :src="previewUrl" alt="썸네일" class="img-fluid h-100" style="object-fit: contain" />
                <span v-else class="text-muted">이미지 없음</span>
              </div>
              <input type="file" ref="fileInput" class="form-control" accept="image/*" @change="handleFileChange" />
            </div>
            <div class="mb-3">
              <label class="form-label">방송 키워드</label>
              <div class="input-group mb-2">
                <input v-model="newKeyword" type="text" class="form-control" @keyup.enter="addKeyword" />
                <button class="btn btn-outline-primary" @click="addKeyword">추가</button>
              </div>
              <div class="d-flex flex-wrap gap-2">
                <span v-for="(kw, i) in keywords" :key="i" class="badge bg-secondary">
                  {{ kw }}
                  <button class="btn-close btn-close-white ms-2" @click="removeKeyword(i)"></button>
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-between mt-4">
          <button class="btn btn-secondary" @click="goBack">← 목록으로</button>
          <div class="d-flex gap-2">
            <button class="btn btn-danger" @click="deleteSchedule">삭제</button>
            <button class="btn btn-primary" @click="updateSchedule">수정</button>
          </div>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.preview-box {
  width: 100%;
  height: 300px;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
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
.no-edit {
  cursor: default;
  user-select: none;
}
</style>
