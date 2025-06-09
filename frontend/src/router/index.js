// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/lawyer',
      name: 'lawyer',
      component: () => import('@/views/lawyer/DashboardView.vue')
    },
    {
      path: '/lawyer/reservation',
      name: 'LawyerReservation',
      component: () => import('@/views/lawyer/ReservationView.vue')
    },
    {
      path: '/lawyer/broadcasts',
      name: 'LawyerBroadcasts',
      component: () => import('@/views/lawyer/BroadcastScheduleView.vue')
    },
    {
      path: '/lawyer/templates',
      name: 'LawyerTemplates',
      component: () => import('@/views/lawyer/TemplateManagementView.vue')
    },
    {
      path: '/lawyer/ads',
      name: 'LawyerAds',
      component: () => import('@/views/lawyer/AdManagementView.vue')
    },
    {
      path: '/lawyer/qna',
      name: 'LawyerQna',
      component: () => import('@/views/lawyer/QnaListView.vue')
    },
    {
      path: '/lawyer/profile',
      name: 'LawyerProfile',
      component: () => import('@/views/lawyer/ProfileSettingView.vue')
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/common/LoginView.vue')
    },
    {
      path: '/user/signup',
      name: 'UserSignup',
      component: () => import('@/views/common/UserSignupView.vue')
    },
    {
      path: '/lawyer/signup',
      name: 'LawyerSignup',
      component: () => import('@/views/common/LawyerSignupView.vue')
    },
    {
      path: '/user/mypage',
      name: 'UserMypage',
      component: () => import('@/views/user/MypageView.vue')
    },
    {
      path: '/user/templates',
      name: 'UserTemplates',
      component: () => import('@/views/user/TemplateView.vue')
    },
    {
      path: '/client/reservations/:lawyerNo/:lawyerName',
      name: 'ClientReservations',
      component: () => import('@/views/user/ClientReservationsView.vue')
    },
    {
      path: '/broadcasts',
      name: 'UserBroadcasts',
      component: () => import('@/views/user/UserBroadcasts.vue')
    },
    {
      path: '/qna',
      name: 'ClientQaList',
      component: () => import('@/views/common/QaListView.vue')
    },
    // 로그인한 “client” 권한으로만 글쓰기
    {
     path: '/client/qna/register',
     name: 'ClientQaRegister',
     component: () => import('@/views/user/ClientQaRegister.vue'),
     meta: { requiresAuth: true, role: 'client' }
    },
    {
      path: '/qna/:id',
      name: 'ClientQaDetail',
      component: () => import('@/views/common/QaDetailView.vue'),
    },
  ]
})

export default router