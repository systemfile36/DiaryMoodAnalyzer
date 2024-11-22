package org.diarymoodanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 관련 설정을 위한 Config 파일/클래스
 */
@Configuration
@EnableAsync //비동기 활성화
@EnableScheduling //스케줄링 활성화
public class AsyncConfig {
    /**
     * 스프링에서 스레드 풀 사용을 위해 ThreadPoolTaskExecutor를 빈으로 등록
     * @return  빈으로 등록할 ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setQueueCapacity(100);
        executor.setMaxPoolSize(8);

        /*
        RejectExecutionException 이 발생했을 때 처리할 핸들러
        CallerRunsPolicy 는 호출한 스레드에게 실행 책임을 넘긴다.
        이는 모든 태스크를 처리하게 하기 위해서이다.
         */
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        return executor;
    }
}
