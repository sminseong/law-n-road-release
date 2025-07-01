import httpRequester from "@/libs/HttpRequester.js";

/**
 * 변호사 대시보드 관련 API 서비스
 */

/** [Lawyer] 오늘 일정 조회 */
export async function fetchTodaySchedule() {
    return await httpRequester.get('/api/lawyer/dashboard/schedule');
}
/** [Lawyer] 내일 상담 신청 목록 조회 */
export async function fetchTomorrowConsultationRequests() {
    return await httpRequester.get(`/api/lawyer/dashboard/consultation-requests/tomorrow`);
}
/** [Lawyer] 내일 방송 조회 */
export async function fetchTomorrowBroadcasts() {
    return await httpRequester.get(`/api/lawyer/dashboard/broadcasts/tomorrow`);
}
// 주간 상담 건수
export async function fetchWeeklyConsultations() {
    const { data } = await httpRequester.get('/api/lawyer/dashboard/weekly-consultations');
    return data;  // DailyCountDto[] 반환
}

// 주간 방송 건수
export async function fetchWeeklyBroadcasts() {
    const { data } = await httpRequester.get('/api/lawyer/dashboard/weekly-broadcasts');
    return data;  // DailyCountDto[] 반환
}

/** [Lawyer] 이달의 수익 조회 */
export async function fetchMonthlyRevenue() {
    return await httpRequester.get('/api/lawyer/dashboard/monthly-revenue');
}

/** [Lawyer] 이달의 템플릿 판매 건수 조회 */
export async function fetchMonthlyTemplateSales() {
    return await httpRequester.get('/api/lawyer/dashboard/monthly-template-sales');
}

/** [Lawyer] 거니짱 - 월별 상담예약 + 템플릿 판매 매출 조회 */
export async function fetchMonthlySalesRevenue() {
    return await httpRequester.get(`/api/lawyer/dashboard/revenue/sales/monthly`);
}