<script setup>
import ClientFrame from '@/components/layout/client/ClientFrame.vue'
import CartItem from '@/components/cart/CartItem.vue'
import CartSummary from '@/components/cart/CartSummary.vue'
import { ref, computed, onMounted } from 'vue'
import http from '@/libs/HttpRequester'

const cartItems = ref([])
const isLoading = ref(true)

onMounted(async () => {
  const res = await http.get('/api/cart')
  cartItems.value = res.data
  isLoading.value = false
})

const totalPrice = computed(() =>
    cartItems.value.reduce((sum, item) => {
      const discounted = item.price * (1 - item.discountRate / 100)
      return sum + discounted * item.quantity
    }, 0)
)
</script>

<template>
  <ClientFrame>
    <div class="container py-5">
      <div class="row">
        <!-- 장바구니 목록 -->
        <div class="col-lg-8 col-md-7">
          <div class="py-3">
            <div
                v-if="cartItems.length === 0 && !isLoading"
                class="alert alert-warning text-center"
            >
              장바구니가 비어 있습니다.
            </div>

            <ul class="list-group list-group-flush" v-else>
              <CartItem
                  v-for="item in cartItems"
                  :key="item.no"
                  :item="item"
                  @remove="removeItem(item.no)"
              />
            </ul>

            <div class="d-flex justify-content-between mt-4">
              <router-link to="/templates" class="btn btn-outline-primary">
                계속 쇼핑하기
              </router-link>
              <button class="btn btn-dark" @click="updateCart">
                장바구니 업데이트
              </button>
            </div>
          </div>
        </div>

        <!-- 요약 카드 -->
        <div class="col-lg-4 col-md-5">
          <CartSummary :totalPrice="totalPrice" />
        </div>
      </div>
    </div>
  </ClientFrame>
</template>
