<script setup>
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import CustomTable from '@/components/table/CustomTable.vue'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'

// 1) params 에서 orderNo 꺼내기
const router = useRouter()
const route = useRoute()
const orderNo = Number(route.params.orderNo)
const isDownloaded = ref(false)

// 2) 리액티브 상태 정의
const rows = ref([])
const currentPage = ref(1)
const totalPages = ref(1)

// 3) 필터 설정
const filters = ref([
  {
    label: '유형',
    key: 'type',
    options: ['전체', '문서 기반 템플릿', 'AI 생성형 템플릿']
  },
  {
    label: '카테고리',
    key: 'categoryName',
    options: [
      '전체',
      '사고 발생/처리',
      '중대사고·형사처벌',
      '음주·무면허 운전',
      '보험·행정처분',
      '과실 분쟁',
      '차량 외 사고'
    ]
  },
  {
    label: '다운로드 여부',
    key: 'isDownloaded',
    options: ['전체', '다운로드 완료', '미다운로드']
  }
])
const currentFilters = ref({})

// 4) 매핑 유틸리티들
// 4.1) 유형
const typeLabelToCode = {
  '문서 기반 템플릿': 'FILE',
  'AI 생성형 템플릿': 'EDITOR'
}
const typeCodeToLabel = Object.fromEntries(
    Object.entries(typeLabelToCode).map(([label, code]) => [code, label])
)
function mapTypeCodeToLabel(code) {
  return typeCodeToLabel[code] ?? '전체'
}

// 4.2) 카테고리
const categoryNameToNo = {
  '사고 발생/처리': 1,
  '중대사고·형사처벌': 2,
  '음주·무면허 운전': 3,
  '보험·행정처분': 4,
  '과실 분쟁': 5,
  '차량 외 사고': 6
}
const categoryNoToName = Object.fromEntries(
    Object.entries(categoryNameToNo).map(([n, no]) => [no, n])
)
function mapCategoryNameToNo(name) {
  return name === '전체' ? null : categoryNameToNo[name] || null
}

// 4.3) 다운로드 여부
function mapDownloadedLabelToValue(label) {
  if (label === '다운로드 완료') return 1
  if (label === '미다운로드')   return 0
  return null
}

// 5) API 호출: 목록 + 페이징
async function fetchItems(page = 1, query = {}) {
  const params = { page, limit: 10 }
  // 필터 한글 → API 파라미터로 변환
  if (query.type)        params.type        = mapTypeLabelToCode(query.type)
  if (query.categoryName) params.categoryNo = mapCategoryNameToNo(query.categoryName)
  if (query.isDownloaded != null)
    params.isDownloaded = mapDownloadedLabelToValue(query.isDownloaded)

  const res = await http.get(
      `/api/client/templates/orders/${orderNo}/items`,
      params
  )
  // merged API returns { items, totalPages }

  console.log('상세구매내역 (list) res:', res.data)

  rows.value       = res.data.templates
  totalPages.value = res.data.totalPages
  currentPage.value= page
  isDownloaded.value = rows.value.some(item => item.isDownloaded === true)
}

// 6) 핸들러들
function handlePageChange(page) {
  fetchItems(page, currentFilters.value)
}
function handleFilterChange(newFilters) {
  // '전체' 건 필터에서 제외
  const mapped = { ...newFilters }
  if (mapped.type === '전체')       delete mapped.type
  if (mapped.categoryName === '전체') delete mapped.categoryName
  if (mapped.isDownloaded === '전체') delete mapped.isDownloaded

  currentFilters.value = mapped
  fetchItems(1, mapped)
}

// 7) 행 클릭 시 단일 상세 페이지로 이동
function handleRowClick(row) {
  if (!row) return
  router.push({
    // detail 라우트 설정에 맞춘 path
    path: `/client/template/orders/detail/${row.tmplNo}`,
    query: {
      orderNo,               // 기존 주문 번호
      type: row.templateType // 'EDITOR' 혹은 'FILE'
    }
  })
}

// 7) 초기 로딩
onMounted(() => fetchItems())

// 환불 버튼
const handleRefund = async () => {
  const ok = confirm('정말 환불하시겠습니까?')
  if (!ok) return

  try {
    // await http.post(`/api/client/orders/${orderNo}/refund`)
    // 예: 라우터 이동 또는 상태 갱신
  } catch (err) {
    alert('환불 중 오류가 발생했습니다.')
    console.error(err)
  }
}

</script>

<template>
  <ClientFrame>
    <div class="container py-5">
      <!-- 주문 번호 + 환불 버튼 -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-4 fw-bold">주문 번호 {{ orderNo }}</h4>
        <button class="btn btn-outline-danger btn-sm" :disabled="isDownloaded" @click="handleRefund">
          환불하기
        </button>
      </div>

      <CustomTable
          :rows="rows"
          :columns="[
          { label: '썸네일',      key: 'thumbnailPath' },
          { label: '템플릿명',       key: 'templateName' },
          { label: '템플릿 유형',     key: 'templateType', formatter: v => mapTypeCodeToLabel(v) },
          { label: '카테고리',       key: 'categoryName' },
          {
            label: '구매당시 가격',
            key: 'price',
            formatter: v => `${v.toLocaleString()}원`
          },
          {
            label: '다운로드 여부',
            key: 'isDownloaded',
            formatter: v => (v ? '다운로드 완료' : '미다운로드')
          }
        ]"
          :image-config="{ enabled: true, key: 'thumbnailPath', targetKey: 'thumbnailPath' }"
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
