<script setup>
import { ref } from 'vue'
import CustomTable from '@/components/table/CustomTable.vue'

// 예시 구매내역 데이터 (실제로는 API 호출로 받아올 것)
const purchaseHistory = ref([
  {
    id: '1',
    date: '2025-05-20',
    productName: '법률 상담 패키지 A',
    amount: '₩150,000',
    status: '완료',
  },
  {
    id: '2',
    date: '2025-05-18',
    productName: '교통사고 소송 패키지',
    amount: '₩300,000',
    status: '취소',
  },
  {
    id: '3',
    date: '2025-05-15',
    productName: '음주운전 변호사 상담',
    amount: '₩100,000',
    status: '진행 중',
  },
  // … 실제 데이터는 API 호출 후 세팅
])

// CustomTable에 넘겨줄 열 정의
const columns = [
  { label: '주문 일자', key: 'date' },
  { label: '상품명', key: 'productName' },
  { label: '금액', key: 'amount' },
  { label: '상태', key: 'status' },
]

// 구매내역에서 상태별 필터링을 위한 옵션
const filters = [
  {
    label: '상태',
    key: 'status',
    options: ['All', '완료', '진행 중', '취소'],
  },
]

// 페이지당 표시 개수
const rowsPerPage = 10

// (선택) 행 클릭 시 상세보기 이벤트
const onRowClick = (row) => {
  // 예: router.push(`/user/purchase/${row.id}`)
  console.log('행 클릭:', row)
}
</script>

<template>
  <div class="purchase-history-view p-4">
    <h3 class="mb-4">구매 내역</h3>

    <!-- CustomTable 컴포넌트에 데이터, 열, 필터를 넘겨줌 -->
    <CustomTable
        :rows="purchaseHistory"
        :columns="columns"
        :filters="filters"
        :rows-per-page="rowsPerPage"
        @edit="onRowClick"
    />
  </div>
</template>

<style scoped>
.purchase-history-view {
  background-color: #f8f9fa;
  border-radius: 8px;
}

/* 제목 스타일 */
.purchase-history-view h3 {
  font-weight: bold;
  color: #343a40;
}

/* CustomTable 내부 카드 여백 조정 (필요 시) */
.purchase-history-view ::v-deep .card {
  background-color: #ffffff;
}
</style>
