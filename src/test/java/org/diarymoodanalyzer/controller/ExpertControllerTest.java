package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.config.TokenAuthenticationFilter;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageForExpertRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryForExpertRequest;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.service.DiaryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    private UserRepository userRepository;

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

    @DisplayName("getManagedUsers: 관리하는 사용자 목록을 성공적으로 받아온다.")
    @Test
    public void getManagedUsers() throws Exception {
        //테스트 데이터 세팅
        final String url = "/api/expert/managedUsers";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        //임의의 전문가 리포지토리에 추가 (영속 상태)
        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        //테스트용으로 담당 전문가 설정
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            User user = User.builder()
                    .email(strBuilder.append("test").append(i+1)
                            .append("@email.com").toString())
                    .password(password)
                    .build();

            strBuilder.setLength(0);

            //관리로 추가, setExpert 를 통해 상호 참조 설정됨
            expert.addManagedUser(user);

            //영속 상태로 만듬
            userRepository.save(user);
        }

        //인증용으로 유효한 토큰 생성
        String accessToken = tokenProvider.createToken(expert, TokenProvider.ACCESS_EXPIRE);

        //토큰 유효성 검증
        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //요청 perform
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        //응답 출력, 유효성 검증
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));

    }

    @DisplayName("getDiariesByEmailForExpert: 전문가가 담당 유저의 다이어리 목록을 조회할 수 있다.")
    @Test
    public void getDiariesByEmailForExpert() throws Exception {
        //테스트 데이터 세팅
        final String url = "/api/expert/diaries";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        //임의의 전문가 리포지토리에 추가 (영속 상태)
        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        //다이어리의 소유주
        final String userEmail = "test@email.com";
        final String userPassword = "password";

        User user = User.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        //담당자로 설정
        expert.addManagedUser(user);

        userRepository.save(user);

        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            Diary diary = Diary.builder()
                    .title(strBuilder.append("test").append(i+1).toString())
                    .content("testContent")
                    .build();

            //소유주 명의로 다이어리 추가. 메소드 내에서 setUser 로 설정됨
            user.addDiary(diary);

            diaryRepository.save(diary);

        }

        String accessToken = tokenProvider.createToken(expert, TokenProvider.ACCESS_EXPIRE);

        //요청 DTO 구성
        GetDiaryByPageForExpertRequest req = new GetDiaryByPageForExpertRequest();
        req.setPage(0);
        req.setSize(5);
        req.setOwnerEmail(userEmail);


        //검증 (GET 파라미터로 넘김)
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .param("ownerEmail", req.getOwnerEmail())
                .param("page", String.valueOf(req.getSize()))
                .param("size", String.valueOf(req.getSize()))
                .accept(MediaType.APPLICATION_JSON));

        //검증
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(req.getSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(10));


    }

    @DisplayName("getDiaryByIdForExpert: 전문가가 담당 유저의 특정 다이어리를 조회할 수 있다.")
    @Test
    public void getDiaryByIdForExpert() throws Exception {
        //테스트 데이터 세팅
        final String url = "/api/expert/diary";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        //임의의 전문가 리포지토리에 추가 (영속 상태)
        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        //다이어리의 소유주
        final String userEmail = "test@email.com";
        final String userPassword = "password";

        User user = User.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        //담당자로 설정
        expert.addManagedUser(user);

        userRepository.save(user);

        List<Long> idOfDiaries = new ArrayList<>();

        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            Diary diary = Diary.builder()
                    .title(strBuilder.append("test").append(i+1).toString())
                    .content("testContent")
                    .build();

            //소유주 명의로 다이어리 추가. 메소드 내에서 setUser 로 설정됨
            user.addDiary(diary);

            //저장하면서 id값 id 목록에 추가
            idOfDiaries.add(diaryRepository.save(diary).getId());

        }

        String accessToken = tokenProvider.createToken(expert, TokenProvider.ACCESS_EXPIRE);

        GetDiaryForExpertRequest req = new GetDiaryForExpertRequest();
        req.setId(idOfDiaries.get(0));
        req.setOwnerEmail(userEmail);

        //검증 (GET 파라미터로 넘김)
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .param("ownerEmail", req.getOwnerEmail())
                .param("id", String.valueOf(req.getId()))
                .accept(MediaType.APPLICATION_JSON));

        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
