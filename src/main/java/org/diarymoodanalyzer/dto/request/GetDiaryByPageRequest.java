package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//사용자의 인증 정보 기반으로 Diary를 페이지 단위로 반환할 때 사용하는 DTO
//인증 정보는 요청 헤더에 포함되므로 DTO에는 미포함
@NoArgsConstructor
@Getter
@Setter
public class GetDiaryByPageRequest {
    //페이지의 인덱스와 사이즈를 받는다.
    private int page;
    private int size;

    //정렬 기준이 될 필드와 오름차순 여부. 기본값 : 생성 시간 기준 역순
    private String sortBy = "createdAt";
    private boolean isAscending = false;
}
