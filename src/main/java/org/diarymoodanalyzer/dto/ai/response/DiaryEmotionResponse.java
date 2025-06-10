package org.diarymoodanalyzer.dto.ai.response;

import lombok.Getter;
import lombok.Setter;

/**
 * AI 서버에서 요청을 받을 때 사용할 DTO
 * 일단은, 우울한 정도에 대한 수치만 받아온다.
 * @deprecated Deprecated due to changes in the AI server architecture.
 * Use {@link DiaryAnalyzeResponse} instead.
 */
@Getter
@Setter
@Deprecated
public class DiaryEmotionResponse {
    private int overall_average_weight;
}
