package org.diarymoodanalyzer.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/*
Diary를 반환할때 사용하는 DTO의 추상 클래스이다.
기본적인 정보들은 한군데로 모아서 관리하고, 자식 객체들은 그때그때 필요한 것을 확장해서 사용한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseGetDiaryResponse {
    private Long id;
    private String title;
    private byte depressionLevel;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;
}
