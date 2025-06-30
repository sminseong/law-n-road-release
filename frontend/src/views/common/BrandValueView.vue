<template>
  <ClientFrame>
    <div class="brand-value">
      <div class="mission-image">
        <img
            src="/img/ci/ci.png"
            alt="로앤로드 미션 이미지"
            class="brand-image"
        >
      </div>
    </div>

  </ClientFrame>
</template>

<script>
import ClientFrame from "@/components/layout/client/ClientFrame.vue";

export default {
  name: 'BrandValueView',
  components: {ClientFrame},
  data() {
    return {
      // 필요시 데이터 추가
    }
  },
  mounted() {
    // 페이지 로드 시 실행될 로직
    console.log('브랜드 가치 페이지가 로드되었습니다.')
  },
  methods: {
    // 필요시 메서드 추가
  }
}
</script>

<style scoped>
.brand-value {
  min-height: 100vh;
}

.mission-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}

.mission-text h3 {
  font-size: 1.8rem;
  color: #667eea;
  margin-bottom: 1rem;
}

.mission-text p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #666;
}


.values-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}


.value-card h3 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.value-card p {
  color: #666;
  line-height: 1.6;
}

.features-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}

.feature-item h3 {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
}


.vision-section h2 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

</style>