package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table(name="user_notification_settings", uniqueConstraints = {
        //사용자와 알림 타입 당 유일하게 존재하도록 설정
    @UniqueConstraint(columnNames = { "user_id", "notification_type_id" })
})
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Lazy fetch. 오버헤드 막기 위함
    @Setter
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="notification_type_id", nullable = false)
    private NotificationType notificationType;

    @Column(name="is_notify_enabled", nullable = false)
    private boolean isNotifyEnabled;

    @Column(name="is_web_enabled", nullable = false)
    private boolean isWebEnabled;

    @Column(name="is_email_enabled", nullable = false)
    private boolean isEmailEnabled;

    @Builder
    public UserNotificationSetting(User user, NotificationType notificationType,
                                   boolean isNotifyEnabled, boolean isWebEnabled, boolean isEmailEnabled) {
        this.user = user; this.notificationType = notificationType;
        this.isEmailEnabled = isEmailEnabled; this.isNotifyEnabled = isNotifyEnabled; this.isWebEnabled = isWebEnabled;
    }

}

/*
CREATE TABLE user_notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type_id BIGINT NOT NULL,
    is_email_enabled BOOLEAN NOT NULL,
    is_notify_enabled BOOLEAN NOT NULL,
    is_web_enabled BOOLEAN NOT NULL,
    ......
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (notification_type_id) REFERENCES notification_type(id) ON DELETE CASCADE,
    UNIQUE(user_id, notification_type_id) --사용자와 알림 타입 당 유일하게 존재하도록 설정
);
 */