import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE || '';
// VITE_API_BASE를 .env에 설정했으면 사용, 아니라면 빈 문자열로 두고 개발서버 프록시 설정 활용

/**
 * 게시글 목록 조회
 * @param {number} page 1부터 시작하는 페이지 번호
 * @param {number} size 페이지당 항목 수
 * @returns {Promise<{ content: Array, page: number, size: number, totalElements?: number, totalPages?: number }>}
 */
export function fetchBoardList(page = 1, size = 10) {
    return axios.get(`${API_BASE}/api/client/qna`, {
        params: { page, size }
    }).then(res => res.data);
}
// Q&A 단일 조회
export async function fetchQnaById(id) {
    const res = await axios.get(`${API_BASE}/api/client/qna/${id}`)
    return res.data
}

//  Q&A 수정 요청
export async function updateQna(id, payload) {
    await axios.put(`/api/client/qna/${id}`, payload)
}