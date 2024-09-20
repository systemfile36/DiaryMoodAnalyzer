package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.Setter;

/*
새 엑세스 토큰을 요구하는 요청에 사용하는 DTO
 */
@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken;
}
