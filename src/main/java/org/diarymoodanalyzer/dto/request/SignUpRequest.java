package org.diarymoodanalyzer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
회원 가입 시 보낼 요청
 */
@AllArgsConstructor
@Getter
public class SignUpRequest {
    private String email;
    private String password;

    /*
    사용자의 정보(이름, 나이, 전화번호 등의 필요한 개인 정보)가 포함될 예정
     */
}
