package org.diarymoodanalyzer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

//Diary 추가 요청을 보낼 때 사용할 DTO
@AllArgsConstructor
@Getter
public class AddDiaryRequest {
    //private String email; //해당 정보 대신 인증 정보로 소유자 결정함
    private String title;
    private String content;
}
