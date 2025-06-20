// src/libs/axios-auth.js
import axios from 'axios'
import { useRouter } from 'vue-router'

export const refreshAccessToken = async () => {
    const router = useRouter() // Composition API 훅 내부에서 호출
    const userNo = localStorage.getItem('no')
    if (!userNo) {
        alert('죄송합니다! 다시 로그인해주세요')
        localStorage.clear()
        router.push('/login')
        return null
    }

    try {
        const res = await axios.post('/api/refresh', { no: Number(userNo) })
        const newAccessToken = res.data.accessToken
        localStorage.setItem('token', newAccessToken)
        axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`
        return newAccessToken
    } catch (err) {
        console.error('❌ 액세스 토큰 재발급 실패:', err)
        localStorage.clear()
        router.push('/login')
        return null
    }
}

export const makeApiRequest = async (config) => {
    let token = localStorage.getItem('token')
    if (!token) {
        token = await refreshAccessToken()
        if (!token) return null
    }

    const isMultipart = config.data instanceof FormData

    try {
        return await axios({
            ...config,
            headers: {
                ...(config.headers || {}),
                'Authorization': `Bearer ${token}`,
                ...(isMultipart ? {} : { 'Content-Type': 'application/json' }) // form 데이터면 생략, 아니면 추가
            },
            timeout: 10000
        })
    } catch (error) {
        if (error.response?.status === 401 || error.response?.status === 403) {
            const newToken = await refreshAccessToken()
            if (!newToken) return null

            return await axios({
                ...config,
                headers: {
                    ...(config.headers || {}),
                    'Authorization': `Bearer ${newToken}`,
                    ...(isMultipart ? {} : { 'Content-Type': 'application/json' })
                },
                timeout: 10000
            })
        }

        throw error
    }
}