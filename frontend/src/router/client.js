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
        meta: { requiresAuth: true, role: 'client' }
    },

    // ---------- 예약 ----------
    {
        path: '/client/reservations/:lawyerNo/:lawyerName',
        name: 'ClientReservations',
        component: () => import('@/views/client/ClientReservationsView.vue')
    },

    // ---------- 방송 ----------
    {
        path: '/client/broadcasts/:broadcastNo',
        name: 'ClientBroadcasts',
        component: () => import('@/views/client/ClientBroadcastsView.vue')
    },
    {
        path: '/client/vod',
        name: 'ClientBroadCastsVod',
        component: () => import('@/views/client/ClientVodView.vue')
    },
    {
        path: '/client/broadcasts/schedule',
        name: 'ClientBroadCastsSchedule',
        component: () => import('@/views/client/ClientScheduleView.vue')
    },
    {
        path: '/client/broadcasts/schedule/:date',
        name: 'ClientScheduleDetailView',
        component: () => import('@/views/client/ClientScheduleDetailView.vue'),
        props: true
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