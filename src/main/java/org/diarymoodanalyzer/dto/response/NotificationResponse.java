package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Notification;

import java.util.Optional;

@NoArgsConstructor
@Setter
@Getter
public class NotificationResponse extends TimeStampedResponse{
    private Long id;
    private String senderEmail;
    private String type;
    private String content;
    private String refLink;
    private boolean isRead;

    //Constructor from DTO
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.senderEmail =
                notification.getSenderUser() == null ? null : notification.getSenderUser().getEmail();
        this.type = notification.getType(); this.content = notification.getContent();
        this.refLink = notification.getRefLink(); this.isRead = notification.isRead();
        this.setCreatedAt(notification.getCreatedAt());
        this.setUpdatedAt(notification.getUpdatedAt());
    }
}
