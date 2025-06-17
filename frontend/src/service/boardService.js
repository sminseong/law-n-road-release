import httpRequester from "@/libs/HttpRequester.js";

/** [Public] 상담글 목록 조회 */
export async function fetchBoardList(page = 1, size = 10) {
    return await httpRequester.get(`/api/qna?page=${page}&size=${size}`)
}
/** [Public] 상담글 상세 조회 */
export async function fetchBoardDetail(id) {
    return await httpRequester.get(`/api/qna/${id}`);
}
/** [Public] 전체 게시글 수 조회 */
export async function fetchBoardCount() {
    return await httpRequester.get(`/api/qna/count`);
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