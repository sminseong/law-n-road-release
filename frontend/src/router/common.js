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


    // ---------- 방송 ----------
    {
        path: '/vod/list',
        name: 'VodList',
        component: () => import('@/views/client/PublicVodListView.vue'),
        props: true
    },
    {
        path: '/vod/:broadcastNo',
        name: 'VodView',
        component: () => import('@/views/client/PublicVodView.vue')
    },
    {
        path: '/broadcasts/schedule',
        name: 'BroadCastsSchedule',
        component: () => import('@/views/client/PublicScheduleView.vue')
    },
    {
        path: '/broadcasts/schedule/:date',
        name: 'ScheduleDetailView',
        component: () => import('@/views/client/PublicScheduleDetailView.vue'),
        props: true
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
        component: () => import('@/views/common/PaymentSuccess.vue')
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