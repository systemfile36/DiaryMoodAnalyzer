package org.diarymoodanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//Diary 추가 요청을 보낼 때 사용할 DTO
@AllArgsConstructor
@Getter
public class AddDiaryRequest {
    private String email;
    private String title;
    private String content;
    /*
    실제로는 인증을 위한 토큰 등도 넘겨야 함. 현재는 생략
     */
}
