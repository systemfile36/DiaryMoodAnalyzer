package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for request daily average depression score
 */
@Getter
@Setter
@NoArgsConstructor
public class DailyAvgDepressionScoreRequest {
    private LocalDate start = null;
    private LocalDate end = null;
}
