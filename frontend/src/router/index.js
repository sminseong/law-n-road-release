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
      name: 'TemplateList',
      component: () => import('@/views/lawyer/template/TemplateListView.vue')
    },
    {
      path: '/lawyer/templates/register',
      name: 'TemplateRegister',
      component: () => import('@/views/lawyer/template/TemplateFormView.vue')
    },
    {
      path: '/lawyer/templates/edit/:id',
      name: 'TemplateEdit',
      component: () => import('@/views/lawyer/template/TemplateFormView.vue'),
      props: true
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


  ]
})

export default router