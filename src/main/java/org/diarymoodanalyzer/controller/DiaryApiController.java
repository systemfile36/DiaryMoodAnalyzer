package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.*;
import org.diarymoodanalyzer.service.DiaryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class DiaryApiController {

    private final DiaryService diaryService;

    @PostMapping("/api/diaries")
    public ResponseEntity<AddDiaryResponse> addDiary(
            @RequestBody AddDiaryRequest req
            ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(diaryService.addDiary(req));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
     */
    @GetMapping("/api/diaries")
    public ResponseEntity<Page<GetDiariesByPageResponse>> getDiariesByEmail(
            @ModelAttribute GetDiariesByPageRequest req //GET으로 받은 파라미터가 DTO로 자동 매핑됨
            ) {
        /*
        스프링 시큐리티를 통해 헤더에서 JWT 토큰을 읽어서 확인하는 부분이 추가될 것
         */
        return ResponseEntity.ok(diaryService.getDiariesByEmail(req));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
    DTO를 통해 제목과 생성/수정 일자, 유저 email만 반환한다.
     */
    @GetMapping("/api/diaries/title")
    public ResponseEntity<Page<GetDiariesTitleByPageResponse>> getDiariesTitleByEmail(
            @ModelAttribute GetDiariesByPageRequest req
            ) {
        /*
        스프링 시큐리티를 통해 헤더에서 JWT 토큰을 읽어서 확인하는 부분이 추가될 것
         */
        return ResponseEntity.ok(diaryService.getDiariesTitleByEmail(req));
    }
}
