package org.diarymoodanalyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration file for {@link org.diarymoodanalyzer.service.DiaryAnalyzeService DiaryAnalyzeService}.
 */
@Component
@ConfigurationProperties(prefix = "diary-emotion")
@Getter
@Setter
public class DiaryAnalyzeProperties {
    private String url;
}
