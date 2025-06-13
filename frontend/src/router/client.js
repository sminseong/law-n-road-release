export default [
    // ---------- 기본, 대시보드 등 ----------
    {
        path: '/client/mypage',
        name: 'ClientMypage',
        component: () => import('@/views/client/MypageView.vue')
    },

    // ---------- QNA ----------
    {
        path: '/client/qna/register',
        name: 'ClientQaRegister',
        component: () => import('@/views/client/ClientQaRegisterView.vue'),
        meta: {requiresAuth: true, role: 'client'}
    },

    // ---------- 예약 ----------
    {
        path: '/client/reservations/:lawyerNo/:lawyerName',
        name: 'ClientReservations',
        component: () => import('@/views/client/ClientReservationsView.vue')
    },
    {
        path: '/client/reservations/:clientNo',
        name: 'ClientReservationsList',
        component: () => import('@/views/client/ClientReservationsList.vue')
    },

    // ---------- 방송 ----------
    {
        path: '/client/broadcasts',
        name: 'ClientBroadcasts',
        component: () => import('@/views/client/ClientBroadcastsView.vue')
    },

    // ---------- 템플릿 ----------
    {
        path: '/client/templates',
        name: 'ClientTemplates',
        component: () => import('@/views/client/TemplateView.vue')
    },
    {
        path: '/templates',
        name: 'ClientTemplateList',
        component: () => import('@/views/client/template/TemplateListView.vue')
    },
    {
        path: '/templates/:no',
        name: 'ClientTemplateDetail',
        component: () => import('@/views/client/template/TemplateDetailView.vue')
    },

    // ---------- 광고 ----------
]