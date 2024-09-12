package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

//일기 정보 저장하는 엔티티
//테스트를 위한 구현입니다.
@NoArgsConstructor
@Getter
@Table(name = "diaries")
@Entity
public class Diary extends BaseEntity { //공통 컬럼 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    //Diary의 외래키 설정, JoinColumn을 통해 외래키 지정
    @ManyToOne
    @Setter //Diary 추가시 유저와 동기화 하기 위하여
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    //SQL 타입을 TEXT로 지정
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Builder
    public Diary(User user, String title, String content) {
        this.user = user; this.title = title; this.content = content;
    }
}
