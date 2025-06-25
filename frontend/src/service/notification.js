import httpRequester from "@/libs/HttpRequester.js";

// 1. 방송 시작 알림톡
export const sendBroadcastStartAlimtalk = (payload) =>
    httpRequester.post("/api/notification/broadcast/start", payload);

// 1-1. 방송 등록 알림톡
export const sendBroadcastCreateAlimtalk = (payload) =>
    httpRequester.post("/api/notification/broadcast/create", payload);

// 2. 인증번호 발송
export const sendVerificationCodeAlimtalk = (payload) =>
    httpRequester.post("/api/notification/verify-code", payload);

// 3. 상담 임박 (의뢰인)
export const sendClientReservationStartedAlimtalk = (payload) =>
    httpRequester.post("/api/notification/client/reservation/started", payload);

// 4. 상담 임박 (변호사)
export const sendLawyerReservationStartedAlimtalk = (payload) =>
    httpRequester.post("/api/notification/lawyer/reservation/started", payload);

// 5. 신규 상담 신청 완료 (의뢰인)
export const sendClientReservationCreatedAlimtalk = (payload) =>
    httpRequester.post("/api/notification/client/reservation/created", payload);

// 6. 신규 상담 신청 완료 (변호사)
export const sendLawyerReservationCreatedAlimtalk = (payload) =>
    httpRequester.post("/api/notification/lawyer/reservation/created", payload);

// 7. 상담 취소 (변호사)
export const sendLawyerReservationCanceledAlimtalk = (payload) =>
    httpRequester.post("/api/notification/lawyer/reservation/canceled", payload);
