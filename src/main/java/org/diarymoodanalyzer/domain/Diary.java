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
     * 우울한 정도를 나타내는 수치
     * -2에서 10의 값을 가진다.
     */
    @Column(name = "depression_level")
    private Byte depressionLevel = -1;

    /**
     * Diary에 달린 코멘트들.
     * Comment와 1대다 관계
     */
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    /**
     * Diary의 코멘트 목록에 코멘트를 추가한다. (상호 관계 설정)
     * @param comment 추가할 코멘트
     */
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setDiary(this);
    }

    @Builder
    public Diary(User user, String title, String content) {
        this.user = user; this.title = title; this.content = content;
    }

    //setter를 통해 depressionLevel의 값이 DepressionLevel의 enum값을 가지도록 유지한다. 

    /**
     * Enum 값을 통해 depressionLevel을 설정한다. 
     * @param level 설정할 depressionLevel의 Enum 값
     */
    public void setDepressionLevel(DepressionLevel level) {
        this.depressionLevel = level.getValue();
    }

    /**
     * byte 값을 통해 depressionLevel을 설정한다.
     * Enum에 정의되지 않은 값이면 DepressionLevel.ERROR 값으로 설정한다.
     * @param value 설정할 depressionLevel의 byte
     */
    public void setDepressionLevel(byte value) {
        try {
            this.setDepressionLevel(DepressionLevel.fromValue(value));
        } catch (IllegalArgumentException e) {
            this.setDepressionLevel(DepressionLevel.ERROR);
        }

    }
}
