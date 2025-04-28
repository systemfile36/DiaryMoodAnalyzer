package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.NotificationType;

@NoArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    private String targetEmail;
    private NotificationType notificationType;
    private String content;
    private String refLink;
}
