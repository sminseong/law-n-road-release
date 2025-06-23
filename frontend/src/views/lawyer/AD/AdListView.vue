<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomTable from '@/components/table/CustomTable.vue'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import http from '@/libs/HttpRequester'

// 라우터
const router = useRouter()

// 상태
const ads = ref([])
const page = ref(1)
const totalPages = ref(1)

// 컬럼 정의
const columns = [
  { label: '광고 이미지', key: 'imagePath' },
  { label: '광고 유형', key: 'adType' },
  { label: '승인 상태', key: 'approvalStatus' },
  { label: '광고 상태', key: 'adStatus' },
  { label: '시작일', key: 'startDate' },
  { label: '종료일', key: 'endDate' },
  { label: '관리', key: 'actions' }
]

/**
 * API로부터 광고 목록을 조회하고 상태를 업데이트합니다.
 * @param {number} pageNo - 조회할 페이지 번호
 */
async function fetchAds(pageNo = 1) {
  try {
    const query = {
      page: pageNo,
      limit: 10
    }

    const res = await http.get('/api/lawyer/ads', query)
    console.log('📦 res.data:', res.data)

    // API 응답이 배열인 경우와 페이징 객체인 경우 모두 처리
    let adsData, totalPagesData

    if (Array.isArray(res.data)) {
      // 배열로 직접 오는 경우
      adsData = res.data
      totalPagesData = 1
    } else {
      // 페이징 객체로 오는 경우
      adsData = res.data.ads || []
      totalPagesData = res.data.totalPages || 1
    }

    ads.value = adsData.map(ad => ({
      adNo: ad.adNo,
      adType: mapAdType(ad.adType),
      mainText: ad.mainText,
      detailText: ad.detailText,
      tipText: ad.tipText,
      imagePath: ad.adPath, // 필드명 매핑
      adStatus: mapAdStatus(ad.adStatus),
      approvalStatus: mapApprovalStatus(ad.approvalStatus),
      startDate: formatDate(ad.startDate),
      endDate: formatDate(ad.endDate)
    }))

    totalPages.value = totalPagesData

  } catch (e) {
    console.error('광고 목록 조회 실패:', e)
    ads.value = []
    totalPages.value = 1
  }
}

// 광고 유형 매핑
function mapAdType(type) {
  switch (type) {
    case 'MAIN':
      return '메인 광고'
    case 'SUB':
      return '서브 광고'
    default:
      return type || '기타'
  }
}

// 광고 상태 매핑
function mapAdStatus(status) {
  switch (status) {
    case 0:
      return '비활성'
    case 1:
      return '활성'
    default:
      return '알 수 없음'
  }
}

// 승인 상태 매핑
function mapApprovalStatus(status) {
  switch (status) {
    case 'PENDING':
      return '승인 대기'
    case 'APPROVED':
      return '승인 완료'
    case 'REJECTED':
      return '승인 거부'
    default:
      return status || '알 수 없음'
  }
}

// 날짜 포맷팅
function formatDate(dateString) {
  if (!dateString) return '-'
  return dateString.split('T')[0]
}

// 최초 로딩
onMounted(() => {
  fetchAds(page.value)
})

// 이벤트 핸들러 :: 행 클릭
function handleRowClick(row) {
  router.push(`/lawyer/ads/${row.adNo}`)
}

// 이벤트 핸들러 :: 수정 버튼 클릭
function handleEdit(row) {
  router.push(`/lawyer/ads/edit/${row.adNo}`)
}

// 이벤트 핸들러 :: 삭제 버튼 클릭
async function handleDelete(row) {
  if (!confirm(`'${row?.mainText}' 광고를 삭제하시겠습니까?`)) return

  try {
    await http.delete(`/api/lawyer/ads/${row.adNo}`)
    await fetchAds(page.value)
    alert('삭제되었습니다.')
  } catch (e) {
    console.error('삭제 실패:', e)
    const message = e?.response?.data?.message || '삭제 중 오류가 발생했습니다.'
    alert(message)
  }
}

// 페이지 변경 핸들러
function handlePageChange(newPage) {
  page.value = newPage
  fetchAds(page.value)
}
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold">광고 목록</h3>
        <button class="btn btn-primary" @click="router.push('/lawyer/ads/register')">+ 광고 등록</button>
      </div>

      <CustomTable
          :rows="ads"
          :columns="columns"
          :show-search-input="false"
          :filters="[]"
          :image-config="{ enabled: true, key: 'imagePath', targetKey: 'imagePath' }"
          :action-buttons="{
            delete: (row) => row.approvalStatus !== '승인 완료'
          }"
          :current-page="page"
          :total-pages="totalPages"
          @row-click="handleRowClick"
          @edit-action="handleEdit"
          @delete-action="handleDelete"
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