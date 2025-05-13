package org.diarymoodanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diarymoodanalyzer.config.TokenAuthenticationFilter;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.*;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.repository.*;
import org.diarymoodanalyzer.service.NotificationService;
import org.diarymoodanalyzer.util.SimpleTemplateRenderer;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
    private NotificationTypeRepository notificationTypeRepository;

    @Autowired
    private UserNotificationSettingRepository notificationSettingRepository;

    @Autowired
    private NotificationService notificationService;

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

        userRepository.deleteAll();
        expertRepository.deleteAll();
        notificationRepository.deleteAll();
        notificationSettingRepository.deleteAll();
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

        final String notificationTypeName = "WEB_TEST";

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

        //Load NotificationType from repository
        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        //Create test data
        for(int i = 0; i < 10; i++) {
            String content = SimpleTemplateRenderer.render(
                    notificationType.getDefaultTemplate(),
                    Map.of("Test", "Test_" + i)
            );
            notifications.add(
                    Notification.builder()
                            .senderUser(sender)
                            .targetUser(target)
                            .content(content)
                            .type(notificationType)
                            .refLink(String.format("/diaries/%d", i))
                            .build()
            );
        }

        notificationRepository.saveAll(notifications);

        String accessToken = tokenProvider.createToken(target, TokenProvider.ACCESS_EXPIRE);

        //Check token
        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //Perform test
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Check response code
        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andDo(MockMvcResultHandlers.print());

        //Check result length
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

        final String notificationTypeName = "WEB_TEST";

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
        req.setNotificationTypeName(notificationTypeName);

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

        //Check NotificationType name
        assertThat(notifications.stream().allMatch((value) -> {
            NotificationType temp = value.getNotificationType();
            return temp.getName().equals(notificationTypeName);
        })).isTrue();

    }

    @DisplayName("updateAsRead: 알림을 읽음으로 표시할 수 있음")
    @Test
    void updateAsRead() throws Exception {
        //Set test data
        final String url = "/api/notifications/{id}/read";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        final String notificationTypeName = "WEB_TEST";

        Expert sender = new Expert(senderEmail, senderPassword);
        expertRepository.save(sender);

        User target = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        // Get NotificationType entity
        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        // Create single notification entity
        Notification notification = Notification.builder()
                .senderUser(sender)
                .targetUser(target)
                .content("TestNotification")
                .refLink("/diaries/10")
                .type(notificationType)
                .build();

        notificationRepository.save(notification);
        
        assertThat(notification.getId()).isNotNull();

        //Create token
        String accessToken = tokenProvider.createToken(target, TokenProvider.ACCESS_EXPIRE);

        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        String reqUrl = url.replace("{id}", String.valueOf(notification.getId()));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(reqUrl)
                .header("Authorization", "Bearer " + accessToken));

        //Check status code
        result.andExpect(MockMvcResultMatchers.status().isOk());

        //Check isRead from repository
        boolean isRead = notificationRepository.findById(notification.getId())
                .orElseThrow().isRead();

        assertThat(isRead).isTrue();
    }

    @DisplayName("updateAsReadInBatch: 알림을 일괄적으로 읽음 표시할 수 있다.")
    @Test
    void updateAsReadInBatch() throws Exception {
        //Set test data
        final String url = "/api/notifications/read";
        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";
        final int notificationCount = 20;

        final String notificationTypeName = "WEB_TEST";

        User targetUser = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        userRepository.save(targetUser);

        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        List<Notification> notifications = new ArrayList<>(notificationCount);

        for(int i = 0; i < notificationCount; i++) {
            notifications.add(
                    Notification.builder()
                            .targetUser(targetUser)
                            .type(notificationType)
                            .refLink("/")
                            .content(String.format("TEST %d", i))
                            .build()
            );
        }

        notificationRepository.saveAll(notifications);

        String accessToken = tokenProvider.createToken(targetUser, TokenProvider.ACCESS_EXPIRE);
        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //Get ids
        List<Long> ids = notifications.stream().map(Notification::getId).toList();
        assertThat(ids.size()).isEqualTo(notificationCount);

        //ids to json
        String reqBody = objectMapper.writeValueAsString(ids);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        List<Notification> resultNotifications =
                notificationRepository.findBySenderUserEmail(targetEmail);

        //Check all notification's isRead field is set to true
        assertThat(
                resultNotifications.stream()
                        .allMatch(Notification::isRead)
        ).isTrue();
    }

    @DisplayName("deleteNotification: 알림을 삭제할 수 있다.")
    @Test
    void deleteNotification() throws Exception {

        final String url = "/api/notifications/{id}";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        final String notificationTypeName = "WEB_TEST";

        Expert sender = new Expert(senderEmail, senderPassword);
        expertRepository.save(sender);

        User target = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        Notification notification = Notification.builder()
                .targetUser(target)
                .senderUser(sender)
                .content("TestContent")
                .refLink("/diaries/11")
                .type(notificationType)
                .build();

        notificationRepository.save(notification);

        assertThat(notification.getId()).isNotNull();

        String accessToken = tokenProvider.createToken(target, TokenProvider.ACCESS_EXPIRE);

        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        String reqUrl = url.replace("{id}", String.valueOf(notification.getId()));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(reqUrl)
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(notificationRepository.existsById(notification.getId())).isFalse();

    }

    @DisplayName("deleteNotifications: 알림의 일괄 삭제가 가능하다. ")
    @Test
    void deleteNotifications() throws Exception {

        final String url = "/api/notifications";
        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";
        final int notificationCount = 20;

        final String notificationTypeName = "WEB_TEST";

        User targetUser = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        userRepository.save(targetUser);

        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        List<Notification> notifications = new ArrayList<>(20);

        for(int i = 0; i < notificationCount; i++) {
            Notification temp = Notification.builder()
                    .targetUser(targetUser)
                    .type(notificationType)
                    .refLink("/")
                    .content(String.format("TEST %d", i))
                    .build();

            notifications.add(temp);
        }

        notificationRepository.saveAll(notifications);

        String accessToken = tokenProvider.createToken(targetUser, TokenProvider.ACCESS_EXPIRE);

        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        List<Long> ids = notifications.stream().map(Notification::getId).toList();
        assertThat(ids.size()).isEqualTo(notificationCount);

        String reqBody = objectMapper.writeValueAsString(ids);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody)
        );

        result.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        //Check all Notifications deleted
        assertThat(
                ids.stream()
                        .noneMatch(value -> notificationRepository.existsById(value))
        ).isTrue();

    }

    @DisplayName("sendNotification: 알림 전송 시, 알림 개수가 최대치를 초과하면 오래된 것부터 삭제한다.")
    @Test
    void sendNotification_deleteOldest() throws Exception {

        final String url = "/api/notifications";
        final String senderEmail = "expert@email.com";
        final String senderPassword = "test1234";

        final String targetEmail = "test@email.com";
        final String targetPassword = "test1234";

        final String notificationTypeName = "WEB_TEST";

        Expert sender = new Expert(senderEmail, senderPassword);
        expertRepository.save(sender);

        User target = User.builder()
                .email(targetEmail)
                .password(targetPassword)
                .build();

        sender.addManagedUser(target);

        userRepository.save(target);

        NotificationType notificationType = notificationTypeRepository.findByName(notificationTypeName)
                .orElse(null);

        assertThat(notificationType).isNotNull();

        List<Notification> notifications = new ArrayList<>();

        //최대치인 50개에 맞춰서 채운다.
        for(int i = 0; i < 50; i++) {
            Notification notification = Notification.builder()
                    .senderUser(sender)
                    .targetUser(target)
                    .type(notificationType)
                    .content(String.valueOf(i))
                    .refLink(String.format("/diaries/%d", i))
                    .build();
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);

        String accessToken = tokenProvider.createToken(target, TokenProvider.ACCESS_EXPIRE);

        assertThat(accessToken).isNotNull();
        assertThat(tokenProvider.validateToken(accessToken)).isTrue();

        //Create notification for test
        NotificationRequest req = new NotificationRequest();
        req.setTargetEmail(targetEmail);
        req.setContent("50");
        req.setRefLink("/diaries/10");
        req.setNotificationTypeName(notificationTypeName);

        String reqBody = objectMapper.writeValueAsString(req);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        //알림을 전부 받아오고 최대치를 넘었는지 여부를 확인한다.
        List<Notification> resultNotifications = notificationRepository.findByTargetUserEmail(targetEmail);
        assertThat(resultNotifications.size()).isEqualTo(50);

        //제대로 오래된 것부터 삭제했는지 확인
        resultNotifications.sort(Comparator.comparing(BaseEntity::getCreatedAt));
        assertThat(resultNotifications.get(resultNotifications.size() - 1).getContent()).isEqualTo(req.getContent());
    }
}