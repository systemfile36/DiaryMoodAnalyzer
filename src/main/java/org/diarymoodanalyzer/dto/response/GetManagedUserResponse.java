package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GetManagedUserResponse {
    private String email;

    /*
    기타 사용자 개인 정보 포함 예정
     */
}
