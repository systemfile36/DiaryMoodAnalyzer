package org.diarymoodanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//test123

@EnableJpaAuditing //@CreatedDate, @LastModifiedDate 자동 반영 위함
@SpringBootApplication
public class DiaryMoodAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiaryMoodAnalyzerApplication.class, args);
    }
}
