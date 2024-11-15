package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Diary 엔티티.
 */
@NoArgsConstructor
@Getter
@Table(name = "diaries")
@Entity
public class Diary extends BaseEntity { //공통 컬럼 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Diary의 소유자.
     * User 엔티티와 다대1 관계로 연결됨 (양방향)
     */
    @ManyToOne
    @Setter //Diary 추가시 유저와 동기화 하기 위하여
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    //SQL 타입을 TEXT로 지정
    @Setter
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Diary에 달린 코멘트들.
     * Comment와 1대다 관계
     */
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @Builder
    public Diary(User user, String title, String content) {
        this.user = user; this.title = title; this.content = content;
    }
}
