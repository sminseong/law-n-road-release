// JWT 토큰을 파싱하고, 토큰에서 사용자 정보를 추출하는 유틸 함수 모음

/**
 * Base64URL 디코딩 및 JSON 파싱
 * @param {string} token - JWT 문자열
 * @returns {object|null} 파싱된 payload 객체 또는 null
 */
export function parseJwt(token) {
    try {
        let base64 = token.split('.')[1];
        // base64url → base64 변환
        base64 = base64.replace(/-/g, '+').replace(/_/g, '/');
        // 패딩 추가 (길이가 4의 배수가 되도록)
        while (base64.length % 4 !== 0) {
            base64 += '=';
        }
        const json = atob(base64);
        return JSON.parse(json);
    } catch (e) {
        console.error('❌ JWT 파싱 실패:', e);
        return null;
    }
}

/**
 * 로컬 스토리지에서 토큰을 꺼내 파싱한 뒤 payload 반환
 * @returns {object|null}
 */
function getPayload() {
    const token = localStorage.getItem('token');
    return token ? parseJwt(token) : null;
}

/**
 * 사용자 번호(no) 반환
 * @returns {number|null}
 */
export function getUserNo() {
    const payload = getPayload();
    return payload?.no ?? null;
}

/**
 * 닉네임 반환
 * @returns {string|null}
 */
export function getUserNickname() {
    const payload = getPayload();
    return payload?.nickname ?? null;
}

/**
 * 전화번호 반환
 * @returns {string|null}
 */
export function getUserPhone() {
    const payload = getPayload();
    return payload?.phone ?? null;
}

/**
 * 로그인 ID(Subject) 반환
 * @returns {string|null}
 */
export function getUserLoginId() {
    const payload = getPayload();
    // JWT 표준 sub 클레임 사용
    return payload?.sub ?? null;
}

/**
 * 이메일 반환
 * @returns {string|null}
 */
export function getUserEmail() {
    const payload = getPayload();
    return payload?.email ?? null;
}

/**
 * 사용자 역할(role) 반환
 * @returns {string|null}
 */
export function getUserRole() {
    const payload = getPayload();
    return payload?.role ?? null;
}

/**
 * 특정 역할이 있는지 확인
 * @param {string} requiredRole
 * @returns {boolean}
 */
export function hasRole(requiredRole) {
    return getUserRole() === requiredRole;
}