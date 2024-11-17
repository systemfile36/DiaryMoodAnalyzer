package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.config.TokenAuthenticationFilter;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.Comment;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.AddCommentRequest;
import org.diarymoodanalyzer.dto.request.AddDiaryRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageRequest;
import org.diarymoodanalyzer.repository.CommentRepository;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.UserRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class DiaryApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //JSON의 직렬화와 역직렬화에 사용할 것

    @Autowired
    private WebApplicationContext webApplicationContext; //Spring MVC의 정보를 담은 컨텍스트

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    /*
    테스트 실행 전마다 MockMvc 초기화. 웹 어플리케이션 컨텍스트 세팅.
    리포지토리 초기화
     */
    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new TokenAuthenticationFilter(tokenProvider))
                .build();
        diaryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("addDiary: Diary를 추가한다.")
    @Test
    public void addDiary() throws Exception {

        //검증용 Diary 데이터 구성
        final String url = "/api/diaries";
        final String title = "Title";
        final String content = "Content";

        //검증용 User 데이터 구성
        final String userEmail = "example@example.com";
        final String userPw = "password";

        //User 데이터 저장
        User user = userRepository.save(User.builder()
                .email(userEmail)
                .password(bCryptPasswordEncoder.encode(userPw))
                .build());

        //엑세스 토큰 발급
        String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);

        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //DTO 생성 후, JSON으로 직렬화
        final AddDiaryRequest req = new AddDiaryRequest(title, content);

        final String reqBody = objectMapper.writeValueAsString(req);

        //실제 요청 perform
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("Authorization", "Bearer " + accessToken) //헤더에 엑세스 토큰 추가
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result.andDo(print());

        //HTTP 코드 검증
        result.andExpect(MockMvcResultMatchers.status().isCreated());

        //테스트를 위해 리포지토리 내의 모든 값 불러옴
        List<Diary> diaries = diaryRepository.findAll();

        //값이 정확하게 들어갔는지 검증
        assertThat(diaries.size()).isEqualTo(1);
        assertThat(diaries.get(0).getTitle()).isEqualTo(title);
        assertThat(diaries.get(0).getContent()).isEqualTo(content);
        assertThat(diaries.get(0).getUser().getEmail()).isEqualTo(userEmail);
    }

    @DisplayName("getDiariesByEmail: 사용자의 이메일을 받아 해당 사용자의 Diary를 Page로 리턴한다.")
    @Test
    public void getDiariesByEmail() throws Exception{
        final String url = "/api/diaries";

        final String userEmail = "example@example.com";
        final String userPw = "password";

        User user = userRepository.save(User.builder()
                .email(userEmail)
                .password(bCryptPasswordEncoder.encode(userPw))
                .build());

        String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);

        final int page = 1;
        final int size = 5;
        final String sortBy = "title";
        final boolean isAscending = true;

        GetDiaryByPageRequest req = new GetDiaryByPageRequest();
        req.setPage(page);
        req.setSize(size);
        req.setSortBy(sortBy);
        req.setAscending(isAscending);

        int diariesLength = 10;

        for(int i = 0; i < diariesLength; i++) {
            diaryRepository.save(Diary.builder()
                    .user(user)
                    .title("Title" + i)
                    .content("Content" + i)
                    .build());
        }

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .param("email", user.getEmail())
                .param("page", String.valueOf(req.getPage()))
                .param("size", String.valueOf(req.getSize()))
                .param("sortBy", req.getSortBy())
                .param("isAscending", String.valueOf(req.isAscending()))
                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(5));
    }

    @DisplayName("getDiariesTitleByEmail: 사용자의 이메일 기반으로 본문 제외한 Diary를 Page로 리턴")
    @Test
    public void getDiariesTitleByEmail() throws Exception {
        final String url = "/api/diaries/title";

        final String userEmail = "example@example.com";
        final String userPw = "password";

        User user = userRepository.save(User.builder()
                .email(userEmail)
                .password(bCryptPasswordEncoder.encode(userPw))
                .build());

        String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);

        final int page = 1;
        final int size = 5;
        final String sortBy = "title";
        final boolean isAscending = true;

        GetDiaryByPageRequest req = new GetDiaryByPageRequest();
        req.setPage(page);
        req.setSize(size);
        req.setSortBy(sortBy);
        req.setAscending(isAscending);

        int diariesLength = 10;

        for(int i = 0; i < diariesLength; i++) {
            diaryRepository.save(Diary.builder()
                    .user(user)
                    .title("Title" + i)
                    .content("Content" + i)
                    .build());
        }

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .param("email", user.getEmail())
                .param("page", String.valueOf(req.getPage()))
                .param("size", String.valueOf(req.getSize()))
                .param("sortBy", req.getSortBy())
                .param("isAscending", String.valueOf(req.isAscending()))
                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0]['content']").doesNotExist()) //content가 제외되었는지
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(5));

    }

    @DisplayName("getCommentsByDiaryId : Diary의 소유자가 해당 Diary의 코멘트를 조회할 수 있다. ")
    @Test
    public void getCommentsByDiaryIdCaseOwner() throws Exception {
        final String url = "/api/diary/{id}/comments";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        //작성자 역할을 할 Expert
        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        final String userEmail = "test@email.com";

        //다이어리의 주인 역할을 할 User
        User user = User.builder()
                .email(userEmail)
                .password(password)
                .build();

        userRepository.save(user);

        //테스트용 다이어리
        Diary diary = Diary.builder()
                .title("test")
                .content("Content")
                .build();


        //소유자 설정
        user.addDiary(diary);

        //소유자 설정 후 저장
        diaryRepository.save(diary);

        StringBuilder strBuilder = new StringBuilder();

        for(int i = 0; i < 10; i++) {
            Comment comment = new Comment();
            comment.setExpert(expert);
            comment.setContent(strBuilder.append("TestContent ").append(i+1).toString());

            //Diary에 코멘트 추가.
            diary.addComment(comment);

            commentRepository.save(comment);
        }

        assertThat(diary.getId()).isNotNull();

        //소유자 명의로 접근
        String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);

        String reqUrl = url.replace("{id}", String.valueOf(diary.getId()));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(reqUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));

    }

    @DisplayName("getCommentsByDiaryId : Diary의 소유자의 담당자가 해당 Diary의 코멘트를 조회할 수 있다. ")
    @Test
    public void getCommentsByDiaryIdCaseExpert() throws Exception {
        final String url = "/api/diary/{id}/comments";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        final String userEmail = "test@email.com";

        User user = User.builder()
                .email(userEmail)
                .password(password)
                .build();

        //관계 설정
        expert.addManagedUser(user);

        userRepository.save(user);

        Diary diary = Diary.builder()
                .title("TestTitle")
                .content("TestContent")
                .build();

        //관계 설정
        user.addDiary(diary);

        diaryRepository.save(diary);

        StringBuilder strBuilder  = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            Comment comment = new Comment();
            comment.setExpert(expert);
            comment.setContent("TestContent");

            diary.addComment(comment);

            commentRepository.save(comment);
        }

        assertThat(diary.getId()).isNotNull();

        //전문가 명의로 접근
        String accessToken = tokenProvider.createToken(expert, TokenProvider.ACCESS_EXPIRE);

        String reqUrl = url.replace("{id}", String.valueOf(diary.getId()));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(reqUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));

    }

    @DisplayName("addCommentToDiary: 전문가가 자신이 담당하는 사용자의 다이어리에 코멘트를 추가할 수 있다.")
    @Test
    public void addCommentToDiary() throws Exception {
        final String url = "/api/diary/{id}/comments";

        final String expertEmail = "manager@email.com";

        final String password = "password";

        Expert expert = new Expert(expertEmail, password);
        expertRepository.save(expert);

        final String userEmail = "test@email.com";

        User user = User.builder()
                .email(userEmail)
                .password(password)
                .build();

        //관계 설정
        expert.addManagedUser(user);

        userRepository.save(user);

        Diary diary = Diary.builder()
                .title("TestTitle")
                .content("TestContent")
                .build();

        //관계 설정
        user.addDiary(diary);

        diaryRepository.save(diary);

        //전문가 명의로 요청
        String accessToken = tokenProvider.createToken(expert, TokenProvider.ACCESS_EXPIRE);

        //코멘트를 추가할 다이어리의 id URL에 매핑
        String reqUrl = url.replace("{id}", String.valueOf(diary.getId()));

        //요청 DTO 생성 후 직렬화
        AddCommentRequest req = new AddCommentRequest();
        req.setContent("TestContent");

        String reqBody = objectMapper.writeValueAsString(req);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(reqUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

}