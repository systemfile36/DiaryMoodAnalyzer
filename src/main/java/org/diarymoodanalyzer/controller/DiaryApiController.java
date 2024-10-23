package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.AddDiaryRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageRequest;
import org.diarymoodanalyzer.dto.response.AddDiaryResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByIdResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByPageResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse;
import org.diarymoodanalyzer.service.DiaryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/api/diary/{id}")
    public ResponseEntity<GetDiaryByIdResponse> getDiaryById(@PathVariable long id) {
        return ResponseEntity.ok().body(diaryService.getDiaryById(id));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
     */
    @GetMapping("/api/diaries")
    public ResponseEntity<Page<GetDiaryByPageResponse>> getDiariesByEmail(
            @ModelAttribute GetDiaryByPageRequest req //GET으로 받은 파라미터가 DTO로 자동 매핑됨
            ) {
        return ResponseEntity.ok(diaryService.getDiariesByEmail(req));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
    DTO를 통해 제목과 생성/수정 일자, 유저 email만 반환한다.
     */
    @GetMapping("/api/diaries/title")
    public ResponseEntity<Page<GetDiaryTitleByPageResponse>> getDiariesTitleByEmail(
            @ModelAttribute GetDiaryByPageRequest req
            ) {
        return ResponseEntity.ok(diaryService.getDiariesTitleByEmail(req));
    }
    /*
    Diary의 id로 수정
     */
    @PutMapping("/api/diary/{id}")
    public ResponseEntity<GetDiaryByIdResponse> updateDiary(
            @PathVariable long id,
            @RequestBody AddDiaryRequest req // 수정에 사용할 요청 DTO
    ) {
        GetDiaryByIdResponse updatedDiary = diaryService.updateDiaryById(id, req);
        return ResponseEntity.ok(updatedDiary);
    }
    /*
    Diary의 id로 삭제
     */
    @DeleteMapping("/api/diary/{id}")
    public ResponseEntity<Void> deleteDiaryById(@PathVariable long id) {

        diaryService.deleteDiaryById(id);

        return ResponseEntity.ok().build();
    }
}
