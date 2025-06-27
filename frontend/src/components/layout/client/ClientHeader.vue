<!-- src/components/layout/User/ClientHeader.vue -->
<script setup>
import { useRoute, useRouter } from 'vue-router'
import {onMounted, onBeforeUnmount, ref} from 'vue'
import axios from "axios";
import { Offcanvas } from 'bootstrap'

const router = useRouter()
const route = useRoute()

// 로그인 관련
const nickname = ref('')
const isLoggedIn = ref(false)
const role = ref('')

function goToMyPage() {
  if (!role.value) {
    alert('로그인이 필요합니다.')
    return router.push('/login')
  }

  const target =
      role.value === 'client' ? '/client/mypage' :
          role.value === 'lawyer' ? '/lawyer' :
              '/'

  if (route.path === target) {
    window.location.reload()
    return
  }

  router.push(target)
}

function updateGradient(e) {
  const headerEl = document.querySelector('header')
  if (!headerEl) return
  const rect = headerEl.getBoundingClientRect()
  const x = ((e.clientX - rect.left) / rect.width) * 100
  const y = ((e.clientY - rect.top) / rect.height) * 100
  headerEl.style.setProperty('--x', `${x}%`)
  headerEl.style.setProperty('--y', `${y}%`)
}

onMounted(() => {
  document.addEventListener('mousemove', updateGradient)

  const token = localStorage.getItem('token')
  const nick = localStorage.getItem('nickname')
  role.value = localStorage.getItem('accountType')

  isLoggedIn.value = !!token
  if (nick && nick !== 'null') {
    nickname.value = nick
  }

  document.addEventListener('mousemove', updateGradient)

  // 백드롭 클릭 시 offcanvas 숨기기
  const backdropHandler = (e) => {
    if (e.target.classList.contains('offcanvas-backdrop')) {
      const el = document.getElementById('navbar-default')
      const inst = Offcanvas.getInstance(el)
      if (inst) inst.hide()

      document.querySelectorAll('.offcanvas-backdrop').forEach(b => b.remove())
    }
  }
  document.body.addEventListener('click', backdropHandler)

  // 핸들러를 later에 제거할 수 있도록 전역에 저장
  window.__backdropHandler__ = backdropHandler

})

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', updateGradient)
  document.body.removeEventListener('click', window.__backdropHandler__)
})

const logout = async () => {
  // 0. (선택) 현재 로그인 유저 번호 가져오기
  const userNo = Number(localStorage.getItem('no'))

  try {
    // 1. 서버에 로그아웃(리프레시 토큰 삭제) 요청
    const response = await axios.post('/api/auth/logout', { userNo })
    console.log('[서버 로그아웃 성공]', response.data)

  } catch (error) {
    // 2. 에러 처리 (네트워크/서버 에러 등)
    console.error('[서버 로그아웃 실패]', error)

  } finally {
    // 3. 로컬스토리지 클리어
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('accountType')
    localStorage.removeItem('name')
    localStorage.removeItem('nickname')
    localStorage.removeItem('no')

    // 4. Axios 기본 헤더에서 Authorization 제거
    delete axios.defaults.headers.common['Authorization']

    // 5. 프론트 상태 초기화
    isLoggedIn.value = false
    nickname.value = '회원'

    // 6. (선택) 페이지 리로드 혹은 라우팅
    setTimeout(() => {
      // 라우터를 쓰신다면 router.push('/login') 로 대체 가능
      location.reload()
    }, 100)
  }
}


</script>

