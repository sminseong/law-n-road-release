import axios from 'axios'
import router from '@/router'
import { getValidToken } from './axios-auth.js'

const instance = axios.create({
    baseURL: '', // 필요하면 설정
    withCredentials: true // 💥 쿠키 기반 인증 대응 (선택)
})

// 요청 시 토큰 자동 삽입
instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 응답 시 토큰 만료 대응 (재발급 → 재시도)
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
                await router.push('/login')
                return Promise.reject(error)
            }

            try {
                const res = await axios.post('/api/refresh', { no: Number(userNo) })
                const newToken = res.data.accessToken

                // 저장
                localStorage.setItem('token', newToken)

                // 재시도
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`
                console.log(originalRequest.FormData)
                if (originalRequest.data instanceof FormData) {
                    console.log('✅ FormData 내용:')
                    for (const [key, value] of originalRequest.data.entries()) {
                        console.log(`${key}:`, value)
                    }
                }
                return instance(originalRequest)
            } catch (refreshError) {
                console.error('🔴 토큰 재발급 실패:', refreshError)
                localStorage.clear()
                await router.push('/login')
                return Promise.reject(refreshError)
            }
        }

        return Promise.reject(error)
    }
)

// 요청 전 사전 토큰 재발급을 위한 공통 래퍼
async function withToken(requestFn) {
    const token = await getValidToken()
    if (!token) {
        alert('로그인이 필요합니다.')
        await router.push('/login')
        return Promise.reject('로그인 필요')
    }
    return await requestFn()
}

// 실제로 요청 보내는 함수들
export default {
    get(url, queryParams = {}) {
        return withToken(() => instance.get(url, { params: queryParams }))
    },
    post(url, data) {
        return withToken(() => instance.post(url, data))
    },
    put(url, data) {
        return withToken(() => instance.put(url, data))
    },
    delete(url) {
        return withToken(() => instance.delete(url))
    }
}