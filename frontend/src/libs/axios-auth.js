import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

export const refreshAccessToken = async () => {
    const userNo = localStorage.getItem('no') //로컬스토리지에 있는 no 값을 선언한다
    if (!userNo) {
        alert('죄송합니다! 다시 로그인해주세요') //없으면 다시 로그인
        router.push('/login')
        return null
    }

    try {
        const res = await axios.post('/api/refresh', { no: Number(userNo) }) //있으면 /api/refresh 호출
        const newAccessToken = res.data.accessToken // 백엔드로 부터 받아온 새로운 accessToken
        localStorage.setItem('token', newAccessToken) // 로컬 스토리지에 저장
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
            const newToken = await refreshAccessToken() // 사용자가 요청 중에 401 또는 403 에러기 발생하면  재발급 하는 함수 호출
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