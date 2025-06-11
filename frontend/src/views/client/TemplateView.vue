<script setup>
import { ref } from 'vue'
import CustomTable from '@/components/table/CustomTable.vue'
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import router from "@/router/index.js";
// 300개 아이템을 반복문으로 생성
const purchaseHistory = ref([])

// 기본 샘플 데이터 3개
const baseData = [
  {
    date: '2025-05-20',
    productName: '법률 상담 패키지 A',
    amount: '₩150,000',
    status: '완료',
  },
  {
    date: '2025-05-18',
    productName: '교통사고 소송 패키지',
    amount: '₩300,000',
    status: '취소',
  },
  {
    date: '2025-05-15',
    productName: '음주운전 변호사 상담',
    amount: '₩100,000',
    status: '진행 중',
  },
]

for (let i = 0; i < 300; i++) {
  // baseData를 순환하면서, no만 고유하게 (i+1)로 설정
  const item = baseData[i % baseData.length]
  purchaseHistory.value.push({
    no: String(i + 1),
    date: item.date,
    productName: item.productName,
    amount: item.amount,
    status: item.status,
  })
}

const purchaseHistory2 = ref([])
const baseData2 = [
  {
    date: '2025-05-20',
    productName: '법률 상담 패키지 A',
    amount: '₩150,000',
    status: '완료',
    imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
  },
  {
    date: '2025-05-18',
    productName: '교통사고 소송 패키지',
    amount: '₩300,000',
    status: '취소',
    imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
  },
  {
    date: '2025-05-15',
    productName: '음주운전 변호사 상담',
    amount: '₩100,000',
    status: '진행 중',
    imageUrl: '/img/templates/thumbnails/product-img-1.jpg',
  },
]

for (let i = 0; i < 300; i++) {
  const item = baseData2[i % baseData2.length]
  purchaseHistory2.value.push({
    no: String(i + 1),
    date: item.date,
    productName: item.productName,
    amount: item.amount,
    status: item.status,
    imageUrl: item.imageUrl,
  })
}

// CustomTable에 넘겨줄 열 정의
const columns = [
  { label: '상품명', key: 'productName' },
  { label: '주문 일자', key: 'date' },
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
  router.push(`/client/purchase/${row.no}`)
  console.log('행 클릭:', row)
}
</script>

<template>
  <ClientFrame>
    <div class="purchase-history-view p-4">
      <h3 class="mb-4">구매 내역</h3>

      <!-- CustomTable 컴포넌트에 데이터, 열, 필터를 넘겨줌 -->
      <CustomTable
          :rows="purchaseHistory"
          :columns="columns"
          :filters="[
            { label: '상품명', key: 'productName', options: ['All', '법률 상담 패키지 A', '교통사고 소송 패키지', '음주운전 변호사 상담'] },
            { label: '상태',     key: 'status',      options: ['All', '완료', '진행 중', '취소'] }
          ]"
          :rows-per-page="rowsPerPage"
          @row-click="onRowClick"
      />
<!--      <CustomTable-->
<!--          :rows="purchaseHistory2"-->
<!--          :columns="columns"-->
<!--          :filters="[-->
<!--            { label: '상품명', key: 'productName', options: ['All', '법률 상담 패키지 A', '교통사고 소송 패키지', '음주운전 변호사 상담'] },-->
<!--            { label: '상태',     key: 'status',      options: ['All', '완료', '진행 중', '취소'] }-->
<!--          ]"-->
<!--          :rows-per-page="rowsPerPage"-->
<!--          :image-config="{ enabled: true, key: 'imageUrl', targetKey: 'productName' }"-->
<!--          @row-click="onRowClick"-->
<!--      />-->
    </div>
  </ClientFrame>
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
.purchase-history-view :deep(.card) {
  background-color: #ffffff;
}
</style>
