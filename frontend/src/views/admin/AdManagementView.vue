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
    key: 'adType', // ✅ 백엔드 DTO와 맞춰야 됨
    options: ['전체', 'MAIN', 'SUB'] // ✅ 서버 enum 그대로
  },
  {
    label: '활성상태',
    key: 'adStatus', // ✅ boolean (0 or 1)
    options: ['전체', '1', '0']  // UI에서 '활성', '비활성'이면 매핑 로직 필요
  },
  {
    label: '승인여부',
    key: 'approvalStatus',
    options: ['전체', 'APPROVED', 'PENDING', 'REJECTED']
  },
])

// 시작하면 여기서 -> API 호출, 페이징
async function fetchItems(page = 1, query = {}) {
  const params = {
    page,
    limit: 10,
    ...query // 👉 필터 적용
  }

  const res = await http.get('/api/admin/ad-purchases', params)

  console.log('조회 결과:', res.data)

  rows.value        = res.data.items
  totalPages.value  = res.data.totalPages
  currentPage.value = page
}

// 페이지 변경시 호출
function handlePageChange(page) {
  fetchItems(page, currentFilters.value)
}

// 필터 변경시 호출
function handleFilterChange(newFilters) {
  // '전체' 건 필터에서 제외
  const mapped = { ...newFilters }
  if (mapped.adType === '전체')        delete mapped.adType
  if (mapped.adStatus === '전체')      delete mapped.adStatus
  if (mapped.approvalStatus === '전체') delete mapped.approvalStatus

  currentFilters.value = mapped
  fetchItems(1, mapped)
}

// 초기 로딩
onMounted(() => fetchItems())

/********/
// 모달
const showModal = ref(false);
const selectedRow = ref(null);

function handleRowClick(row) {
  // PENDING 상태인 경우만 모달 표시
  if (row.approvalStatus === 'PENDING') {
    selectedRow.value = row;
    showModal.value = true;
  }
}

function closeModal() {
  showModal.value = false;
}

// 승인/반려
async function approveAd(adNo) {
  console.log('클릭됨', adNo)
  await http.post(`/api/admin/ad-purchases/${adNo}/approve`);
  alert('승인 완료');
  closeModal();
  await fetchItems(currentPage.value, currentFilters.value);
}

async function rejectAd(adNo) {
  await http.post(`/api/admin/ad-purchases/${adNo}/reject`);
  alert('반려 완료');
  closeModal();
  await fetchItems(currentPage.value, currentFilters.value);
}
</script>

<template>
  <AdminFrame>
    <div class="container py-5">
      <CustomTable
          :rows="rows"
          :columns="[
            { label: '광고번호',    key: 'no' },
            { label: '광고유형',    key: 'adType' },
            { label: '광고주',      key: 'advertiserName' },
            { label: '광고 시작일', key: 'startDate' },
            { label: '광고 종료일', key: 'endDate' },
            { label: '활성상태',    key: 'adStatus' },
            { label: '승인여부',    key: 'approvalStatus' }
          ]"
          :filters="filters"
          :current-page="currentPage"
          :total-pages="totalPages"
          @update:filters="handleFilterChange"
          @page-change="handlePageChange"
          @row-click="handleRowClick"
      />
    </div>

<!--    &lt;!&ndash; 광고 모달 &ndash;&gt;-->
<!--    <div v-if="showModal" class="modal-overlay">-->
<!--      <div class="modal-container">-->
<!--        <button class="modal-close-btn" @click="closeModal">✕</button>-->

