package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.Setter;

//Diary를 가져오는 요청에 대한 추상 클래스
//기본적으로 User의 이메일 기반으로 가져온다.
@Getter
@Setter
public abstract class BaseGetDiaryRequest {
    private String email;
}
