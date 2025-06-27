<script setup>
import { ref, onMounted } from 'vue'
import http from '@/libs/HttpRequester'

const topLawyers = ref([])

const openChatbot = () => {
  window.dispatchEvent(new Event('open-chatbot'))
}

onMounted(async () => {
  const res = await http.get('/api/public/main/top-lawyers')
  console.log(res.data)
  topLawyers.value = res.data
})
</script>

<template>
  <aside class="right-nav">
    <div class="right-nav">
      <div class="ranking-box">
        <div class="ranking-title">조회수 랭킹</div>
        <div class="lawyer-list collapsed mb-sm-3" id="lawyerList">
          <div
              v-for="lawyer in topLawyers"
              :key="lawyer.lawyerNo"
              class="lawyer-card"
              @click="$router.push(`/lawyer/${lawyer.lawyerNo}/homepage`)"
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