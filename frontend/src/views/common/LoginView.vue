<script setup>
import { ref } from 'vue'
import UserFrame from '@/components/layout/User/UserFrame.vue' // ✅ 추가

const email = ref('')
const password = ref('')
const accountType = ref('user') // 기본은 '의뢰인'

const submitLogin = () => {
  console.log({
    email: email.value,
    password: password.value,
    type: accountType.value
  })
  // TODO: API 호출 및 라우팅 처리
}
</script>

<template>
  <UserFrame>
    <section class="my-lg-14 my-8">
      <div class="container">
        <div class="row justify-content-center align-items-center">
          <div class="col-12 col-md-6 col-lg-4 order-lg-1 order-2">
            <img
                src="/img/account/signin-g.png"
                alt="Login Illustration"
                class="img-fluid"
            />
          </div>

          <div class="col-12 col-md-6 offset-lg-1 col-lg-4 order-lg-2 order-1">
            <div class="mb-lg-9 mb-5">
              <h1 class="mb-1 h2 fw-bold">로그인</h1>
              <p>이메일과 비밀번호를 입력하고 계정 유형을 선택하세요.</p>
            </div>

            <form @submit.prevent="submitLogin">
              <div class="row g-3">
                <div class="col-12">
                  <input
                      v-model="email"
                      type="email"
                      class="form-control"
                      placeholder="이메일"
                      required
                  />
                </div>

                <div class="col-12">
                  <input
                      v-model="password"
                      type="password"
                      class="form-control"
                      placeholder="비밀번호"
                      required
                  />
                </div>

                <!-- 계정 유형 선택 -->
                <div class="col-12">
                  <label class="form-label">계정 유형</label>
                  <div class="d-flex gap-3">
                    <div class="form-check">
                      <input
                          class="form-check-input"
                          type="radio"
                          id="userType"
                          value="user"
                          v-model="accountType"
                      />
                      <label class="form-check-label" for="userType">의뢰인</label>
                    </div>
                    <div class="form-check">
                      <input
                          class="form-check-input"
                          type="radio"
                          id="lawyerType"
                          value="lawyer"
                          v-model="accountType"
                      />
                      <label class="form-check-label" for="lawyerType">변호사</label>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-between">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="rememberMe" />
                    <label class="form-check-label" for="rememberMe">자동 로그인</label>
                  </div>
                  <div>
                    <a href="/forgot-password">비밀번호 찾기</a>
                  </div>
                </div>

                <div class="col-12 d-grid">
                  <button type="submit" class="btn btn-primary">로그인</button>
                </div>

                <div>
                  아직 계정이 없으신가요? <a href="/signup">회원가입</a>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  </UserFrame>
</template>

<style scoped>
</style>
