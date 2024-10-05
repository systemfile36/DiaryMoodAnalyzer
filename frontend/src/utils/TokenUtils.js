
/**
 * 토큰 관련 유틸 클래스
 */
export default class TokenUtils {
    static ACCESS_TOKEN_KEY = 'accessToken';
    static REFRESH_TOKEN_KEY = 'refreshToken';
    /**
     * 
     * @returns { string | null } accessToken이 존재하면 반환, 아니면 null
     */
    static getAccessToken() {
        return localStorage.getItem(this.ACCESS_TOKEN_KEY);
    }

    /**
     * 
     * @returns { string | null } refreshToken이 존재하면 반환, 아니면 null
     */
    static getRefreshToken() {
        return localStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    static setAccessToken(token) {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
    }

    static setRefreshToken(token) {
        localStorage.setItem(this.REFRESH_TOKEN_KEY, token)
    }

    static deleteTokens() {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * 
     * @param { string } encodedToken - base64로 인코딩된 JWT 토큰
     * @returns { string | null } 디코딩된 JWT 페이로드 객체, 유효하지 않으면 null
     */
    static getClaim(encodedToken) {
        let claim = null;
        try {
            //페이로드 부분만 추출해서 디코딩
            //디코딩한 문자열 parse로 객체로 변환
            claim = JSON.parse(window.atob(encodedToken.split('.')[1]));
        } catch(error) {
            console.log(error);
            return null;
        }

        return claim;
    }

    /**
     * 
     * @param { string } token - JWT 토큰
     * @returns { string | null } Claim의 subject, 본 프로젝트에서는 사용자의 이메일이다.
     */
    static getSubject(token) {
        try {
            const claim = this.getClaim(token);
            return claim.sub;
        } catch(error) {
            return null;
        }
    }

    /**
     * 
     * @param { string } token - JWT 토큰
     * @returns { Date | null } Claim의 만료 시간을 나타내는 Date 객체
     */
    static getExp(token) {
        try {
            const claim = this.getClaim(token);
            return new Date(claim.exp * 1000);
        } catch(error) {
            return null;
        }
    }

    /**
     * 
     * @param { string } token - JWT 토큰
     * @returns { string | null } Claim의 role
     */
    static getRole(token) {
        try {
            const claim = this.getClaim(token);
            return claim.role;
        } catch(error) {
            return null;
        }
    }

}