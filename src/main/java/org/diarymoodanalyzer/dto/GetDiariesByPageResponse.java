package org.diarymoodanalyzer.dto;

import lombok.Getter;
import org.diarymoodanalyzer.domain.Diary;

import java.time.LocalDateTime;

/*
Page<Diary>로 직접 엔티티를 반환하면 User 엔티티도 반환되어 불필요한 정보가 넘어가고,
또한 순환참조도 발생한다. 이를 위해 필요한 정보만 넘기는 DTO를 만든다.
 */
@Getter
public class GetDiariesByPageResponse {
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String userEmail;

    public GetDiariesByPageResponse(Diary diary) {
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.createdAt = diary.getCreatedAt();
        this.updatedAt = diary.getUpdatedAt();
        this.userEmail = diary.getUser().getEmail();
    }
}
