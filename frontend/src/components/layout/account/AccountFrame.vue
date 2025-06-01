<script setup>
import { defineProps } from 'vue'
import logo from '@/assets/images/logo/logo.png'

const props = defineProps({
  // 헤더 중앙에 표시할 텍스트 (예: "로그인" 또는 "회원가입")
  title: {
    type: String,
    default: '로그인'
  }
})
</script>

<template>
  <div class="account-layout d-flex flex-column min-vh-100 bg-white">
    <!-- ─── 헤더 ─────────────────────────────────────────────────────────────────── -->
    <header>
      <!-- sticky 헤더 컨테이너에 position-relative를 추가 -->
      <div
          class="border-bottom sticky-top bg-white shadow-extra-light position-relative"
          style="z-index: 9999;"
      >
        <!-- (A) 상단 패딩 + 로고 -->
        <div class="py-4 pt-lg-3 pb-lg-0">
          <div class="container d-flex align-items-center py-2 px-1">
            <!-- 왼쪽: 로고만 남김 -->
            <div class="d-flex align-items-center">
              <a class="navbar-brand" href="/">
                <img src="@/assets/images/logo/logo.png" alt="Law & Road" />
              </a>
            </div>
            <!-- 로고와 타이틀·우측 여백 사이를 띄우기 위한 빈 flex-grow 공간 -->
            <div class="flex-grow-1"></div>
          </div>
        </div>

        <!-- (B) 중앙에 absolute로 “로그인” 제목 배치 -->
        <div class="position-absolute top-50 start-50 translate-middle">
          <h5 class="m-0 fw-bold">{{ props.title }}</h5>
        </div>

        <!-- (C) nav 블록: 로그인 페이지에서는 메뉴가 필요 없으므로 내부만 빈 채로 유지 -->
        <nav class="navbar navbar-expand-lg navbar-light navbar-default py-0 py-lg-4">
          <div class="container px-0 px-md-3">
            <ul class="navbar-nav align-items-center account-nav-items">
              <!-- 빈 채로 두면 됩니다 (opacity:0 처리) -->
            </ul>
          </div>
        </nav>
      </div>
    </header>
    <!-- ──────────────────────────────────────────────────────────────────────────────────── -->

    <!-- ─── 본문 영역 ─────────────────────────────────────────────────────────────────── -->
    <main class="flex-grow-1 d-flex justify-content-center align-items-start pt-5">
      <div class="container">
        <div class="content-container mx-auto py-4 px-3">
          <slot />
        </div>
      </div>
    </main>
    <!-- ──────────────────────────────────────────────────────────────────────────────────── -->

    <!-- ─── 푸터 ────────────────────────────────────────────────────────────────────────────── -->
    <footer class="account-footer text-center text-muted small py-3 border-top">
      © 2025 Law & Road · <a href="/terms">이용약관</a> · <a href="/privacy">개인정보처리방침</a>
    </footer>
    <!-- ──────────────────────────────────────────────────────────────────────────────────── -->
  </div>
</template>

<style scoped>
/* 메인 헤더와 동일한 그림자 */
.shadow-extra-light {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

/* UserHeader.vue와 동일하게 로고는 원본 크기 그대로 렌더링 */
.navbar-brand img {
  display: block;
  width: auto;
  height: auto;
}

/*
  nav 내부 항목(.account-nav-items)을 투명 처리만 하면
  높이는 그대로 유지되므로 로그인 페이지도 메인 헤더와 높이가 1px 오차 없이 동일하게 유지됩니다.
*/
.account-nav-items {
  opacity: 0;
}

/* 본문 카드 최대 너비 고정 */
.content-container {
  margin: 10px;
  width: 100%;
  max-width: 420px;
}

/* 푸터 배경 흰색 유지 */
.account-footer {
  background-color: #fff;
}
</style>
