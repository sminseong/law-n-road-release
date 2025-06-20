import axios from 'axios'
import router from '@/router'
import { useAccountStore } from '@/stores/account'

const instance = axios.create({
    baseURL: '', // 필요하면 설정
    withCredentials: true // 💥 쿠키 기반 인증 대응 (선택)
})

// ✅ 요청 시 토큰 자동 삽입
instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// ✅ 응답 시 토큰 만료 대응 (재발급 → 재시도)
instance.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config
        const status = error.response?.status

        if ((status === 401 || status === 403) && !originalRequest._retry) {
            originalRequest._retry = true

            const userNo = localStorage.getItem('no')
            if (!userNo) {
                localStorage.clear()
                router.push('/login')
                return Promise.reject(error)
            }

            try {
                const res = await axios.post('/api/refresh', { no: Number(userNo) })
                const newToken = res.data.accessToken

                // ✅ 저장
                localStorage.setItem('token', newToken)

                // ✅ 재시도
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`
                return instance(originalRequest)
            } catch (refreshError) {
                console.error('🔴 토큰 재발급 실패:', refreshError)
                localStorage.clear()
                router.push('/login')
                return Promise.reject(refreshError)
            }
        }

        return Promise.reject(error)
    }
)

// ✅ 실제로 요청 보내는 함수들
export default {
    get(url, queryParams = {}) {
        return instance.get(url, { params: queryParams })
    },
    post(url, data) {
        return instance.post(url, data)
    },
    put(url, data) {
        return instance.put(url, data)
    },
    delete(url) {
        return instance.delete(url)
    }
}