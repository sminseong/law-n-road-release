// src/libs/http.js
import axios from 'axios'

// 1) axios 인스턴스 생성
const http = axios.create({
    baseURL: '/api',
    withCredentials: false,      // 리프레시 토큰을 localStorage 에 저장 중이므로 false
})

// 2) 요청 인터셉터: localStorage 에 있는 액세스 토큰을 자동으로 붙여줌
http.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

// 3) 응답 인터셉터: 401이 뜨면 refresh 로직 수행 후 재시도
http.interceptors.response.use(
    res => res,
    async err => {
        const original = err.config
        // 401 에러 & 한 번만 재시도
        if (err.response?.status === 401 && !original._retry) {
            original._retry = true
            try {
                // 리프레시 토큰으로 재발급 요청
                const refreshRes = await axios.post(
                    '/api/refresh',
                    { refreshToken: localStorage.getItem('refreshToken') }
                )
                const newAccess = refreshRes.data.accessToken

                // 새 액세스 토큰 저장 & 인터셉터 헤더 갱신
                localStorage.setItem('token', newAccess)
                axios.defaults.headers.common['Authorization'] = `Bearer ${newAccess}`
                original.headers['Authorization'] = `Bearer ${newAccess}`

                // 원래 요청 재시도
                return http(original)
            } catch (_e) {
                // 재발급 실패하면 로그아웃
                localStorage.clear()
                window.location.href = '/login'
                return Promise.reject(_e)
            }
        }

        return Promise.reject(err)
    }
)

export default http
