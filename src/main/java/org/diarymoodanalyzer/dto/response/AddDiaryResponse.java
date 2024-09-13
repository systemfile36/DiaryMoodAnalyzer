package org.diarymoodanalyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//Diary 추가에 대한 응답 DTO
@AllArgsConstructor
@Getter
public class AddDiaryResponse {
    private Long id;
    private String email;
    private String title;
}
