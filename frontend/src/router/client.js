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
    {
        path: '/client/qna/edit/:id',
        name: 'ClientQaEditView',
        component: () => import('@/views/client/ClientQaEditView.vue')
    },

    // ---------- 예약 ----------
    {
        path: '/client/reservations/:lawyerNo/:lawyerName',
        name: 'ClientReservations',
        component: () => import('@/views/client/ClientReservationsView.vue'),
        props: true
    },
    {
        path: '/client/reservations/list',
        name: 'ClientReservationsList',
        component: () => import('@/views/client/ClientReservationsList.vue')
    },
    {
        path: '/client/reservations/payment',
        name: 'ClientReservationsPayment',
        component: () => import('@/views/client/ClientReservationsPayment.vue')
    },

    // ---------- 방송 ----------
    {
        path: '/client/broadcasts/:broadcastNo',
        name: 'ClientBroadcasts',
        component: () => import('@/views/client/ClientBroadcastsView.vue')
    },
    {
        path: '/client/broadcasts/schedule/:scheduleNo/preQuestion',
        name: 'ClientBroadCastsPreQuestion',
        component: () => import('@/views/client/ClientPreQuestionView.vue')
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
    {
        path: '/client/broadcasts/list',
        name: 'ClientBroadCastsList',
        component: () => import('@/views/client/ClientBroadcastListView.vue')
    },

    // ---------- 템플릿 ----------
    {
        path: '/client/cart', // 장바구니
        name: 'ClientCart',
        component: () => import('@/views/client/template/CartView.vue')
    },
    {
        path: '/client/template/payment', // 결제 페이지
        name: 'TemplatePaymentView',
        component: () => import('@/views/client/template/TemplatePaymentView.vue')
    },
    {
        path: '/client/template/orders', // 마이페이지 -> 주문내역
        name: 'TmplHistoryListView',
        component: () => import('@/views/client/template/TmplHistoryListView.vue')
    },
    {
        path: '/client/template/orders/:orderNo', // 마이페에지 -> 주문상세내역
        name: 'TmpHistoryDetailListView',
        component: () => import('@/views/client/template/TmplHistoryDetailListView.vue')
    },
    {
        path: '/client/template/orders/detail/:tmplNo', // 마이페에지 -> 주문상세내역 -> 단일 상품 조회
        name: 'TmplHistoryDetailOneView',
        component: () => import('@/views/client/template/TmplHistoryDetailOneView.vue')
    },


    // ---------- 회원 ----------
    {
        path: '/client/profile/edit',
        name: 'ClientProfileEdit',
        component: () => import('@/views/client/ClientProfileEditView.vue')

    },




    // ---------- 광고 ----------
]