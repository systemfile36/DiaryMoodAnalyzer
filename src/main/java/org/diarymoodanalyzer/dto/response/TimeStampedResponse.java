package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * createdAt, updatedAt과 같은 타임 스탬프 정보가 포함된
 * Response DTO의 공통 필드를 묶는 추상 클래스
 */
@NoArgsConstructor
@Getter
@Setter
public abstract class TimeStampedResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
