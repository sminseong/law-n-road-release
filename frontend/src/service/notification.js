import httpRequester from "@/libs/HttpRequester.js";

// 알림톡 - 방송 시작 알림 발송 요청
export const sendBroadcastStartAlimtalk = (payload) => {
    return httpRequester.post("/api/notification/broadcast/start", payload);
};