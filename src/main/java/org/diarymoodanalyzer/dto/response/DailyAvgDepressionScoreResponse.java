package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * DTO contain daily average depression score as <code>Map&lt;String, Double&gt;</code>
 * <br/>
 * It's grouped by date. key is date and value is average depression score.
 * <br/>
 * Will be serialized to JSON like :
 * <code>
 *     {
 *         "1970-01-01": 45.3,
 *         "1970-01-02": 67.4,
 *         ...
 *     }
 * </code>
 */
@Setter
@Getter
@NoArgsConstructor
public class DailyAvgDepressionScoreResponse {

    private Map<String, Double> dailyAvg;

    public DailyAvgDepressionScoreResponse(Map<String, Double> dailyAvg) { this.dailyAvg = dailyAvg; }
}
