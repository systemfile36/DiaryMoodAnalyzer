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
     * Diary의 title 최대 길이
     */
    static DIARY_TITLE_MAX_LENGTH = 256;

    /**
     * Diary의 content 최대 길이
     */
    static DIARY_CONTENT_MAX_LENGTH = 1500;

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

    /**
     * Diary의 title의 최대 길이 검증 
     * @param {string} title 
     * @returns { boolean } 유효하면 true, otherwise, false
     */
    static checkDiaryTitleMaxLength(title) {
        return title.length <= this.DIARY_TITLE_MAX_LENGTH;
    }

    /**
     * Diary의 content의 최대 길이 검증 
     * @param { string } content 
     * @returns { boolean } 유효하면 true, otherwise, false
     */
    static checkDiaryContentMaxLength(content) {
        return content.length <= this.DIARY_CONTENT_MAX_LENGTH;
    }
}