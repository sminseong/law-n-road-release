import { createRouter, createWebHistory } from 'vue-router'
import commonRoutes from './common'
import lawyerRoutes from './lawyer'
import clientRoutes from './client'
import adminRoutes from './admin'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        ...commonRoutes,
        ...lawyerRoutes,
        ...clientRoutes,
        ...adminRoutes,
    ]
})

// ✅ 전역 네비게이션 가드 추가
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token")
    const role = localStorage.getItem("accountType") // 'CLIENT' | 'LAWYER' | 'ADMIN'

    if (to.path.startsWith('/lawyer')) {
        if (!token || role !== 'lawyer') {
            alert('변호사만 접근 가능한 페이지입니다. 다시 로그인 해주세요 ^^')
            return next('/login')
        }
    }

    if (to.path.startsWith('/client')) {
        if (!token || role !== 'client') {
            alert('일반 사용자만 접근 가능한 페이지입니다. 다시 로그인 해주세요 ^^')
            return next('/login')
        }
    }

    if (to.path.startsWith('/admin')) {
        if (!token || role !== 'admin') {
            alert('관리자만 접근 가능한 페이지입니다. 다시 로그인 해주세요 ^^')
            return next('/login')
        }
    }

    next()
})

export default router
