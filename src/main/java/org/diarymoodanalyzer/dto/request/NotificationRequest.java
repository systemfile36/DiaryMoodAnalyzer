package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Notification;

@NoArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    private String targetEmail;
    private String type;
    private String content;
    private String refLink;
}
