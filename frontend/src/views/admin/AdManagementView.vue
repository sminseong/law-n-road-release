<script setup>
import AdminFrame from "@/components/layout/admin/AdminFrame.vue";
import CustomTable from "@/components/table/CustomTable.vue";
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'

const router = useRouter()
const route = useRoute()

// 조회정보를 담을 list, 현재 페이지, 전체 페이지 수
const rows = ref([])
const currentPage = ref(1)
const totalPages = ref(1)

// 필터 설정
const currentFilters = ref({})
const filters = ref([
  {
    label: '광고유형',
    key: 'type',
    options: ['전체', '메인 베너', '서브 베너']
  },
  {
    label: '활성상태',
    key: 'status',
    options: [
      '전체',
      '활성',
      '비활성',
    ]
  },
  {
    label: '승인여부',
    key: 'status',
    options: [
      '전체',
      '승인',
      '승인대기',
      '반려',
    ]
  },
])

// 시작하면 여기서 -> API 호출, 페이징
async function fetchItems(page = 1, query = {}) {
  const params = { page, limit: 10 }

  const res = await http.get(
      // api 주소 연결
      `/api/`,
      params
  )

  console.log('조회 결과 (list) res:', res.data)

  rows.value       = res.data
  totalPages.value = res.data
  currentPage.value= page
}

// 페이지 변경시 호출
function handlePageChange(page) {
  fetchItems(page, currentFilters.value)
}

// 필터 변경시 호출
function handleFilterChange(newFilters) {
  // '전체' 건 필터에서 제외
  const mapped = { ...newFilters }
  if (mapped.type === '전체')       delete mapped.type
  if (mapped.categoryName === '전체') delete mapped.status

  currentFilters.value = mapped
  fetchItems(1, mapped)
}

// 행 클릭 시 페이지 이동이 필요하다면
function handleRowClick(row) {
  if (!row) return
  router.push({
    // detail 라우트 설정에 맞춘 path
    path: `/client/template/orders/detail/${row.tmplNo}`,
    query: {
      orderNo,               // 이동되는 페이지로 값 전달할거라면
      type: row.templateType // 이동되는 페이지로 값 전달할거라면 (ex. 변호사일때 보여줄 세부 화면, 일반사용자일때 보여줄 세부 화면 지정하고 싶을 때)
    }
  })
}

// 수정 버튼 클릭시 호출
function handleEdit(row) {
  router.push({
    path: `/lawyer/templates/edit/${row.no}`,
    query: { type: typeLabelToCode[row.type] } // 전달할 값이 있다면
  })
}

// 삭제 버튼 클릭시 호출
async function handleDelete(row) {
  if (!confirm(`'${row?.name}' 회원을 삭제하시겠습니까?`)) return
  try {
    await http.delete(`/api/lawyer/templates/${row.no}`)
    await fetchTemplates(currentFilters.value, page.value) // 삭제 후 새로고침
    alert('삭제되었습니다.')
  } catch (e) {
    console.error('삭제 실패:', e)
    const message = e?.response?.data?.message || '삭제 중 오류가 발생했습니다.'
    alert(message)
  }
}

// 초기 로딩
onMounted(() => fetchItems())
</script>

<template>
  <AdminFrame>
    <div class="container py-5">
      <CustomTable
          :rows="rows"
          :columns="[
            { label: '광고번호',      key: 'no' },
            { label: '광고유형',      key: 'no' },
            { label: '광고주',      key: 'no' },
            { label: '광고 시작일',      key: 'no' },
            { label: '광고 종료일',      key: 'no' },
            { label: '활성상태',      key: 'no' },
            { label: '승인여부',      key: 'no' },
          ]"
          :filters="filters"
          :current-page="currentPage"
          :total-pages="totalPages"
          @update:filters="handleFilterChange"
          @page-change="handlePageChange"
          @row-click="handleRowClick"
      />
    </div>
  </AdminFrame>
</template>
