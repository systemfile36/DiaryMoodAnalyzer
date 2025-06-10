package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 일별 평균 depression level을 받는 DTO
 * @deprecated Deprecated due to changes in the AI server architecture.
 * Use {@link DailyAvgDepressionScoreRequest} instead.
 */
@Getter
@Setter
@Deprecated
public class GetDailyAvgDepressionLevelRequest {
    private LocalDate start = null;
    private LocalDate end = null;
}
