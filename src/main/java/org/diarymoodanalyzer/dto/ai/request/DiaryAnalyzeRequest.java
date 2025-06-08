package org.diarymoodanalyzer.dto.ai.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for AI server request.
 */
@Getter
@Setter
public class DiaryAnalyzeRequest {
    private String diaryContent;
}
