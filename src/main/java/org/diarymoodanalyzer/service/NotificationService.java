package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Notification;
import org.diarymoodanalyzer.domain.NotificationType;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.domain.UserNotificationSetting;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.dto.request.NotificationSettingRequest;
import org.diarymoodanalyzer.dto.request.NotificationTypeRequest;
import org.diarymoodanalyzer.dto.response.NotificationResponse;
import org.diarymoodanalyzer.dto.response.UserNotificationSettingResponse;
import org.diarymoodanalyzer.repository.NotificationRepository;
import org.diarymoodanalyzer.repository.NotificationTypeRepository;
import org.diarymoodanalyzer.repository.UserNotificationSettingRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.diarymoodanalyzer.util.SimpleTemplateRenderer;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationTypeRepository notificationTypeRepository;

    private final UserNotificationSettingRepository notificationSettingRepository;

    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final int MAX_NOTIFICATIONS_PER_USER = 50;

    // Notification methods

    public List<NotificationResponse> getNotificationsForTarget() {

        //현재 유저 이메일 조회
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        //Mapping Entity to DTO
        return notificationRepository.findByTargetUserEmail(currentUserEmail)
                .stream().map(NotificationResponse::new).toList();
    }

    /**
     * is_read 필드를 true로 설정하여 읽음으로 표시.
     * @param id 읽음으로 표시할 알림의 id
     */
    @Transactional
    public void updateAsRead(Long id) {
        //권한 확인 필요

        //현재 유저 이메일 조회
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        Notification notification = notificationRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no notification : " + id));

        //해당 알림의 target이 현재 인증된 유저와 다를 경우, 예외 throw
        if(!notification.getTargetUser().getEmail().equals(currentUserEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        notificationRepository.updateIsReadById(id, true);
    }

    /**
     * is_read 필드를 true로 설정하여 읽음으로 표시
     * @param ids 읽음으로 표시할 알림의 id 리스트
     */
    @Transactional
    public void updateAsRead(List<Long> ids) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        List<Notification> notifications = notificationRepository.findAllById(ids);

        //인자로 받은 모든 알림의 target과 현재 유저의 이메일이 일치하지 않을 시, 예외 throw
        if(!notifications.stream().allMatch(
                (value) -> value.getTargetUser().getEmail().equals(currentUserEmail)
        )) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        } else if (notifications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id list is empty");
        }

        notificationRepository.updateIsReadByIds(ids, true);
    }

    /**
     * 현재 인증된 사용자의 모든 알림을 읽음으로 처리
     */
    @Transactional
    public void updateAllAsRead() {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));
        
       updateAllAsRead(currentUserEmail);
    }

    /**
     * userEmail에 해당하는 사용자의 모든 알림을 읽음으로 처리
     * @param userEmail
     */
    @Transactional
    public void updateAllAsRead(String userEmail) {
        notificationRepository.updateIsReadByTargetUserEmail(userEmail, true);
    }

    /**
     * 알림을 삭제한다.
     * @param id 삭제할 알림의 id
     */
    public void deleteNotification(Long id) {
        //권한 확인 필요

        //현재 유저 이메일 조회
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no notification : " + id));

        //해당 알림의 target이 현재 인증된 유저와 다를 경우, 예외 throw
        if(!notification.getTargetUser().getEmail().equals(currentUserEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        notificationRepository.deleteById(id);
    }

    /**
     * 알림을 삭제한다.
     * @param ids 삭제할 알림의 id 목록
     */
    public void deleteNotification(List<Long> ids) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        List<Notification> notifications = notificationRepository.findAllById(ids);

        if(!notifications.stream().allMatch(
                (value) -> value.getTargetUser().getEmail().equals(currentUserEmail)
        )) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        } else if (notifications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id list is empty");
        }

        notificationRepository.deleteAllById(ids);
    }

    /**
     * 현재 인증된 사용자의 모든 알림을 삭제한다.
     */
    public void deleteAllNotification() {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        deleteAllNotification(currentUserEmail);
    }

    /**
     * userEmail에 해당하는 사용자의 모든 알림을 삭제한다.
     * @param userEmail 알림을 삭제할 사용자의 이메일
     */
    public void deleteAllNotification(String userEmail) {
        notificationRepository.deleteAllByTargetUserEmail(userEmail);
    }

    /**
     * 알림을 보낸다. 보낸 이는 현재 인증된 유저로 설정된다.
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    public void sendNotificationWithAuth(NotificationRequest req) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        sendNotification(currentUserEmail, req);
    }

    /**
     * 알림을 보낸다. 보낸 이는 null로 설정된다.
     * @param req
     */
    public void sendNotification(NotificationRequest req) {
        Long targetId = userRepository.findIdByEmail(req.getTargetEmail());
        sendNotification(targetId, req);
    }

    /**
     * 알림을 보낸다.
     * @param senderEmail 보내는 이의 이메일 주소
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    public void sendNotification(String senderEmail, NotificationRequest req) {
        Long senderId = userRepository.findIdByEmail(senderEmail);
        Long targetId = userRepository.findIdByEmail(req.getTargetEmail());

        sendNotification(senderId, targetId, req);
    }

    /**
     * 알림을 보낸다.
     * @param senderId 보내는 이의 id
     * @param targetId 받는 이의 id
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    public void sendNotification(Long senderId, Long targetId, NotificationRequest req) {

        User senderRef = entityManager.getReference(User.class, senderId);
        User targetRef = entityManager.getReference(User.class, targetId);

        sendNotification(senderRef, targetRef, req);
    }

    /**
     * 알림을 보낸다.
     * @param targetId 받는 이의 id
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    public void sendNotification(Long targetId, NotificationRequest req) {

        User targetRef = entityManager.getReference(User.class, targetId);

        sendNotification(null, targetRef, req);
    }

    /**
     * 알림을 보낸다. 공통 로직
     * @param senderRef 보낸 이의 엔티티 참조 (nullable)
     * @param targetRef 받는 이의 엔티티 참조
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    @Transactional
    public void sendNotification(User senderRef, User targetRef, NotificationRequest req) {

        //사용자와 알림 타입 이름으로 설정 조회
        UserNotificationSetting setting =
                notificationSettingRepository.findByUserEmailAndTypeName(targetRef.getEmail(), req.getNotificationTypeName());

        //해당 알림이 꺼져 있다면 알림을 생성하지 않고 종료한다.
        //Check Notification of this type is enabled
        if(!setting.isNotifyEnabled()) {
            return;
        }

        // 템플릿 처리를 위한 캐싱
        String content = req.getContent();
        NotificationType notificationType = getNotificationType(req.getNotificationTypeName());

        // Apply default template when content is null or empty string
        // otherwise, apply content as template
        if(Objects.isNull(content) || content.isEmpty()) {
            content = SimpleTemplateRenderer.render(notificationType.getDefaultTemplate(), req.getValues().split(","));
        } else {
            content = SimpleTemplateRenderer.render(content, req.getValues().split(","));
        }


        Notification notification = Notification.builder()
                .senderUser(senderRef)
                .targetUser(targetRef)
                .type(notificationType)
                .content(content)
                .refLink(req.getRefLink())
                .build();

        notificationRepository.save(notification);

        if(setting.isEmailEnabled()) {
            //이메일 보내기 처리 
        }

        deleteOldestNotifications(req.getTargetEmail());
    }


    // NotificationType methods

    /**
     * 모든 알림 타입을 가져온다. 
     * @return 모든 알림 타입 리스트 
     */
    public List<NotificationType> getAllNotificationTypes() {
        return notificationTypeRepository.findAll();
    }

    public NotificationType getNotificationType(String typeName) {
        return notificationTypeRepository.findByName(typeName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no notification " + typeName)
        );
    }

    /**
     * 새로운 알림 타입을 추가한다. 
     * @param req 추가할 알림의 정보를 담은 DTO
     * @return 추가된 알림 타입 엔티티 
      */
    @Transactional
    public NotificationType addNotificationType(NotificationTypeRequest req) {
        return notificationTypeRepository.save(req.toNotificationType());
    }

    /**
     * 알림 타입을 삭제한다. 
     * @param typeId 삭제할 알림 타입의 id
     */
    public void deleteNotificationType(Long typeId) {
        notificationTypeRepository.deleteById(typeId);
    }

    /**
     * 알림 타입을 삭제한다. 
     * @param typeName 삭제할 알림 타입의 name 컬럼
     */
    public void deleteNotificationType(String typeName) {
        notificationTypeRepository.deleteByName(typeName);
    }

    /**
     * 알림 타입의 description을 엄데이트한다.
     * @param typeId 업데이트할 알림 타입의 id
     * @param description 업데이트할 description 내용
     */
    public void updateNotificationType(Long typeId, String description) {
        notificationTypeRepository.updateDescriptionById(typeId, description);
    }

    /**
     * 알림 타입의 description을 엄데이트한다.
     * @param typeName 업데이트할 알림 타입의 name 컬럼
     * @param description 업데이트할 description 내용
     */
    public void updateNotificationType(String typeName, String description) {
        updateNotificationType(
                notificationTypeRepository.findIdByName(typeName), description
        );
    }

    // UserNotificationSetting methods

    /**
     * 현재 인증된 사용자의 모든 알림 설정을 반환한다.
     * DTO로 necessary 정보만 반환한다.
     * @return 알림 설정 DTO 리스트
     */
    public List<UserNotificationSettingResponse> getUserNotificationSettings() {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        List<UserNotificationSetting> result = getUserNotificationSettings(currentUserEmail);

        // Mapping to DTO
        return result.stream().map(UserNotificationSettingResponse::new).toList();
    }

    /**
     * userEmail에 해당하는 사용자의 모든 알림 설정을 반환한다.
     * @param userEmail 알림 설정을 조회할 사용자의 이메일
     * @return 알림 설정 리스트
     */
    public List<UserNotificationSetting> getUserNotificationSettings(String userEmail) {
        return notificationSettingRepository.findByUserEmail(userEmail);
    }

    /**
     * 현재 인증된 사용자의 지정된 알림 타입에 대한 알림 설정을 반환한다.
     * @param typeName 지정할 알림 타입
     * @return 알림 설정 엔티티
     */
    public UserNotificationSetting getUserNotificationSetting(String typeName) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        return getUserNotificationSetting(currentUserEmail, typeName);
    }

    /**
     * userEmail에 해당하는 사용자의 지정된 알림 타입에 대한 알림 설정을 반환한다.
     * @param userEmail 사용자의 이메일
     * @param typeName 지정할 알림 타입
     * @return 알림 설정 엔티티
     */
    public UserNotificationSetting getUserNotificationSetting(String userEmail, String typeName) {
        return notificationSettingRepository.findByUserEmailAndTypeName(userEmail, typeName);
    }

    /**
     * 알림 설정을 업데이트 한다.
     * @param userEmail 알림 설정을 갱신할 사용자의 이메일
     * @param req 알림 설정 정보를 가진 DTO
     */
    @Transactional
    public void updateUserNotificationSetting(String userEmail, NotificationSettingRequest req) {

        //변경 감지(Dirty Check)를 사용하기 위해 조회해서 영속 상태(Persistence)로 만든다.
        UserNotificationSetting setting
                = notificationSettingRepository.findByUserEmailAndTypeName(userEmail, req.getTypeName());

        //알림 설정을 DTO의 값으로 업데이트
        req.updateNotificationSetting(setting);

        //
        notificationSettingRepository.save(setting);
    }

    /**
     * 현재 인증된 사용자의 알림 설정을 업데이트 한다.
     * @param req 알림 설정 정보를 가진 DTO
     */
    @Transactional
    public void updateUserNotificationSetting(NotificationSettingRequest req) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        updateUserNotificationSetting(currentUserEmail, req);
    }

    /**
     * 인자로 받은 유저에 알림 설정을 모두 기본값으로 초기화한다. (유저 초기 생성 시 사용)
     * @param user 알림을 초기화할 User 엔티티 또는 프록시 엔티티
     */
    @Transactional
    public void initializeDefaultNotificationSettings(User user) {

        List<NotificationType> types = getAllNotificationTypes();

        List<UserNotificationSetting> settings = new ArrayList<>();

        //Init notification settings for all notification types by default setting
        types.forEach((value) -> {
            settings.add(UserNotificationSetting.builder()
                    .user(user)
                    .notificationType(value)
                    .isEmailEnabled(value.isDefaultEmailEnabled())
                    .isNotifyEnabled(value.isDefaultNotifyEnabled())
                    .isWebEnabled(value.isDefaultWebEnabled())
                    .build());
        });

        notificationSettingRepository.saveAll(settings);
    }

    /**
     * 인자로 받은 유저에 알림 설정을 모두 기본값으로 초기화한다. (유저 초기 생성 시 사용)
     * @param userEmail 알림을 초기화할 User의 이메일
     */
    @Transactional
    public void initializeDefaultNotificationSettings(String userEmail) {
        initializeDefaultNotificationSettings(
                entityManager.getReference(User.class, userRepository.findIdByEmail(userEmail))
        );
    }

    /**
     * 현재 알림 개수가 최대 알림 개수를 초과할 경우, 가장 오래된 것 부터 삭제한다.
     * @param targetUserEmail 삭제할 알림의 target 사용자 email
     */
    @Transactional
    private void deleteOldestNotifications(String targetUserEmail) {
        //알림 개수 조회
        int notificationCount = notificationRepository.countByTargetUserEmail(targetUserEmail);

        if(notificationCount > MAX_NOTIFICATIONS_PER_USER) {
            //초과한 개수를 구한다.
            int exceedCount = notificationCount - MAX_NOTIFICATIONS_PER_USER;

            //SELECT n.id FROM notifications n JOIN users u ON n.user_id = u.id WHERE u.email = :email ORDER BY created_at ASC LIMIT :exceedCount
            //위 쿼리와 (거의) 동일한 쿼리가 실행됨. 오래된 순으로 정렬한 후, 초과한 개수만큼 id를 받아온다.
            List<Long> oldestNotificationIds
                    = notificationRepository.findOldestNotificationIds(targetUserEmail, PageRequest.of(0, exceedCount));

            //Batch로 받아온 id 들에 대해 전부 삭제한다.
            notificationRepository.deleteAllByIdInBatch(oldestNotificationIds);
        }
    }
}
