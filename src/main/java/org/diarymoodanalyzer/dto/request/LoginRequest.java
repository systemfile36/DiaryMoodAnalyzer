package org.diarymoodanalyzer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
로그인 요청 DTO
 */
@AllArgsConstructor
@Getter
public class LoginRequest {
    private String email;
    private String password;
}
