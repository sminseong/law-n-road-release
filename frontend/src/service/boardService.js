import httpRequester from "@/libs/HttpRequester.js";

/**
 * 상담글 목록 조회 API 호출
 * @param {number} page - 현재 페이지 번호 (기본값: 1)
 * @param {number} size - 페이지당 항목 수 (기본값: 10)
 * @returns {Promise<Object>} - Axios 응답 객체 (data, status 등 포함)
 */
export async function fetchBoardList(page = 1, size = 10) {
    return await httpRequester.get(`/api/client/qna?page=${page}&size=${size}`)
}
/**
 * 게시글 등록
 * @param {Object} payload - 게시글 등록 데이터 (title, content, incidentDate, userNo 포함)
 * @returns {Promise<string>}
 */
export async function createQna(payload) {
    console.log('payload', payload)
    return await httpRequester.post(`/api/client/qna`, payload)
}
/**
 * Q&A 상세 조회
 * @param {number} id - 게시글 ID
 * @returns {Promise<Object>}
 */
export const fetchBoardDetail = async (id) => {
    return await httpRequester.get(`/api/client/qna/${id}`)
}
/**
 * Q&A 수정
 * @param {number} id - 게시글 ID
 * @param {Object} payload - 수정할 내용
 * @returns {Promise<void>}
 */
export async function updateQna(id, payload) {
    return  await httpRequester.put(`/api/client/qna/${id}`, payload)
}
/**
 * Q&A 삭제
 * @param {number} id - 삭제할 게시글 ID
 * @returns {Promise<void>}
 */
export async function deleteQna(id) {
    return await httpRequester.delete(`/api/client/qna/${id}`)
}