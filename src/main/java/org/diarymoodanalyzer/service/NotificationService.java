package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Notification;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.dto.response.NotificationResponse;
import org.diarymoodanalyzer.repository.NotificationRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    public List<NotificationResponse> getNotificationsForTarget() {

        //현재 유저 이메일 조회
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        //Mapping Entity to DTO
        return notificationRepository.findByTargetUserEmail(currentUserEmail)
                .stream().map(NotificationResponse::new).toList();
    }

    //Update is_read field to true.
    @Transactional
    public void updateAsRead(Long id) {
        notificationRepository.updateIsReadById(id, true);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void sendNotification(NotificationRequest req) {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        
    }

}
