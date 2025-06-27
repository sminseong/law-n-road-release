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

function handleScroll() {
  const scrollBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 50
  if (scrollBottom && !isLoading.value && hasMore.value) {
    fetchItems()
  }
}

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

function handleRowClick(row) {
  if (!row) return
  router.push(`/admin/lawyer/detail/${row.no}`)
}

function handleEdit(row) {
  router.push(`/admin/lawyer/edit/${row.no}`)
}

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
  </AdminFrame>
</template>

<style scoped>
img {
  object-fit: cover;
}
</style>
