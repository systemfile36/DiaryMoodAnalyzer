package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.dto.ai.request.VadScore;
import org.diarymoodanalyzer.dto.ai.response.DiaryAnalyzeResponse;

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
     * VAD score. Will be set by {@link org.diarymoodanalyzer.service.DiaryAnalyzeService DiaryAnalyzeService} asynchronously
     */
    @Embedded
    private VadScore vadScore;

    /**
     * Depression score. Will be set by {@link org.diarymoodanalyzer.service.DiaryAnalyzeService DiaryAnalyzeService} asynchronously
     */
    @Column(name = "depression_score")
    private int depressionScore;

    /**
     * Result of PHQ classification. Set type to String temporary.
     * Will be set by {@link org.diarymoodanalyzer.service.DiaryAnalyzeService DiaryAnalyzeService} asynchronously
     */
    @Column(name = "classification")
    private String classification;

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

    /**
     * Set result of analyze from {@link DiaryAnalyzeResponse DiaryAnalyzeResponse}.
     * <br/>
     * Used by {@link org.diarymoodanalyzer.service.DiaryAnalyzeService DiaryAnalyzeService}.
     * @param dto DTO
     */
    public void setAnalyzeResult(DiaryAnalyzeResponse dto) {
        this.vadScore = dto.getVad_score();
        this.depressionScore = dto.getDepression_score();
        this.classification = dto.getClassification();
    }

    /**
     * Set result of analyze to failure.
     * <br/>
     * This method sets fields related to analyze to failure-indicate values like -1, null, or an empty string
     */
    public void setAnalyzeAsFail() {
        this.vadScore = null;
        this.depressionScore = -1;
        this.classification = null;
    }

    /**
     * Check analyze was successful.
     * <br/>
     * This method check fields related to analyze has valid value
     * @return Return true if result of analyze has valid value, otherwise false
     */
    public boolean isAnalyzeSuccess() {
        return this.vadScore != null || this.depressionScore != -1 || this.classification != null;
    }
}
