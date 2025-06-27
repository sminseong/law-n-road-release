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
/**
 * 주간 상담 & 방송 통계 조회
 */
export async function fetchWeeklyStats() {
    try {
        // console.log('주간 통계 API 요청')
        const response = await httpRequester.get('/api/lawyer/dashboard/weekly-stats')
        // console.log('주간 통계 API 응답:', response)
        return response
    } catch (error) {
        // console.error('주간 통계 조회 실패:', error)
        // 오류 시 기본값 반환
        return {
            success: false,
            message: '주간 통계 조회 실패',
            data: {
                consultations: [0, 0, 0, 0, 0, 0, 0],
                broadcasts: [0, 0, 0, 0, 0, 0, 0]
            }
        }
    }
}