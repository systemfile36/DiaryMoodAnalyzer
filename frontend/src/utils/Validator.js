/**
 * 각종 값의 유효성을 검증하는 유틸리티 클래스
 */
export default class Validator {

    /**
     * 이메일의 정규식
     */ 
    static EMAIL_REGEX = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,}$/
    
    /**
     * 비밀번호의 정규식
     */
    static PASSWORD_REGEX = /^(?=.*\d)(?=.*[a-z]).{8,12}$/

    /**
     * 
     * @param { string } email 
     * @returns { boolean } 유효하면 true, otherwise, false
     */
    static checkEmail(email) {
        return this.EMAIL_REGEX.test(email);
    }

    /**
     * 
     * @param { string } password 
     * @returns { boolean } 유효하면 true, otherwise, false
     */
    static checkPassword(password) {
        return this.PASSWORD_REGEX.test(password);
    }
}