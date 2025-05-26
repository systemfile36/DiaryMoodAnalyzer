package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Notification;
import org.diarymoodanalyzer.domain.NotificationType;

@NoArgsConstructor
@Setter
@Getter
public class NotificationResponse extends TimeStampedResponse{
    private Long id;
    private String senderEmail;
    // 타입 관련 정보는 그리 크지 않으므로 그대로 보낸다.
    private NotificationType notificationType;
    private String content;
    private String refLink;
    private boolean isRead;

    // constructor from Entity
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.senderEmail =
                notification.getSenderUser() == null ? null : notification.getSenderUser().getEmail();
        this.notificationType = notification.getNotificationType(); this.content = notification.getContent();
        this.refLink = notification.getRefLink(); this.isRead = notification.isRead();
        this.setCreatedAt(notification.getCreatedAt());
        this.setUpdatedAt(notification.getUpdatedAt());
    }
}
