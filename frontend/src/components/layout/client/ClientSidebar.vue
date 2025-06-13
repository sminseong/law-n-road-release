<template>
    <aside class="right-nav">
        <!-- 사이드 네비게이션 -->
        <div class="right-nav">
            <div class="ranking-box">
                <div class="ranking-title">조회수 랭킹</div>

                <div class="lawyer-list collapsed mb-sm-3" id="lawyerList">
                <div class="lawyer-card" onclick="location.href='/lawyer';">
                    <img src="/img/profiles/kim.png" alt="김수영" />
                    <span>김수영</span>
                </div>
                <div class="lawyer-card" onclick="location.href='/lawyer';">
                    <img src="/img/profiles/lee.png" alt="이철민" />
                    <span>이철민</span>
                </div>
                <div class="lawyer-card" onclick="location.href='/lawyer';">
                    <img src="/img/profiles/park.png" alt="박하늘" />
                    <span>박하늘</span>
                </div>
                </div>

                <!-- 더 보기 대신 목록 페이지로 이동 -->
                <a href="./search.html" class="more-link">더 알아보기 →</a>
            </div>

            <div class="chatbots">
                <button onclick="location.href='./chatbot.html'">과실비율</button>
                <button onclick="location.href='./chatbot.html'">고객센터</button>
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