// /src/libs/axios-auth.js
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

export const refreshAccessToken = async () => {
    const userNo = localStorage.getItem('no')
    if (!userNo) {
        alert('사용자 정보 없음 → 로그인 필요')
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
        console.error('❌ 재발급 실패:', err)
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

    try {
        return await axios({
            ...config,
            headers: {
                ...config.headers,
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            timeout: 10000
        })
    } catch (error) {
        if (error.response?.status === 401 || error.response?.status === 403) {
            const newToken = await refreshAccessToken()
            if (!newToken) return null

            return await axios({
                config,
                headers: {
                    ...config.headers,
                    'Authorization': `Bearer ${newToken}`,
                    'Content-Type': 'application/json'
                },
                timeout: 10000
            })
        }

        throw error
    }
}
