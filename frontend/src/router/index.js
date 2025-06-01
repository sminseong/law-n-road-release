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
  ]
})

export default router
