package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.Comment;

@NoArgsConstructor
@Getter
@Setter
public class GetCommentByDiaryIdResponse  extends TimeStampedResponse{
    private Long id;
    private String content;
    private String expertEmail;
    private Long diaryId;
}
