package org.diarymoodanalyzer.dto.ai.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.dto.ai.request.VadScore;

/**
 * DTO for AI server response.
 * <br/>
 * Contain the result of diary analyze.
 * <br/>
 * <code>
 *     {
 *     "vad_score": {
 *         "v": "([1, 9], float)",
 *         "a": "([1, 9], float)",
 *         "d": "([1, 9], float)"
 *     },
 *     "depression_score": "[0, 100], int",
 *     "classification": "string"
 * }
 * </code>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryAnalyzeResponse {

    /**
     * Result of VAD score
     */
    private VadScore vad_score;

    /**
     * Result of depression regression score
     */
    private int depression_score;


    /**
     * Result of emotion classification. Set type as String temporary
     */
    private String classification;

}
