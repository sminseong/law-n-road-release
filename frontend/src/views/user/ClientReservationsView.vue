<script setup>
import {ref, onMounted} from 'vue'
import axios from 'axios'
import {useRoute} from 'vue-router'
import TimeSlot from "@/components/common/TimeSlot.vue";
import UserFrame from "@/components/layout/User/UserFrame.vue";

const route = useRoute()
const lawyerNo = ref(route.params.lawyerNo)
const lawyerName = ref(route.params.lawyerName)

const startDate = ref(new Date().toISOString().slice(0, 10))

const weeklySlots = ref([])

onMounted(async () => {
  try {
    const res = await axios.get(
        `/api/lawyers/${lawyerNo.value}/slots`,
        {
          params: {
            startDate: startDate.value
          }
        }
    )
    weeklySlots.value = res.data
  } catch (err) {
    console.error('주간 슬롯 조회 실패', err)
  }
})

function handleSelect(payload) {
  console.log('선택된 예약 →', payload)
}
</script>

<template>
  <UserFrame>
    <div class="container mx-auto p-6">
      <h1 class="text-2xl font-bold mb-6">
        변호사 {{ lawyerName }}님 주간 예약 가능한 시간
      </h1>

      <TimeSlot
          :weeklySlots="weeklySlots"
          @select="handleSelect"
      />
    </div>
  </UserFrame>
</template>

<style scoped>
</style>
