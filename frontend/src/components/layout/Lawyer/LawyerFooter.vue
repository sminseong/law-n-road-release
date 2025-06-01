<template>
  <footer class="footer border-top py-4 mt-auto text-muted">
    <div class="container small">

      <!-- 슬로건 / 브랜드 소개 -->
      <div class="mb-0">
        <div class="fw-semibold">로앤로드 - 도로 위 모든 사건을 다루다</div>
      </div>

      <!-- 브랜드 가치 설명 -->
      <div class="mb-2">
        관찰을 기반으로 신뢰를 형성하고, 신뢰를 통해 전문가와 연결되는 구조를 지향합니다.
      </div>

      <!-- 고객센터 / 링크 모음 -->
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-start">
        <div>
          © 2025 Law &amp; Road. All rights reserved.
        </div>
        <ul class="list-inline mb-0">
          <!-- 외부 링크는 그대로 <a> 유지 -->
          <li class="list-inline-item">
            <a
                href="https://github.com/lawroad-project"
                target="_blank"
                class="link-secondary text-decoration-none"
            >
              GitHub
            </a>
          </li>
          <li class="list-inline-item">
            <a
                href="https://www.notion.so/lawroad-docs"
                target="_blank"
                class="link-secondary text-decoration-none"
            >
              문서
            </a>
          </li>

          <!-- 내부 링크들은 <router-link>로 변환 -->
          <li class="list-inline-item">
            <router-link to="/ci" class="link-secondary text-decoration-none">
              브랜드 소개
            </router-link>
          </li>
          <li class="list-inline-item">
            <router-link to="/terms" class="link-secondary text-decoration-none">
              이용약관
            </router-link>
          </li>
          <li class="list-inline-item">
            <router-link to="/privacy" class="link-secondary text-decoration-none">
              개인정보처리방침
            </router-link>
          </li>
          <li class="list-inline-item">
            <router-link to="/ci" class="link-secondary text-decoration-none">
              과실비율
            </router-link>
          </li>
          <li class="list-inline-item">
            <router-link to="/ci" class="link-secondary text-decoration-none">
              고객센터
            </router-link>
          </li>
        </ul>
      </div>

      <!-- 저작권 -->

    </div>
  </footer>
</template>

<style scoped>
.footer {
  font-size: 0.85rem;
  background-color: #ffffff;
  color: #6c757d;
}
a.link-secondary,
.router-link-active.link-secondary {
  color: #6c757d;
  text-decoration: none;
}
a.link-secondary:hover,
.router-link-active.link-secondary:hover {
  text-decoration: underline;
}
</style>
