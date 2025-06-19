export default [
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/admin/DashboardView.vue')
    },

    {
        path: '/admin/member',
        name: 'MemberManagement',
        component: () => import('@/views/admin/MemberManagementView.vue')
    },

    {
        path: '/admin/ads',
        name: 'AdManagement',
        component: () => import('@/views/admin/AdManagementView.vue')
    },

]