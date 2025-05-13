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
    /**
     * 템플릿에 치환될 문자열. comma-seperated string.
     */
    private String values = "";
    private String refLink;

    @Builder
    public NotificationRequest(String targetEmail, String notificationTypeName, String content, String refLink, String values) {
        this.targetEmail = targetEmail; this.notificationTypeName = notificationTypeName; this.content = content; this.refLink = refLink;
        this.values = values;
    }
}
