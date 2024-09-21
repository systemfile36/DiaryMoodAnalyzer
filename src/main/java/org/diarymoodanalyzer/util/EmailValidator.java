package org.diarymoodanalyzer.util;

import java.util.Objects;
import java.util.regex.Pattern;

/*
이메일이 유효한지 정규식으로 체크하는 유틸 클래스
 */
public class EmailValidator {
    //이메일 유효성 정규표현식
    private static final String REGEX_EMAIL
            = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,}$";

    //정규표현식
    private static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);

    //이메일이 유효한 형식인지 반환
    public static boolean isValidEmail(String email) {

        if(Objects.isNull(email) || email.isEmpty())
            return false;

        //이메일이 정규식에 매치하는지 반환
        return PATTERN_EMAIL.matcher(email).matches();
    }
}
