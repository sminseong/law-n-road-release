import { createRouter, createWebHistory } from 'vue-router'
import commonRoutes from './common'
import lawyerRoutes from './lawyer'
import clientRoutes from './client'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        ...commonRoutes,
        ...lawyerRoutes,
        ...clientRoutes,
    ]
})

export default router;