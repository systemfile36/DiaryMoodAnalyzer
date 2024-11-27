package org.diarymoodanalyzer.dto.response;

import lombok.Getter;

import java.util.Map;

/**
 * 평균 depressionLevel을 Map으로 반환하는 DTO 이다.
 * 기준 컬럼(날짜 등)을 key, 평균을 value로 가지는 Map으로 표현된다.
 */
public class GetAvgDepressionLevel {
    @Getter
    private final Map<String, Double> dailyAvg;

    public GetAvgDepressionLevel(Map<String, Double> dailyAvg) {
        this.dailyAvg = dailyAvg;
    }
}
