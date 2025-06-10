package org.diarymoodanalyzer.dto.ai.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 웹 서버에서 AI 서버로 보낼 요청의 DTO
 * @deprecated Deprecated due to changes in the AI server architecture.
 * Use {@link DiaryAnalyzeRequest} instead.
 */
@Getter
@Setter
@Deprecated
public class DiaryEmotionRequest {
    private String diary;
}
