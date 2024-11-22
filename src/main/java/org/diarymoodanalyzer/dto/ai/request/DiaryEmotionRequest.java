package org.diarymoodanalyzer.dto.ai.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 웹 서버에서 AI 서버로 보낼 요청의 DTO
 */
@Getter
@Setter
public class DiaryEmotionRequest {
    private String diary;
}
