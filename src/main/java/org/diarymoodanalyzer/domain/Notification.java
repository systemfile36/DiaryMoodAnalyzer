package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 알림 엔티티
 */
@NoArgsConstructor
@Table(name = "notifications")
@Entity
@Getter
@Setter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User senderUser;

    @ManyToOne //타입은 참조할 일이 많고, 가벼우므로 EAGER 로딩 사용 (기본값)
    @JoinColumn(name = "notification_type_id", nullable = false)
    private NotificationType notificationType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ref_link")
    private String refLink;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(User targetUser, User senderUser, NotificationType type, String content, String refLink) {
        this.targetUser = targetUser; this.senderUser = senderUser; this.notificationType = type;
        this.content = content; this.refLink = refLink;
    }
}
