package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//Diary의 본문을 제외한 것만 리턴한다. (목록에서 사용함)
@Getter
@Setter
public class GetDiaryTitleByPageResponse extends BaseGetDiaryResponse{

    public GetDiaryTitleByPageResponse(long id, String title, byte depressionLevel, LocalDateTime createdAt, LocalDateTime updatedAt, String userEmail) {
        super(id, title, depressionLevel, createdAt, updatedAt, userEmail);
    }

}
