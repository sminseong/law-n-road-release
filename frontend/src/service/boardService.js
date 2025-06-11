import axios from 'axios';
import httpRequester from "@/libs/HttpRequester.js";

/**
 * 게시글 목록 조회
 * @param {number} page 1부터 시작하는 페이지 번호
 * @param {number} size 페이지당 항목 수
 * @returns {Promise<{ content: Array, page: number, size: number, totalElements?: number, totalPages?: number }>}
 */
export function fetchBoardList(page = 1, size = 10) {
    return axios.get(`/api/client/qna`, {
        params: { page, size }
    }).then(res => res.data);
}
// Q&A 단일 조회
export async function fetchQnaById(id) {
    const res = await axios.get(`/api/client/qna/${id}`)
    return res.data
}

//  Q&A 수정 요청
export async function updateQna(id, payload) {
    await axios.put(`/api/client/qna/${id}`, payload)
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