import httpRequester from "@/libs/HttpRequester.js";

/**
 * 변호사 대시보드 관련 API 서비스
 */

/** [Lawyer] 오늘 일정 조회 */
export async function fetchTodaySchedule(lawyerNo) {
    return await httpRequester.get(`/api/lawyer/dashboard/${lawyerNo}/schedule`);
}
/** [Lawyer] 내일 상담 신청 목록 조회 */
export async function fetchTomorrowConsultationRequests() {
    return await httpRequester.get(`/api/lawyer/dashboard/consultation-requests/tomorrow`);
}
/** [Lawyer] 내일 방송 조회 */
export async function fetchTomorrowBroadcasts() {
    return await httpRequester.get(`/api/lawyer/dashboard/broadcasts/tomorrow`);
}