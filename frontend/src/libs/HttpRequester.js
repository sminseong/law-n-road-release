import axios from "axios";
import {useAccountStore} from "@/stores/account";

const instance = axios.create();

// interceptor는 이 인스턴스에 붙여야 동작함
instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// HTTP 요청 설정 생성
const generateConfig = () => {
    // 계정 스토어
    const accountStore = useAccountStore();

    if (accountStore.accessToken) {
        return {
            headers: {authorization: `Bearer ${accountStore.accessToken}`}
        };
    }

    return {};
};

// // ✅ 응답 인터셉터에서 토큰 만료 시 재발급 → 재요청
// instance.interceptors.response.use(
//     response => response,
//     async error => {
//         const originalRequest = error.config
//         const status = error.response?.status
//
//         // 401 or 403 에러면서 재시도 안한 요청이면
//         if ((status === 401 || status === 403) && !originalRequest._retry) {
//             originalRequest._retry = true
//
//             const userNo = localStorage.getItem('no')
//             if (!userNo) {
//                 localStorage.clear()
//                 router.push('/login')
//                 return Promise.reject(error)
//             }
//
//             try {
//                 const res = await axios.post('/api/refresh', { no: Number(userNo) })
//                 const newToken = res.data.accessToken
//                 localStorage.setItem('token', newToken)
//                 instance.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
//                 originalRequest.headers['Authorization'] = `Bearer ${newToken}`
//                 return instance(originalRequest)
//             } catch (refreshError) {
//                 console.error('🔴 토큰 재발급 실패:', refreshError)
//                 localStorage.clear()
//                 router.push('/login')
//                 return Promise.reject(refreshError)
//             }
//         }
//
//         return Promise.reject(error)
//     }
// )

export default {
    get(url, queryParams = {}) {
        const config = generateConfig();
        return instance.get(url, {
            ...config,
            params: queryParams
        });
    },
    post(url, params) {
        return instance.post(url, params, generateConfig());
    },
    put(url, params) {
        return instance.put(url, params, generateConfig());
    },
    delete(url) {
        return instance.delete(url, generateConfig());
    },
};
