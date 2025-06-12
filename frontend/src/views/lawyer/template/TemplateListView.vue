<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomTable from '@/components/table/CustomTable.vue'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import http from '@/libs/HttpRequester'

// 라우터
const router = useRouter()

// 상태
const templateList = ref([])
const currentFilters = ref({})
const page = ref(1)
const totalPages = ref(1)

// 컬럼 정의
const columns = [
  { label: '썸네일', key: 'thumbnailPath' },
  { label: '템플릿명', key: 'name' },
  { label: '유형', key: 'type' },
  { label: '카테고리', key: 'categoryName' },
  { label: '가격', key: 'price' },
  { label: '할인율', key: 'discountRate' },
  { label: '판매량', key: 'salesCount' },
  { label: '생성일시', key: 'createdAt' },
  { label: '관리', key: 'actions' }
]

// 필터 옵션
const filters = [
  {
    key: 'type',
    label: '유형',
    options: ['전체', '문서 기반 템플릿', 'AI 생성형 템플릿']
  },
  {
    key: 'categoryName',
    label: '카테고리',
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
    key: 'sort',
    label: '정렬',
    options: ['신규순', '인기순', '최저가순', '최고가순']
  }
]

/**
 * API로부터 템플릿 목록을 조회하고 상태를 업데이트합니다.
 * @param {Object} filters - 검색 및 필터 조건
 * @param {number} pageNo - 조회할 페이지 번호
 */
async function fetchTemplates(filters, pageNo) {
  try {
    const query = {
      ...normalizeFilters(filters),
      page: pageNo,
      limit: 10
    }

    const res = await http.get('/api/lawyer/templates', query)

    const { templates, totalPages: tp } = res.data
    templateList.value = templates.map(t => ({
      no: t.no,
      type: mapTypeCodeToLabel(t.type),
      categoryNo: t.categoryNo,
      categoryName: mapCategoryNoToName(t.categoryNo),
      name: t.name,
      price: `${t.price.toLocaleString()}원`,
      discountRate: t.discountRate != null ? `${t.discountRate}%` : '0%',
      salesCount: t.salesCount ?? 0,
      createdAt: t.createdAt?.split('T')[0],
      thumbnailPath: t.thumbnailPath
    }))
    totalPages.value = tp
  } catch (e) {
    console.error('템플릿 목록 조회 실패:', e)
  }
}

// filters 변환
function normalizeFilters(filters) {
  return {
    categoryNo: filters.categoryName === '전체' ? null : mapCategoryNameToNo(filters.categoryName),
    type: mapTypeLabelToCode(filters.type),
    sort: convertSort(filters.sort),
    keyword: filters.keyword || null
  }
}

// 카테고리 변환
// 1) 이름 → 번호 (filters 보낼 때)
const categoryNameToNo = {
  '사고 발생/처리': 1,
  '중대사고·형사처벌': 2,
  '음주·무면허 운전': 3,
  '보험·행정처분': 4,
  '과실 분쟁': 5,
  '차량 외 사고': 6
}

// 2) 번호 → 이름 (화면 표시용)
const categoryNoToName = {}
Object.entries(categoryNameToNo).forEach(([name, no]) => {
  categoryNoToName[no] = name
})

// 3) 변환 함수들
function mapCategoryNameToNo(name) {
  return categoryNameToNo[name] ?? null
}

function mapCategoryNoToName(no) {
  return categoryNoToName[no] || '기타'
}

// 서버 정렬 기준으로 매핑
function convertSort(sortLabel) {
  switch (sortLabel) {
    case '신규순':
      return 'new'
    case '인기순':
      return 'popular'
    case '최저가순':
      return 'priceAsc'
    case '최고가순':
      return 'priceDesc'
    default:
      return null
  }
}

// 화면 → 서버 전송용
const typeLabelToCode = {
  'AI 생성형 템플릿': 'EDITOR',
  '문서 기반 템플릿': 'FILE',
  '전체': null
}

// 서버 → 화면 표시용
const typeCodeToLabel = {}
Object.entries(typeLabelToCode).forEach(([label, code]) => {
  if (code !== null) typeCodeToLabel[code] = label
})

// 변환 함수들
function mapTypeLabelToCode(label) {
  return typeLabelToCode[label] ?? null
}

function mapTypeCodeToLabel(code) {
  return typeCodeToLabel[code] ?? '전체'
}

// 최초 로딩
onMounted(() => {
  fetchTemplates(currentFilters.value, page.value)
})

// 이벤트 핸들러 :: 행 클릭
function handleRowClick(row) {
  router.push({
    path: `/lawyer/templates/${row.no}`,
    query: { type: typeLabelToCode[row.type] } // 예: 'AI 생성형 템플릿' → 'EDITOR'
  })
}

// 이벤트 핸들러 :: 수정 버튼 클릭
function handleEdit(row) {
  router.push({
    path: `/lawyer/templates/edit/${row.no}`,
    query: { type: typeLabelToCode[row.type] } // 예: 'AI 생성형 템플릿' → 'EDITOR'
  })
}

// 이벤트 핸들러 :: 삭제 버튼 클릭
async function handleDelete(row) {
  if (!confirm(`'${row?.name}' 템플릿을 삭제하시겠습니까?`)) return
  try {
    await http.delete(`/api/lawyer/templates/${row.no}`)
    await fetchTemplates(currentFilters.value, page.value)
    alert('삭제되었습니다.')
  } catch (e) {
    console.error('삭제 실패:', e)
    const message = e?.response?.data?.message || '삭제 중 오류가 발생했습니다.'
    alert(message)
  }
}

function handleFilterChange(newFilters) {
  currentFilters.value = { ...newFilters }
  page.value = 1
  fetchTemplates(currentFilters.value, page.value)
}

function handlePageChange(newPage) {
  page.value = newPage
  fetchTemplates(currentFilters.value, page.value)
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold">등록한 템플릿 목록</h3>
        <button class="btn btn-primary" @click="router.push('/lawyer/templates/register')">+ 템플릿 등록</button>
      </div>

      <CustomTable
        :rows="templateList"
        :columns="columns"
        :filters="filters"
        :show-search-input="true"
        :image-config="{ enabled: true, key: 'thumbnailPath', targetKey: 'thumbnailPath' }"
        :action-buttons="{ edit: true, delete: true }"
        :current-page="page"
        :total-pages="totalPages"
        @row-click="handleRowClick"
        @edit-action="handleEdit"
        @delete-action="handleDelete"
        @update:filters="handleFilterChange"
        @page-change="handlePageChange"
      />
    </div>
  </LawyerFrame>
</template>

<style scoped>
.container {
  background-color: #f9f9f9;
  border-radius: 8px;
}
</style>
