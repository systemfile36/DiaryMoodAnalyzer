package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Diary;

import java.time.LocalDateTime;

//Diary의 본문을 제외한 것만 리턴한다. (목록에서 사용함)
@Getter
@Setter
public class GetDiaryTitleByPageResponse extends BaseGetDiaryResponse{
    public GetDiaryTitleByPageResponse(Diary diary) {
        super(diary);
    }
}
