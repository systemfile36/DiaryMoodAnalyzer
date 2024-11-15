package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCommentByDiaryIdRequest{
    private String ownerEmail;
}
