import axios from "axios";
import {useAccountStore} from "@/stores/account";

const instance = axios.create();

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
