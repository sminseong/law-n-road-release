<script setup>
import AdminFrame from "@/components/layout/admin/AdminFrame.vue";
import CustomTable from "@/components/table/CustomTable.vue";
import { ref, onMounted } from 'vue';
import axios from 'axios';

const rows = ref([]);
const isLoading = ref(false);
const userNoKeyword = ref('');


// 목록 조회
async function fetchReports() {
  isLoading.value = true;
  try {
    const response = await axios.get('/api/admin/report/client/penalized');
    rows.value = response.data;

  } catch (error) {
    console.error('신고 목록 조회 실패:', error);
  } finally {
    isLoading.value = false;
  }
}

// 개별 패널티 부여 (테이블 내 버튼)
function handlePenalty(row) {
  if (!confirm(`신고번호 ${row.no}에 대해 패널티를 부여하시겠습니까?`)) return;

  axios.post('/api/admin/report/penalty_chatNo', { reportNo: row.no , userNo : row.userNo })
      .then(() => {
        alert('패널티 부여 완료');
        fetchReports();
      })
      .catch(e => {
        console.error('패널티 부여 실패:', e);
        alert(e?.response?.data?.message || '패널티 처리 중 오류가 발생했습니다.');
      });
}






onMounted(() => {
  fetchReports();
});
</script>

<template>
  <AdminFrame>
    <div class="container py-5">
      <h2 class="mb-4">미처리 신고 목록</h2>

      <!-- 사용자 번호 기반 입력 -->


      <!-- 테이블 출력 -->
      <CustomTable
          :rows="rows"
          :columns="[
          { label: '신고번호',        key: 'no' },
          { label: '신고자 번호',     key: 'reportedUserNo' },
          { label: '피신고자 번호',   key: 'userNo' },
          { label: '닉네임',          key: 'nickname' },
          { label: '신고 메시지',     key: 'message' },
          { label: '처리 상태',       key: 'reportStatus' },
          { label: '신고일시',        key: 'createdAt' },
          { label: '관리',         key: 'actions' }
        ]"
          :show-search-input="true"
          :action-buttons="{ edit: false, delete: true }"
          @delete-action="handlePenalty"

      />

      <div v-if="isLoading" class="text-center my-4">불러오는 중...</div>
      <div v-else-if="rows.length === 0" class="text-center my-4 text-muted">미처리 신고가 없습니다.</div>
    </div>
  </AdminFrame>
</template>
