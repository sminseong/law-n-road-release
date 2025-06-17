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
        path: '/lawyer/signup',
        name: 'LawyerSignup',
        component: () => import('@/views/common/LawyerSignupView.vue')
    },
    {
        path: '/client/signup',
        name: 'ClientSignup',
        component: () => import('@/views/common/ClientSignupView.vue')
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

    {
        path: '/forgot-password',
        component: () => import('@/views/common/ForgotPasswordView.vue')
    },
    {
        path: '/user/mypage',
        name: 'UserMypage',
        component: () => import('@/views/common/UserMypageHome.vue')
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

]