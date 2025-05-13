package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 알림 타입 저장 엔티티
 *
 */
@NoArgsConstructor
@Getter
@Setter
@Table(name = "notification_types")
@Entity
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_notify_enabled")
    private boolean defaultNotifyEnabled = true;

    @Column(name = "default_email_enabled")
    private boolean defaultEmailEnabled = true;

    @Column(name = "default_web_enabled")
    private boolean defaultWebEnabled = true;

    /**
     * 알림 표시를 위한 기본 템플릿. e.g., {expert} 님의 새 코멘트, ...
     */
    @Column(name = "default_template", nullable = false, columnDefinition = "TEXT")
    private String defaultTemplate;

    @Builder
    public NotificationType(String name, String description, String defaultTemplate,
                            boolean defaultNotifyEnabled, boolean defaultWebEnabled, boolean defaultEmailEnabled) {
        this.name = name; this.description = description; this.defaultTemplate = defaultTemplate;
        this.defaultNotifyEnabled = defaultNotifyEnabled; this.defaultWebEnabled = defaultWebEnabled; this.defaultEmailEnabled = defaultEmailEnabled;
    }
}

/*
CREATE TABLE notification_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    default_notify_enabled BOOLEAN,
    default_email_enabled BOOLEAN,
    default_web_enabled BOOLEAN,
    description TEXT,
    default_template TEXT
);
*/