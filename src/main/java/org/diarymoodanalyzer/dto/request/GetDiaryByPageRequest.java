package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//사용자의 인증 정보 기반으로 Diary를 페이지 단위로 반환할 때 사용하는 DTO
//인증 정보는 요청 헤더에 포함되므로 DTO에는 미포함
@NoArgsConstructor
@Getter
@Setter
public class GetDiaryByPageRequest extends BasePagingRequest{

}
