<!-- src/components/layout/User/ClientHeader.vue -->
<script setup>
import { useRoute, useRouter } from 'vue-router'
import {onMounted, onBeforeUnmount, ref} from 'vue'
import axios from "axios";

const router = useRouter()
const route = useRoute()

// 로그인 관련
const nickname = ref('')
const isLoggedIn = ref(false)

function goToMyPage() {
  const target = '/client/mypage'
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

  isLoggedIn.value = !!token
  if (nick && nick !== 'null') {
    nickname.value = nick
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', updateGradient)
})

// 로그아웃 처리
const logout = () => {
  // ✅ 1. 로컬스토리지 항목 삭제
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('accountType')
  localStorage.removeItem('name')
  localStorage.removeItem('nickname')

  // ✅ 2. Axios 인증 헤더 제거
  delete axios.defaults.headers.common['Authorization']

  // ✅ 3. 프론트 상태 초기화
  isLoggedIn.value = false
  nickname.value = '회원'

  // ✅ 4. 콘솔 로그 출력: 삭제 여부 확인
  console.log('[로그아웃 완료] localStorage 상태 확인:')
  console.log('token:', localStorage.getItem('token'))
  console.log('refreshToken:', localStorage.getItem('refreshToken'))
  console.log('accountType:', localStorage.getItem('accountType'))
  console.log('name:', localStorage.getItem('name'))
  console.log('nickname:', localStorage.getItem('nickname'))

  // ✅ 5. 로그인 페이지로 이동 + 새로고침
  router.push('/login')
  setTimeout(() => location.reload(), 300) // 새로고침으로 컴포넌트 초기화
  console.log('[로그아웃 완료] localStorage 상태 확인:')
  console.log('token:', localStorage.getItem('token'))
  console.log('refreshToken:', localStorage.getItem('refreshToken'))
  console.log('accountType:', localStorage.getItem('accountType'))
  console.log('name:', localStorage.getItem('name'))
  console.log('nickname:', localStorage.getItem('nickname'))
}


</script>

<template>
  <header>
    <!-- 본문 시작 -->
    <div class="border-bottom sticky-top shadow-extra-light header-bg" style="z-index: 9999;">
      <div class="py-4 pt-lg-3 pb-lg-0">
        <div class="container">
          <div class="row w-100 align-items-center gx-lg-2 gx-0">

            <div class="col-xxl-2 col-lg-3">
              <!-- 데스크탑 전용 로고 -->
              <a class="navbar-brand d-none d-lg-block" href="/">
                <img src="@/assets/images/logo/logo.png" />
              </a>

              <!-- 모바일 전용 로고 -->
              <div class="d-flex justify-content-between w-100 d-lg-none">
                <a class="navbar-brand" href="/">
                  <img src="@/assets/images/logo/logo.png" />
                </a>

                <!-- 모바일 아이콘 + 햄버거 아이콘 -->
                <div class="d-flex align-items-center lh-1">
                  <div class="list-inline me-4">
                    <div class="list-inline-item">
                      <!-- 사용자 아이콘: goToMyPage 호출 -->
                      <!-- 로그인 상태일 때 -->
                      <a
                          v-if="isLoggedIn"
                          href="#"
                          class="text-muted"
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
                      <!-- 비로그인 상태일 때 -->
                      <div v-else>
                        <router-link to="/login" class="btn btn-primary">로그인</router-link>
                      </div>
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
                        class="bi bi-text-indent-left text-primary"
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
                      class="form-control rounded"
                      type="search"
                      name="search"
                      placeholder="궁금하신 법률 문제를 검색해보세요"
                  />
                  <span class="input-group-append">
                    <button
                        class="btn bg-white border border-start-0 ms-n10 rounded-0 rounded-end"
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
                  </span>
                </div>
              </form>
            </div>

            <!-- 위치 선택 버튼 -->
            <div class="col-md-2 col-xxl-3 d-none d-lg-block"></div>

            <!-- 장바구니, 유저정보 등 아이콘 -->
            <div class="col-md-2 col-xxl-1 text-end d-none d-lg-block">
              <div v-if="isLoggedIn" class="list-inline">
                <div class="list-inline-item">
                  <!-- 사용자 아이콘: goToMyPage 호출 -->
                  <a
                      href="#"
                      class="text-muted"
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
                </div>
                <div class="list-inline-item">
                  <a
                      class="text-muted position-relative"
                      href="/client/cart"
                      role="button"
                      aria-controls="offcanvasRight"
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
              </div>
              <div v-else>
                <router-link to="/login" class="btn btn-primary">로그인</router-link>
              </div>
            </div>

          </div>
        </div>
      </div>

      <!-- 네비게이션 시작 -->
      <nav class="navbar navbar-expand-lg navbar-light navbar-default py-0 py-lg-4">
        <div class="container px-0 px-md-3">

          <!-- 모바일용 네비게이션 (햄버거 버튼 클릭시 등장) -->
          <div class="offcanvas offcanvas-start p-4 p-lg-0" id="navbar-default">
            <!-- 햄버거창 로고 -->
            <div class="d-flex justify-content-between align-items-center mb-2 d-block d-lg-none">
              <a href="/"><img src="@/assets/images/logo/logo.png" alt="eCommerce HTML Template" /></a>
              <!-- 햄버거창 닫기 버튼 -->
              <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
            </div>

            <!-- 햄버거창 검색창 -->
            <div class="d-block d-lg-none my-4">
              <form action="#">
                <div class="input-group">
                  <input
                      class="form-control rounded"
                      type="search"
                      name="search"
                      placeholder="궁금하신 법률 문제를 검색해보세요"
                  />
                  <span class="input-group-append">
                    <button
                        class="btn bg-white border border-start-0 ms-n10 rounded-0 rounded-end"
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
                  </span>
                </div>
              </form>
            </div>

            <!-- 데스크탑용 메뉴이동 -->
            <div class="d-none d-lg-block">
              <ul class="navbar-nav align-items-center">
                <li class="nav-item">
                  <a class="nav-link" href="/">홈</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/client/broadcasts">라이브 방송</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/replay">방송 다시보기</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/templates">법률서류 템플릿</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/qna">법률Q&A</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/ci">브랜드 가치</a>
                </li>
              </ul>
            </div>

            <!-- 모바일 - 햄버거창 메뉴이동 네비게이션 -->
            <div class="d-block d-lg-none h-100" data-simplebar="">
              <ul class="navbar-nav">
                <li class="nav-item">
                  <a class="nav-link" href="/">홈</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/client/broadcasts">라이브 방송</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/replay">방송 다시보기</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/templates">법률서류 템플릿</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/qna">법률Q&A</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/ci">브랜드 가치</a>
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
header {
  position: fixed;
  z-index: 1050; /* 필요에 따라 조절 */
  /* 초기 CSS 변수를 중앙으로 설정 */
  width: 100%;
  --x: 50%;
  --y: 50%;
}

/* 헤더 배경색 (예: 연한 그레이) */
.header-bg {
  background-color: #ffffff;
}

/* 헤더 바로 아래 그림자처럼 퍼지는 그라데이션 */
header::before {
  content: '';
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  height: 2px; /* 아래로 퍼지는 거리 */
  background: radial-gradient(
      circle at var(--x) var(--y),
      rgb(62, 111, 180) 0%,      /* 최상단 강한 파랑 */ rgba(169, 182, 246, 0.4) 30%,     /* 중간 노랑 포인트 */
      transparent 100%               /* 아래로 갈수록 투명 */
  );
  pointer-events: none;
  z-index: -1;
}
</style>
