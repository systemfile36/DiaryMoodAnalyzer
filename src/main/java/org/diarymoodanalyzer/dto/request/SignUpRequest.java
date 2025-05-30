package org.diarymoodanalyzer.dto.request;

import lombok.*;
import org.diarymoodanalyzer.domain.UserAuthority;

/*
회원 가입 시 보낼 요청
 */
@Getter
@Setter
public class SignUpRequest {
    private String email;
    private String password;

    private UserAuthority authority = UserAuthority.USER;

    public SignUpRequest(String email, String password) {
        this.email = email; this.password = password;
    }
    /*
    사용자의 정보(이름, 나이, 전화번호 등의 필요한 개인 정보)가 포함될 예정
     */
}
