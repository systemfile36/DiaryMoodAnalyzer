package org.diarymoodanalyzer.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.NotificationType;

@NoArgsConstructor
@Getter
@Setter
public class NotificationTypeRequest {
    private String name;
    private String description = "";

    private boolean defaultNotifyEnabled;

    private boolean defaultWebEnabled;

    private boolean defaultEmailEnabled;

    @Builder
    public NotificationTypeRequest(String name, String description,
                                   boolean defaultNotifyEnabled, boolean defaultWebEnabled, boolean defaultEmailEnabled) {
       this.name = name; this.description = description;
       this.defaultNotifyEnabled = defaultNotifyEnabled; this.defaultWebEnabled = defaultWebEnabled; this.defaultEmailEnabled = defaultEmailEnabled;
    }

    /**
     * 현재 DTO의 값으로 NotificationType 엔티티를 만들어 반환한다.
     * @return DTO 값 기반 NotificationType
     */
    public NotificationType toNotificationType() {
        return NotificationType.builder()
                .name(this.name)
                .description(this.description)
                .defaultNotifyEnabled(this.defaultNotifyEnabled)
                .defaultWebEnabled(this.defaultWebEnabled)
                .defaultEmailEnabled(this.defaultEmailEnabled)
                .build();
    }
}
