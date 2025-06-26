<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import { makeApiRequest } from '@/libs/axios-auth.js'

const router = useRouter()

const token = localStorage.getItem('token')
const userNo = localStorage.getItem('no')

const officeNumber = ref('')
const phone = ref('')
const detailAddress = ref('')

// ✅ 주소 관련 필드
const zipcode = ref('')
const roadAddress = ref('')
const landAddress = ref('')

// ✅ Daum 주소 검색 API 로드
onMounted(() => {
  const script = document.createElement('script')
  script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
  script.async = true
  document.head.appendChild(script)

  console.log('=== Lawyer Profile Edit Debug ===')
  console.log('Token:', token)
  console.log('User No:', userNo)
  console.log('================================')
})

// ✅ 주소 검색 팝업 실행 함수
function openPostcodePopup() {
  if (!window.daum || !window.daum.Postcode) {
    alert('주소 검색 API 로드 실패')
    return
  }

  new window.daum.Postcode({
    oncomplete(data) {
      zipcode.value = data.zonecode
      roadAddress.value = data.roadAddress
      landAddress.value = data.jibunAddress
    }
  }).open()
}

const updateLawyerInfo = async () => {
  if (!token || !userNo) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  if (!officeNumber.value.trim() || !phone.value.trim() || !detailAddress.value.trim()) {
    alert('모든 필드를 입력해주세요.')
    return
  }

  try {
    const response = await makeApiRequest({
      method: 'put',
      url: '/api/lawyer/info',
      data: {
        officeNumber: officeNumber.value,
        phone: phone.value,
        detailAddress: detailAddress.value,
        zipcode: zipcode.value,
        roadAddress: roadAddress.value,
        landAddress: landAddress.value
      }
    })

    if (response && response.status === 200) {
      alert('✅ 정보가 성공적으로 수정되었습니다.')
      router.push('/lawyer')
    } else {
      alert('❌ 수정 요청에 실패했습니다. 다시 시도해주세요.')
    }
  } catch (error) {
    console.error('❌ 정보 수정 실패:', error)
    alert('❌ 정보 수정 중 오류가 발생했습니다.')
  }
}
</script>

<template>
  <LawyerFrame>
    <div class="lawyer-container">
      <h2 class="page-title">변호사 계정 설정</h2>
      <p class="section-subtitle">사무실 연락처 및 상세 주소를 관리합니다.</p>

      <div class="info-card">
        <div class="form-group">
          <label for="officeNumber">사무실 번호</label>
          <input
              id="officeNumber"
              type="text"
              class="form-control"
              v-model="officeNumber"
              placeholder="사무실 번호를 입력하세요"
          />
        </div>

        <div class="form-group">
          <label for="phone">전화번호</label>
          <input
              id="phone"
              type="text"
              class="form-control"
              v-model="phone"
              placeholder="전화번호를 입력하세요"
          />
        </div>

        <!-- ✅ 우편번호 검색 (가로 정렬) -->
        <div class="form-group">
          <label for="zipcode">우편번호</label>
          <div class="zipcode-row">
            <input
                id="zipcode"
                type="text"
                class="form-control"
                v-model="zipcode"
                readonly
                placeholder="우편번호"
            />
            <button
                type="button"
                class="btn btn-outline-secondary"
                @click="openPostcodePopup"
            >
              주소 검색
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="roadAddress">도로명 주소</label>
          <input id="roadAddress" type="text" class="form-control" v-model="roadAddress" readonly />
        </div>

        <div class="form-group">
          <label for="landAddress">지번 주소</label>
          <input id="landAddress" type="text" class="form-control" v-model="landAddress" readonly />
        </div>

        <div class="form-group">
          <label for="detailAddress">상세 주소</label>
          <input
              id="detailAddress"
              type="text"
              class="form-control"
              v-model="detailAddress"
              placeholder="상세 주소를 입력하세요"
          />
        </div>

        <button class="btn btn-primary w-100" @click="updateLawyerInfo">정보 수정</button>
      </div>
    </div>
  </LawyerFrame>
</template>

<style scoped>
.lawyer-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 40px 16px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #2E4065;
  margin-bottom: 10px;
}

.section-subtitle {
  font-size: 15px;
  color: #666;
  margin-bottom: 28px;
}

.info-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
}

.btn-primary {
  background-color: #2E4065;
  color: white;
  border: none;
  font-weight: 600;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #1f2d4c;
}

/* ✅ 우편번호 + 버튼 가로 정렬 스타일 */
.zipcode-row {
  display: flex;
  gap: 8px;
}

.zipcode-row input {
  flex: 1;
}

.zipcode-row button {
  white-space: nowrap;
  padding: 10px 12px;
}
</style>
