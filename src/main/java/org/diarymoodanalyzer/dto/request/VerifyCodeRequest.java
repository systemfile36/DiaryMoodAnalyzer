package org.diarymoodanalyzer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Use for verify code request
 */
@Getter
@Setter
@AllArgsConstructor
public class VerifyCodeRequest {
    private String email;
    private String code;
}
