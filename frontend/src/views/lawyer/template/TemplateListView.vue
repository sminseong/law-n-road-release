<script setup>
import { ref } from 'vue'
import CustomTable from '@/components/table/CustomTable.vue'
import LawyerFrame from '@/components/layout/Lawyer/LawyerFrame.vue'
import http from '@/libs/HttpRequester'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'

const router = useRouter()

// DB 데이터 가져오기
const templateList = ref([])
onMounted(async () => {
  try {
    const res = await http.get('/api/templates/lawyer')
    console.log(res.data)
    templateList.value = res.data.map(t => ({
      no: t.no,
      categoryNo: '', // 수정에서 카테고리 no를 알기 위해 필요
      categoryName: t.category_name,
      name: t.name,
      price: `₩${t.price.toLocaleString()}`,
      discountRate: t.discount_rate != null ? `${t.discount_rate}%` : '0%',
      salesCount: t.sales_count ?? 0,
      createdAt: t.created_at?.split('T')[0],
      imageUrl: t.thumbnail_path
    }))
    // ✅ 여기에 옮긴다
    localStorage.setItem('templateList', JSON.stringify(templateList.value))
  } catch (e) {
    console.error('템플릿 목록 조회 실패:', e)
  }
})

// 테이블 열
const columns = [
  { label: '이미지', key: 'imageUrl' },
  { label: '템플릿명', key: 'name' },
  { label: '가격', key: 'price' },
  { label: '할인율', key: 'discountRate' },
  { label: '판매량', key: 'salesCount' },
  { label: '카테고리', key: 'categoryName' },
  { label: '생성일시', key: 'createdAt' },
  { label: '관리', key: 'actions' }
]

// 필터
const filters = [ ]

// 행 클릭 -> 상세 조회 페이지
const onRowClick = (row) => {
  console.log('✅ row.no:', row.no) // undefined면 문제
  router.push(`/lawyer/templates/${row.no}`)
}

// 수정 버튼 클릭 -> 수정 페이지
function handleEdit(row) {
  console.log('handleEdit row:', row) // 🔍 확인!
  router.push(`/lawyer/templates/edit/${row.no}`)
}

// 삭제 버튼 클릭 -> 해당 행 소프트 딜리트
async function handleCancel(row) {
  if (!confirm(`'${row.name}' 템플릿을 삭제하시겠습니까?`)) return
  try {
    await http.delete(`/api/templates/lawyer/${row.no}`)
    // 삭제 후 목록 갱신
    templateList.value = templateList.value.filter(t => t.no !== row.no)
    localStorage.setItem('templateList', JSON.stringify(templateList.value))
    alert('삭제되었습니다.')
  } catch (e) {
    console.error('삭제 실패:', e)
    alert('삭제 중 오류가 발생했습니다.')
  }
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
          :rows-per-page="10"
          :image-config="{ enabled: true, key: 'imageUrl', targetKey: 'imageUrl' }"
          :action-buttons="{ edit: true, delete: true }"
          @row-click="onRowClick"
          @edit-action="handleEdit"
          @delete-action="handleCancel"
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
