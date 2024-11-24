package org.diarymoodanalyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Diary 감정 분석 서버와 통신하기 위한 설정 파일
 */
@Component
@ConfigurationProperties(prefix = "diary-emotion")
@Getter
@Setter
public class DiaryEmotionProperties {

    private String url;
}
