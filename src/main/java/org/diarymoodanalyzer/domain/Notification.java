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

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User senderUser;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ref_link")
    private String refLink;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(User targetUser, User senderUser, String type, String content, String refLink) {
        this.targetUser = targetUser; this.senderUser = senderUser; this.type = type;
        this.content = content; this.refLink = refLink;
    }
}
