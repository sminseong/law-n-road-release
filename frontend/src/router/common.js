export default [
    {
        path: '/',
        name: 'home',
        component: () => import('@/views/HomeView.vue')
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/common/LoginView.vue')
    },
    {
        path: '/login/admin',
        name: 'AdminLogin',
        component: () => import('@/views/common/AdminLoginView.vue')
    },
    {
        path: '/signup/lawyer',
        name: 'LawyerSignup',
        component: () => import('@/views/common/LawyerSignupView.vue')
    },
    {
        path: '/signup/client',
        name: 'ClientSignup',
        component: () => import('@/views/common/ClientSignupView.vue')
    },
    {
        path: '/ci',
        name: 'BrandValue',
        component: () => import('@/views/common/BrandValueView.vue')
    },
    {
        path: '/homepage/:lawyerNo',
        name: 'LawyerHomePage',
        component: () => import('@/views/lawyer/LawyerHomePageView.vue')
    },

    // ---------- 검색 결과 ----------
    // src/router/index.js
    {
        path: '/search',
        name: 'SearchResults',
        component: () => import('@/views/common/SearchResultsView.vue'),
        props: route => {
            // 1) 원본문자열
            const raw = route.query.keyword?.trim() || ''

            // 2) onlyLawyers 플래그
            const onlyLawyers = raw.includes('변호사만')

            // 3) keyword 는 수정 없이 원본 그대로
            const keyword = raw

            // 4) category 기본값 (쿼리파라미터 우선)
            let category = route.query.category ?? null

            // 5) 쿼리 없고 키워드가 있을 때만 자동 매핑
            if (!category && raw) {
                if (/(중대사고|형사처벌)/.test(raw)) {
                    // 사고 키워드가 중복이라 2번 먼저 검사
                    category = 2
                }
                else if (/(자전거|킥보드|보행자|차량 외 사고)/.test(raw)) {
                    // 사고 키워드가 중복이라 6번 먼저 검사
                    category = 6
                }
                else if (/사고/.test(raw)) {
                    category = 1
                }
                else if (/(음주|무면허)/.test(raw)) {
                    category = 3
                }
                else if (/(보험|행정)/.test(raw)) {
                    category = 4
                }
                else if (/(과실|분쟁)/.test(raw)) {
                    category = 5
                }
                else {
                    category = null   // 아무 패턴도 안 걸리면 null
                }
            }

            return {
                keyword,
                onlyLawyers,
                category
            }
        }
    },

    // ---------- QNA ----------
    {
        path: '/qna',
        name: 'QaList',
        component: () => import('@/views/common/QaListView.vue')
    },
    {
        path: '/qna/:id',
        name: 'QaDetailView',
        component: () => import('@/views/common/QaDetailView.vue')
    },
    // -------- id/Pw 찾기
    {
        path: '/forgot-password',
        component: () => import('@/views/common/ForgotPasswordView.vue')
    },





    {
        path: '/user/mypage',
        name: 'UserMypage',
        component: () => import('@/views/common/UserMypageHome.vue')
    },
    {
        path: '/client/profile/edit',
        name: 'ClientProfileEdit',
        component: () => import('@/views/client/ClientProfileEditView.vue'),
        meta: { requiresAuth: true }  // ✅ 여기에 로그인 필요 표시
    },




    // ---------- 템플릿 ----------
    {
        path: '/templates',
        name: 'TemplateList',
        component: () => import('@/views/client/template/TemplateListView.vue')
    },
    {
        path: '/templates/:no',
        name: 'TemplateDetail',
        component: () => import('@/views/client/template/TemplateDetailView.vue')
    },

    // ---------- 결제 ----------
    {
        path: '/payment/success',
        name: 'PaymentSuccess',
        component: () => import('@/views/common/PaymentSuccess.vue'),
        props: route => ({
            orderId:       route.query.orderId,
            amount:        Number(route.query.amount),
            paymentKey:    route.query.paymentKey,
            reservationNo: Number(route.query.reservationNo)
        })
    },
    {
        path: '/payment/fail',
        name: 'PaymentFail',
        component: () => import('@/views/common/PaymentFail.vue')
    },
    {
        path: '/naver-login',
        name: 'NaverLoginHandler',
        component: () => import('@/views/common/NaverLoginHandler.vue')
    }

]