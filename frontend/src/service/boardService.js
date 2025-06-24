import httpRequester from "@/libs/HttpRequester.js";

/** [Public] 상담글 목록 조회 */
export async function fetchBoardList(page = 1, size = 10) {
    return await httpRequester.get(`/api/public/qna?page=${page}&size=${size}`)
}
/** [Public] 상담글 상세 조회 */
export async function fetchBoardDetail(id) {
    return await httpRequester.get(`/api/public/qna/${id}`);
}
/** [Public] 전체 게시글 수 조회 */
export async function fetchBoardCount() {
    return await httpRequester.get(`/api/public/qna/count`);
}

/** [Client] 게시글 등록 */
export async function createQna(payload) {
    return await httpRequester.post(`/api/client/qna`, payload)
}

/** [Client] 게시글 수정 */
export async function updateQna(id, payload) {
    return  await httpRequester.put(`/api/client/qna/${id}`, payload)
}
/** [Client] 게시글 삭제 */
export async function deleteQna(id) {
    return await httpRequester.delete(`/api/client/qna/${id}`)
}
// [Lawyer] 답변 등록
export const registerComment = (payload) => {
    return httpRequester.post('/api/lawyer/comment', payload)
}
// [Lawyer] 답변 목록 조회 (특정 게시글 기준)
export const fetchCommentList = (boardNo) => {
    return httpRequester.get(`/api/lawyer/comment/board/${boardNo}`)
}

// [Lawyer] 내가 쓴 답변 목록 조회
export async function fetchMyComment(page = 1, size = 10) {
    return await httpRequester.get('/api/lawyer/comment/answers', { page, size })
}
// [Lawyer] 특정 답변 상세 조회
export const fetchCommentDetail = (commentId) => {
    return httpRequester.get(`/api/lawyer/comment/detail/${commentId}`)
}
// [Lawyer] 답변 수정
export const updateComment = (commentId, payload) => {
    return httpRequester.put(`/api/lawyer/comment/${commentId}`, payload)
}
// [Lawyer] 답변 삭제
export async function deleteComment(commentId) {
    console.log('📡 boardService.js - commentId:', commentId)
    return await httpRequester.delete(`/api/lawyer/comment/${commentId}`)
}

export const fetchBoardComments = (boardId) => {
    return httpRequester.get(`/api/public/qna/${boardId}/comments`)
}

// 답변 채택
export async function selectCommentAnswer(boardId, commentId) {
    return await httpRequester.post('/api/client/qna/select', {
        boardId, commentId })
}