<template>
  <header>
    <!-- 본문 시작 -->
    <div class="border-bottom sticky-top shadow-extra-light header-bg" style="z-index: 9999;">
      <div class="py-4 pt-lg-3 pb-lg-0">
        <div class="container mt-2">
          <div class="row w-100 align-items-center gx-lg-2 gx-0">

            <div class="col-xxl-2 col-lg-3">
              <!-- 데스크탑 전용 로고 -->
              <a class="navbar-brand d-none d-lg-block" href="/">
                <img src="@/assets/images/logo/logo-dark.png" />
              </a>

              <!-- 모바일 전용 로고 -->
              <div class="d-flex justify-content-between w-100 d-lg-none">
                <a class="navbar-brand" href="/">
                  <img src="@/assets/images/logo/logo-dark.png" />
                </a>

                <!-- 모바일 아이콘 + 햄버거 아이콘 -->
                <div class="d-flex align-items-center lh-1">
                  <div class="list-inline me-4">
                    <div v-if="isLoggedIn" class="list-inline-item">
                      <!-- 사용자 아이콘: goToMyPage 호출 -->
                      <!-- 로그인 상태일 때 -->
                      <a
                          href="#"
                          class="text-white"
                          @click.prevent="goToMyPage"
                      >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="20"
                            height="20"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="2"
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            class="feather feather-user"
                        >
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                          <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                      </a>

                      <div v-if="role === 'client'" class="ms-4 list-inline-item">
                        <!-- 장바구니 아이콘 -->
                        <a
                            class="text-white position-relative"
                            href="/client/cart"
                            role="button"
                            aria-controls="offcanvasRight"
                            title="장바구니"
                        >
                          <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="20"
                              height="20"
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              stroke-width="2"
                              stroke-linecap="round"
                              stroke-linejoin="round"
                              class="feather feather-shopping-bag"
                          >
                            <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                            <line x1="3" y1="6" x2="21" y2="6"></line>
                            <path d="M16 10a4 4 0 0 1-8 0"></path>
                          </svg>
                        </a>
                      </div>

                      <div class="ms-3 list-inline-item">
                        <!-- 로그아웃 아이콘: logout 호출 -->
                        <a
                            href="#"
                            class="text-white"
                            @click="logout"
                            title="로그아웃"
                        >
                          <svg
                              xmlns="http://www.w3.org/2000/svg"
                              width="20"
                              height="20"
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              stroke-width="2"
                              stroke-linecap="round"
                              stroke-linejoin="round"
                              class="feather feather-log-out"
                          >
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                            <polyline points="16 17 21 12 16 7" />
                            <line x1="21" y1="12" x2="9" y2="12" />
                          </svg>
                        </a>
                      </div>
                    </div>
                    <!-- 비로그인 상태일 때 -->
                    <div v-else>
                      <router-link to="/login" class="btn btn-login-custom">로그인</router-link>
                    </div>
                  </div>
                  <!-- 햄버거 버튼 -->
                  <button
                      class="navbar-toggler collapsed"
                      type="button"
                      data-bs-toggle="offcanvas"
                      data-bs-target="#navbar-default"
                      aria-controls="navbar-default"
                      aria-expanded="false"
                      aria-label="Toggle navigation"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="32"
                        height="32"
                        fill="currentColor"
                        class="bi bi-text-indent-left text-white"
                        viewBox="0 0 16 16"
                    >
                      <path
                          d="M2 3.5a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm.646 2.146a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1 0 .708l-2 2a.5.5 0 0 1-.708-.708L4.293 8 2.646 6.354a.5.5 0 0 1 0-.708zM7 6.5a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5zm0 3a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5zm-5 3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- 검색창 -->
            <div class="col-xxl-6 col-lg-5 d-none d-lg-block">
              <form action="#">
                <div class="input-group">
                  <input
                      class="form-control rounded-start"
                      type="search"
                      name="search"
                      placeholder="궁금하신 법률 문제를 검색해보세요"
                  />
                  <button
                      class="btn border border-start-0 rounded-end text-dark"
                      style="background-color: #a8abc1;"
                      type="button"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="feather feather-search"
                    >
                      <circle cx="11" cy="11" r="8"></circle>
                      <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                  </button>
                </div>
              </form>
            </div>

            <!-- 위치 선택 버튼 -->
            <div class="col-md-2 col-xxl-3 d-none d-lg-block"></div>

            <!-- 장바구니, 유저정보 등 아이콘 -->
            <div class="col-md-2 col-xxl-1 text-end d-none d-lg-block">
              <div v-if="isLoggedIn" class="d-flex align-items-center justify-content-end gap-2">
                <span class="text-muted me-1" style="min-width: 150px;">
                  <strong class="text-white">{{ nickname }}</strong> 님 환영합니다.
                </span>
                <div class="list-inline-item">
                  <!-- 사용자 아이콘: goToMyPage 호출 -->
                  <a
                      href="#"
                      class="text-white"
                      @click.prevent="goToMyPage"
                      title="마이페이지"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="feather feather-user"
                    >
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                      <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                  </a>
                </div>

                <div v-if="role === 'client'" class="list-inline-item">
                  <!-- 장바구니 아이콘 -->
                  <a
                      class="text-white position-relative"
                      href="/client/cart"
                      role="button"
                      aria-controls="offcanvasRight"
                      title="장바구니"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="feather feather-shopping-bag"
                    >
                      <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                      <line x1="3" y1="6" x2="21" y2="6"></line>
                      <path d="M16 10a4 4 0 0 1-8 0"></path>
                    </svg>
                  </a>
                </div>

                <div class="list-inline-item">
                  <!-- 로그아웃 아이콘: logout 호출 -->
                  <a
                      href="#"
                      class="text-white"
                      @click="logout"
                      title="로그아웃"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="feather feather-log-out"
                    >
                      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                      <polyline points="16 17 21 12 16 7" />
                      <line x1="21" y1="12" x2="9" y2="12" />
                    </svg>
                  </a>
                </div>
              </div>
              <div v-else>
                <router-link to="/login" class="btn btn-login-custom">로그인</router-link>
              </div>
            </div>

          </div>
        </div>
      </div>

      <!-- 네비게이션 시작 -->
      <nav class="navbar navbar-expand-lg navbar-light navbar-default py-0 py-lg-4">
        <div class="container px-0 px-md-3">

          <!-- 모바일용 네비게이션 (햄버거 버튼 클릭시 등장) -->
          <div class="offcanvas offcanvas-start p-4 p-lg-0" id="navbar-default" data-bs-backdrop="true" data-bs-keyboard="true" data-bs-scroll="false">
            <!-- 햄버거창 로고 -->
            <div class="d-flex justify-content-between align-items-center mb-2 d-block d-lg-none">
              <a href="/"><img src="@/assets/images/logo/logo-dark.png" alt="eCommerce HTML Template" /></a>
              <!-- 햄버거창 닫기 버튼 -->
              <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
            </div>

            <!-- 햄버거창 검색창 -->
            <div class="d-block d-lg-none my-4">
              <form action="#">
                <div class="input-group">
                  <input
                      class="form-control rounded-start"
                      type="search"
                      name="search"
                      placeholder="궁금하신 법률 문제를 검색해보세요"
                  />
                  <button
                      class="btn border border-start-0 rounded-end text-dark"
                      style="background-color: #a8abc1;"
                      type="button"
                  >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="feather feather-search"
                    >
                      <circle cx="11" cy="11" r="8"></circle>
                      <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                  </button>
                </div>
              </form>
            </div>

            <!-- 데스크탑용 메뉴이동 -->
            <div class="d-none d-lg-block mb-2">
              <ul class="navbar-nav align-items-center">
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path === '/' }" href="/">홈</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/client/broadcasts') }" href="/client/broadcasts/list">라이브 방송</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/broadcasts/schedule">방송 스케줄</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/replay') }" href="/vod/list">방송 다시보기</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/templates') }" href="/templates">법률서류 템플릿</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/qna') }" href="/qna">법률Q&A</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/ci') }" href="/ci">브랜드 가치</a>
                </li>
              </ul>
            </div>

            <!-- 모바일 - 햄버거창 메뉴이동 네비게이션 -->
            <div class="d-block d-lg-none h-100" data-simplebar="">
              <ul class="navbar-nav">
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path === '/' }" href="/">홈</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/client/broadcasts') }" href="/client/broadcasts">라이브 방송</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/replay') }" href="/replay">방송 다시보기</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/templates') }" href="/templates">법률서류 템플릿</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/qna') }" href="/qna">법률Q&A</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" :class="{ active: route.path.startsWith('/ci') }" href="/ci">브랜드 가치</a>
                </li>
              </ul>
            </div>

          </div>
          <!-- 네비게이션 끝 -->
        </div>
      </nav>
    </div>
  </header>
