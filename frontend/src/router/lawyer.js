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
    {
        path: '/lawyer/:lawyerNo/homepage',
        name: 'LawyerHomePage',
        component: () => import('@/views/lawyer/LawyerHomePageView.vue')
    },

    // ---------- QNA ----------
    {
        path: '/lawyer/qna',
        name: 'LawyerQna',
        component: () => import('@/views/lawyer/QnaListView.vue')
    },
    {
        path: '/lawyer/qna/comment/:id',
        name: 'CommentRegister',
        component: () => import('@/views/lawyer/QnaRegisterView.vue')
    },
    {
        path: '/lawyer/qna/comment/edit/:id',
        name: 'CommentEdit',
        component: () => import('@/views/lawyer/QnaEditView.vue')
    },


    // ---------- 예약 ----------
    {
        path: '/lawyer/:lawyerNo/reservation',
        name: 'LawyerReservation',
        component: () => import('@/views/lawyer/LawyerReservationView.vue'),
        props: true
    },
    {
        path: '/lawyer/:lawyerNo/slots',
        name: 'TimeSlotUpdate',
        component: () => import('@/views/lawyer/TimeSlotUpdate.vue'),
        props: true
    },

    // ---------- 방송 ----------
    {
        path: '/lawyer/broadcasts/schedule',
        name: 'LawyerBroadcastsSchedule',
        component: () => import('@/views/lawyer/LawyerScheduleView.vue')
    },
    {
        path: '/lawyer/broadcasts/live',
        name: 'LawyerBroadcastsLive',
        component: () => import('@/views/lawyer/LawyerBroadcastsView.vue')
    },
    {
        path: '/lawyer/broadcasts/schedule/register',
        name: 'LawyerBroadcastsScheduleRegister',
        component: () => import('@/views/lawyer/LawyerScheduleRegisterView.vue')
    },
    {
        path: '/lawyer/broadcasts/setting/:scheduleNo',
        name: 'LawyerBroadcastSetting',
        component: () => import('@/views/lawyer/LawyerBroadcastSettingView.vue')
    },
    {
        path: '/lawyer/broadcasts/schedule/:scheduleNo',
        name: 'LawyerBroadcastsScheduleDetail',
        component: () => import('@/views/lawyer/LawyerScheduleDetailView.vue')
    },


    // ---------- 템플릿 ----------
    {
        path: '/lawyer/templates',
        name: 'LawyerTemplateList',
        component: () => import('@/views/lawyer/template/TemplateListView.vue')
    },
    {
        path: '/lawyer/templates/register',
        name: 'TemplateEditor',
        component: () => import('@/views/lawyer/template/TemplateRegisterView.vue')
    },
    {
        path: '/lawyer/templates/:no',
        name: 'LawyerTemplateDetail',
        component: () => import('@/views/lawyer/template/TemplateDetailView.vue')
    },
    {
        path: '/lawyer/templates/edit/:no',
        name: 'TemplateEdit',
        component: () => import('@/views/lawyer/template/TemplateEditView.vue'),
        props: true
    },

    // ---------- 광고 ----------
    {
        path: '/lawyer/ads',
        name: 'AdList',
        component: () => import('@/views/lawyer/AD/AdListView.vue')
    },
    {
        path: '/lawyer/ads/register',
        name: 'AdRegister',
        component: () => import('@/views/lawyer/AD/AdRegisterView.vue')
    },
    {
        path: '/lawyer/ads/success',
        name: 'AdPaymentSuccess',
        component: () => import('@/views/lawyer/AD/AdPaymentSuccessView.vue')
    },
    {
        path: '/lawyer/ads/fail',
        name: 'AdPaymentFail',
        component: () => import('@/views/lawyer/AD/AdPaymentFailView.vue')
    }
]