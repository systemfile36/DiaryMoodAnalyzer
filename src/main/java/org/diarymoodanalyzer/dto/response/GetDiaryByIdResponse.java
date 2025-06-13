package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Diary;

@Getter
@Setter
public class GetDiaryByIdResponse extends BaseGetDiaryResponse {
    private String content;

    //엔티티에서 DTO를 만듬
    public GetDiaryByIdResponse(Diary diary) {
        // init fields from entity by calling super constructor
        super(diary);
        this.content = diary.getContent();
    }
}
