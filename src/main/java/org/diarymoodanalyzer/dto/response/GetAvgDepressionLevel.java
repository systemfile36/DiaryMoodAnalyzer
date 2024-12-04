package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 평균 depressionLevel을 Map으로 반환하는 DTO 이다.
 * 기준 컬럼(날짜 등)을 key, 평균을 value로 가지는 Map으로 표현된다.
 */
@NoArgsConstructor
@Getter
@Setter //ObjectMapper에서 사용하기 위해 Getter와 Setter, 기본 생성자 추가.
public class GetAvgDepressionLevel {
    @Getter
    private Map<String, Double> dailyAvg;

    public GetAvgDepressionLevel(Map<String, Double> dailyAvg) {
        this.dailyAvg = dailyAvg;
    }
}