<!--        <h3 class="modal-title">광고 상세 (#{{ selectedRow?.no }})</h3>-->

<!--        &lt;!&ndash; 광고 이미지 &ndash;&gt;-->
<!--        <div v-if="selectedRow?.adPath" class="image-container">-->
<!--          <img-->
<!--              :src="selectedRow.adPath"-->
<!--              :alt="'광고 이미지 #' + selectedRow.no"-->
<!--              class="ad-image"-->
<!--              @error="handleImageError"-->
<!--          />-->
<!--        </div>-->

<!--        &lt;!&ndash; 이미지 없음 &ndash;&gt;-->
<!--        <div v-else class="no-image">📷 이미지 없음</div>-->

<!--        &lt;!&ndash; 광고 정보 &ndash;&gt;-->
<!--        <ul class="ad-info-list">-->
<!--          <li><strong>광고유형:</strong> {{ selectedRow?.adType }}</li>-->
<!--          <li><strong>광고주:</strong> {{ selectedRow?.advertiserName }}</li>-->
<!--          <li><strong>시작일:</strong> {{ selectedRow?.startDate }}</li>-->
<!--          <li><strong>종료일:</strong> {{ selectedRow?.endDate }}</li>-->
<!--          <li><strong>활성상태:</strong> {{ selectedRow?.adStatus ? '활성' : '비활성' }}</li>-->
<!--          <li><strong>승인여부:</strong> {{ selectedRow?.approvalStatus }}</li>-->
<!--        </ul>-->

<!--        &lt;!&ndash; 처리 버튼 &ndash;&gt;-->
<!--        <div class="modal-footer">-->
<!--          <p class="modal-description">해당 광고를 어떻게 처리하시겠습니까?</p>-->
<!--          <div class="button-group">-->
<!--            <button-->
<!--                v-if="selectedRow?.approvalStatus !== 'APPROVED'"-->
<!--                class="btn btn-approve"-->
<!--                @click="approveAd(selectedRow.no)"-->
<!--            >-->
<!--              승인-->
<!--            </button>-->
<!--            <button-->
<!--                v-if="selectedRow?.approvalStatus !== 'REJECTED'"-->
<!--                class="btn btn-reject"-->
<!--                @click="rejectAd(selectedRow.no)"-->
<!--            >-->
<!--              반려-->
<!--            </button>-->
<!--            <button class="btn btn-cancel" @click="closeModal">닫기</button>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

  </AdminFrame>
</template>

<style scoped>
/* 모달 오버레이 */
.modal-overlay {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 9999 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background-color: rgba(0, 0, 0, 0.5) !important;
}

/* 모달 컨테이너 */
.modal-container {
  background: white !important;
  width: 500px !important;
  max-width: 90% !important;
  padding: 24px !important;
  position: relative !important;
  border-radius: 8px !important;
  border: 1px solid #d1d5db !important;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04) !important;
  z-index: 10000 !important;
  max-height: 90vh !important;
  overflow-y: auto !important;
}

/* 닫기 버튼 */
.modal-close-btn {
  position: absolute !important;
  top: 8px !important;
  right: 8px !important;
  color: #6b7280 !important;
  background: none !important;
  border: none !important;
  cursor: pointer !important;
  font-size: 18px !important;
  padding: 4px !important;
}

.modal-close-btn:hover {
  color: #374151 !important;
}

/* 모달 제목 */
.modal-title {
  font-size: 1.25rem !important;
  font-weight: 600 !important;
  margin-bottom: 16px !important;
  color: #111827 !important;
}

/* 이미지 컨테이너 */
.image-container {
  margin-bottom: 20px !important;
  text-align: center !important;
}

/* 광고 이미지 */
.ad-image {
  max-width: 100% !important;
  max-height: 200px !important;
  object-fit: contain !important;
  border: 1px solid #e5e7eb !important;
  border-radius: 4px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
}

/* 이미지 없음 */
.no-image {
  margin-bottom: 20px !important;
  padding: 40px !important;
  background-color: #f9fafb !important;
  border: 2px dashed #d1d5db !important;
  border-radius: 4px !important;
  text-align: center !important;
  color: #6b7280 !important;
}

/* 광고 정보 리스트 */
.ad-info-list {
  margin-bottom: 24px !important;
  color: #374151 !important;
  list-style: none !important;
  padding: 0 !important;
}

.ad-info-list li {
  margin-bottom: 8px !important;
  line-height: 1.5 !important;
}

/* 모달 푸터 */
.modal-footer {
  border-top: 1px solid #e5e7eb !important;
  padding-top: 16px !important;
  display: block !important;
}

/* 설명 텍스트 */
.modal-description {
  color: #6b7280 !important;
  margin-bottom: 16px !important;
}

/* 버튼 그룹 */
.button-group {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 8px !important;
}

/* 공통 버튼 스타일 */
.btn {
  padding: 8px 16px !important;
  border-radius: 4px !important;
  border: none !important;
  cursor: pointer !important;
  font-weight: 500 !important;
  transition: background-color 0.2s !important;
}

/* 승인 버튼 */
.btn-approve {
  background-color: #059669 !important;
  color: white !important;
}

.btn-approve:hover {
  background-color: #047857 !important;
}

/* 반려 버튼 */
.btn-reject {
  background-color: #dc2626 !important;
  color: white !important;
}

.btn-reject:hover {
  background-color: #b91c1c !important;
}

/* 취소 버튼 */
.btn-cancel {
  background-color: #d1d5db !important;
  color: #374151 !important;
}

.btn-cancel:hover {
  background-color: #9ca3af !important;
}
</style>