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
        name: 'QaDetail',
        component: () => import('@/views/common/QaDetailView.vue')
    },

    {
        path: '/forgot-password',
        component: () => import('@/views/common/ForgotPasswordView.vue')
    }



]