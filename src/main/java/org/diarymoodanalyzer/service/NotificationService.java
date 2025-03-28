package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Notification;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.dto.response.NotificationResponse;
import org.diarymoodanalyzer.repository.NotificationRepository;
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

    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final int MAX_NOTIFICATIONS_PER_USER = 50;

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
                .type(req.getType())
                .content(req.getContent())
                .refLink(req.getRefLink())
                .build();

        notificationRepository.save(notification);

        deleteOldestNotifications(req.getTargetEmail());
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
