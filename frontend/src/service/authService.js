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
 * 로컬 스토리지에서 토큰을 꺼내 파싱한 뒤 사용자 번호(no)를 반환
 * @returns {number|null}
 */
export function getUserNo() {
    const token = localStorage.getItem('token');
    const payload = token ? parseJwt(token) : null;
    return payload?.no ?? null;
}

/**
 * 로컬 스토리지에서 토큰을 꺼내 파싱한 뒤 사용자 역할(role)을 반환
 * @returns {string|null}
 */
export function getUserRole() {
    const token = localStorage.getItem('token');
    const payload = token ? parseJwt(token) : null;
    return payload?.role ?? null;
}

/**
 * 토큰이 유효하고, 특정 역할을 가지고 있는지 확인
 * @param {string} requiredRole
 * @returns {boolean}
 */
export function hasRole(requiredRole) {
    const role = getUserRole();
    return role === requiredRole;
}
