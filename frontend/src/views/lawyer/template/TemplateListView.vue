<script setup>
import { ref } from 'vue'
import CustomTable from '@/components/table/CustomTable.vue'
import LawyerFrame from '@/components/layout/Lawyer/LawyerFrame.vue'
import http from '@/libs/HttpRequester'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'

const router = useRouter()

// 샘플 데이터
const templateList = ref([])
onMounted(async () => {
  try {
    const res = await http.get('/api/templates/lawyer')
    templateList.value = res.data.map(t => ({
      no: t.no,
      categoryName: t.categoryName,
      name: t.name,
      price: `₩${t.price.toLocaleString()}`,
      discountRate: `${t.discountRate}%`,
      salesCount: t.salesCount,
      createdAt: t.createdAt?.split('T')[0],
      imageUrl: t.thumbnailPath
    }))
    // ✅ 여기에 옮긴다
    localStorage.setItem('templateList', JSON.stringify(templateList.value))
  } catch (e) {
    console.error('템플릿 목록 조회 실패:', e)
  }
})

// 테이블 열
const columns = [
  { label: '번호', key: 'no' },
  { label: '이미지', key: 'img' },
  { label: '카테고리', key: 'categoryName' },
  { label: '템플릿명', key: 'name' },
  { label: '가격', key: 'price' },
  { label: '할인율', key: 'discountRate' },
  { label: '판매량', key: 'salesCount' },
  { label: '생성일시', key: 'createdAt' },
  { label: '관리', key: 'actions' }
]

// 필터
const filters = [ ]

// 행 클릭 -> 상세 조회 페이지
const onRowClick = (row) => {
  router.push(`/lawyer/templates/${row.no}`)
}


// 수정 버튼 클릭 -> 수정 페이지
function handleEdit(row) {
  console.log('handleEdit row:', row) // 🔍 확인!
  router.push(`/lawyer/templates/edit/${row.no}`)
}

// 삭제 버튼 클릭 -> 해당 행 소프트 딜리트
function handleCancel(row) {
  console.log('취소 버튼 눌림', row)
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
          :image-config="{ enabled: true, key: 'imageUrl', targetKey: 'img' }"
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
