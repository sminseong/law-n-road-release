<script setup>
import AdminFrame from "@/components/layout/admin/AdminFrame.vue";
import CustomTable from "@/components/table/CustomTable.vue";
import { ref, onMounted } from 'vue'
import axios from 'axios'

const rows = ref([])
const isLoading = ref(false)
const searchKeyword = ref('')

// 목록 조회
async function fetchReports() {
  isLoading.value = true
  try {
    const params = { keyword: searchKeyword.value }
    const response = await axios.get('/api/admin/report/unpenalized', { params })
    rows.value = response.data
    console.log(rows)
  } catch (error) {
    console.error('신고 목록 조회 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 개별 패널티 부여 (테이블 내 버튼)
function handlePenalty(row) {
  if (!confirm(`방송번호 ${row.broadcastNo}에 대해 패널티를 부여하시겠습니까?`)) return

  axios.post('/api/admin/report/penalty', { broadcastNo: row.broadcastNo })
      .then(() => {
        alert('패널티 부여 완료')
        fetchReports()
      })
      .catch(e => {
        console.error('패널티 부여 실패:', e)
        alert(e?.response?.data?.message || '패널티 처리 중 오류가 발생했습니다.')
      })
}



onMounted(() => {
  fetchReports()
})
</script>

<template>
  <AdminFrame>
    <div class="container py-5">
      <h2 class="mb-4">미처리 신고 목록</h2>



      <!-- CustomTable 그대로 유지 -->
      <CustomTable
          :rows="rows"
          :columns="[
          { label: '신고번호',      key: 'no' },
          { label: '신고자 번호',   key: 'userNo' },
          { label: '방송번호',      key: 'broadcastNo' },
          { label: '신고 사유 코드', key: 'reason' },
          { label: '상세 사유',     key: 'detailReason' },
          { label: '신고일시',      key: 'createdAt' },
          { label: '관리',         key: 'actions' }

        ]"
          :show-search-input="true"
          :action-buttons="{ edit: false, delete: true }"
          @delete-action="handlePenalty"/>

      <div v-if="isLoading" class="text-center my-4">불러오는 중...</div>
      <div v-else-if="rows.length === 0" class="text-center my-4 text-muted">미처리 신고가 없습니다.</div>
    </div>
  </AdminFrame>
</template>
