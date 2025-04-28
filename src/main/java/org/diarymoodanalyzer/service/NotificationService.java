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
import org.diarymoodanalyzer.repository.NotificationRepository;
import org.diarymoodanalyzer.repository.NotificationTypeRepository;
import org.diarymoodanalyzer.repository.UserNotificationSettingRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
     * 알림을 보낸다. 보낸 이는 현재 인증된 유저로 설정된다.
     * @param req 알림에 필요한 정보를 담은 DTO
     */
    public void sendNotification(NotificationRequest req) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        sendNotification(currentUserEmail, req);
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

        Notification notification = Notification.builder()
                .senderUser(senderRef)
                .targetUser(targetRef)
                .type(req.getNotificationType())
                .content(req.getContent())
                .refLink(req.getRefLink())
                .build();

        notificationRepository.save(notification);

        deleteOldestNotifications(req.getTargetEmail());
    }


    // NotificationType methods

    public List<NotificationType> getAllNotificationTypes() {
        return notificationTypeRepository.findAll();
    }

    public NotificationType getNotificationType(String typeName) {
        return notificationTypeRepository.findByName(typeName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no notification " + typeName)
        );
    }

    public NotificationType addNotificationType(NotificationTypeRequest req) {
        return notificationTypeRepository.save(req.toNotificationType());
    }

    public void deleteNotificationType(Long typeId) {
        notificationTypeRepository.deleteById(typeId);
    }

    public void deleteNotificationType(String typeName) {
        notificationTypeRepository.deleteByName(typeName);
    }

    public void updateNotificationType(Long typeId, String description) {
        notificationTypeRepository.updateDescriptionById(typeId, description);
    }

    public void updateNotificationType(String typeName, String description) {
        updateNotificationType(
                notificationTypeRepository.findIdByName(typeName), description
        );
    }

    // UserNotificationSetting methods

    public List<UserNotificationSetting> getUserNotificationSettings() {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        return getUserNotificationSettings(currentUserEmail);
    }

    public List<UserNotificationSetting> getUserNotificationSettings(String userEmail) {
        return notificationSettingRepository.findByUserEmail(userEmail);
    }

    public UserNotificationSetting getUserNotificationSetting(String typeName) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        return getUserNotificationSetting(currentUserEmail, typeName);
    }

    public UserNotificationSetting getUserNotificationSetting(String userEmail, String typeName) {
        return notificationSettingRepository.findByUserEmailAndTypeName(userEmail, typeName);
    }

    public void updateUserNotificationSetting(String userEmail, NotificationSettingRequest req) {

        //변경 감지(Dirty Check)를 사용하기 위해 조회해서 영속 상태(Persistence)로 만든다.
        UserNotificationSetting setting
                = notificationSettingRepository.findByUserEmailAndTypeName(userEmail, req.getTypeName());

        //알림 설정을 DTO의 값으로 업데이트
        req.updateNotificationSetting(setting);

        //
        notificationSettingRepository.save(setting);
    }

    public void updateUserNotificationSetting(NotificationSettingRequest req) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        updateUserNotificationSetting(currentUserEmail, req);
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
