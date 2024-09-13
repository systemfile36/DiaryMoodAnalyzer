package org.diarymoodanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Diary;

import java.time.LocalDateTime;

//Diary의 본문을 제외한 것만 리턴한다. (목록에서 사용함)
@Getter
@Setter
@AllArgsConstructor
public class GetDiariesTitleByPageResponse {
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;
}