</template>

<style scoped>

/* 브랜드 컬러: 짙은 남색 계열 */
.header-bg {
  background-color: #394663;
  color: white;
}

/* 공통 네비게이션 스타일 */
.navbar-nav .nav-link {
  color: white !important;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem; /* ← 상하좌우 모두 둥글게 */
  display: inline-block;
  transition: background-color 0.2s, color 0.2s;
}

input.form-control {
  background-color: #f1f3f5; /* 약간 부드러운 회색 */
  border: none;
  color: #24364a;
}

input.form-control::placeholder {
  color: #6c757d;
}

/* 선택된 메뉴 스타일 - 밝은 강조 배경 */
.navbar-nav .nav-link.active {
  background-color: #445b7c;
  color: white !important;
  font-weight: bold;
}

/* 헤더 내부 아이콘/텍스트 모두 흰색 유지 */
.header-bg a:not(.btn),
.header-bg span,
.header-bg .feather {
  color: white !important;
}

/* 헤더 위치 */
header {
  position: fixed;
  z-index: 1050;
  width: 100%;
  --x: 50%;
  --y: 50%;
}

.btn-login-custom {
  background-color: white;
  color: #24364a !important; /* ← 덮어쓰기 */
  border: 1px solid white;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-login-custom:hover {
  background-color: #f0f0f0;
  color: #1c2b42 !important; /* ← 덮어쓰기 */
  text-decoration: none;
}


.input-group-append button svg {
  stroke: #495057 !important; /* 부드러운 다크그레이 */
}

.input-group-append button {
  background-color: #f8f9fa  !important; /* 배경색 */
  border-color: #ced4da !important;     /* 테두리색 */
}

/* 햄버거 창(offcanvas) 배경색을 헤더와 동일하게 */
.offcanvas {
  background-color: #394663 !important;
  color: white !important;
}

/* 햄버거 창 내부 모든 텍스트를 흰색으로 */
.offcanvas * {
  color: white !important;
}

/* 햄버거 창 내부 검색창도 동일한 스타일 적용 */
.offcanvas input.form-control {
  background-color: #f1f3f5;
  border: none;
  color: #24364a;
}

.offcanvas .btn-close {
  filter: invert(1) !important;
}

.offcanvas input.form-control::placeholder {
  color: #6c757d;
}

.offcanvas .input-group-append button {
  background-color: #f8f9fa !important;
  border-color: #ced4da !important;
}

/* 햄버거 창 내부 네비게이션 링크 스타일 */
.offcanvas .navbar-nav .nav-link {
  color: white !important;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  display: block; /* inline-block 대신 block 사용 */
  transition: background-color 0.2s, color 0.2s;
  text-decoration: none !important; /* 밑줄 제거 */
  border: none !important; /* 테두리 제거 */
  margin-bottom: 0.25rem; /* 메뉴 간격 추가 */
}



/* 전역 또는 scoped CSS 에 넣으셔도 됩니다 */
.form-control:focus {
  outline: none !important;
  box-shadow: none !important;
}

::v-deep .navbar-nav .nav-link.active:hover {
  background-color: #4F5F7A !important;
  color: white        !important;
}
/* hover 시 - 약간 더 진한 배경 */
.navbar-nav .nav-link:hover:not(.active) {
  background-color: #4F5F7A !important;
  color: white !important;
}

/* 검색 input 버튼 그룹 전체에서 불필요한 테두리 없애기 */
.input-group .form-control,
.input-group .btn {
  border: none !important;
}

/* input 포커스 아웃라인/박스쉐도우 완전 제거 */
.input-group .form-control:focus {
  outline: none !important;
  box-shadow: none !important;
}

</style>
