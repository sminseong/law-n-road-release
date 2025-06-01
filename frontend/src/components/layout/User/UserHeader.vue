<!-- src/components/layout/User/UserHeader.vue -->
<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <header>
    <!-- 본문 시작 -->
    <div class="border-bottom sticky-top bg-white shadow-extra-light" style="z-index: 9999;">
      <div class="py-4 pt-lg-3 pb-lg-0">
        <div class="container">
          <div class="row w-100 align-items-center gx-lg-2 gx-0">

            <div class="col-xxl-2 col-lg-3">
              <!-- 데스크탑 전용 로고 -->
              <router-link class="navbar-brand d-none d-lg-block" to="/">
                <img src="@/assets/images/logo/logo.png" />
              </router-link>

              <!-- 모바일 전용 로고 -->
              <div class="d-flex justify-content-between w-100 d-lg-none">
                <router-link class="navbar-brand" to="/">
                  <img src="@/assets/images/logo/logo.png" />
                </router-link>

                <!-- 모바일 아이콘 + 햄버거 아이콘 -->
                <div class="d-flex align-items-center lh-1">
                  <div class="list-inline me-4">
                    <div class="list-inline-item">
                      <!-- 사용자 아이콘: router-link 사용 -->
                      <router-link class="text-muted" to="/user/mypage">
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
                      </router-link>
                    </div>
                    <div class="list-inline-item">
                      <a
                          class="text-muted position-relative"
                          data-bs-toggle="offcanvas"
                          data-bs-target="#offcanvasRight"
                          href="#offcanvasExample"
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
                        <span
                            class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-success"
                        >
                          1
                          <span class="visually-hidden">unread messages</span>
                        </span>
                      </a>
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
              <div class="list-inline">
                <div class="list-inline-item">
                  <!-- 사용자 아이콘: router-link 사용 -->
                  <router-link class="text-muted" to="/user/mypage">
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
                  </router-link>
                </div>
                <div class="list-inline-item">
                  <router-link class="text-muted position-relative" to="/user/cart">
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
                  </router-link>
                </div>
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
              <router-link to="/">
                <img src="@/assets/images/logo/logo.png" alt="eCommerce HTML Template" />
              </router-link>
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
                  <router-link class="nav-link" to="/">홈</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/broadcasts">라이브 방송</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/replay">방송 다시보기</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/templates">법률서류 템플릿</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/qna">법률Q&A</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/ci">브랜드 가치</router-link>
                </li>
              </ul>
            </div>

            <!-- 모바일 - 햄버거창 메뉴이동 네비게이션 -->
            <div class="d-block d-lg-none h-100" data-simplebar="">
              <ul class="navbar-nav">
                <li class="nav-item">
                  <router-link class="nav-link" to="/">홈</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/broadcasts">라이브 방송</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/replay">방송 다시보기</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/templates">법률서류 템플릿</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/qna">법률Q&A</router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/ci">브랜드 가치</router-link>
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
/* 필요시 여기에 추가 스타일을 작성하세요 */
</style>
