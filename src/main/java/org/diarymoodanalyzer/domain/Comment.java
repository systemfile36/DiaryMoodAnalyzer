package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "comments")
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 코멘트의 대상이 된 Diary에 대한 참조
     */
    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false) //외래 키를 통해 엔티티 로드(Lazy)
    private Diary diary;

    /**
     * 코멘트를 작성한 Expert
     */
    @ManyToOne
    @JoinColumn(name = "expert_id")
    private Expert expert;

    /**
     * 코멘트 본문
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Comment(String content) {
        this.content = content;
    }
}
