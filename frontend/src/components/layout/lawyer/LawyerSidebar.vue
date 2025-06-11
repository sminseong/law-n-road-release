<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, watch } from 'vue'
const router = useRouter()
const route = useRoute()
const emit = defineEmits(['update:title'])

const lawyer = {
  // 실제 적용 시에는 로그인 완료 후 API로 받아오는 값으로 대체하면 됨
  name: '김수영',
  profileImage: '/img/profiles/kim.png',
}

const menuItems = [
  { label: '홈 대시보드', icon: 'bi-house-door', path: '/lawyer' },
  { label: '1:1 상담예약', icon: 'bi-chat-dots', path: '/lawyer/reservation' },
  { label: '방송 스케줄', icon: 'bi-mic', path: '/lawyer/broadcasts/schedule' },
  { label: '문서 템플릿', icon: 'bi-folder2-open', path: '/lawyer/templates' },
  { label: 'Q&A 관리', icon: 'bi-chat-left-dots', path: '/lawyer/qna' },
  { label: '광고 관리', icon: 'bi-megaphone', path: '/lawyer/ads' },
  { label: '계정 설정', icon: 'bi-person-gear', path: '/lawyer/profile' },
]

function go(path, label) {
  emit('update:title', label)
  router.push(path)
}

function logout() {
  // 여기에 실제 로그아웃 처리 로직 넣기
  // 예: 토큰 삭제, 상태 초기화, 로그인 페이지로 이동
  console.log('로그아웃')
  router.push('/login')
}

watch(
  () => route.path,
  () => {
    const current = menuItems.find(item => item.path === route.path)
    if (current) emit('update:title', current.label)
  },
  { immediate: true }
)
</script>

<template>
  <aside class="sidebar d-flex flex-column justify-content-between align-items-center">
    <div class="w-100">
      <!-- 로고 -->
      <div class="logo-box mb-5 text-center">
        <img src="@/assets/images/logo/logo-dark.png" alt="로앤로드 로고" class="logo-img" />
      </div>

      <!-- 프로필 -->
      <div class="profile-box text-center mb-5 mt-3">
        <img :src="lawyer.profileImage" alt="프로필" class="profile-img" />
        <div class="profile-name mt-2 fw-semibold">{{ lawyer.name }} 변호사</div>
        <button class="btn btn-sm btn-outline-light mt-2"
                @click="go('/lawyer/profile', '계정 설정')">
          내 홈페이지 보기
        </button>
      </div>

      <!-- 메뉴 -->
      <ul class="nav-list w-100 px-3">
        <li v-for="item in menuItems" :key="item.path"
            :class="{ active: route.path === item.path }"
            @click="go(item.path, item.label)">
          <i :class="`bi ${item.icon} me-2`"></i> {{ item.label }}
        </li>
      </ul>
    </div>

    <!-- 하단 로그아웃 버튼 -->
    <div class="logout-box w-100 text-start px-3 pb-20">
      <small class="logout-link" @click="logout">로그아웃</small>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 240px;
  min-height: 100vh;
  background-color: #2e3b4e;
  color: white;
  padding: 24px 12px;
}
.logo-img {
  width: 160px;
}
.profile-img {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}
.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.nav-list li {
  padding: 10px;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}
.nav-list li:hover {
  background-color: #3c4c60;
}
.nav-list li.active {
  background-color: #445b7c;
  font-weight: bold;
}
.logout-box {
  width: 100%;
}

.logout-link {
  color: #767779; /* 옅은 회색 */
  font-size: 0.75rem;
  cursor: pointer !important;
  transition: opacity 0.2s;
}

.logout-link:hover {
  opacity: 0.6;
  text-decoration: underline;
}
</style>
