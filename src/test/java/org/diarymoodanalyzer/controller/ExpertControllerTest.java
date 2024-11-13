package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.config.TokenAuthenticationFilter;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.service.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpertControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //JSON의 직렬화와 역직렬화에 사용할 것

    @Autowired
    private WebApplicationContext webApplicationContext; //Spring MVC의 정보를 담은 컨텍스트

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private TokenProvider tokenProvider; //

    /**
     * 테스트 실행 전마다 MockMvc 초기화. 웹 어플리케이션 컨텍스트 세팅.
     * 리포지토리 초기화
     */
    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new TokenAuthenticationFilter(tokenProvider)) //인증 구현을 위한 수동 필터 추가
                .build();
        diaryRepository.deleteAll();
        expertRepository.deleteAll();
    }

}
