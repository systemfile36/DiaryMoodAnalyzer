package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.LoginRequest;
import org.diarymoodanalyzer.dto.request.SignUpRequest;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //JSON의 직렬화와 역직렬화에 사용할 것

    @Autowired
    private WebApplicationContext webApplicationContext; //Spring MVC의 정보를 담은 컨텍스트

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("login: 정확한 정보로 로그인하면 인증 정보가 저장된다.")
    @Test
    public void login() throws Exception {
        //테스트 데이터 세팅
        String url = "/api/auth/login";
        String userEmail = "test@gmail.com";
        String password = "testPassword";

        User user = userRepository.save(User.builder()
                .email(userEmail)
                .password(bCryptPasswordEncoder.encode(password))
                .build());

        //요청 구성
        LoginRequest req = new LoginRequest(user.getEmail(), password);
        String reqBody = objectMapper.writeValueAsString(req);

        //실제 요청 보냄
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());



    }

    @DisplayName("signup: 올바른 요청을 보내면 회원가입에 성공한다.")
    @Test
    public void signUp() throws Exception {
        //테스트 데이터 세팅
        String url = "/api/auth/signup";

        String userEmail = "test@gmail.com";
        String password = "password";

        SignUpRequest req = new SignUpRequest(userEmail, password);

        String reqBody = objectMapper.writeValueAsString(req);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result.andExpect(MockMvcResultMatchers.status().isCreated());

        //제대로 들어갔는지 확인
        assertThat(userRepository.existsByEmail(userEmail)).isTrue();
    }

}