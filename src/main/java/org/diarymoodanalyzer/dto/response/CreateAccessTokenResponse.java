package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
새 엑세스 토큰을 만들어서 반환할 때 사용하는 DTO
 */
@AllArgsConstructor
@Getter
public class CreateAccessTokenResponse {
    private String accessToken;
}
