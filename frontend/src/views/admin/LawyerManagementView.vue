<script setup>
import AdminFrame from "@/components/layout/admin/AdminFrame.vue";
import CustomTable from "@/components/table/CustomTable.vue";
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const rows = ref([])
const isLoading = ref(false)
const hasMore = ref(true)
const offset = ref(0)
const limit = 20

const searchKeyword = ref('')
const currentFilters = ref({})
const filters = ref([
  { label: '계정 상태', key: 'status', options: ['전체', '가입승인', '가입거절', '탈퇴 중', '탈퇴회원'] }
])

const statusMap = {
  APPROVED_JOIN: '가입승인',
  REJECTED_JOIN: '가입거절',
  PENDING_LEAVE: '탈퇴 중',
  APPROVED_LEAVE: '탈퇴회원'
}

// 무한 스크롤
function handleScroll() {
  const scrollBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 50
  if (scrollBottom && !isLoading.value && hasMore.value) {
    fetchItems()
  }
}

// 데이터 조회
async function fetchItems() {
  isLoading.value = true
  const params = {
    offset: offset.value,
    limit,
    ...currentFilters.value,
    keyword: searchKeyword.value
  }

  try {
    const res = await axios.get('/api/admin/lawyer', { params })
    const list = res.data.list || []

    if (list.length < limit) hasMore.value = false
    rows.value.push(...list)
    offset.value += list.length
  } catch (e) {
    console.error('조회 실패:', e)
  } finally {
    isLoading.value = false
  }
}

// 필터 변경
function handleFilterChange(newFilters) {
  const reverseStatusMap = Object.fromEntries(Object.entries(statusMap).map(([k, v]) => [v, k]))
  const mapped = { ...newFilters }

  if (mapped.status === '전체') delete mapped.status
  else mapped.status = reverseStatusMap[mapped.status]

  searchKeyword.value = newFilters.keyword || ''
  rows.value = []
  offset.value = 0
  hasMore.value = true
  currentFilters.value = mapped

  fetchItems()
}

// 상세 모달
const showModal = ref(false)
const selectedLawyer = ref(null)

function handleRowClick(row) {
  console.log("[DEBUG] 선택된 변호사 row:", row)
  console.log("  👉 profile:", row.profile)
  console.log("  👉 cardFront:", row.cardFront)
  console.log("  👉 cardBack:", row.cardBack)

  selectedLawyer.value = row
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  selectedLawyer.value = null
}

// 수정
function handleEdit(row) {
  router.push(`/admin/lawyer/edit/${row.no}`)
}

// 삭제
function handleDelete(row) {
  if (!confirm(`'${row?.name}' 변호사를 삭제하시겠습니까?`)) return
  axios.delete(`/api/admin/member/lawyer/${row.no}`)
      .then(() => {
        rows.value = rows.value.filter(r => r.no !== row.no)
        alert('삭제되었습니다.')
      })
      .catch(e => {
        console.error('삭제 실패:', e)
        alert(e?.response?.data?.message || '삭제 중 오류가 발생했습니다.')
      })
}

onMounted(() => {
  fetchItems()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <AdminFrame>
    <div class="container py-5">
      <CustomTable
          :rows="rows"
          :columns="[
            { label: '번호',       key: 'no' },
            { label: '이름',       key: 'name' },
            { label: '아이디',     key: 'lawyerId' },
            { label: '전화번호',   key: 'phone' },
            { label: '이메일',     key: 'email' },
            { label: '가입일',     key: 'createdAt' },
            { label: '계정 상태',  key: 'status' }
          ]"
          :action-buttons="{ edit: true, delete: true }"
          :filters="filters"
          :show-search-input="true"
          @edit-action="handleEdit"
          @delete-action="handleDelete"
          @update:filters="handleFilterChange"
          @row-click="handleRowClick"
      >
        <template #cell-status="{ row }">
          <span :style="{ color: row.status === 'APPROVED_JOIN' ? '#003366' : 'inherit' }">
            {{ statusMap[row.status] || row.status }}
          </span>
        </template>
      </CustomTable>

      <div v-if="isLoading" class="text-center my-4">불러오는 중...</div>
      <div v-if="!hasMore" class="text-center my-4 text-muted">모든 변호사를 불러왔습니다.</div>
    </div>

    <!-- 상세 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-container">
        <button class="modal-close-btn" @click="closeModal">✕</button>

        <h3 class="modal-title">변호사 상세 정보 (#{{ selectedLawyer?.no }})</h3>

        <ul class="info-list">
          <li><strong>이름:</strong> {{ selectedLawyer?.name }}</li>
          <li><strong>이메일:</strong> {{ selectedLawyer?.email }}</li>
          <li><strong>전화번호:</strong> {{ selectedLawyer?.phone }}</li>
          <li><strong>계정 상태:</strong> {{ statusMap[selectedLawyer?.status] || selectedLawyer?.status }}</li>
        </ul>

        <div class="image-section">
          <div>
            <p>프로필 사진</p>
            <img :src="selectedLawyer?.profile" alt="프로필" @error="e => e.target.style.display='none'" />
          </div>
          <div>
            <p>신분증 앞면</p>
            <img :src="selectedLawyer?.cardFront" alt="신분증 앞" @error="e => e.target.style.display='none'" />
          </div>
          <div>
            <p>신분증 뒷면</p>
            <img :src="selectedLawyer?.cardBack" alt="신분증 뒤" @error="e => e.target.style.display='none'" />
          </div>
        </div>
      </div>
    </div>
  </AdminFrame>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.modal-container {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}
.modal-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}
.modal-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 16px;
}
.info-list {
  list-style: none;
  padding: 0;
  margin-bottom: 16px;
}
.info-list li {
  margin-bottom: 8px;
}
.image-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.image-section img {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
