<script setup>
import LawyerFrame from '@/components/layout/lawyer/LawyerFrame.vue'
import VueCal from 'vue-cal'
import 'vue-cal/dist/vuecal.css'

const events = [
  {
    start: '2025-06-10 10:00',
    end: '2025-06-10 11:00',
    title: '교통사고 법률 방송'
  },
  {
    start: '2025-06-12 14:00',
    end: '2025-06-12 15:30',
    title: '음주운전 Q&A'
  }
]
</script>

<template>
  <LawyerFrame>
    <div class="container py-4">
      <VueCal
          :events="events"
          style="height: 600px"
          default-view="month"
          hide-title
          active-view="month"
          :on-event-click="event => alert('방송 클릭: ' + event.title)"
      />
    </div>
  </LawyerFrame>
</template>
