package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import org.diarymoodanalyzer.domain.Diary;

/*
Page<Diary>로 직접 엔티티를 반환하면 User 엔티티도 반환되어 불필요한 정보가 넘어가고,
또한 순환참조도 발생한다. 이를 위해 필요한 정보만 넘기는 DTO를 만든다.
 */
@Getter
public class GetDiaryByPageResponse extends BaseGetDiaryResponse{

    private final String content;


    public GetDiaryByPageResponse(Diary diary) {
        super(diary.getId(), diary.getTitle(), diary.getCreatedAt(), diary.getUpdatedAt(), diary.getUser().getEmail());
        this.content = diary.getContent();
    }
}
