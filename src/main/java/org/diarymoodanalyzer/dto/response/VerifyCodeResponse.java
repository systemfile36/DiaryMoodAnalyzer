package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyCodeResponse {
    private boolean isVerified;
    private boolean isFailCountExceed = false;
}
