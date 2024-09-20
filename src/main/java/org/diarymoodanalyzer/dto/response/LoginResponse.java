package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.Setter;

/*
로그인 성공 시, 액세스 토큰과 리프레쉬 토큰 반환
 */
@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
