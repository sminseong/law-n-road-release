<template>
  <ClientFrame>
    <div class="brand-value">
      <!-- 헤더 섹션 -->
      <div class="mission-image">
        <img
            src="https://kr.object.ncloudstorage.com/law-n-road/uploads/dummy_img/ads/%EC%A0%9C%EB%AA%A9%20%EC%97%86%EC%9D%8C-1296.png"
            alt="로앤로드 미션 이미지"
            class="brand-image"
        >
      </div>

      <section class="hero-section">
        <div class="container">
          <h1 class="main-title">브랜드 가치</h1>
          <p class="subtitle">도로 위 모든 사건을 다루다</p>
        </div>
      </section>

      <!-- 미션 섹션 -->
      <section class="mission-section">
        <div class="container">
          <h2>우리의 미션</h2>
          <div class="mission-content">
            <div class="mission-text">
              <h3>교통 법률 서비스의 첫 문턱을 낮추는 플랫폼</h3>
              <p>
                교통사고, 음주운전, 무면허 운전, 뺑소니 등 도로 위에서 누구나 겪을 수 있는
                다양한 법적 문제는 일상 속에서 예고 없이 발생할 수 있습니다.
                로앤로드는 이러한 어려움에 처한 이용자의 진입장벽을 낮추기 위해 기획된 플랫폼입니다.
              </p>
            </div>
            <div class="mission-image">
              <!-- 이미지 영역 -->
              <div class="placeholder-image">미션 이미지</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 핵심 가치 섹션 -->
      <section class="values-section">
        <div class="container">
          <h2>핵심 가치</h2>
          <div class="values-grid">
            <div class="value-card">
              <div class="value-icon">🤝</div>
              <h3>신뢰성</h3>
              <p>실시간 라이브 방송을 통해 변호사의 상담 방식과 전문성을 미리 확인할 수 있습니다.</p>
            </div>
            <div class="value-card">
              <div class="value-icon">🔓</div>
              <h3>접근성</h3>
              <p>복잡한 법률 서비스를 누구나 쉽게 접근할 수 있도록 진입장벽을 낮췄습니다.</p>
            </div>
            <div class="value-card">
              <div class="value-icon">💬</div>
              <h3>소통</h3>
              <p>실시간 채팅을 통해 변호사와 직접 소통하며 궁금증을 해결할 수 있습니다.</p>
            </div>
            <div class="value-card">
              <div class="value-icon">🎯</div>
              <h3>전문성</h3>
              <p>교통사고 전문 변호사들이 양질의 법률 정보와 서비스를 제공합니다.</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 서비스 특징 섹션 -->
      <section class="features-section">
        <div class="container">
          <h2>로앤로드만의 특별함</h2>
          <div class="features-list">
            <div class="feature-item">
              <h3>📺 실시간 라이브 방송</h3>
              <p>변호사의 전문성과 상담 스타일을 사전에 체험할 수 있는 혁신적인 서비스</p>
            </div>
            <div class="feature-item">
              <h3>📋 법률 문서 템플릿</h3>
              <p>전문 변호사가 작성한 표준화된 법률 문서로 간단한 문제는 직접 해결</p>
            </div>
            <div class="feature-item">
              <h3>🔔 스마트 알림 시스템</h3>
              <p>관심 있는 방송 시작이나 상담 예약 시간을 카카오톡 알림톡으로 안내</p>
            </div>
            <div class="feature-item">
              <h3>💰 합리적인 비용</h3>
              <p>부담스러운 비용 걱정 없이 필요한 만큼만 이용할 수 있는 유연한 요금제</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 비전 섹션 -->
      <section class="vision-section">
        <div class="container">
          <div class="vision-content">
            <h2>우리의 비전</h2>
            <p class="vision-text">
              "로앤로드는 교통 법률 서비스의 패러다임을 바꾸는 핵심 서비스로 자리매김하여,
              모든 운전자가 안심하고 도로를 달릴 수 있는 세상을 만들어갑니다."
            </p>
          </div>
        </div>
      </section>
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

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 헤더 섹션 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 100px 0;
  text-align: center;
}

.main-title {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.5rem;
  opacity: 0.9;
}

/* 미션 섹션 */
.mission-section {
  padding: 80px 0;
  background-color: #f8f9fa;
}

.mission-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}

.mission-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
  align-items: center;
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

.placeholder-image {
  background-color: #e9ecef;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: #6c757d;
  font-size: 1.2rem;
}

/* 핵심 가치 섹션 */
.values-section {
  padding: 80px 0;
}

.values-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.value-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.value-card:hover {
  transform: translateY(-5px);
}

.value-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
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

/* 서비스 특징 섹션 */
.features-section {
  padding: 80px 0;
  background-color: #f8f9fa;
}

.features-section h2 {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #333;
}

.features-list {
  display: grid;
  gap: 2rem;
}

.feature-item {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  border-left: 5px solid #667eea;
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

/* 비전 섹션 */
.vision-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  color: white;
  text-align: center;
}

.vision-section h2 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.vision-text {
  font-size: 1.3rem;
  line-height: 1.8;
  max-width: 800px;
  margin: 0 auto;
  opacity: 0.95;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .main-title {
    font-size: 2rem;
  }

  .subtitle {
    font-size: 1.2rem;
  }

  .mission-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .values-grid {
    grid-template-columns: 1fr;
  }

  .hero-section,
  .mission-section,
  .values-section,
  .features-section,
  .vision-section {
    padding: 50px 0;
  }
}
</style>