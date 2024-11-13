package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Expert에 의한 소유자 기반 Diary 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class GetDiaryForExpertRequest {
    private Long id;
    private String ownerEmail;
}
