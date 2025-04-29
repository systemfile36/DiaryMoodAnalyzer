package org.diarymoodanalyzer.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.NotificationType;

@NoArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    private String targetEmail;
    private String notificationTypeName;
    private String content;
    private String refLink;

    @Builder
    public NotificationRequest(String targetEmail, String notificationTypeName, String content, String refLink) {
        this.targetEmail = targetEmail; this.notificationTypeName = notificationTypeName; this.content = content; this.refLink = refLink;
    }
}
