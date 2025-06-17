<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const clientId = ref('')
const nickname = ref('')
const userNo = ref(null)
const error = ref('')

onMounted(async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/api/user/mypage', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    clientId.value = res.data.clientId
    nickname.value = res.data.nickname
    userNo.value = res.data.userNo

  } catch (err) {
    console.error(err)
    error.value = '데이터를 불러오지 못했습니다.'
  }
})
</script>

<template>
  <section>
    <h2>마이페이지</h2>
    <div v-if="error">{{ error }}</div>
    <div v-else>
      <p><strong>회원번호:</strong> {{ userNo }}</p>
      <p><strong>아이디:</strong> {{ clientId }}</p>
      <p><strong>닉네임:</strong> {{ nickname }}</p>
    </div>
  </section>
</template>

<style scoped>
h2 {
  font-weight: bold;
  margin-bottom: 1rem;
}
</style>
