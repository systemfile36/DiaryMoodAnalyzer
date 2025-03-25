package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.config.TokenAuthenticationFilter;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.Notification;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.NotificationRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationsApiControllerTest {

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

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * 테스트 실행 전마다 MockMvc 초기화. 웹 어플리케이션 컨텍스트 세팅.
     * 리포지토리 초기화
     */
    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new TokenAuthenticationFilter(tokenProvider)) //인증 구현을 위한 수동 필터 추가
                .build();
    }

    @DisplayName("getNotifications: target 사용자가 자신의 알림을 조회할 수 있다.")
    @Test
    void getNotifications() throws Exception {

        //Set test data
        final String url = "/api/notifications";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        //Set sender
        Expert sender = new Expert(senderEmail, senderPassword);

        expertRepository.save(sender);

        //Set target
        User target = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        List<Notification> notifications = new ArrayList<>();

        //Create test data
        for(int i = 0; i < 10; i++) {
            notifications.add(
                    Notification.builder()
                            .senderUser(sender)
                            .targetUser(target)
                            .content(String.format("TestNotification %d", i))
                            .type("NEW_COMMENT")
                            .refLink(String.format("/diaries/%d", i))
                            .build()
            );
        }

        notificationRepository.saveAll(notifications);

        String accessToken = tokenProvider.createToken(target, TokenProvider.ACCESS_EXPIRE);

        //Check token
        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Check response code
        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andDo(MockMvcResultHandlers.print());

        result.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));

    }

    @DisplayName("sendNotification: target 사용자에게 알림을 보낼 수 있다.")
    @Test
    void sendNotification() throws Exception {
        //Set test data
        final String url = "/api/notifications";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        Expert sender = new Expert(senderEmail, senderPassword);
        expertRepository.save(sender);

        User target = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        //Create Token
        String accessToken = tokenProvider.createToken(sender, TokenProvider.ACCESS_EXPIRE);

        //Check token
        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //Create notification for test
        NotificationRequest req = new NotificationRequest();
        req.setTargetEmail(targetEmail);
        req.setContent("TestNotification");
        req.setRefLink("/diaries/10");
        req.setType("NEW_COMMENT");

        //convert to request body
        String reqBody = objectMapper.writeValueAsString(req);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        //Check response code
        result.andExpect(MockMvcResultMatchers.status().isOk());

        //Check repository
        List<Notification> notifications = notificationRepository.findByTargetUserEmail(targetEmail);

        assertThat(notifications).isNotNull();
        assertThat(notifications.size()).isEqualTo(1);
        assertThat(notifications.get(0).getContent()).isEqualTo(req.getContent());
        assertThat(notifications.get(0).getTargetUser().getEmail()).isEqualTo(targetEmail);

    }

    @DisplayName("updateAsRead: 알림을 읽음으로 표시할 수 있음")
    @Test
    void updateAsRead() {
        //Set test data
        final String url = "/api/notifications/{id}/read";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        Expert sender = new Expert(senderEmail, senderPassword);
        expertRepository.save(sender);

        User target = User.builder()
                .email(targetEmail)
                .password(senderPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        Notification notification = Notification.builder()
                .senderUser(sender)
                .targetUser(target)
                .content("TestNotification")
                .refLink("/diaries/10")
                .type("NEW_COMMENT")
                .build();

        notificationRepository.save(notification);
        

    }

    @Test
    void deleteNotification() {
    }
}