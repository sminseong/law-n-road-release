<script setup>
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import CustomTable from '@/components/table/CustomTable.vue'
import { ref, onMounted } from 'vue'
import http from '@/libs/HttpRequester'
import { useRouter } from 'vue-router'

// 상태
const router = useRouter()
const rows = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const filters = ref([
  {
    label: '주문상태',
    key: 'status',
    options: ['전체', '결제완료', '취소', '환불']
  }
])
const currentFilters = ref({})

// 템플릿 목록 불러오기
async function fetchOrders(page = 1, query = {}) {
  const params = { page, limit: 10, ...query }
  const res = await http.get('/api/client/templates/orders', params)

  console.log(res.data.orders)

  rows.value = res.data.orders
  totalPages.value = res.data.totalPages
  currentPage.value = page
}

function handlePageChange(page) {
  fetchOrders(page, currentFilters.value)
}

function handleFilterChange(newFilters) {
  const mapped = { ...newFilters }
  if (mapped.status === '전체') delete mapped.status
  currentFilters.value = mapped
  fetchOrders(1, mapped)
}

function handleRowClick(row) {
  router.push(`/client/template/orders/${row.orderNo}`)
}

// 초기 로딩
onMounted(() => fetchOrders())

</script>

<template>
  <ClientFrame>
    <div class="container py-5">
      <h4 class="mb-4 fw-bold">템플릿 주문 내역</h4>
      <CustomTable
          :rows="rows"
          :columns="[
            { label: '주문번호', key: 'orderNo' },
            { label: '주문일자', key: 'orderDate' },
            {
              label: '주문상품',
              key: 'firstTemplateName',
              formatter: (v, row) => {
                const count = row.templateCount || 0
                return count > 1 ? `${v} 외 ${count - 1}건` : v
              }
            },
            {
              label: '총금액',
              key: 'totalAmount',
              formatter: (v) => `${v.toLocaleString()}원`
            },
            {
              label: '주문상태',
              key: 'status',
              formatter: (v) => {
                const map = {
                  ORDERED: '결제완료',
                  CANCELED: '취소',
                  REFUNDED: '환불'
                }
                return map[v] || v
              }
            },
          ]"
          :filters="filters"
          :current-page="currentPage"
          :total-pages="totalPages"
          @update:filters="handleFilterChange"
          @page-change="handlePageChange"
          @row-click="handleRowClick"
      />
    </div>
  </ClientFrame>
</template>
