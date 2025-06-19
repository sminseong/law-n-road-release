// src/router/index.js
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

export default router