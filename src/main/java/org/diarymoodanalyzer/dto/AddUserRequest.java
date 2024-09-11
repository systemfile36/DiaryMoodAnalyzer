package org.diarymoodanalyzer.dto;

import lombok.Getter;
import lombok.Setter;

//회원 가입 요청 시 사용하는 DTO
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
