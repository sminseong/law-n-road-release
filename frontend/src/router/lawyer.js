export default [
    // ---------- 기본, 대시보드 등 ----------
    {
        path: '/lawyer',
        name: 'lawyer',
        component: () => import('@/views/lawyer/DashboardView.vue')
    },
    {
        path: '/lawyer/profile',
        name: 'LawyerProfile',
        component: () => import('@/views/lawyer/ProfileSettingView.vue')
    },

    // ---------- QNA ----------
    {
        path: '/lawyer/qna',
        name: 'LawyerQna',
        component: () => import('@/views/lawyer/QnaListView.vue')
    },

    // ---------- 예약 ----------
    {
        path: '/lawyer/reservation',
        name: 'LawyerReservation',
        component: () => import('@/views/lawyer/ReservationView.vue')
    },

    // ---------- 방송 ----------
    {
        path: '/lawyer/broadcasts/schedule',
        name: 'LawyerBroadcasts',
        component: () => import('@/views/lawyer/BroadcastScheduleView.vue')
    },

    // ---------- 템플릿 ----------
    {
        path: '/lawyer/templates',
        name: 'LawyerTemplateList',
        component: () => import('@/views/lawyer/template/TemplateListView.vue')
    },
    // {
    //     path: '/lawyer/templates/register',
    //     name: 'TemplateRegister',
    //     component: () => import('@/views/lawyer/template/TemplateFormView.vue')
    // },
    {
        path: '/lawyer/templates/register',
        name: 'TemplateEditor',
        component: () => import('@/views/lawyer/template/TemplateEditorView.vue')
    },
    {
        path: '/lawyer/templates/:no',
        name: 'LawyerTemplateDetail',
        component: () => import('@/views/lawyer/template/TemplateDetailView.vue')
    },
    {
        path: '/lawyer/templates/edit/:no',
        name: 'TemplateEdit',
        component: () => import('@/views/lawyer/template/TemplateFormView.vue'),
        props: true
    },

    // ---------- 광고 ----------
    {
        path: '/lawyer/ads',
        name: 'LawyerAds',
        component: () => import('@/views/lawyer/AdManagementView.vue')
    },
]