package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Use for send verification code request
 */
@Getter
@Setter
public class SendVerificationCodeRequest {
    private String email;
}
