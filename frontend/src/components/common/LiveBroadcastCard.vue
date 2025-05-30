<script setup>
const props = defineProps({
    broadcast: {
        type: Object,
        required: true
    }
})

// 예상 데이터 구조 예시
// broadcast = {
//   isLive: true,
//   videoEmbedUrl: 'https://player.example.com/embed/...',
//   thumbnail: '/images/thumbnail_waiting.png',
//   title: '방송 제목',
//   tags: [...],
//   hostImage: '...',
//   hostName: '...',
//   hostDesc: '...',
//   link: '/live.html'
// }
</script>

<template>
    <section class="mb-10">
        <div class="container">
            <div class="live-card mx-auto position-relative rounded shadow-sm overflow-hidden">

                <!-- 🎥 영상 or 썸네일 영역 (고정 높이) -->
                <div class="live-media-wrapper">
                    <div class="media-inner">
                        <iframe v-if="broadcast.isLive" :src="broadcast.videoEmbedUrl" frameborder="0"
                            allow="autoplay; fullscreen" allowfullscreen class="media-iframe"></iframe>

                        <div v-else class="media-thumbnail-wrapper">
                            <img :src="broadcast.thumbnail" alt="방송 썸네일" class="media-thumbnail" />
                            
                            <div class="loading-overlay">
                                <div class="dots-loader">
                                    <span></span><span></span><span></span>
                                    <p class="placeholder-text">라이브 방송 준비 중…</p>
                                </div>
                            </div>
                        </div>

                        <span class="badge live-badge">
                            {{ broadcast.isLive ? 'LIVE ON' : '' }}
                        </span>
                    </div>
                </div>

                <!-- 📄 본문 영역 -->
                <div class="live-body position-relative bg-white p-4">
                    <h4 class="live-title">
                        {{ broadcast.title }}
                        <div class="live-tags">
                            <span v-for="(tag, index) in broadcast.tags" :key="index" class="tag">
                                {{ tag }}
                            </span>
                        </div>
                    </h4>

                    <div class="live-footer">
                        <div class="host-info d-flex align-items-center mb-4">
                            <img :src="broadcast.hostImage" :alt="broadcast.hostName" class="host-avatar me-2" />
                            <div class="host-meta">
                                <strong>{{ broadcast.hostName }}</strong><br />
                                <small>{{ broadcast.hostDesc }}</small>
                            </div>
                        </div>

                        <a :href="broadcast.link" class="btn btn-secondary live-btn">
                            {{ broadcast.isLive ? '방송 보러가기' : '사전질문 하러가기' }}
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>
</template>

<style scoped>
.live-media-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 16 / 9;
    /* ✅ CSS로 명시: 최신 브라우저 지원 */
    background-color: #000;
}

.media-inner {
    position: relative;
    width: 100%;
    height: 100%;
}

.media-iframe {
    position: absolute;
    inset: 0;
    /* top: 0, bottom: 0, left: 0, right: 0 */
    width: 100%;
    height: 100%;
    border: 0;
}

.media-thumbnail-wrapper {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.media-thumbnail {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.live-badge {
    position: absolute;
    top: 0.5rem;
    left: 0.5rem;
    z-index: 3;
    background: #d33;
    color: white;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
}

.live-card {
  max-width: 100%;
}

.placeholder-text {
  color: white;
  font-weight: bold;
  font-size: 1.75rem;
  text-shadow: 0 0 6px rgba(0, 0, 0, 0.8); /* ✅ 어두운 배경 위에서 강조 */
}

.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 2;
  text-align: center;
  pointer-events: none;
  background-color: rgba(0, 0, 0, 0.5); /* 반투명 블랙 오버레이 */
  backdrop-filter: blur(1px); /* optional: 배경 살짝 흐리게 */
}

.dots-loader {
  display: flex;
  gap: 0.4rem;
  margin-bottom: 0.5rem;
}

.dots-loader span {
  width: 10px;
  height: 10px;
  background: white;
  border-radius: 50%;
  animation: bounce 0.6s infinite alternate;
}

.dots-loader span:nth-child(2) {
  animation-delay: 0.2s;
}

.dots-loader span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes bounce {
  to {
    transform: translateY(-10px);
    opacity: 0.5;
  }
}
</style>
