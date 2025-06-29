<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/libs/HttpRequester'

const route = useRoute()
const topLawyers = ref([])

// 현재 페이지가 마이페이지면 랭킹 사이드바는 숨김
const showRanking = computed(() =>
    !route.path.startsWith('/client/mypage')
)

// 새로운 탭에서 페이지 열기
function openInNewTab(lawyerNo) {
  window.open(`/homepage/${lawyerNo}`, '_blank')
}

onMounted(async () => {
  const res = await http.get('/api/public/main/top-lawyers')
  // console.log(res.data)
  topLawyers.value = res.data
})
</script>

<template>
  <aside v-if="showRanking" class="right-nav">
    <div class="right-nav">
      <div class="ranking-box">
        <div class="ranking-title">조회수 랭킹</div>
        <div class="lawyer-list collapsed mb-sm-3" id="lawyerList">
          <div
              v-for="lawyer in topLawyers"
              :key="lawyer.lawyerNo"
              class="lawyer-card"
              @click="openInNewTab(lawyer.lawyerNo)"
          >
            <img :src="lawyer.profileImage || '/img/default-profile.png'" :alt="lawyer.name" />
            <span>{{ lawyer.name }}</span>
          </div>
        </div>
        <a href="/search" class="more-link">더 알아보기 →</a>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.right-nav {
  position: fixed;
  top: 210px;
  left: 50%;
  margin-left: 300px; /* (1200px / 2) + 여백 40px */
  width: 140px;
  z-index: 9999;
}
@media (max-width: 1300px) {
  .right-nav {
    display: none;
  }
}

</style>